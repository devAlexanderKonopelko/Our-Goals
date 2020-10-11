package by.konopelko.ourgoals.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.konopelko.ourgoals.view.home.ActivityMain
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.view.signin.ActivityLogIn
import by.konopelko.ourgoals.presenter.splash.SplashPresenter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

class SplashActivity : AppCompatActivity(), SplashView {

    private lateinit var scope: Scope

    private val presenter = SplashPresenter(
        this,
        checkFirstRun = scope.get()
    )

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        scope = getKoin().getOrCreateScope(DI_SCOPE_NAME, named(DI_SCOPE_NAME))



        CoroutineScope(Dispatchers.IO).launch {
            presenter.loadUserData()
        }
    }

    private fun setCurrentSessionRun(state: Boolean) {
        presenter.onCurrentSessionRunSet(state)
    }

    private suspend fun loadCurrentUserData(): Boolean {
        var result = false
        auth.currentUser?.let { user ->
            result = presenter.onCurrentUserDataLoaded(user.uid, this) // загружаем данные текущего пользователя
        }
        return result
    }

    private suspend fun loadUserGuestData(): Boolean {
        val guestId = getString(R.string.guestId)
        val guestExist = presenter.onGuestUserExistenceChecked(guestId, this)
        var guestCreated = false
        return if (guestExist) {
            guestExist
        } else {
            // создание пользователя Гость в БД
            guestCreated =
                presenter.onGuestUserCreated(guestId, getString(R.string.username_guest), this)
            if (guestCreated) {
                // создание стандартных категорий для Гостя в БД
                val titles = ArrayList<String>()
                titles.addAll(resources.getStringArray(R.array.default_categories_titles))
                guestCreated = presenter.onDefaultCategoriesCreated(guestId, titles, this)
            }
            // создание аналитики Гостя в БД
            if (guestCreated) guestCreated = presenter.onDefaultAnalyticsCreated(guestId, this)
            guestCreated
        }
    }

    override suspend fun transitToMainScreen() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@SplashActivity, ActivityMain::class.java))
        }
    }

    override suspend fun transitToSignInScreen() {
        // start ActivityLogIn
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@SplashActivity, ActivityLogIn::class.java))
        }
    }

    companion object {
        const val DI_SCOPE_NAME = "SplashScope"
    }
}

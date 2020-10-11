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

        presenter.loadUserData()

        CoroutineScope(Dispatchers.IO).launch {
            when {
                // Не первый запуск
                savedVersionCode == currentVersionCode -> {
                    setCurrentSessionRun(false) //
                    if (auth.currentUser != null) {
                        if (auth.currentUser!!.isEmailVerified) {
                            loadCurrentUserData() // загрузка данных текущего пользователя
                            transitToMainScreen() //переход к ActivityMain
                        } else {
                            transitToSignInScreen() //переход к ActivityLogIn
                        }
                    } else {
                        loadCurrentUserData() //  загрузка данных текущего пользователя
                        transitToMainScreen() //  переход к MainActivity
                    }
                }
                // Первый запуск/очищены prefs
                savedVersionCode == PREFS_CODE_DOESNT_EXIST -> {
                    loadUserGuestData() //  загрузка данных Гостя
                    loadCurrentUserData() //  загрузка данных текущего пользователя
                    setCurrentSessionRun(true) //
                    transitToSignInScreen() //  переход к ActivityLogIn
                }
                currentVersionCode > savedVersionCode -> {
                    // TODO Обновить приложение
                }
            }

//        В конце надо записывать в prefs текущую версию, чтобы сохранилась информация о версии.
//        Иначе постоянно будет первый запуск.
            prefs.edit().putInt(PREFS_VERSION_CODE_KEY, currentVersionCode).apply()
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

package by.konopelko.ourgoals.ui.startscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.konopelko.ourgoals.ui.mainscreen.ActivityMain
import by.konopelko.ourgoals.BuildConfig
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.ui.authentication.ActivityLogIn
import by.konopelko.ourgoals.mvp.startscreen.presenter.StartScreenPresenterDefault
import by.konopelko.ourgoals.mvp.startscreen.view.StartScreenView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

const val PREFS_NAME = "shared-prefs"
const val PREFS_VERSION_CODE_KEY = "VERSION_CODE"
const val PREFS_CODE_DOESNT_EXIST = -1

class ActivityStart : AppCompatActivity(),
    StartScreenView {
    private val presenter =
        StartScreenPresenterDefault(
            this,
            this
        )
    private val currentVersionCode =
        BuildConfig.VERSION_CODE

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedVersionCode = prefs.getInt(
            PREFS_VERSION_CODE_KEY,
            PREFS_CODE_DOESNT_EXIST)

        CoroutineScope(Dispatchers.IO).launch {
            loadDatabaseInstance() // загрузка ссылки на БД NEW
            when {
                // Не первый запуск
                savedVersionCode == currentVersionCode -> {
                    setCurrentSessionRun(false) // NEW
                    if (auth.currentUser != null) {
                        if (auth.currentUser!!.isEmailVerified) {
                            loadCurrentUserData() // NEW загрузка данных текущего пользователя
                            transitToMainScreen() // NEW переход к ActivityMain
                        } else {
                            transitToSignInScreen() // NEW переход к ActivityLogIn
                        }
                    } else {
                        loadCurrentUserData() // NEW загрузка данных текущего пользователя
                        transitToMainScreen() // NEW переход к MainActivity
                    }
                }
                // Первый запуск/очищены prefs
                savedVersionCode == PREFS_CODE_DOESNT_EXIST -> {
                    loadUserGuestData() // NEW загрузка данных Гостя
                    loadCurrentUserData() // NEW загрузка данных текущего пользователя
                    setCurrentSessionRun(true) // NEW
                    transitToSignInScreen() // NEW переход к ActivityLogIn
                }
                currentVersionCode > savedVersionCode -> {
                    // TODO Обновление приложения
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

    private fun loadDatabaseInstance(): Boolean {
        return presenter.onDatabaseInstanceLoaded(this)
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
            startActivity(Intent(this@ActivityStart, ActivityMain::class.java))
        }
    }

    override suspend fun transitToSignInScreen() {
        // start ActivityLogIn
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityStart, ActivityLogIn::class.java))
        }
    }
}

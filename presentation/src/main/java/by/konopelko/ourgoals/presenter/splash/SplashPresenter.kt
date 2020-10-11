package by.konopelko.ourgoals.presenter.splash

import android.content.Context
import by.konopelko.domain.interactors.startscreen.StartScreenInteractor
import by.konopelko.ourgoals.domain.entity.AppState
import by.konopelko.ourgoals.domain.usecases.checkfirstrun.CheckFirstRunUseCase
import by.konopelko.ourgoals.view.splash.SplashView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SplashPresenter(
    private val view: SplashView,
    private val checkFirstRun: CheckFirstRunUseCase
) {

    private val interactor = StartScreenInteractor()

    // load users data depending on app first start by checking version code
    fun loadUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            val firstRun = checkFirstRun()
            when (firstRun) {
                AppState.FIRST_RUN -> {
                    loadUserGuestData() //  загрузка данных Гостя
                    loadCurrentUserData() //  загрузка данных текущего пользователя
                    setCurrentSessionRun(true) //
                    transitToSignInScreen() //  переход к ActivityLogIn
                }
                AppState.REPEAT_RUN -> {
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
                AppState.NEED_UPDATE -> {
                } // TODO: create update popup dialog
            }
        }
        //        В конце надо записывать в prefs текущую версию, чтобы сохранилась информация о версии.
//        Иначе постоянно будет первый запуск.
        prefs.edit().putInt(PREFS_VERSION_CODE_KEY, currentVersionCode).apply()
    }

    suspend fun onGuestUserExistenceChecked(uid: String, context: Context): Boolean {
        return interactor.checkGuestUserExistence(uid, context)
    }

    suspend fun onGuestUserCreated(uid: String, name: String, context: Context): Boolean {
        return interactor.createGuestUser(uid, name, context)
    }

    suspend fun onDefaultCategoriesCreated(
        uid: String,
        titles: ArrayList<String>,
        context: Context
    ): Boolean {
        return interactor.createDefaultCategories(uid, titles, context)
    }

    suspend fun onDefaultAnalyticsCreated(uid: String, context: Context): Boolean {
        return interactor.createDefaultAnalytics(uid, context)
    }

    suspend fun onCurrentUserDataLoaded(uid: String, context: Context): Boolean {
        val result: Boolean = interactor.setCurrentUser(uid, context)
        if (result) {
            onUsersCategoriesLoaded(uid, context)
            onUsersPersonalGoalsLoaded(uid, context)
            onUsersSocialGoalsLoaded(uid, context)
            onUsersAnalyticsLoaded(uid, context)
        }
        return result
    }

    suspend fun onUsersCategoriesLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersCategories(uid, context)
    }

    suspend fun onUsersPersonalGoalsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersPersonalGoals(uid, context)
    }

    suspend fun onUsersSocialGoalsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersSocialGoals(uid, context)
    }

    suspend fun onUsersAnalyticsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersAnalytics(uid, context)
    }

    fun onCurrentSessionRunSet(state: Boolean) {
        interactor.setCurrentSessionRun(state)
    }

    companion object {
        const val PREFS_CODE_DOESNT_EXIST = -1
    }
}
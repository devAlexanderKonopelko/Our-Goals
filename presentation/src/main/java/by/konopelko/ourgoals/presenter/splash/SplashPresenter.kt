package by.konopelko.ourgoals.presenter.splash

import android.content.Context
import by.konopelko.domain.interactors.startscreen.StartScreenInteractor
import by.konopelko.ourgoals.domain.entity.AppState
import by.konopelko.ourgoals.domain.usecases.checkfirstrun.CheckFirstRunUseCase
import by.konopelko.ourgoals.domain.usecases.setappstate.SetAppStateUseCase
import by.konopelko.ourgoals.domain.usecases.updateversioncode.UpdateVersionCodeUseCase
import by.konopelko.ourgoals.view.splash.SplashView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashPresenter(
    private val view: SplashView,
    private val checkFirstRun: CheckFirstRunUseCase,
    private val updateVersionCode: UpdateVersionCodeUseCase,
    private val setAppState: SetAppStateUseCase
) {

    private var isFirstRun: AppState? = null

    private val interactor = StartScreenInteractor()

    fun transitToNextScreen() {
        CoroutineScope(IO).launch {
            isFirstRun?.let {
                when (isFirstRun) {
                    AppState.FIRST_RUN -> {
                        // Перейти в Sign In Activity
                        // Если пользователь зарегался - добавить пользователя в бд
                        // Отметить пользователя как последнего вошеднего пользователя
                        // Иначе если пользователь вошёл как гость -
                            // Создать гостя в бд
                            // Отметить гостя как последнего вошеднего пользователя
                        // Перейти в Home Activity


//                    loadUserGuestData() //  вынести заргузку гостя в Sign In Activity
//                    loadCurrentUserData() // ? загрузка данных текущего пользователя
                        transitToSignInScreen() //  переход к ActivityLogIn
                    }
                    AppState.REPEAT_RUN -> {
                        // Проверить наличие последнего пользователя в преференсах
                        val lastUserId = getLastUserIdUseCase() // TODO: create use case
                        // Если последнего пользователя нет - задать и загрузить гостя как последнего пользователя
                        // Если последний пользователь есть - загрузить его по id из бд
                        // Перейти в Home Activity

//                        loadUserData() // загрузка данных текущего пользователя

                        transitToMainScreen()
                    }
                    AppState.NEED_UPDATE -> {
                    } // TODO: create update popup dialog
                }
            }
        }
    }

    //TODO("Not yet implemented")
    private suspend fun loadUserData() {

//        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
//        } else {}
    }

    fun checkFirstRun() {
        isFirstRun = checkFirstRun.invoke()
        setAppState(isFirstRun!!)
        updateVersionCode()
    }

    private fun updateVersionCode() {
        updateVersionCode.invoke()
    }

    private suspend fun transitToMainScreen() {
        withContext(Main) {
            view.transitToMainScreen()
        }
    }

    private suspend fun transitToSignInScreen() {
        withContext(Main) {
            view.transitToSignInScreen()
        }
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
        const val MAIN_SCREEN = "MainScreen"
        const val SIGN_IN_SCREEN = "SignInScreen"
    }
}
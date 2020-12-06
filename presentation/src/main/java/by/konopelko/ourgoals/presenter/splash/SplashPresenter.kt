package by.konopelko.ourgoals.presenter.splash

import android.content.Context
import by.konopelko.domain.interactors.startscreen.StartScreenInteractor
import by.konopelko.ourgoals.domain.entity.AppState
import by.konopelko.ourgoals.domain.usecases.checkfirstrun.CheckFirstRunUseCase
import by.konopelko.ourgoals.domain.usecases.getlastuserid.GetLastUserIdUseCase
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
    private val setAppState: SetAppStateUseCase,
    private val getLastUserId: GetLastUserIdUseCase
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
                        val lastUserId = getLastUserId()
                        // Если последнего пользователя нет - передаём пустоту в Home Activity
                        // Если последний пользователь есть - передать в Home Activity его id
                        // Перейти в Home Activity
                        // Если был передан id последнего пользователя - загрузить данные пользователя по id из бд
                        // Если id не был передан - загрузить данные гостя

//                        loadUserData() // загрузка данных текущего пользователя

                        transitToMainScreen(lastUserId)
                    }
                    AppState.NEED_UPDATE -> {
                    } // TODO: create update popup dialog
                }
            }
        }
    }

    //TODO("Not yet implemented")
    // перенести в Home Activity
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

    private suspend fun transitToMainScreen(lastUserId: String?) {
        withContext(Main) {
            view.transitToMainScreen(lastUserId)
        }
    }

    private suspend fun transitToSignInScreen() {
        withContext(Main) {
            view.transitToSignInScreen()
        }
    }

    // TODO: удалить или перенести в Home Activity
    suspend fun onGuestUserExistenceChecked(uid: String, context: Context): Boolean {
        return interactor.checkGuestUserExistence(uid, context)
    }

    // TODO: удалить или перенести в другую Activity
    suspend fun onGuestUserCreated(uid: String, name: String, context: Context): Boolean {
        return interactor.createGuestUser(uid, name, context)
    }

    // TODO: удалить или перенести в Home Activity
    suspend fun onDefaultCategoriesCreated(
        uid: String,
        titles: ArrayList<String>,
        context: Context
    ): Boolean {
        return interactor.createDefaultCategories(uid, titles, context)
    }

    // TODO: удалить или перенести в Home Activity
    suspend fun onDefaultAnalyticsCreated(uid: String, context: Context): Boolean {
        return interactor.createDefaultAnalytics(uid, context)
    }

    // TODO: удалить или перенести в Home Activity
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

    // TODO: удалить или перенести в Home Activity
    suspend fun onUsersCategoriesLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersCategories(uid, context)
    }

    // TODO: удалить или перенести в Home Activity
    suspend fun onUsersPersonalGoalsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersPersonalGoals(uid, context)
    }

    // TODO: удалить или перенести в Home Activity
    suspend fun onUsersSocialGoalsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersSocialGoals(uid, context)
    }

    // TODO: удалить или перенести в Home Activity
    suspend fun onUsersAnalyticsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersAnalytics(uid, context)
    }

    // TODO: удалить или перенести в другую Activity
    fun onCurrentSessionRunSet(state: Boolean) {
        interactor.setCurrentSessionRun(state)
    }
}
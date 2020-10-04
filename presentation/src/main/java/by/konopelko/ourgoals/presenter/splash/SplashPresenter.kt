package by.konopelko.ourgoals.presenter.splash

import android.content.Context
import by.konopelko.domain.interactors.startscreen.StartScreenInteractor
import by.konopelko.domain.usecases.getversioncode.GetVersionCodeUseCase
import by.konopelko.ourgoals.view.splash.SplashView


class SplashPresenter(
    private val view: SplashView,
    private val getVersionCode: GetVersionCodeUseCase
) {

    private val interactor = StartScreenInteractor()

    // load users data depending on app first start by checking version code
    fun loadUserData() {
        when(getVersionCode()) {
        }
    }

    fun onDatabaseInstanceLoaded(context: Context): Boolean {
        return interactor.loadDatabaseInstance(context)
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
}
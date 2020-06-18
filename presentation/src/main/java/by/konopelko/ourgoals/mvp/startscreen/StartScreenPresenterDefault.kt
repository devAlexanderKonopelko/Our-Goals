package by.konopelko.ourgoals.mvp.startscreen

import android.content.Context
import by.konopelko.domain.interactors.startscreen.StartScreenInteractor


class StartScreenPresenterDefault(val view: StartScreenView, context: Context): StartScreenPresenter {
    private val interactor = StartScreenInteractor()

    override fun onDatabaseInstanceLoaded(context: Context): Boolean {
        return interactor.loadDatabaseInstance(context)
    }

    override suspend fun onGuestUserExistenceChecked(uid: String, context: Context): Boolean {
        return interactor.checkGuestUserExistence(uid, context)
    }

    override suspend fun onGuestUserCreated(uid: String, name: String, context: Context): Boolean {
        return interactor.createGuestUser(uid, name, context)
    }

    override suspend fun onDefaultCategoriesCreated(uid: String, titles: ArrayList<String>, context: Context): Boolean {
        return interactor.createDefaultCategories(uid, titles, context)
    }

    override fun onDefaultAnalyticsCreated(): Boolean {
        return true
    }

    override fun onCurrentUserDataLoaded(): Boolean {
        return true
    }

    override fun onUsersCategoriesLoaded(): Boolean {
        return true
    }

    override fun onUsersPersonalGoalsLoaded(): Boolean {
        return true
    }

    override fun onUsersSocialGoalsLoaded(): Boolean {
        return true
    }

    override fun onUsersAnalyticsLoaded(): Boolean {
        return true
    }
}
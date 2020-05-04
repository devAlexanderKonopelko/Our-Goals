package by.konopelko.ourgoals.authentication.core

import by.konopelko.ourgoals.database.entities.User

class LogInPresenter(private val view: LogInContract.View) :
    LogInContract.Presenter, LogInContract.OnOperationListener {
    private val interactor: LogInContract.Interactor = LogInInteractor(this)

    override fun logIn(email: String, password: String) {
        interactor.performLogIn(email, password)
    }

    override fun loadUserFromServer(uid: String) {
        interactor.performUserDownLoad(uid)
    }

    override fun loadSocialGoalsFromServer(uid: String) {
        interactor.performSocialGoalsDownload(uid)
    }

    override fun loadFriendsNotifications(uid: String) {
        interactor.performFriendsNotificationsDownload(uid)
    }

    override fun loadGoalsNotifications(uid: String) {
        interactor.performGoalsNotificationsDownload(uid)
    }

    override fun onLogIn(result: Int, uid: String) {
        view.onLogIn(result, uid)
    }

    override fun onUserLoadedFromServer(user: User) {
        view.onUserLoadedFromServer(user)
    }

    override fun onSocialGoalsLoaded(result: Boolean) {
        view.onSocialGoalsLoaded(result)
    }

    override fun onFriendsNotificationsLoaded(result: Boolean) {
        view.onFriendsNotificationsLoaded(result)
    }

    override fun onGoalsNotificationsLoaded(result: Boolean) {
        view.onGoalsNotificationsLoaded(true)
    }
}
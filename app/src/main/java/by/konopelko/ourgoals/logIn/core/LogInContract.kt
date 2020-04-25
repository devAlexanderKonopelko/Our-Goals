package by.konopelko.ourgoals.logIn.core

import by.konopelko.ourgoals.database.User

interface LogInContract {

    interface View {
        fun onLogIn(result: Boolean, uid: String)
        fun onUserLoadedFromServer(user: User)
        fun onSocialGoalsLoaded(result: Boolean)
        fun onFriendsNotificationsLoaded(result: Boolean)
        fun onGoalsNotificationsLoaded(result: Boolean)
    }

    interface Presenter {
        fun logIn(email: String, password: String)
        fun loadUserFromServer(uid: String)
        fun loadSocialGoalsFromServer(uid: String)
        fun loadFriendsNotifications(uid: String)
        fun loadGoalsNotifications(uid: String)
    }

    interface Interactor {
        fun performLogIn(email: String, password: String)
        fun performUserDownLoad(uid: String)
        fun performSocialGoalsDownload(uid: String)
        fun performFriendsNotificationsDownload(uid: String)
        fun performGoalsNotificationsDownload(uid: String)
    }

    interface OnOperationListener {
        fun onLogIn(result: Boolean, uid: String)
        fun onUserLoadedFromServer(user: User)
        fun onSocialGoalsLoaded(result: Boolean)
        fun onFriendsNotificationsLoaded(result: Boolean)
        fun onGoalsNotificationsLoaded(result: Boolean)
    }
}
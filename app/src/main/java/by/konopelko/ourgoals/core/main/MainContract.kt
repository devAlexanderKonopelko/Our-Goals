package by.konopelko.ourgoals.core.main

import by.konopelko.ourgoals.database.User

interface MainContract {
    interface View {
        fun onNotificationsChanged(listSize: Int)
    }

    interface Presenter {
        fun observeNotifications(uid: String)
    }

    interface Interactor {
        fun performNotificationsObservation(uid: String)
        fun performFriendsNotificationsObservation(uid: String)
        fun performGoalsNotificationsObservation(uid: String)
    }

    interface OnOperationListener {
        fun onNotificationsChanged(listSize: Int)
    }
}
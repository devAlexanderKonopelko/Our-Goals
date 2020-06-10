package by.konopelko.ourgoals.core.main

interface MainContract {
    interface View {
        fun onNotificationsChanged(listSize: Int)
        fun onNotificationsListenersRemoved()
    }

    interface Presenter {
        fun observeNotifications(uid: String)
        fun removeNotificationsListeners()
    }

    interface Interactor {
        fun performNotificationsObservation(uid: String)
        fun performFriendsNotificationsObservation(uid: String)
        fun performGoalsNotificationsObservation(uid: String)
        fun performNotificationsListenersRemoval()
    }

    interface OnOperationListener {
        fun onNotificationsChanged(listSize: Int)
        fun onNotificationsListenersRemoved()
    }
}
package by.konopelko.ourgoals.core.main

class MainPresenter(private val view: MainContract.View) : MainContract.Presenter,
    MainContract.OnOperationListener {
    private val interactor: MainContract.Interactor = MainInteractor(this)
    override fun observeNotifications(uid: String) {
        interactor.performNotificationsObservation(uid)
    }

    override fun removeNotificationsListeners() {
        interactor.performNotificationsListenersRemoval()
    }

    override fun onNotificationsChanged(listSize: Int) {
        view.onNotificationsChanged(listSize)
    }

    override fun onNotificationsListenersRemoved() {
        view.onNotificationsListenersRemoved()
    }
}
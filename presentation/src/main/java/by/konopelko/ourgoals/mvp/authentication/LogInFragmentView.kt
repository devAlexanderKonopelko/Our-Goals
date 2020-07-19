package by.konopelko.ourgoals.mvp.authentication


interface LogInFragmentView {
    fun showAuthenticationError()
    fun showInternetError()
    fun showGeneralError()
}
package by.konopelko.ourgoals.mvp.authentication


interface LogInFragmentView {
    // NEW
    fun showAuthenticationError()
    fun showInternetError()
    fun showGeneralError()

    // OLD
//    fun onLogIn(result: Int, uid: String)
//    fun onUserLoadedFromServer(user: User)
//    fun onSocialGoalsLoaded(result: Boolean)
}
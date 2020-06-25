package by.konopelko.ourgoals.mvp.authentication


interface LogInFragmentView {
    // OLD
    fun onLogIn(result: Int, uid: String)
//    fun onUserLoadedFromServer(user: User)
    fun onSocialGoalsLoaded(result: Boolean)
}
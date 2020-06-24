package by.konopelko.ourgoals.mvp.authentication

import by.konopelko.ourgoals.database.entities.User

interface LogInView {
    fun onLogIn(result: Int, uid: String)
    fun onUserLoadedFromServer(user: User)
    fun onSocialGoalsLoaded(result: Boolean)
}
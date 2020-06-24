package by.konopelko.ourgoals.mvp.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LogInPresenter {
    fun logIn(email: String, password: String)
    fun logInWithGoogle(googleSignInAccount: GoogleSignInAccount)
    fun loadUserFromServer(uid: String)
    fun loadSocialGoalsFromServer(uid: String)
}
package by.konopelko.ourgoals.authentication.core

import by.konopelko.ourgoals.database.entities.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LogInContract {

    interface View {
        fun onLogIn(result: Int, uid: String)
        fun onUserLoadedFromServer(user: User)
        fun onSocialGoalsLoaded(result: Boolean)
    }

    interface Presenter {
        fun logIn(email: String, password: String)
        fun logInWithGoogle(googleSignInAccount: GoogleSignInAccount)
        fun loadUserFromServer(uid: String)
        fun loadSocialGoalsFromServer(uid: String)
    }

    interface Interactor {
        fun performLogIn(email: String, password: String)
        fun performLogInWithGoogle(googleSignInAccount: GoogleSignInAccount)
        fun performUserDownLoad(uid: String)
        fun performSocialGoalsDownload(uid: String)
    }

    interface OnOperationListener {
        fun onLogIn(result: Int, uid: String)
        fun onUserLoadedFromServer(user: User)
        fun onSocialGoalsLoaded(result: Boolean)
    }
}
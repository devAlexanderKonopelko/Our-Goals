package by.konopelko.ourgoals.authentication.core

import by.konopelko.ourgoals.database.entities.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LogInPresenter(private val view: LogInContract.View) :
    LogInContract.Presenter, LogInContract.OnOperationListener {
    private val interactor: LogInContract.Interactor = LogInInteractor(this)

    override fun logIn(email: String, password: String) {
        interactor.performLogIn(email, password)
    }

    override fun logInWithGoogle(googleSignInAccount: GoogleSignInAccount) {
        interactor.performLogInWithGoogle(googleSignInAccount)
    }

    override fun loadUserFromServer(uid: String) {
        interactor.performUserDownLoad(uid)
    }

    override fun loadSocialGoalsFromServer(uid: String) {
        interactor.performSocialGoalsDownload(uid)
    }

    override fun onLogIn(result: Int, uid: String) {
        view.onLogIn(result, uid)
    }

    override fun onUserLoadedFromServer(user: User) {
        view.onUserLoadedFromServer(user)
    }

    override fun onSocialGoalsLoaded(result: Boolean) {
        view.onSocialGoalsLoaded(result)
    }
}
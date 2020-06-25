package by.konopelko.ourgoals.mvp.authentication

import android.content.Context
import androidx.fragment.app.FragmentActivity
import by.konopelko.domain.interactors.authentication.AuthenticationInteractor
import by.konopelko.ourgoals.database.entities.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LogInPresenterDefault(private val view: LogInGeneralView): LogInPresenter {
    private val interactor = AuthenticationInteractor()
    override suspend fun onCurrentUserDataLoaded(uid: String, context: Context): Boolean {
        val result: Boolean = interactor.setCurrentUser(uid, context)
        if (result) {
            onUsersCategoriesLoaded(uid, context)
            onUsersPersonalGoalsLoaded(uid, context)
            onUsersAnalyticsLoaded(uid, context)
        }
        return result
    }

    override suspend fun onUsersCategoriesLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersCategories(uid, context)
    }

    override suspend fun onUsersPersonalGoalsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersPersonalGoals(uid, context)
    }

    override suspend fun onUsersAnalyticsLoaded(uid: String, context: Context): Boolean {
        return interactor.loadUsersAnalytics(uid, context)
    }

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

    override suspend fun onCurrentSessionRunChecked() {
        val activityNumber = interactor.checkCurrentSessionRun()
        if (activityNumber == 1) {
            interactor.setCurrentSessionRun(false)
            view.startGuideActivity()
        } else {
            view.startMainActivity()
        }
    }

    override fun onGoogleRequestCreated(activity: FragmentActivity) {
        interactor.createGoogleRequest(activity)
    }
}
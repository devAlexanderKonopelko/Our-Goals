package by.konopelko.ourgoals.mvp.authentication

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import by.konopelko.domain.interactors.authentication.AuthenticationInteractor
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LogInPresenterDefault(private val generalView: LogInGeneralView): LogInPresenter {

    constructor(generalView: LogInGeneralView, fragmentView: LogInFragmentView) : this(generalView) {
        this.fragmentView = fragmentView
    }

    private lateinit var fragmentView: LogInFragmentView
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

    override suspend fun logIn(email: String, password: String, context: Context) {
        val result = interactor.performLogIn(email, password, context)
        when (result) {
            0 -> { // проверяем и запускаем нужную активити
                onCurrentSessionRunChecked()
            }
            1 -> {
                // Вывести ошибку аутентификации
                fragmentView.showAuthenticationError()
            }
            2 -> {
                // Вывести ошибку интернет-соединения
                fragmentView.showInternetError()
            }
            3 -> {
                // Вывести общую ошибку входа
                fragmentView.showGeneralError()
            }
        }
    }

    override suspend fun onLoggedInWithGoogle(googleSignInAccount: GoogleSignInAccount, context: Context) {
        val result = interactor.performLogInWithGoogle(googleSignInAccount, context)
        when (result) {
            0 -> { // проверяем и запускаем нужную активити
                onCurrentSessionRunChecked()
            }
            1 -> {
                // Вывести ошибку аутентификации
                fragmentView.showAuthenticationError()
            }
            2 -> {
                // Вывести ошибку интернет-соединения
                fragmentView.showInternetError()
            }
            3 -> {
                // Вывести общую ошибку входа
                fragmentView.showGeneralError()
            }
        }
    }

    // OLD
//    override fun loadUserFromServer(uid: String) {
//        interactor.performUserDownLoad(uid)
//    }

    // OLD
//    override fun loadSocialGoalsFromServer(uid: String) {
//        interactor.performSocialGoalsDownload(uid)
//    }

    override fun onRegisteredWithGoogle(): Intent? {
        return interactor.getGoogleAuthIntent()
    }

    override suspend fun onCurrentSessionRunChecked() {
        val activityNumber = interactor.checkCurrentSessionRun()
        if (activityNumber == 1) {
            interactor.setCurrentSessionRun(false)
            generalView.startGuideActivity()
        } else {
            generalView.startMainActivity()
        }
    }

    override fun onGoogleRequestCreated(activity: FragmentActivity, webClientId: String) {
        interactor.createGoogleRequest(activity, webClientId)
    }
}
package by.konopelko.ourgoals.mvp.authentication.presenter

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import by.konopelko.domain.interactors.authentication.AuthenticationInteractor
import by.konopelko.ourgoals.mvp.authentication.view.RegisterFragmentView
import by.konopelko.ourgoals.mvp.authentication.view.LogInFragmentView
import by.konopelko.ourgoals.mvp.authentication.view.LogInGeneralView
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LogInPresenterDefault():
    LogInPresenter {

    constructor(logInGeneralView: LogInGeneralView): this() {
        generalView = logInGeneralView
    }

    constructor(logInFragmentView: LogInFragmentView, registerView: RegisterFragmentView): this() {
        fragmentView = logInFragmentView
        registerFragmentView = registerView
    }

    constructor(generalView: LogInGeneralView, logInFragmentView: LogInFragmentView) : this(generalView) {
        fragmentView = logInFragmentView
    }

    private lateinit var fragmentView: LogInFragmentView
    private lateinit var generalView: LogInGeneralView
    private lateinit var registerFragmentView: RegisterFragmentView
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

    override fun onRegisteredWithGoogle(): Intent? {
        return interactor.getGoogleAuthIntent()
    }

    override suspend fun onRegisteredWithEmailPassword(
        email: String,
        password: String,
        name: String,
        context: Context
    ) {
        val result = interactor.performRegisterWithEmailPassword(email, password, name, context)

        when(result) {
            0 -> { // всё ок, показываем фрагмент входа
                registerFragmentView.transitToLogInFragment()
            }
            1 -> { // сообщение о том, что пользователь уже существует
                registerFragmentView.showUserExistError()
            }
            2 -> { // ошибка авторизации
                fragmentView.showAuthenticationError()
            }
            3 -> { // ошибка соединения с интернетом
                fragmentView.showInternetError()
            }
            4 -> { // неизвестная ошибка
                fragmentView.showGeneralError()
            }
        }
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
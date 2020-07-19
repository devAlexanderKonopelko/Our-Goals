package by.konopelko.domain.interactors.authentication

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import by.konopelko.domain.R
import by.konopelko.domain.repositories.FirebaseAuthRepository
import by.konopelko.domain.repositories.GoogleAuthRepository
import by.konopelko.domain.repositories.UserRepository
import by.konopelko.domain.repositories.UserRepositoryDefault
import by.konopelko.domain.repositories.session.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class AuthenticationInteractor {
    lateinit var categoryRepository: CategoryRepository
    lateinit var personalGoalRepository: PersonalGoalRepository
    lateinit var analyticsRepository: AnalyticsRepository
    lateinit var sessionGeneralRepository: SessionGeneralRepository
    lateinit var googleAuthRepository: GoogleAuthRepository
    lateinit var firebaseAuthRepository: FirebaseAuthRepository
    lateinit var userRepository: UserRepository
    lateinit var teamGoalRepository: TeamGoalsRepository

    suspend fun performLogIn(email: String, password: String, context: Context): Int {
        firebaseAuthRepository = FirebaseAuthRepository()
        val resultCode = firebaseAuthRepository.logInWithEmailPassword(email, password)

        if (resultCode == 0) {
            loadUsersData(context)
        }
        return resultCode
    }

    suspend fun performLogInWithGoogle(googleSignInAccount: GoogleSignInAccount, context: Context): Int {
        googleAuthRepository = GoogleAuthRepository()
        firebaseAuthRepository = FirebaseAuthRepository()
        userRepository = UserRepositoryDefault()
        analyticsRepository = AnalyticsRepositoryDefault()
        personalGoalRepository = PersonalGoalRepositoryDefault()
        teamGoalRepository = TeamGoalsRepositoryDefault()
        categoryRepository = CategoryRepositoryDefault()

        var resultCode = -1
        val credential = googleAuthRepository.getAuthCredential(googleSignInAccount)
        resultCode = firebaseAuthRepository.logInWithGoogle(credential, googleSignInAccount)

        // После того, как мы вошли в аккаунт, проверяем код ответа
        if (resultCode == 0) { // если вход прошёл успешно, то
            loadUsersData(context) // загружаем данные пользователя
        } // если что-то пошло не так - возвращается код ошибки
        return resultCode
    }

    private suspend fun loadUsersData(context: Context) {
        val uid = userRepository.getAuthorizedUserId() // берём id пользователя
        val userExists = userRepository.checkUserExistence(uid, context) // Проверяем, есть ли уже пользователь в бд

        if (!userExists) { // 2.1) Если нету
            userRepository.createUserFromServer(uid, context) // загружаем с сервера, добавляем в текущую сессию и бд
        } else { // 2.2) Если есть
            userRepository.setCurrentUser(uid, context) // добавляем пользователя из бд в текущую сессию
        }
        val titles = ArrayList<String>()
        titles.addAll(context.resources.getStringArray(R.array.default_categories_titles))
        categoryRepository.createDefaultCategories(uid, titles, context) // Загружаем стандартные категории Пользователя в бд
        categoryRepository.loadUsersCategoris(uid, context) // загружаем категории в текущую сессию
        analyticsRepository.createDefaultAnalytics(uid, context) // загружаем стандартную аналитику Пользователя в бд
        analyticsRepository.loadUsersAnalytics(uid, context) // загружаем аналитику в текущую сессию
        personalGoalRepository.loadUsersPersonalGoals(uid, context) // Загружаем в текущую сессию личные цели
        teamGoalRepository.loadUsersTeamGoals(uid, context) // и командные цели
    }

    suspend fun setCurrentUser(uid: String, context: Context): Boolean {
        userRepository = UserRepositoryDefault()
        return userRepository.setCurrentUser(uid, context)
    }

    suspend fun loadUsersCategories(uid: String, context: Context): Boolean {
        categoryRepository = CategoryRepositoryDefault()
        return categoryRepository.loadUsersCategoris(uid, context)
    }

    suspend fun loadUsersPersonalGoals(uid: String, context: Context): Boolean {
        personalGoalRepository = PersonalGoalRepositoryDefault()
        return personalGoalRepository.loadUsersPersonalGoals(uid, context)
    }

    suspend fun loadUsersAnalytics(uid: String, context: Context): Boolean {
        analyticsRepository = AnalyticsRepositoryDefault()
        return analyticsRepository.loadUsersAnalytics(uid, context)
    }

    fun checkCurrentSessionRun(): Int {
        sessionGeneralRepository = SessionGeneralRepositoryDefault()
        return sessionGeneralRepository.checkCurrentSessionRun()
    }

    fun setCurrentSessionRun(state: Boolean) {
        sessionGeneralRepository = SessionGeneralRepositoryDefault()
        sessionGeneralRepository.setCurrentSessionRun(state)
    }

    fun createGoogleRequest(activity: FragmentActivity, webClientId: String) {
        googleAuthRepository =
            GoogleAuthRepository()
        googleAuthRepository.createGoogleRequest(activity, webClientId)
    }

    fun getGoogleAuthIntent(): Intent? {
        googleAuthRepository =
            GoogleAuthRepository()
        return googleAuthRepository.getGoogleAuthIntent()
    }

    suspend fun performRegisterWithEmailPassword(email: String, password: String, name: String, context: Context): Int {
        firebaseAuthRepository = FirebaseAuthRepository()
        val resultCode = firebaseAuthRepository.registerWithEmailPassword(email, password, name)

        if (resultCode == 0) { // если регистрация прошла успешно, то
            loadUsersData(context) // загружаем данные пользователя
        } // если что-то пошло не так - возвращается код ошибки
        return resultCode
    }
}
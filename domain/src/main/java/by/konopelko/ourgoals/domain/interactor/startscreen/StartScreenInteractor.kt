package by.konopelko.domain.interactors.startscreen

import android.content.Context
import by.konopelko.domain.repositories.database.DatabaseRepository
import by.konopelko.domain.repositories.database.DatabaseRepositoryDefault
import by.konopelko.domain.repositories.UserRepository
import by.konopelko.domain.repositories.UserRepositoryDefault
import by.konopelko.domain.repositories.session.*

class StartScreenInteractor {
    lateinit var userRepository: UserRepository
    lateinit var databaseRepository: DatabaseRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var analyticsRepository: AnalyticsRepository
    lateinit var personalGoalRepository: PersonalGoalRepository
    lateinit var teamGoalsRepository: TeamGoalsRepository
    lateinit var sessionGeneralRepository: SessionGeneralRepository

    suspend fun checkGuestUserExistence(uid: String, context: Context): Boolean {
        userRepository = UserRepositoryDefault()
        return userRepository.checkUserExistence(uid, context)
    }

    fun loadDatabaseInstance(context: Context): Boolean {
        databaseRepository =
            DatabaseRepositoryDefault()
        return databaseRepository.loadDatabaseInstance(context)
    }

    suspend fun createGuestUser(uid: String, name: String, context: Context):Boolean {
        userRepository = UserRepositoryDefault()
        return userRepository.createUser(uid, name, context)
    }

    suspend fun createDefaultCategories(uid: String, titles: ArrayList<String>, context: Context): Boolean {
        categoryRepository = CategoryRepositoryDefault()
        return categoryRepository.createDefaultCategories(uid, titles, context)
    }

    suspend fun createDefaultAnalytics(uid: String, context: Context): Boolean {
        analyticsRepository = AnalyticsRepositoryDefault()
        return analyticsRepository.createDefaultAnalytics(uid, context)
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

    suspend fun loadUsersSocialGoals(uid: String, context: Context): Boolean {
        teamGoalsRepository = TeamGoalsRepositoryDefault()
        return teamGoalsRepository.loadUsersTeamGoals(uid, context)
    }

    suspend fun loadUsersAnalytics(uid: String, context: Context): Boolean {
        analyticsRepository = AnalyticsRepositoryDefault()
        return analyticsRepository.loadUsersAnalytics(uid, context)
    }

    fun setCurrentSessionRun(state: Boolean) {
        sessionGeneralRepository = SessionGeneralRepositoryDefault()
        sessionGeneralRepository.setCurrentSessionRun(state)
    }
}
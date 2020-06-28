package by.konopelko.data.repositories.session

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.repositories.firebase.TeamGoalRepositoryImpl
import by.konopelko.data.session.GoalsData
import by.konopelko.data.session.TeamGoalsData

class GoalRepositoryImpl {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    lateinit var teamGoalRepositoryImpl: TeamGoalRepositoryImpl

    // Загружает из бд в колелкцию личные цели по Id пользователя
    suspend fun loadUsersPersonalGoals(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        GoalsData.instance.goalsInProgressList = databaseRepositoryImpl.loadUsersPersonalGoals(uid, context)
        return true
    }

    fun loadUsersTeamGoals(uid: String, context: Context): Boolean {
        teamGoalRepositoryImpl = TeamGoalRepositoryImpl()
        return teamGoalRepositoryImpl.loadUsersTeamGoals(uid, context)
    }
}
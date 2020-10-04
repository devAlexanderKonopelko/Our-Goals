package by.konopelko.domain.repositories.session

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.repositories.firebase.TeamGoalRepositoryImpl
import by.konopelko.data.repositories.session.GoalRepositoryImpl

class TeamGoalsRepositoryDefault : TeamGoalsRepository {
    lateinit var goalRepositoryImpl: GoalRepositoryImpl
    override suspend fun loadUsersTeamGoals(uid: String, context: Context): Boolean {
        goalRepositoryImpl = GoalRepositoryImpl()
        return goalRepositoryImpl.loadUsersTeamGoals(uid, context)
    }
}
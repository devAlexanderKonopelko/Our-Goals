package by.konopelko.domain.repositories.session

import android.content.Context
import by.konopelko.data.repositories.session.GoalRepositoryImpl

class PersonalGoalRepositoryDefault : PersonalGoalRepository {
    lateinit var goalRepositoryImpl: GoalRepositoryImpl
    override suspend fun loadUsersPersonalGoals(uid: String, context: Context): Boolean {
        goalRepositoryImpl = GoalRepositoryImpl()
        return goalRepositoryImpl.loadUsersPersonalGoals(uid, context)
    }
}
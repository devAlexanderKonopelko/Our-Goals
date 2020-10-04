package by.konopelko.domain.repositories.session

import android.content.Context

interface PersonalGoalRepository {
    suspend fun loadUsersPersonalGoals(uid: String, context: Context): Boolean
}
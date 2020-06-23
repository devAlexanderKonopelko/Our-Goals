package by.konopelko.domain.repositories.session

import android.content.Context

interface TeamGoalsRepository {
    suspend fun loadUsersTeamGoals(uid: String, context: Context): Boolean
}
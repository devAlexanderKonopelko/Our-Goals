package by.konopelko.domain.repositories.session

import android.content.Context

interface AnalyticsRepository {
    suspend fun createDefaultAnalytics(uid: String, context: Context): Boolean
    suspend fun loadUsersAnalytics(uid: String, context: Context): Boolean
}
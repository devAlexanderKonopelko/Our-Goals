package by.konopelko.data.repositories.session

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.local.AnalyticsData

class AnalyticsRepositoryImpl {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    suspend fun loadUsersAnalytics(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        AnalyticsData.instance.analytics = databaseRepositoryImpl.loadUsersAnalytics(uid, context)
        return true
    }
}
package by.konopelko.domain.repositories.session

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.repositories.session.AnalyticsRepositoryImpl

class AnalyticsRepositoryDefault: AnalyticsRepository {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    lateinit var analyticsRepositoryImpl: AnalyticsRepositoryImpl
    override suspend fun createDefaultAnalytics(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        return databaseRepositoryImpl.addAnalytics(uid, context)
    }

    override suspend fun loadUsersAnalytics(uid: String, context: Context): Boolean {
        analyticsRepositoryImpl = AnalyticsRepositoryImpl()
        return analyticsRepositoryImpl.loadUsersAnalytics(uid, context)
    }
}
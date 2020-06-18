package by.konopelko.domain.repositories

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl

class DatabaseRepositoryDefault: DatabaseRepository {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    override fun loadDatabaseInstance(context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        return databaseRepositoryImpl.loadDatabaseInstance(context)
    }
}
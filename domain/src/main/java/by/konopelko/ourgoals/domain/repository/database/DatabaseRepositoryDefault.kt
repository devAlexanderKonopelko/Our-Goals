package by.konopelko.domain.repositories.database

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.domain.repositories.database.DatabaseRepository

class DatabaseRepositoryDefault:
    DatabaseRepository {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    override fun loadDatabaseInstance(context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        return databaseRepositoryImpl.loadDatabaseInstance(context)
    }
}
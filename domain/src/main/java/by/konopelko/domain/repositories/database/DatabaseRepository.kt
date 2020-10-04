package by.konopelko.domain.repositories.database

import android.content.Context

interface DatabaseRepository {
    fun loadDatabaseInstance(context: Context): Boolean
}
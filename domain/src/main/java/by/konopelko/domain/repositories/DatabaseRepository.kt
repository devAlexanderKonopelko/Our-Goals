package by.konopelko.domain.repositories

import android.content.Context

interface DatabaseRepository {
    fun loadDatabaseInstance(context: Context): Boolean
}
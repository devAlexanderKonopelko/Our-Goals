package by.konopelko.data.database

import android.content.Context
import androidx.room.Room

class DatabaseInstance(context: Context) {
    val database by lazy {
        Room.databaseBuilder(
            context,
            Database::class.java,
            "goals-database"
        )
            .fallbackToDestructiveMigration() // TODO: заменить на миграцию без отчистки бд
            .build()
    }

    companion object {
        fun getInstance(context: Context) = DatabaseInstance(context)
    }
}
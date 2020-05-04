package by.konopelko.ourgoals.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.konopelko.ourgoals.database.converters.*
import by.konopelko.ourgoals.database.dao.*
import by.konopelko.ourgoals.database.entities.*
import by.konopelko.ourgoals.database.motivations.Image
import by.konopelko.ourgoals.database.motivations.Link
import by.konopelko.ourgoals.database.motivations.Motivation
import by.konopelko.ourgoals.database.motivations.Note

@Database(
    entities = [Goal::class,
        Task::class,
        User::class,
        Category::class,
        Analytics::class,
        Motivation::class,
        Materials::class,
        Image::class,
        Link::class,
        Note::class],
    version = 15
)
@TypeConverters(
    TaskDataConverter::class,
    UserDataConverter::class,
    ImageDataConverter::class,
    LinkDataConverter::class,
    NoteDataConverter::class
)
abstract class Database : RoomDatabase() {
    abstract fun getGoalDao(): GoalDao
    abstract fun getUserDao(): UserDao
    abstract fun getCategoryDao(): CategoryDao
    abstract fun getMotivationsDao(): MotivationsDao
    abstract fun getAnalyticsDao(): AnalyticsDao
}
package by.konopelko.ourgoals.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Goal::class, Task::class, User::class, Category::class], version = 8)
@TypeConverters(TaskDataConverter::class, UserDataConverter::class)
abstract class GoalDatabase : RoomDatabase() {
    abstract fun getGoalDao(): GoalDao
    abstract fun getUserDao(): UserDao
    abstract fun getCategoryDao(): CategoryDao
}
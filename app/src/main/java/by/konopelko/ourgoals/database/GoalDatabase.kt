package by.konopelko.ourgoals.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Goal::class, Task::class], version = 2)
@TypeConverters(TaskDataConverter::class)
abstract class GoalDatabase : RoomDatabase() {
    abstract fun getGoalDao(): GoalDao
}
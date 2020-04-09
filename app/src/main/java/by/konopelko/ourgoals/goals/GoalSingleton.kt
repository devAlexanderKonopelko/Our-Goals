package by.konopelko.ourgoals.goals

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.GoalDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoalSingleton(context: Context) {
    val database by lazy {
        Room.databaseBuilder(
            context,
            GoalDatabase::class.java,
            "goals-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    companion object {
        fun getInstance(context: Context) = GoalSingleton(context)
    }

    fun addGoal(goal: Goal) {
        CoroutineScope(Dispatchers.IO).launch {
            database.getGoalDao().addGoal(goal)
        }
    }
}
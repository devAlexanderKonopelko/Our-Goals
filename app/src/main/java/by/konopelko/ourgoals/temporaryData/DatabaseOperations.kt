package by.konopelko.ourgoals.temporaryData

import android.content.Context
import android.util.Log
import androidx.room.Room
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.GoalDatabase
import kotlinx.coroutines.*

class DatabaseOperations(context: Context) {
    val database by lazy {
        Room.databaseBuilder(
            context,
            GoalDatabase::class.java,
            "goals-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    companion object {
        fun getInstance(context: Context) =
            DatabaseOperations(context)
    }

    suspend fun loadDatabase(): Deferred<ArrayList<Goal>> {
        val goalsList = CoroutineScope(Dispatchers.IO).async {
            database.getGoalDao().getAllGoals() as ArrayList<Goal>
        }
        return goalsList
    }

    suspend fun getLastGoal(): Deferred<Goal> {
        val lastIndex = database.getGoalDao().getAllGoals().lastIndex
        val lastGoal = CoroutineScope(Dispatchers.IO).async {
            database.getGoalDao().getAllGoals()[lastIndex]
        }
        return lastGoal
    }


    fun addGoaltoDatabase(goal: Goal): Deferred<Long> {
        val newId =
            CoroutineScope(Dispatchers.IO).async {
                database.getGoalDao().addGoal(goal)
            }

        return newId
    }

    fun removeGoalfromDatabase(goalId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("-----ENTRANCE------", "removeGoalfromDatabase()")
            database.getGoalDao().deleteGoal(goalId)
            Log.e(
                "DATABASE OPERATION",
                " DELETED ----- ${database.getGoalDao().getAllGoals().size}"
            )
        }
    }

    fun resetDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            database.clearAllTables()
        }
    }
}
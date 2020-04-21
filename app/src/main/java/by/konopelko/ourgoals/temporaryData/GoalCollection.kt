package by.konopelko.ourgoals.temporaryData

import android.content.Context
import android.util.Log
import androidx.room.Room
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.GoalDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoalCollection {
    var goalsList = ArrayList<Goal>()
    var visible = true

    companion object {
        val instance = GoalCollection()
    }

    fun getGoalsDatabase(goals: ArrayList<Goal>) {
        goalsList = goals
    }

    fun addGoal(goal: Goal) {
        goalsList.add(goal)
    }

    fun removeGoal(goal: Goal) {
        goalsList.remove(goal)
    }

    fun resetDatabase() {
        //
    }
}
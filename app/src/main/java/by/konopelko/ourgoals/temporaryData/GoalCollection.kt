package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.entities.Goal

class GoalCollection {
    var goalsInProgressList = ArrayList<Goal>()
    var visible = true

    companion object {
        val instance = GoalCollection()
    }

    fun setGoalsInProgress(goals: ArrayList<Goal>) {
        for (goal in goals) {
            if (!goal.isDone) {
                goalsInProgressList.add(goal)
            }
        }
    }

    fun addGoal(goal: Goal) {
        goalsInProgressList.add(goal)
    }

    fun removeGoal(goal: Goal) {
        goalsInProgressList.remove(goal)
    }

    fun resetDatabase() {
        //
    }
}
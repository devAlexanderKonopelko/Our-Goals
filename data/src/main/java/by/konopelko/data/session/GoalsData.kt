package by.konopelko.data.session

import by.konopelko.data.database.entities.Goal

class GoalsData {
    var goalsInProgressList = ArrayList<Goal>()
    var visible = true

    companion object {
        val instance = GoalsData()
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
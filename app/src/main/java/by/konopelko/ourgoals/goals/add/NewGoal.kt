package by.konopelko.ourgoals.goals.add

import by.konopelko.ourgoals.database.Goal

class NewGoal {
    val goal = Goal("","",0, ArrayList(),false,false)

    companion object {
        val instance = NewGoal()
    }
}
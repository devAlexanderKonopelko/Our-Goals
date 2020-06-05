package by.konopelko.ourgoals.goals.add

import by.konopelko.ourgoals.database.entities.Goal

class NewGoal {
    var goal = Goal(
        "",
        "",
        "",
        0,
        ArrayList(),
        false,
        false
    )

    companion object {
        val instance = NewGoal()
    }
}
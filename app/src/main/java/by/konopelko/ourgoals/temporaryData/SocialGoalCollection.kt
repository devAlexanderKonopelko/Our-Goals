package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.Goal

class SocialGoalCollection {
    val goalList = ArrayList<Goal>()
    var visible = false

    companion object {
        val instance = SocialGoalCollection()
    }
}
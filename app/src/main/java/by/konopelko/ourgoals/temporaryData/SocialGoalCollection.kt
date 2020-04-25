package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.Goal

class SocialGoalCollection {
    val goalList = ArrayList<Goal>()
    val keysList = ArrayList<String>()
    var visible = false

    companion object {
        val instance = SocialGoalCollection()
    }
}
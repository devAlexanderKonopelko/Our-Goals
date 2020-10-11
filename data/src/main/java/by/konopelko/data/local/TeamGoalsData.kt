package by.konopelko.data.local

import by.konopelko.data.database.entity.Goal

class TeamGoalsData {
    val goalList = ArrayList<Goal>()
    val keysList = ArrayList<String>()
    var visible = false

    companion object {
        val instance = TeamGoalsData()
    }
}
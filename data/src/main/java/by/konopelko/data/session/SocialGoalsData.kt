package by.konopelko.data.session

import by.konopelko.data.database.entities.Goal

class SocialGoalsData {
    val goalList = ArrayList<Goal>()
    val keysList = ArrayList<String>()
    var visible = false

    companion object {
        val instance = SocialGoalsData()
    }
}
package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.entities.User

class FriendsProgressCollection {
    val userList = ArrayList<User>()
    val progressList = ArrayList<Int>()

    companion object {
        val instance = FriendsProgressCollection()
    }
}
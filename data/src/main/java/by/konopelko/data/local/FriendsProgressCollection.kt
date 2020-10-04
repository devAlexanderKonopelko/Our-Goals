package by.konopelko.data.local

import by.konopelko.data.database.entities.User

class FriendsProgressCollection {
    val userList = ArrayList<User>()
    val progressList = ArrayList<Int>()

    companion object {
        val instance = FriendsProgressCollection()
    }
}
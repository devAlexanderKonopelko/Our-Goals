package by.konopelko.data.session

import by.konopelko.data.database.entities.User

class FriendsProgressCollection {
    val userList = ArrayList<User>()
    val progressList = ArrayList<Int>()

    companion object {
        val instance = FriendsProgressCollection()
    }
}
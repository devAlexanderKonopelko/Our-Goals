package by.konopelko.data.local

import by.konopelko.data.database.entity.User

class UserData {
    var currentUser =
        User("0", "Гость", ArrayList())

    companion object {
        val instance = UserData()
    }
}
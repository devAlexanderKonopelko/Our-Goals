package by.konopelko.data.local

import by.konopelko.data.database.entities.User

class UserData {
    var currentUser =
        User("0", "Гость", ArrayList())

    companion object {
        val instance = UserData()
    }
}
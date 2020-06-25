package by.konopelko.data.session

import by.konopelko.data.database.entities.User

class UserData {
    var currentUser =
        User("0", "Гость", ArrayList())

    companion object {
        val instance = UserData()
    }
}
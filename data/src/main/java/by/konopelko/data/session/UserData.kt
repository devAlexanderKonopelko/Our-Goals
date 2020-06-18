package by.konopelko.data.session

import by.konopelko.data.database.entities.User

class UserData {
    var firstTimeRun = true
    var currentUser =
        User("0", "Гость", ArrayList())

    companion object {
        val instance = UserData()
    }
}
package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.User

class CurrentSession {
    var firstTimeRun = true
    var currentUser = User("0", "Гость", ArrayList())

    companion object {
        val instance = CurrentSession()
    }
}
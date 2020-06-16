package by.konopelko.ourgoals.temporaryData
import android.content.Context
import by.konopelko.ourgoals.database.entities.User

class CurrentSession {
    var firstTimeRun = true
    var currentUser =
        User("0", "Гость", ArrayList())

    companion object {
        val instance = CurrentSession()
    }
}
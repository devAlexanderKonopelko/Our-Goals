package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.User

class NotificationsCollection {
    val friendsRequests = ArrayList<User>()

    val goalsRequests = ArrayList<Goal>()
    val goalsRequestsSenders = ArrayList<User>()
    val goalsRequestsGoalKeys = ArrayList<String>()

    val requestsKeys = ArrayList<String>()

    companion object {
        val instance = NotificationsCollection()
    }
}
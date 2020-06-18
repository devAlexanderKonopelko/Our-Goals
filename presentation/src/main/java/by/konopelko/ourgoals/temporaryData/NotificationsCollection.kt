package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.User

class NotificationsCollection {
    val friendsRequests = ArrayList<User>()
    val goalsRequests = ArrayList<Goal>()
    val goalsRequestsSenders = ArrayList<User>()
    val goalsRequestsGoalKeys = ArrayList<String>()

    val requestsKeys = ArrayList<String>()

    companion object {
        val instance = NotificationsCollection()
    }

    fun clearTempNotificationsList() {
        friendsRequests.clear()
        goalsRequests.clear()
        goalsRequestsSenders.clear()
        requestsKeys.clear()
    }
}
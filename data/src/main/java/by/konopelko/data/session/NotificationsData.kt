package by.konopelko.data.session

import by.konopelko.data.database.entities.Goal
import by.konopelko.data.database.entities.User

class NotificationsData {
    val friendsRequests = ArrayList<User>()
    val goalsRequests = ArrayList<Goal>()
    val goalsRequestsSenders = ArrayList<User>()
    val goalsRequestsGoalKeys = ArrayList<String>()

    val requestsKeys = ArrayList<String>()

    companion object {
        val instance = NotificationsData()
    }

    fun clearTempNotificationsList() {
        friendsRequests.clear()
        goalsRequests.clear()
        goalsRequestsSenders.clear()
        requestsKeys.clear()
    }
}
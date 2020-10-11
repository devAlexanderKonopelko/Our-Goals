package by.konopelko.data.local

import by.konopelko.data.database.entity.Goal
import by.konopelko.data.database.entity.User

class NotificationData {
    val friendsRequests = ArrayList<User>()
    val goalsRequests = ArrayList<Goal>()
    val goalsRequestsSenders = ArrayList<User>()
    val goalsRequestsGoalKeys = ArrayList<String>()

    val requestsKeys = ArrayList<String>()

    companion object {
        val instance = NotificationData()
    }

    fun clearTempNotificationsList() {
        friendsRequests.clear()
        goalsRequests.clear()
        goalsRequestsSenders.clear()
        requestsKeys.clear()
    }
}
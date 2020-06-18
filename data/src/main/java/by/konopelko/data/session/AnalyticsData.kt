package by.konopelko.data.session

import by.konopelko.data.database.entities.Analytics

class AnalyticsData {
    var analytics = Analytics("0", 0, 0, 0, 0)

    companion object {
        val instance = AnalyticsData()
    }
}
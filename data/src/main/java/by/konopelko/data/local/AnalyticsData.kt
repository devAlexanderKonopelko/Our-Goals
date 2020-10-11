package by.konopelko.data.local

import by.konopelko.data.database.entity.Analytics

class AnalyticsData {
    var analytics = Analytics("0", 0, 0, 0, 0)

    companion object {
        val instance = AnalyticsData()
    }
}
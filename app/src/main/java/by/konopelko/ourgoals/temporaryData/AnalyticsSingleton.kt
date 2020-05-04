package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.entities.Analytics

class AnalyticsSingleton {
    var analytics = Analytics("0", 0, 0, 0, 0)

    companion object {
        val instance = AnalyticsSingleton()
    }
}
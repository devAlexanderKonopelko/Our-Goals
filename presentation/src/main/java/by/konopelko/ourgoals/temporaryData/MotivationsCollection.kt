package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.motivations.Motivation

class MotivationsCollection {
    var motivations = Motivation("0", "none", ArrayList(), ArrayList(), ArrayList())

    companion object {
        val instance = MotivationsCollection()
    }
}
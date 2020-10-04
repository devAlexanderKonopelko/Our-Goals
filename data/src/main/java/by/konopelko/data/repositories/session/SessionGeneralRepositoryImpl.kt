package by.konopelko.data.repositories.session

import by.konopelko.data.local.SessionGeneralData

class SessionGeneralRepositoryImpl {
    fun setCurrentSessionRun(state: Boolean) {
        SessionGeneralData.instance.firstTimeRun = state
    }

    fun checkCurrentSessionRun(): Int {
        return if (SessionGeneralData.instance.firstTimeRun) {
            1
        } else 2
    }
}
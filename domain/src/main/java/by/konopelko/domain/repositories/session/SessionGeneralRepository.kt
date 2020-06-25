package by.konopelko.domain.repositories.session

interface SessionGeneralRepository {
    fun setCurrentSessionRun(state: Boolean)
    fun checkCurrentSessionRun(): Int
}
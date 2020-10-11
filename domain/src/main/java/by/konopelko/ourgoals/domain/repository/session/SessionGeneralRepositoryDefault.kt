package by.konopelko.domain.repositories.session

import by.konopelko.data.repositories.session.SessionGeneralRepositoryImpl

class SessionGeneralRepositoryDefault: SessionGeneralRepository {
    lateinit var sessionGeneralRepositoryImpl: SessionGeneralRepositoryImpl
    override fun setCurrentSessionRun(state: Boolean) {
        sessionGeneralRepositoryImpl = SessionGeneralRepositoryImpl()
        sessionGeneralRepositoryImpl.setCurrentSessionRun(state)
    }

    override fun checkCurrentSessionRun(): Int {
        sessionGeneralRepositoryImpl = SessionGeneralRepositoryImpl()
        return sessionGeneralRepositoryImpl.checkCurrentSessionRun()
    }
}
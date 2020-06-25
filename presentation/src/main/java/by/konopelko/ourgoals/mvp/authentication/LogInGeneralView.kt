package by.konopelko.ourgoals.mvp.authentication

import by.konopelko.ourgoals.database.entities.User

interface LogInGeneralView {
    suspend fun startGuideActivity()
    suspend fun startMainActivity()
}
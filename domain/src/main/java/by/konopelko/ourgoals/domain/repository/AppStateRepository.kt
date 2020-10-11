package by.konopelko.ourgoals.domain.repository

import by.konopelko.ourgoals.domain.entity.AppState

interface AppStateRepository {

    fun setAppState(appState: AppState)
}
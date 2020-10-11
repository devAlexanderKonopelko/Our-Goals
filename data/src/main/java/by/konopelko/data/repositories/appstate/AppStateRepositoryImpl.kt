package by.konopelko.data.repositories.appstate

import by.konopelko.ourgoals.domain.entity.AppState
import by.konopelko.ourgoals.domain.repository.AppStateRepository

class AppStateRepositoryImpl: AppStateRepository {

    override fun setAppState(appState: AppState) {
        AppStateData.appState = appState
    }
}
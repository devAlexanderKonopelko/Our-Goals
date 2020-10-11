package by.konopelko.ourgoals.domain.usecases.setappstate

import by.konopelko.ourgoals.domain.entity.AppState

interface SetAppStateUseCase {

    operator fun invoke(appState: AppState)
}
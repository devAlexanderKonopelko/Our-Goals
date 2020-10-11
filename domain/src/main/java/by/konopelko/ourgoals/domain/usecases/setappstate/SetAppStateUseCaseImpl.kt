package by.konopelko.ourgoals.domain.usecases.setappstate

import by.konopelko.ourgoals.domain.entity.AppState
import by.konopelko.ourgoals.domain.repository.AppStateRepository

class SetAppStateUseCaseImpl(
    private val appStateRepository: AppStateRepository
): SetAppStateUseCase {

    override fun invoke(appState: AppState) {
        appStateRepository.setAppState(appState)
    }
}
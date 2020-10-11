package by.konopelko.ourgoals.domain.usecases.checkfirstrun

import by.konopelko.ourgoals.domain.entity.AppState
import by.konopelko.ourgoals.domain.repositories.sharedpreferences.SharedPreferencesRepository

class CheckFirstRunUseCaseImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository
): CheckFirstRunUseCase {

    override fun invoke(): AppState {
        return when (sharedPreferencesRepository.getSavedVersionCode()) {
            sharedPreferencesRepository.getDoesntExistCode() -> AppState.FIRST_RUN
            sharedPreferencesRepository.getCurrentVersionCode() -> AppState.REPEAT_RUN
            else -> AppState.NEED_UPDATE
        }
    }
}
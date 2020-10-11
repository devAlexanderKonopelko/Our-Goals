package by.konopelko.ourgoals.domain.usecases.updateversioncode

import by.konopelko.ourgoals.domain.repositories.sharedpreferences.SharedPreferencesRepository

class UpdateVersionCodeUseCaseImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository
): UpdateVersionCodeUseCase {

    override fun invoke() {
        sharedPreferencesRepository.updateVersionCode()
    }
}
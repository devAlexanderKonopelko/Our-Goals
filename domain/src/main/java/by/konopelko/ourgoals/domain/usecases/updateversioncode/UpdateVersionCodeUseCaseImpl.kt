package by.konopelko.ourgoals.domain.usecases.updateversioncode

import by.konopelko.ourgoals.domain.repository.sharedpreferences.SharedPreferencesRepository

class UpdateVersionCodeUseCaseImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository
): UpdateVersionCodeUseCase {

    override fun invoke() {
        sharedPreferencesRepository.updateVersionCode()
    }
}
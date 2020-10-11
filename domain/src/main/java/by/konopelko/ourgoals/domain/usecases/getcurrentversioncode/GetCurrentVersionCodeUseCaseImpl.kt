package by.konopelko.ourgoals.domain.usecases.getcurrentversioncode

import by.konopelko.ourgoals.domain.repositories.sharedpreferences.SharedPreferencesRepository

class GetCurrentVersionCodeUseCaseImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository
): GetCurrentVersionCodeUseCase {

    override fun invoke(): Int = sharedPreferencesRepository.getCurrentVersionCode()
}
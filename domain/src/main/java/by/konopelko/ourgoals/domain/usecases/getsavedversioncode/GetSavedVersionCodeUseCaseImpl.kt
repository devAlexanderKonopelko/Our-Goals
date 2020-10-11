package by.konopelko.ourgoals.domain.usecases.getsavedversioncode

import by.konopelko.ourgoals.domain.repositories.sharedpreferences.SharedPreferencesRepository

class GetSavedVersionCodeUseCaseImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository
): GetSavedVersionCodeUseCase {

    override fun invoke() = sharedPreferencesRepository.getSavedVersionCode()
}
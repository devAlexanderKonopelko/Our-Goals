package by.konopelko.ourgoals.domain.usecases.getlastuserid

import by.konopelko.ourgoals.domain.repository.sharedpreferences.SharedPreferencesRepository

class GetLastUserIdUseCaseImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository
): GetLastUserIdUseCase {

    override fun invoke(): String? = sharedPreferencesRepository.getLastUserId()
}
package by.konopelko.ourgoals.domain.repositories.sharedpreferences

interface SharedPreferencesRepository {

    fun getSavedVersionCode(): Int
    fun getCurrentVersionCode(): Int
}
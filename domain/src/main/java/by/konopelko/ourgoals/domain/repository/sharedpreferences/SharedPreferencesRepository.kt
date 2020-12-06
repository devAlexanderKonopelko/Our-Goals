package by.konopelko.ourgoals.domain.repository.sharedpreferences

interface SharedPreferencesRepository {

    fun getSavedVersionCode(): Int
    fun getCurrentVersionCode(): Int
    fun getDoesntExistCode(): Int
    fun updateVersionCode()
    fun getLastUserId(): String?
}
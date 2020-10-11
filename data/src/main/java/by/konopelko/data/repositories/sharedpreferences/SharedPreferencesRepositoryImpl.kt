package by.konopelko.data.repositories.sharedpreferences

import by.konopelko.data.sharedpreferences.SharedPreferences
import by.konopelko.data.sharedpreferences.SharedPreferencesConst
import by.konopelko.ourgoals.domain.repository.sharedpreferences.SharedPreferencesRepository

class SharedPreferencesRepositoryImpl(
    private val sharedPreferences: SharedPreferences
): SharedPreferencesRepository {

    override fun getSavedVersionCode(): Int = sharedPreferences.versionCode
    override fun getCurrentVersionCode(): Int = SharedPreferencesConst.CURRENT_VERSION_CODE
    override fun getDoesntExistCode(): Int = SharedPreferencesConst.PREFS_CODE_DOESNT_EXIST

    override fun updateVersionCode() {
        sharedPreferences.updateVersionCode()
    }
}
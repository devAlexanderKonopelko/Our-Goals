package by.konopelko.data.sharedpreferences

import android.content.Context
import by.konopelko.data.sharedpreferences.SharedPreferencesConst.CURRENT_VERSION_CODE
import by.konopelko.data.sharedpreferences.SharedPreferencesConst.PREFS_CODE_DOESNT_EXIST
import by.konopelko.data.sharedpreferences.SharedPreferencesConst.PREFS_NAME
import by.konopelko.data.sharedpreferences.SharedPreferencesConst.PREFS_VERSION_CODE_KEY

class SharedPreferencesImpl(
    context: Context
): SharedPreferences {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override var versionCode: Int
        get() = sharedPreferences.getInt(
            PREFS_VERSION_CODE_KEY,
            PREFS_CODE_DOESNT_EXIST)
        set(value) {
            sharedPreferences.edit().putInt(PREFS_VERSION_CODE_KEY, value).apply()
        }

    override fun updateVersionCode() {
        sharedPreferences.edit().putInt(PREFS_VERSION_CODE_KEY, CURRENT_VERSION_CODE).apply()
    }
}
package by.konopelko.data.manager

import by.konopelko.data.BuildConfig

class SharedPreferencesManager {

    companion object {
        const val PREFS_NAME = "shared-prefs"
        const val PREFS_VERSION_CODE_KEY = "VERSION_CODE"
        const val PREFS_CODE_DOESNT_EXIST = -1
        const val CURRENT_VERSION_CODE = BuildConfig.VERSION_CODE
    }
}
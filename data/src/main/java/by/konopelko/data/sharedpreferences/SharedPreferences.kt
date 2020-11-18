package by.konopelko.data.sharedpreferences

interface SharedPreferences {

    var versionCode: Int

    var lastUserId: String?

    fun updateVersionCode()
}
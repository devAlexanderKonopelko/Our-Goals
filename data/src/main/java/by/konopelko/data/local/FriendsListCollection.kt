package by.konopelko.data.local

import android.util.Log
import by.konopelko.data.database.entity.User

class FriendsListCollection {
    val friendsList = ArrayList<User>()
    val keysList = ArrayList<String>()

    companion object {
        val instance = FriendsListCollection()
    }

    fun show() {
        Log.e("FRIENDS", friendsList.toString())
        Log.e("KEYS", keysList.toString())
    }
}
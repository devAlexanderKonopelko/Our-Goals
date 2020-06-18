package by.konopelko.data.session

import android.util.Log
import by.konopelko.data.database.entities.User

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
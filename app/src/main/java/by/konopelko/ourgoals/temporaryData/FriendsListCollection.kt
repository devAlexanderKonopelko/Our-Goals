package by.konopelko.ourgoals.temporaryData

import android.util.Log
import by.konopelko.ourgoals.database.User
import kotlinx.coroutines.*

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
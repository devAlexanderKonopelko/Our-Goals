package by.konopelko.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: String,

    val name: String,
    var friendsList: ArrayList<User>?
)
package by.konopelko.ourgoals.database.motivations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Note(
    val title: String,
    val text: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
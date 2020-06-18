package by.konopelko.data.database.entities.motivations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Link(
    val url: String,
    val description: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
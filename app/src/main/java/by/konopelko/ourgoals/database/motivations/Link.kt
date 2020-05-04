package by.konopelko.ourgoals.database.motivations

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Link(
    val url: String,
    val description: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
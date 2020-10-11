package by.konopelko.data.database.entity.motivations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Image(
    val imageUrl: String,
    var isWeb: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
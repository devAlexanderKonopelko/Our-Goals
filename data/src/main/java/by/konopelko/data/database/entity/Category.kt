package by.konopelko.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Category(
    var ownerId: String,
    var title: String,
    var bgImageURI: String?,
    var bgColor: Int?
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null

    override fun toString(): String {
        return "$id, $ownerId, $title, $bgImageURI, $bgColor"
    }
}
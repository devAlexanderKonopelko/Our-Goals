package by.konopelko.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import by.konopelko.data.database.entity.motivations.Image
import by.konopelko.data.database.entity.motivations.Link
import by.konopelko.data.database.entity.motivations.Note

@Entity
class Materials(
    val ownerId: String,
    val category: String,
    var linkList: ArrayList<Link>?,
    var imageList: ArrayList<Image>?,
    var noteList: ArrayList<Note>?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
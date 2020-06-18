package by.konopelko.ourgoals.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import by.konopelko.ourgoals.database.motivations.Image
import by.konopelko.ourgoals.database.motivations.Link
import by.konopelko.ourgoals.database.motivations.Note

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
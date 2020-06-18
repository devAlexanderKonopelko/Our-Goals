package by.konopelko.ourgoals.database.motivations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Motivation(
    val ownerId: String,
    val category: String,
    var linkList: ArrayList<Link>?,
    var imageList: ArrayList<Image>?,
    var noteList: ArrayList<Note>?
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null

    override fun toString(): String {
        return "id:$id, $ownerId, $category,\n $linkList,\n $imageList,\n $noteList\n"
    }
}
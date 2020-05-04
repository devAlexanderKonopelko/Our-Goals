package by.konopelko.ourgoals.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Goal(
    var ownerId: String,
    var category: String,
    var text: String,
    var progress: Int,
    var tasks: ArrayList<Task>?,

    var isDone: Boolean,
    var isSocial: Boolean
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null

    override fun toString(): String {
        return "{$ownerId, $category, $text, $progress, $tasks, $isDone, $isSocial}"
    }
}
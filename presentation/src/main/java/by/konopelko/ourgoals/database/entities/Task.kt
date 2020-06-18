package by.konopelko.ourgoals.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    val text: String,
    val finishDate: String,
    var isComplete: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
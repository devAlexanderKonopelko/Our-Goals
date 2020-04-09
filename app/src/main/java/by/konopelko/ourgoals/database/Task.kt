package by.konopelko.ourgoals.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    val text: String,
    val finishDate: String,
    val isComplete: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
package by.konopelko.data.database.entity

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
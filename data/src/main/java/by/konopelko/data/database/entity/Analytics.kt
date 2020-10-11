package by.konopelko.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Analytics(
    var ownerId: String,
    var goalsCompleted: Int,
    var goalsSet: Int,
    var tasksCompleted: Int,
    var tasksSet: Int
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null

    override fun toString(): String {
        return "$id, $ownerId, $goalsCompleted, $goalsSet, $tasksCompleted, $tasksSet"
    }
}
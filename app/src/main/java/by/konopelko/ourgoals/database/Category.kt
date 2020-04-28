package by.konopelko.ourgoals.database

import android.net.Uri
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
}
package by.konopelko.data.database.converter

import androidx.room.TypeConverter
import by.konopelko.data.database.entity.motivations.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class NoteDataConverter: Serializable {
    @TypeConverter
    fun fromNoteList(noteList: ArrayList<Note>?): String? {
        if (noteList != null) {
            if (noteList.isEmpty()){
                return null
            }
        } else return null
        val gson = Gson()

        return gson.toJson(noteList)
    }

    @TypeConverter
    fun toNoteList(noteListString: String?): ArrayList<Note>? {
        if (noteListString != null) {
            if (noteListString.isEmpty()) {
                return null
            }
        }
        else return null
        val gson = Gson()
        val noteListType = object: TypeToken<ArrayList<Note>>(){}.type

        return gson.fromJson(noteListString, noteListType)
    }
}
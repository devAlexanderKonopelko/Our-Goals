package by.konopelko.data.database.converter

import androidx.room.TypeConverter
import by.konopelko.data.database.entity.motivations.Link
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class LinkDataConverter: Serializable {
    @TypeConverter
    fun fromLinkList(linkList: ArrayList<Link>?): String? {
        if (linkList != null) {
            if (linkList.isEmpty()){
                return null
            }
        }
        else return null
        val gson = Gson()

        return gson.toJson(linkList)
    }

    @TypeConverter
    fun toLinkList(linkListString: String?): ArrayList<Link>? {
        if (linkListString != null) {
            if (linkListString.isEmpty()) {
                return null
            }
        }
        else return null
        val gson = Gson()
        val linkListType = object: TypeToken<ArrayList<Link>>(){}.type

        return gson.fromJson(linkListString, linkListType)
    }
}
package by.konopelko.data.database.converter

import androidx.room.TypeConverter
import by.konopelko.data.database.entity.motivations.Image
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class ImageDataConverter: Serializable {
    @TypeConverter
    fun fromImageList(imageList: ArrayList<Image>?): String? {
        if (imageList != null) {
            if (imageList.isEmpty()){
                return null
            }
        }
        else return null
        val gson = Gson()

        return gson.toJson(imageList)
    }

    @TypeConverter
    fun toImageList(imageListString: String?): ArrayList<Image>? {
        if (imageListString != null) {
            if (imageListString.isEmpty()) {
                return null
            }
        }
        else return null
        val gson = Gson()
        val imageListType = object: TypeToken<ArrayList<Image>>(){}.type

        return gson.fromJson(imageListString, imageListType)
    }
}
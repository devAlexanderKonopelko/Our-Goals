package by.konopelko.ourgoals.database.converters

import androidx.room.TypeConverter
import by.konopelko.ourgoals.database.entities.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserDataConverter {
    @TypeConverter
    fun fromUserList(userList: ArrayList<User>): String? {
        if (userList.isEmpty()){
            return null
        }
        val gson = Gson()

        return gson.toJson(userList)
    }

    @TypeConverter
    fun toUserList(userListString: String?): ArrayList<User>? {
        if (userListString != null) {
            if (userListString.isEmpty()) {
                return null
            }
        }
        else return null
        val gson = Gson()
        val userListType = object: TypeToken<ArrayList<User>>(){}.type

        return gson.fromJson(userListString, userListType)
    }
}
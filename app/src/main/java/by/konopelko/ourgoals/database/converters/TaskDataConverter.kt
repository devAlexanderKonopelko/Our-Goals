package by.konopelko.ourgoals.database.converters

import androidx.room.TypeConverter
import by.konopelko.ourgoals.database.entities.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class TaskDataConverter: Serializable {
    @TypeConverter
    fun fromTaskList(taskList: ArrayList<Task>?): String? {
        if (taskList != null) {
            if (taskList.isEmpty()){
                return null
            }
        } else return null
        val gson = Gson()

        return gson.toJson(taskList)
    }

    @TypeConverter
    fun toTaskList(taskListString: String?): ArrayList<Task>? {
        if (taskListString != null) {
            if (taskListString.isEmpty()) {
                return null
            }
        }
        else return null
        val gson = Gson()
        val taskListType = object: TypeToken<ArrayList<Task>>(){}.type

        return gson.fromJson(taskListString, taskListType)
    }
}
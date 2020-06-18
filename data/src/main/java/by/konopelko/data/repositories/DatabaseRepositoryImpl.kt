package by.konopelko.data.repositories

import android.content.Context
import by.konopelko.data.database.DatabaseInstance
import by.konopelko.data.database.entities.Category
import by.konopelko.data.database.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class DatabaseRepositoryImpl {
    fun loadDatabaseInstance(context: Context): Boolean {
        DatabaseInstance.getInstance(context)
        return true
    }

    suspend fun findUserById(uid: String, context: Context): Boolean {
        // work with database
        val user = CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getUserDao().findUserById(uid)
        }.await()

        return user == 1
    }

    suspend fun addUser(uid: String, name: String, context: Context): Boolean {
        val guest = User(uid, name, ArrayList())
        CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getUserDao().addUser(guest)
        }.await()

        return true
    }

    // добавляет несколько категорий в БД
    suspend fun addCategories(ownerId: String, titles: ArrayList<String>, bgUrls: ArrayList<String?>, bgColors: ArrayList<Int?>, context: Context): Boolean {
        val categories = ArrayList<Category>()

        for (i in 0 until titles.size) {
            categories.add(Category(ownerId, titles[i], bgUrls[i], bgColors[i]))
        }

        CoroutineScope(Dispatchers.IO).async {
            for (category in categories) {
                DatabaseInstance.getInstance(context).database.getCategoryDao().addCategory(category)
            }
        }.await()

        return true
    }
}
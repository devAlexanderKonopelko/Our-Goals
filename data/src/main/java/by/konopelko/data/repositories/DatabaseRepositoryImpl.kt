package by.konopelko.data.repositories

import android.content.Context
import by.konopelko.data.database.DatabaseInstance
import by.konopelko.data.database.entity.Analytics
import by.konopelko.data.database.entity.Category
import by.konopelko.data.database.entity.Goal
import by.konopelko.data.database.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class DatabaseRepositoryImpl {

    suspend fun findUserById(uid: String, context: Context): Boolean {
        // work with database
        val user = CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getUserDao().findUserById(uid)
        }.await()

        return user == 1
    }

    suspend fun getUserById(uid: String, context: Context): User {
        val user = CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getUserDao().getUserById(uid)
        }.await()
        return user
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

    suspend fun addAnalytics(ownerId: String, context: Context): Boolean {
        val analytics = Analytics(ownerId, 0, 0, 0, 0)
        CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getAnalyticsDao().addAnalytics(analytics)
        }.await()

        return true
    }

    suspend fun loadUsersAnalytics(uid: String, context: Context): Analytics {
        return CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getAnalyticsDao().getAnalyticsByUid(uid)
        }.await()
    }

    suspend fun loadUsersPersonalGoals(uid: String, context: Context): ArrayList<Goal> {
        return CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getGoalDao().getGoalsByUsersId(uid) as ArrayList<Goal>
        }.await()
    }

    suspend fun loadUsersCategories(uid: String, context: Context): ArrayList<Category> {
        return CoroutineScope(Dispatchers.IO).async {
            DatabaseInstance.getInstance(context).database.getCategoryDao().getCategoriesByUsersId(uid) as ArrayList<Category>
        }.await()
    }
}
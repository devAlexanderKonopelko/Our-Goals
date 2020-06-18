package by.konopelko.ourgoals.temporaryData

import android.content.Context
import android.util.Log
import androidx.room.Room
import by.konopelko.ourgoals.database.entities.Category
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.Database
import by.konopelko.ourgoals.database.entities.Analytics
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.database.motivations.Motivation
import kotlinx.coroutines.*

class DatabaseOperations(context: Context) {
    val database by lazy {
        Room.databaseBuilder(
            context,
            Database::class.java,
            "goals-database"
        )
            .fallbackToDestructiveMigration() // заменить на миграцию без отчистки бд
            .build()
    }

    companion object {
        fun getInstance(context: Context) =
            DatabaseOperations(context)
    }

    suspend fun getUsersDatabaseSize(): Deferred<Int> {
        val size = CoroutineScope(Dispatchers.IO).async {
            database.getUserDao().size()
        }
        return size
    }

    suspend fun loadUsersDatabase(): Deferred<ArrayList<User>> {
        val usersList = CoroutineScope(Dispatchers.IO).async {
            database.getUserDao().getAllUsers() as ArrayList<User>
        }
        return usersList
    }

    suspend fun addUsertoDatabase(user: User): Deferred<Boolean> {
        val result =
            CoroutineScope(Dispatchers.IO).async {
                database.getUserDao().addUser(user)
                Log.e("USER ADDED TO DATABASE:", " ${database.getUserDao().getAllUsers()}")
                true
            }
        return result
    }

    suspend fun getUserById(id: String): Deferred<User> {
        val user = CoroutineScope(Dispatchers.IO).async {
            database.getUserDao().getUserById(id)
        }
        return user
    }

    suspend fun loadGoalsDatabase(currentUserId: String): Deferred<ArrayList<Goal>> {
        val goalsList = CoroutineScope(Dispatchers.IO).async {
            Log.e(
                "GOAL DATABASE OP SIZE: ",
                database.getGoalDao().getGoalsByUsersId(currentUserId).size.toString()
            )
            database.getGoalDao().getGoalsByUsersId(currentUserId) as ArrayList<Goal>
        }
        return goalsList
    }

    fun addGoaltoDatabase(goal: Goal): Deferred<Long> {
        val newId =
            CoroutineScope(Dispatchers.IO).async {
                database.getGoalDao().addGoal(goal)
            }
        return newId
    }

    fun removeGoalfromDatabase(goalId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("-----ENTRANCE------", "DB_OPERATIONS: removeGoalfromDatabase()")
            database.getGoalDao().deleteGoal(goalId)
            Log.e(
                "DATABASE OPERATION",
                " DELETED ----- DB_SIZE: ${database.getGoalDao().getAllGoals().size}"
            )
        }
    }

    fun updateGoalInDatabase(goal: Goal) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("-----ENTRANCE------", "DB_OPERATIONS: updateGoalInDatabase()")
            database.getGoalDao().updateGoal(goal)
        }
    }

    fun resetDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            database.clearAllTables()
        }
    }

    suspend fun getCategoriesByUserId(ownerId: String): Deferred<ArrayList<Category>> {
        val categoriesList = CoroutineScope(Dispatchers.IO).async {
            Log.e("CATEGORIES_", "$ownerId: ${database.getCategoryDao().getCategoriesByUsersId(ownerId).size}")

            database.getCategoryDao().getCategoriesByUsersId(ownerId) as ArrayList<Category>
        }
        return categoriesList
    }

    suspend fun setDefaultCategoriesList(ownerId: String, list: ArrayList<String>): Deferred<Unit> {
        val defaultCategories = ArrayList<Category>()
        defaultCategories.add(
            Category(
                ownerId,
                list[0],
                null,
                -49862
            )
        )
        defaultCategories.add(
            Category(
                ownerId,
                list[1],
                null,
                -12168193
            )
        )
        defaultCategories.add(
            Category(
                ownerId,
                list[2],
                null,
                -7591681
            )
        )
        defaultCategories.add(
            Category(
                ownerId,
                list[3],
                null,
                -11862145
            )
        )

        val result = CoroutineScope(Dispatchers.IO).async {
            for (category in defaultCategories) {
                database.getCategoryDao().addCategory(category)

                // создаём пустой объект мотиваций
                database.getMotivationsDao().addMotivations(Motivation(ownerId, category.title, ArrayList(), ArrayList(), ArrayList()))
            }
        }

        return result
    }

    suspend fun addCategoryToDatabase(category: Category): Deferred<Long> {
        val newId =
            CoroutineScope(Dispatchers.IO).async {
                // создаём пустой объект мотиваций для новой категории
                database.getMotivationsDao().addMotivations(Motivation(category.ownerId, category.title, ArrayList(), ArrayList(), ArrayList()))

                database.getCategoryDao().addCategory(category)
            }
        return newId
    }

    fun updateCategoryInDatabase(category: Category) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("-----ENTRANCE------", "DB_OPERATIONS: updateCategoryInDatabase()")
            Log.e("-----UPDATE------", "$category")
            database.getCategoryDao().updateCategory(category)
        }
    }

    fun removeCategoryFromDatabase(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("-----ENTRANCE------", "DB_OPERATIONS: removeCategoryFromDatabase()")
            database.getCategoryDao().deleteCategory(id)
            Log.e(
                "DATABASE OPERATION",
                " DELETED ----- DB_SIZE: ${database.getCategoryDao().getAllCategories().size}"
            )
        }
    }

    suspend fun getGoalsByCategory(category: String): Deferred<ArrayList<Goal>> {
        val goals = CoroutineScope(Dispatchers.IO).async {
            Log.e("UPDATE GOALS", ": ${database.getGoalDao().getGoalsByCategory(category)}")
            database.getGoalDao().getGoalsByCategory(category) as ArrayList<Goal>
        }

        return goals
    }

    suspend fun getMotivationsByCategory(category: String, ownerId: String): Deferred<Motivation> {
        val motivations = CoroutineScope(Dispatchers.IO).async {
            Log.e("GET MOTIVATIONS", ": ${database.getMotivationsDao().getMotivationsByCategory(category, ownerId)}")
            database.getMotivationsDao().getMotivationsByCategory(category, ownerId)
        }

        return motivations
    }

    fun updateMotivationInDatabase(motivation: Motivation) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("-----ENTRANCE------", "DB_OPERATIONS: updateMotivationInDatabase()")
            Log.e("-----UPDATE TO------", "$motivation")
            database.getMotivationsDao().updateMotivations(motivation)
            Log.e("-----UPDATED------", "${database.getMotivationsDao().getMotivationsByCategory(motivation.category, motivation.ownerId)}")
        }
    }

    suspend fun loadAnalytics(currentUserId: String): Deferred<Analytics> {
        val analytics = CoroutineScope(Dispatchers.IO).async {
            Log.e(
                "---ANALYTICS---",
                database.getAnalyticsDao().getAnalyticsByUid(currentUserId).toString()
            )
            database.getAnalyticsDao().getAnalyticsByUid(currentUserId)
        }
        return analytics
    }

    suspend fun setDefaultAnalytics(id: String): Deferred<Unit> {
        val analytics = Analytics(id, 0, 0, 0, 0)
        val result = CoroutineScope(Dispatchers.IO).async {
            database.getAnalyticsDao().addAnalytics(analytics)
        }
        return result
    }

    fun updateAnalytics(analytics: Analytics): Deferred<Unit> {
        val result  = CoroutineScope(Dispatchers.IO).async {
            database.getAnalyticsDao().updateAnalytics(analytics)
        }
        return result
    }
}
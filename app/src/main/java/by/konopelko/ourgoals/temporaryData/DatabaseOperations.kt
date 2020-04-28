package by.konopelko.ourgoals.temporaryData

import android.content.Context
import android.util.Log
import androidx.room.Room
import by.konopelko.ourgoals.database.Category
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.GoalDatabase
import by.konopelko.ourgoals.database.User
import kotlinx.coroutines.*

class DatabaseOperations(context: Context) {
    val database by lazy {
        Room.databaseBuilder(
            context,
            GoalDatabase::class.java,
            "goals-database"
        )
            .fallbackToDestructiveMigration()
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

    suspend fun getLastGoal(): Deferred<Goal> {
        val lastIndex = database.getGoalDao().getAllGoals().lastIndex
        val lastGoal = CoroutineScope(Dispatchers.IO).async {
            database.getGoalDao().getAllGoals()[lastIndex]
        }
        return lastGoal
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

    suspend fun setDefaultCategoriesList(ownerId: String): Deferred<Unit> {
        val defaultCategories = ArrayList<Category>()
        defaultCategories.add(Category(ownerId, "Здоровье", null, -49862))
        defaultCategories.add(Category(ownerId, "Образование", null, -12168193))
        defaultCategories.add(Category(ownerId, "Финансы", null, -7591681))
        defaultCategories.add(Category(ownerId, "Хобби", null, -11862145))

        val result = CoroutineScope(Dispatchers.IO).async {
            for (category in defaultCategories) {
                database.getCategoryDao().addCategory(category)
            }
        }

        return result
    }

    suspend fun addCategoryToDatabase(category: Category): Deferred<Long> {
        val newId =
            CoroutineScope(Dispatchers.IO).async {
                database.getCategoryDao().addCategory(category)
            }
        return newId
    }

    fun updateCategoryInDatabase(category: Category) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("-----ENTRANCE------", "DB_OPERATIONS: updateCategoryInDatabase()")
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
}
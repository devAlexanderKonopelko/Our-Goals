package by.konopelko.data.repositories.session

import android.content.Context
import by.konopelko.data.database.entities.Category
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.session.CategoriesData

class CategoryRepositoryImpl {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl

    suspend fun loadUsersCategoris(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        val categories = databaseRepositoryImpl.loadUsersCategoris(uid, context)
        if (categories.isNotEmpty()) {
            CategoriesData.instance.categoryList = categories
            return true
        }
        return false
    }
}
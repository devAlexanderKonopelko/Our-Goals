package by.konopelko.domain.repositories.session

import android.content.Context
import by.konopelko.data.database.entities.Category
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.repositories.session.CategoryRepositoryImpl

class CategoryRepositoryDefault: CategoryRepository {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    lateinit var categoryRepositoryImpl: CategoryRepositoryImpl
    override suspend fun createDefaultCategories(ownerId: String, titles: ArrayList<String>, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()

        val defaultBgUrls = ArrayList<String?>()
        defaultBgUrls.add(null)
        defaultBgUrls.add(null)
        defaultBgUrls.add(null)
        defaultBgUrls.add(null)

        val defaultbgColors = ArrayList<Int?>()
        defaultbgColors.add(-49862)
        defaultbgColors.add(-12168193)
        defaultbgColors.add(-7591681)
        defaultbgColors.add(-11862145)

        return databaseRepositoryImpl.addCategories(ownerId, titles, defaultBgUrls, defaultbgColors, context)
    }

    override suspend fun loadUsersCategoris(uid: String, context: Context): Boolean {
        categoryRepositoryImpl = CategoryRepositoryImpl()
        return categoryRepositoryImpl.loadUsersCategoris(uid, context)
    }
}
package by.konopelko.data.repositories.session

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.session.CategoriesData

class CategoryRepositoryImpl {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl

    suspend fun loadUsersCategoris(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        val categories = databaseRepositoryImpl.loadUsersCategories(uid, context)
        if (categories.isNotEmpty()) {
            CategoriesData.instance.categoryList = categories
            return true
        }
        return false
    }

    fun getToolbarCategoryList(allCategoriesString: String): ArrayList<String> {
        CategoriesData.instance.toolbarFilterCategoryList.add(allCategoriesString)
        for (category in CategoriesData.instance.categoryList) {
            CategoriesData.instance.toolbarFilterCategoryList.add(category.title)
        }
        return CategoriesData.instance.toolbarFilterCategoryList
    }

    fun getToolbarCategory(position: Int): String {
        return CategoriesData.instance.toolbarFilterCategoryList[position]
    }
}
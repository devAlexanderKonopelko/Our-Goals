package by.konopelko.domain.repositories.session

import android.content.Context

interface CategoryRepository {
    suspend fun createDefaultCategories(ownerId: String, titles: ArrayList<String>, context: Context): Boolean
    suspend fun loadUsersCategoris(uid: String, context: Context): Boolean
    fun getToolbarCategoryList(allCategoriesString: String): ArrayList<String>
    fun getToolbarCategory(position: Int): String
}
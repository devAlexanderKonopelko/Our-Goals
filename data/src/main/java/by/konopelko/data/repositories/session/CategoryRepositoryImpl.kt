package by.konopelko.data.repositories.session

import by.konopelko.data.database.entities.Category
import by.konopelko.data.session.CategoriesData

class CategoryRepositoryImpl {
    suspend fun loadCategories(list: ArrayList<Category>): Boolean {
        CategoriesData.instance.categoryList = list
        return true
    }
    suspend fun addCategory(category: Category): Boolean {
        CategoriesData.instance.categoryList.add(category)
        return true
    }
    suspend fun removeCategory(category: Category): Boolean {
        CategoriesData.instance.categoryList.remove(category)
        return true
    }
}
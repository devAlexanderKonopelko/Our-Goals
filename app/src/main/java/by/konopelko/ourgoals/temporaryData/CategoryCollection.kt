package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.Category

class CategoryCollection {
    var categoryList = ArrayList<Category>()

    companion object {
        val instance = CategoryCollection()
    }

    fun setDefaultCategories(ownerId: String) {
        val defaultCategories = ArrayList<Category>()
        defaultCategories.add(Category(ownerId, "Здоровье", null, -49862))
        defaultCategories.add(Category(ownerId, "Образование", null, -12168193))
        defaultCategories.add(Category(ownerId, "Финансы", null, -7591681))
        defaultCategories.add(Category(ownerId, "Хобби", null, -11862145))

        categoryList.addAll(defaultCategories)
    }

    fun removeCategory(category: Category) {
        categoryList.remove(category)
    }
}
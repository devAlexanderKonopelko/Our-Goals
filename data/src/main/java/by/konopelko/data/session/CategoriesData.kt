package by.konopelko.data.session

import by.konopelko.data.database.entities.Category

class CategoriesData {
    var categoryList = ArrayList<Category>() // список категорий пользователя
    var toolbarFilterCategoryList = ArrayList<String>() // список категорий, отображающийся при фильтрации по категориям

    companion object {
        val instance = CategoriesData()
    }

    fun setDefaultCategories(ownerId: String, list: ArrayList<String>) {
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

        categoryList.addAll(defaultCategories)
    }

    fun removeCategory(category: Category) {
        categoryList.remove(category)
    }
}
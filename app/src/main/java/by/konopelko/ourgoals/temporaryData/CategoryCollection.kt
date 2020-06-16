package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.entities.Category

class CategoryCollection {
    var categoryList = ArrayList<Category>()

    companion object {
        val instance = CategoryCollection()
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
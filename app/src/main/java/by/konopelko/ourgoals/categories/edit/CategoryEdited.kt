package by.konopelko.ourgoals.categories.edit

import by.konopelko.ourgoals.database.Category

class CategoryEdited {
    var category: Category? = null

    companion object {
        val instance = CategoryEdited()
    }
}
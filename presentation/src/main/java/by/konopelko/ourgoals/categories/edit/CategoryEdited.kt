package by.konopelko.ourgoals.categories.edit

import by.konopelko.ourgoals.database.entities.Category

class CategoryEdited {
    var category: Category? = null

    companion object {
        val instance = CategoryEdited()
    }
}
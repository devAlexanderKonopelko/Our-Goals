package by.konopelko.ourgoals.temporaryData

import by.konopelko.ourgoals.database.Category

class CategoryCollection {
    val categoryList = ArrayList<Category>()

    companion object {
        val instance = CategoryCollection()
    }

    fun addcAtegories() {
        categoryList.add(Category("Здоровье"))
        categoryList.add(Category("Финансы"))
        categoryList.add(Category("Хобби"))
        categoryList.add(Category("Работа"))
    }
}
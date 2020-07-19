package by.konopelko.domain.interactors.mainscreen

import by.konopelko.domain.repositories.session.CategoryRepository
import by.konopelko.domain.repositories.session.CategoryRepositoryDefault

class MainScreenInteractor {
    lateinit var categoryRepository: CategoryRepository

    fun getToolbarCategoryList(allCategoriesString: String): ArrayList<String> {
        categoryRepository = CategoryRepositoryDefault()
        return categoryRepository.getToolbarCategoryList(allCategoriesString)
    }

    fun getToolbarCategory(position: Int): String {
        categoryRepository = CategoryRepositoryDefault()
        return categoryRepository.getToolbarCategory(position)
    }
}
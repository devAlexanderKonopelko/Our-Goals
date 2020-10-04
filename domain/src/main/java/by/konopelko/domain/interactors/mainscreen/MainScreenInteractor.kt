package by.konopelko.domain.interactors.mainscreen

import by.konopelko.domain.repositories.firebase.FirebaseNotificationRepository
import by.konopelko.domain.repositories.session.CategoryRepository
import by.konopelko.domain.repositories.session.CategoryRepositoryDefault

class MainScreenInteractor {
    lateinit var categoryRepository: CategoryRepository
    lateinit var firebaseNotificationRepository: FirebaseNotificationRepository

    fun getToolbarCategoryList(allCategoriesString: String): ArrayList<String> {
        categoryRepository = CategoryRepositoryDefault()
        return categoryRepository.getToolbarCategoryList(allCategoriesString)
    }

    fun getToolbarCategory(position: Int): String {
        categoryRepository = CategoryRepositoryDefault()
        return categoryRepository.getToolbarCategory(position)
    }

    fun performNotificationObservation() {
        firebaseNotificationRepository =
            FirebaseNotificationRepository()
        firebaseNotificationRepository.observeNotifications()

    }
}
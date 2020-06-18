package by.konopelko.domain.interactors.startscreen

import android.content.Context
import by.konopelko.domain.repositories.DatabaseRepository
import by.konopelko.domain.repositories.DatabaseRepositoryDefault
import by.konopelko.domain.repositories.UserRepository
import by.konopelko.domain.repositories.UserRepositoryDefault
import by.konopelko.domain.repositories.session.CategoryRepository
import by.konopelko.domain.repositories.session.CategoryRepositoryDefault

class StartScreenInteractor {
    lateinit var userRepository: UserRepository
    lateinit var databaseRepository: DatabaseRepository
    lateinit var categoryRepository: CategoryRepository

    suspend fun checkGuestUserExistence(uid: String, context: Context): Boolean {
        userRepository = UserRepositoryDefault()
        return userRepository.checkUserExistence(uid, context)
    }

    fun loadDatabaseInstance(context: Context): Boolean {
        databaseRepository = DatabaseRepositoryDefault()
        return databaseRepository.loadDatabaseInstance(context)
    }

    suspend fun createGuestUser(uid: String, name: String, context: Context):Boolean {
        userRepository = UserRepositoryDefault()
        return userRepository.createUser(uid, name, context)
    }

    suspend fun createDefaultCategories(uid: String, titles: ArrayList<String>, context: Context): Boolean {
        categoryRepository = CategoryRepositoryDefault()
        return categoryRepository.createDefaultCategories(uid, titles, context)
    }
}
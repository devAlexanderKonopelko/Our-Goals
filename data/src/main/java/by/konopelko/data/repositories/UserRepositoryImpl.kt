package by.konopelko.data.repositories

import android.content.Context
import by.konopelko.data.database.entity.User
import by.konopelko.data.repositories.firebase.FirebaseAuthRepositoryImpl
import by.konopelko.data.repositories.firebase.FirebaseUserRepositoryImpl
import by.konopelko.data.local.UserData

class UserRepositoryImpl {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    lateinit var firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    lateinit var firebaseUserRepositoryImpl: FirebaseUserRepositoryImpl

    suspend fun checkUserExistence(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        return databaseRepositoryImpl.findUserById(uid, context)
    }

    suspend fun createUser(uid: String, name: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        return databaseRepositoryImpl.addUser(uid, name, context)
    }

    suspend fun setCurrentUser(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        val user = databaseRepositoryImpl.getUserById(uid, context)
        UserData.instance.currentUser = user
        return true
    }

    fun getAuthorizedUserId(): String {
        firebaseAuthRepositoryImpl = FirebaseAuthRepositoryImpl()
        return firebaseAuthRepositoryImpl.getAuthorizedUserId()
    }

    suspend fun createUserFromServer(uid: String, context: Context) {
        firebaseUserRepositoryImpl = FirebaseUserRepositoryImpl()
        databaseRepositoryImpl = DatabaseRepositoryImpl()

        val user: User = firebaseUserRepositoryImpl.getUserById(uid) // загрузить объект пользователя из firebase db
        databaseRepositoryImpl.addUser(user.id, user.name, context) // добавить в бд
        setCurrentUser(user.id, context) // добавить в сессию
    }
}
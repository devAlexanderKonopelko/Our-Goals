package by.konopelko.data.repositories

import android.content.Context
import by.konopelko.data.session.UserData

class UserRepositoryImpl {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
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
}
package by.konopelko.data.repositories

import android.content.Context

class UserRepositoryImpl {
    lateinit var databaseRepositoryImpl: DatabaseRepositoryImpl
    suspend fun checkUserExistence(uid: String, context: Context): Boolean {
        databaseRepositoryImpl = DatabaseRepositoryImpl()
        return databaseRepositoryImpl.findUserById(uid, context)
    }

    suspend fun createUser(uid: String, name: String, context: Context): Boolean {
        return databaseRepositoryImpl.addUser(uid, name, context)
    }
}
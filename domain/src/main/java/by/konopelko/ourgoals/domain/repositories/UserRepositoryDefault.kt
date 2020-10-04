package by.konopelko.domain.repositories

import android.content.Context
import by.konopelko.data.repositories.DatabaseRepositoryImpl
import by.konopelko.data.repositories.UserRepositoryImpl

class UserRepositoryDefault: UserRepository {
    lateinit var userRepositoryImpl: UserRepositoryImpl


    override suspend fun checkUserExistence(uid: String, context: Context): Boolean {
        userRepositoryImpl = UserRepositoryImpl()
        return userRepositoryImpl.checkUserExistence(uid, context)
    }

    // Adding User to the database
    override suspend fun createUser(uid: String, name: String, context: Context): Boolean {
        userRepositoryImpl = UserRepositoryImpl()
        return userRepositoryImpl.createUser(uid, name, context)
    }

    override suspend fun setCurrentUser(uid: String, context: Context): Boolean {
        userRepositoryImpl = UserRepositoryImpl()
        return userRepositoryImpl.setCurrentUser(uid, context)
    }

    override fun getAuthorizedUserId(): String {
        userRepositoryImpl = UserRepositoryImpl()
        return userRepositoryImpl.getAuthorizedUserId()
    }

    override suspend fun createUserFromServer(uid: String, context: Context) {
        userRepositoryImpl = UserRepositoryImpl()
        userRepositoryImpl.createUserFromServer(uid, context)
    }


}
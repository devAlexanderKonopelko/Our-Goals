package by.konopelko.domain.repositories

import android.content.Context

interface UserRepository {
    suspend fun checkUserExistence(uid: String, context: Context): Boolean
    suspend fun createUser(uid: String, name: String, context: Context): Boolean
}
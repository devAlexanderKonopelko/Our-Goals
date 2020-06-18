package by.konopelko.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.konopelko.data.database.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun addUser(user: User)

    @Query("SELECT COUNT(*) FROM User")
    suspend fun size(): Int

    @Query("SELECT * FROM User")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUserById(id: String): User

    @Query("SELECT COUNT(*) FROM User WHERE id = :id")
    suspend fun findUserById(id: String): Int
}
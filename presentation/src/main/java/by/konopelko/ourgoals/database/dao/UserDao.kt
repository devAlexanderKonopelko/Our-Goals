package by.konopelko.ourgoals.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.konopelko.ourgoals.database.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun addUser(user: User)

    @Query("SELECT count(*) FROM User")
    suspend fun size(): Int

    @Query("SELECT * FROM User")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUserById(id: String): User
}
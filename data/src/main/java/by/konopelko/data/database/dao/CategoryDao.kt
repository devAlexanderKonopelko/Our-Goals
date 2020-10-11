package by.konopelko.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.konopelko.data.database.entity.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun addCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Query("DELETE FROM Category WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Int)

    @Query("SELECT * FROM Category")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM Category WHERE ownerId = :ownerId")
    suspend fun getCategoriesByUsersId(ownerId: String): List<Category>
}
package by.konopelko.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.konopelko.data.database.entity.motivations.Motivation

@Dao
interface MotivationsDao {
    @Insert
    suspend fun addMotivations(motivation: Motivation)

    @Update
    suspend fun updateMotivations(motivation: Motivation)

    @Query ("SELECT * FROM Motivation WHERE category = :categoryName AND ownerId = :ownerId")
    suspend fun getMotivationsByCategory(categoryName: String, ownerId: String): Motivation
}
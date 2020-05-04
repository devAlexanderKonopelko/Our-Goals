package by.konopelko.ourgoals.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.konopelko.ourgoals.database.motivations.Motivation

@Dao
interface MotivationsDao {
    @Insert
    suspend fun addMotivations(motivation: Motivation)

    @Update
    suspend fun updateMotivations(motivation: Motivation)

    @Query ("SELECT * FROM Motivation WHERE category = :categoryName AND ownerId = :ownerId")
    suspend fun getMotivationsByCategory(categoryName: String, ownerId: String): Motivation
}
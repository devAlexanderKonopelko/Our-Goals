package by.konopelko.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.konopelko.data.database.entities.Analytics

@Dao
interface AnalyticsDao {
    @Insert
    suspend fun addAnalytics(analytics: Analytics)

    @Update
    suspend fun updateAnalytics(analytics: Analytics)

    @Query ("SELECT * FROM Analytics WHERE ownerId = :ownerId")
    suspend fun getAnalyticsByUid(ownerId: String): Analytics
}
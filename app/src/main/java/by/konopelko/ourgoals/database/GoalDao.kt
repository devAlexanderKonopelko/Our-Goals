package by.konopelko.ourgoals.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GoalDao {
    @Insert
    suspend fun addGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM Goal")
    suspend fun getAllGoals(): List<Goal>
}
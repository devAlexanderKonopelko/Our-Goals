package by.konopelko.ourgoals.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.Deferred

@Dao
interface GoalDao {
    @Insert
    suspend fun addGoal(goal: Goal): Long

    @Query("DELETE FROM Goal WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Int)

    @Query("SELECT * FROM Goal")
    suspend fun getAllGoals(): List<Goal>
}
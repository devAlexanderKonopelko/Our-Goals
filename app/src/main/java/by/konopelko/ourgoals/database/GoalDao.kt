package by.konopelko.ourgoals.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.Deferred

@Dao
interface GoalDao {
    @Insert
    suspend fun addGoal(goal: Goal): Long

    @Query("DELETE FROM Goal WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Int)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("SELECT * FROM Goal")
    suspend fun getAllGoals(): List<Goal>

    @Query("SELECT * FROM Goal WHERE ownerId = :ownerId")
    suspend fun getGoalsByUsersId(ownerId: String): List<Goal>
}
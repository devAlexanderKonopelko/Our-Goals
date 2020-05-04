package by.konopelko.ourgoals.database.dao

import androidx.room.*
import by.konopelko.ourgoals.database.entities.Goal

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

    @Query("SELECT * FROM Goal WHERE category = :category")
    suspend fun getGoalsByCategory(category: String): List<Goal>

    @Query("SELECT * FROM Goal WHERE ownerId = :ownerId AND isDone = 1")
    suspend fun getCompletedGoals(ownerId: String): List<Goal>
}
package by.konopelko.data.repositories.firebase

import android.content.Context
import by.konopelko.data.database.entities.Goal
import by.konopelko.data.database.entities.Task
import by.konopelko.data.session.TeamGoalsData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TeamGoalRepositoryImpl {
    val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    fun loadUsersTeamGoals(uid: String, context: Context): Boolean {
        val list = ArrayList<Goal>()
        val keyList = ArrayList<String>()
        userDatabase.child(uid).child("socialGoals")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(socialGoals: DataSnapshot) {
                    for (goal in socialGoals.children) {

                        val tasks = ArrayList<Task>()
                        for (taskSnapshot in goal.child("tasks").children) {
                            val task =
                                Task(
                                    taskSnapshot.child("text").value.toString(),
                                    taskSnapshot.child("finishDate").value.toString(),
                                    taskSnapshot.child("complete").value.toString().toBoolean()
                                )

                            tasks.add(task)
                        }

                        val newGoal =
                            Goal(
                                goal.child("ownerId").value.toString(),
                                goal.child("category").value.toString(),
                                goal.child("text").value.toString(),
                                goal.child("progress").value.toString().toInt(),
                                tasks,
                                goal.child("done").value.toString().toBoolean(),
                                goal.child("social").value.toString().toBoolean()
                            )

                        list.add(newGoal)
                        keyList.add(goal.key.toString())
                    }
                    setUsersTeamGoals(list, keyList)
                    return
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            }).also {
                return true
            }
    }
    private fun setUsersTeamGoals(list: ArrayList<Goal>, keys: ArrayList<String>) {
        TeamGoalsData.instance.goalList.clear()
        TeamGoalsData.instance.keysList.clear()
        TeamGoalsData.instance.goalList.addAll(list)
        TeamGoalsData.instance.keysList.addAll(keys)
    }
}
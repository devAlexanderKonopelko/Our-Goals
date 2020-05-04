package by.konopelko.ourgoals.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.temporaryData.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_recycler_goal_tasks.view.*

class AdapterTasksList(
    val list: ArrayList<Task>?,
    val fragmentGoals: FragmentGoals,
    val goalPosition: Int
) :
    RecyclerView.Adapter<AdapterTasksList.TasksListViewHolder>() {
    class TasksListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_goal_tasks, parent, false)
        return TasksListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (list != null) {
            return list.size
        }
        else return 0
    }

    override fun onBindViewHolder(holder: TasksListViewHolder, position: Int) {
        val view = holder.itemView
        if (list != null) {
            view.itemGoalTaskText.text = list[position].text
            view.itemGoalTaskFinishDate.text = list[position].finishDate

            view.itemGoalTaskCheckBox.isChecked = list[position].isComplete

            view.itemGoalTaskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                // изменяем прогресс задачи в локальном списке этого адаптера
                list[position].isComplete = isChecked
                //изменяем соответствующую цель (личную/социальную)
                if (GoalCollection.instance.visible) {
                    changeGoalProgress(goalPosition, position, isChecked)
                }
                if (SocialGoalCollection.instance.visible) {
                    changeSocialGoalProgress(goalPosition, position, isChecked)
                }

                // изменение прогресс бара
                fragmentGoals.updateRecyclerItemProgress(goalPosition)
            }

        }
    }

    private fun changeSocialGoalProgress(goalPosition: Int, taskPosition: Int, isChecked: Boolean) {
        // изменяем прогресс задачи соответствующей цели в локальной коллекции
        SocialGoalCollection.instance.goalList[goalPosition].tasks?.get(taskPosition)?.isComplete =
            isChecked

        // изменение прогресса всей соответствующей цели
        SocialGoalCollection.instance.goalList[goalPosition].progress = calculateProgress()

        val progress = SocialGoalCollection.instance.goalList[goalPosition].progress
        val isDone = SocialGoalCollection.instance.goalList[goalPosition].isDone

        // изменяем прогресс задачи и цели на Сервере
        changeProgressInServer(progress, isDone, isChecked, goalPosition, taskPosition)
    }

    private fun changeProgressInServer(
        progress: Int,
        done: Boolean,
        checked: Boolean,
        goalPosition: Int,
        taskPosition: Int
    ) {
        val currentUserId = CurrentSession.instance.currentUser.id
        val goalKey = SocialGoalCollection.instance.keysList[goalPosition]

        // изменить прогресс цели, прогресс задачи у нас в аккаунте по ключу цели
        userDatabase.child(currentUserId).child("socialGoals").child(goalKey).child("done")
            .setValue(done).addOnSuccessListener {
                userDatabase.child(currentUserId).child("socialGoals").child(goalKey)
                    .child("progress").setValue(progress).addOnSuccessListener {
                        userDatabase.child(currentUserId).child("socialGoals").child(goalKey)
                            .child("tasks").child(taskPosition.toString()).child("complete")
                            .setValue(checked).addOnSuccessListener {
                                Toast.makeText(
                                    fragmentGoals.context,
                                    "Наша Цель на сервере изменина",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }

        // изменить наш прогресс цели в аккаунтах всех друзей, подключённых к этой цели по ключу цели
        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userDatabaseSnapshot: DataSnapshot) {
                for (user in userDatabaseSnapshot.children) {
                    if (user.hasChild("socialGoals")) {
                        if (user.child("socialGoals").hasChild(goalKey) && user.key != currentUserId) {
                            userDatabase.child(user.key!!).child("socialGoals").child(goalKey)
                                .child("friends").child(currentUserId).child("progress")
                                .setValue(progress)
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun changeGoalProgress(goalPosition: Int, taskPosition: Int, isChecked: Boolean) {
        // изменяем прогресс задачи соответствующей цели в локальной коллекции
        GoalCollection.instance.goalsInProgressList[goalPosition].tasks?.get(taskPosition)?.isComplete = isChecked

        // изменение прогресса всей соответствующей цели
        GoalCollection.instance.goalsInProgressList[goalPosition].progress = calculateProgress()

        val goal = GoalCollection.instance.goalsInProgressList[goalPosition]

        // изменяем прогресс задачи и цели в локальной бд
        changeProgressInDatabase(goal, isChecked)
    }

    private fun changeProgressInDatabase(goal: Goal, isChecked: Boolean) {
        fragmentGoals.context?.let {
            DatabaseOperations.getInstance(it).updateGoalInDatabase(goal)

            val analytics = AnalyticsSingleton.instance.analytics
            if (isChecked) {
                analytics.tasksCompleted++
            } else {
                analytics.tasksCompleted--
            }

            // возможно запаздывание именений в аналитике
            DatabaseOperations.getInstance(it).updateAnalytics(analytics)
        }
    }

    private fun calculateProgress(): Int {
        var completeTasksAmount = 0
        if (list != null) {
            for (task in list) {
                if (task.isComplete) completeTasksAmount++
            }
            return ((completeTasksAmount / list.size.toDouble()) * 100).toInt()
        }
        else return 0
    }
}
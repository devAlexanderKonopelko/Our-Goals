package by.konopelko.ourgoals.goals.recyclerGoals

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.goals.AdapterTasksList
import by.konopelko.ourgoals.goals.FragmentFriendsProgress
import by.konopelko.ourgoals.goals.FragmentGoals
import by.konopelko.ourgoals.temporaryData.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_recycler_goal.view.*
import kotlinx.coroutines.*

class GoalAdapter(val list: List<Goal>, val fragmentGoals: FragmentGoals) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {
    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var tasksVisible = false
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val goalRequestDatabase = FirebaseDatabase.getInstance().reference.child("GoalRequests")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_goal, parent, false)
        return GoalViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goalView = holder.itemView

        goalView.itemGoalCategory.text = list[position].category
        goalView.itemGoalText.text = list[position].text
        goalView.itemGoalProgressBarIndicator.progress = list[position].progress
        goalView.itemGoalProgressBarValue.text = list[position].progress.toString() + "%"

        if (tasksVisible) {
            goalView.itemGoalTasksRecycler.visibility = View.VISIBLE
            goalView.itemGoalDetailsButton.text = "Свернуть"
        } else {
            goalView.itemGoalTasksRecycler.visibility = View.GONE
            goalView.itemGoalDetailsButton.text = "Подробнее"
        }

        Log.e("TASKS", "${list[position].tasks}")
        if (list[position].tasks == null || list[position].tasks?.size ?: 1 == 0) {
            goalView.itemGoalDetailsButton.visibility = View.GONE
        } else {
            goalView.itemGoalSingleCheckBox.visibility = View.GONE
        }

        goalView.itemGoalTasksRecycler.adapter =
            AdapterTasksList(list[position].tasks, fragmentGoals, position)
        goalView.itemGoalTasksRecycler.layoutManager = LinearLayoutManager(fragmentGoals.context)
        goalView.itemGoalTasksRecycler.setHasFixedSize(true)

        goalView.itemGoalSingleCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (GoalCollection.instance.visible) {
                    GoalCollection.instance.goalsInProgressList[position].progress = 100
                } else {
                    SocialGoalCollection.instance.goalList[position].progress = 100
                    changeSocialGoalProgress(
                        100,
                        true,
                        SocialGoalCollection.instance.keysList[position]
                    )
                }

                goalView.itemGoalProgressBarIndicator.progress = 100
                goalView.itemGoalProgressBarValue.text = "100%"
                goalView.itemGoalCompleteButton.visibility = View.VISIBLE
                setCompleteClickListener(goalView, position)
            } else {
                if (GoalCollection.instance.visible) {
                    GoalCollection.instance.goalsInProgressList[position].progress = 0
                } else {
                    SocialGoalCollection.instance.goalList[position].progress = 0
                    changeSocialGoalProgress(
                        0,
                        false,
                        SocialGoalCollection.instance.keysList[position]
                    )
                }
                goalView.itemGoalProgressBarIndicator.progress = 0
                goalView.itemGoalProgressBarValue.text = "0%"
                goalView.itemGoalCompleteButton.visibility = View.INVISIBLE
            }
        }

        if (list[position].progress == 100) {
            goalView.itemGoalCompleteButton.visibility = View.VISIBLE
            goalView.itemGoalSingleCheckBox.isChecked = true
            setCompleteClickListener(goalView, position)
        } else goalView.itemGoalCompleteButton.visibility = View.GONE

        if (list[position].isSocial) {
            goalView.itemGoalSocialButton.visibility = View.VISIBLE

            goalView.itemGoalSocialButton.setOnClickListener {
                // надуваем фрагмент просмотра прогресса друзей
                val friendsProgressDialog = FragmentFriendsProgress(
                    SocialGoalCollection.instance.keysList[position],
                    list[position].text
                )
                fragmentGoals.fragmentManager?.let { fm -> friendsProgressDialog.show(fm, "") }
            }
        } else goalView.itemGoalSocialButton.visibility = View.INVISIBLE


        // удалять в зависимости от цели (социальная или нет)
        goalView.itemGoalDeleteButton.setOnClickListener {

            // inflating confirmation dialog
            MaterialAlertDialogBuilder(fragmentGoals.context)
                .setTitle("Подтвердите действие")
                .setMessage("Вы уверены, что хотите удалить данную цель?")
                .setNegativeButton("Отмена") { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton("Удалить") { dialog, which ->
                    // Respond to positive button press
                    if (list[position].isSocial) {
                        // deleting social goal
                        deleteSocialGoal(position)
                    } else {
                        //deleting personal goal from database a updating goal recycler view
                        removeGoal(list[position])
                    }

                }
                .show()
        }
        goalView.itemGoalDetailsButton.setOnClickListener {
            // развёртывание цели ("подробнее")
            if (goalView.itemGoalTasksRecycler.visibility == View.GONE) {
                goalView.itemGoalTasksRecycler.visibility = View.VISIBLE
                goalView.itemGoalDetailsButton.text = "Свернуть"
            } else { // свёртывание цели ("свернуть")
                goalView.itemGoalTasksRecycler.visibility = View.GONE

                goalView.itemGoalDetailsButton.text = "Подробнее"
            }
        }
    }

    private fun changeSocialGoalProgress(progress: Int, isDone: Boolean, goalKey: String) {
        val currentUserId = CurrentSession.instance.currentUser.id

        // изменить прогресс цели, прогресс задачи у нас в аккаунте по ключу цели
        userDatabase.child(currentUserId).child("socialGoals").child(goalKey).child("done")
            .setValue(isDone).addOnSuccessListener {
                userDatabase.child(currentUserId).child("socialGoals").child(goalKey)
                    .child("progress").setValue(progress).addOnSuccessListener {
                        Toast.makeText(
                            fragmentGoals.context,
                            "Наша Цель на сервере изменина",
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun setCompleteClickListener(goalView: View, position: Int) {
        goalView.itemGoalCompleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(fragmentGoals.context)
                .setTitle("Подтвердите действие")
                .setMessage("Вы уверены, что хотите завершить данную цель?")
                .setNegativeButton("Отмена") { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton("Завершить") { dialog, which ->
                    // Respond to positive button press
                    if (list[position].isSocial) {
                        // completing social goal

                        fragmentGoals.context?.let { ctx ->
                            val analytics = AnalyticsSingleton.instance.analytics
                            analytics.goalsCompleted++
                            DatabaseOperations.getInstance(ctx).updateAnalytics(analytics)
                        }

                        // удалить цель из локальной коллекции
                        SocialGoalCollection.instance.goalList.removeAt(position)
                        val goalKey = SocialGoalCollection.instance.keysList[position]
                        SocialGoalCollection.instance.keysList.removeAt(position)
                        notifyItemRemoved(position)
                        // удалить цель из нашего аккаунта на сервере
                        removeSocialGoalFromAccount(goalKey)

                        // TODO
                        // добавить в статистику инфу
                    } else {
                        //completing personal goal

                        // поставить отметку "выполнена"
                        list[position].isDone = true
                        //обновить в бд цель и аналитику
                        fragmentGoals.context?.let { ctx ->
                            DatabaseOperations.getInstance(ctx).updateGoalInDatabase(list[position])

                            val analytics = AnalyticsSingleton.instance.analytics
                            analytics.goalsCompleted++
                            DatabaseOperations.getInstance(ctx).updateAnalytics(analytics)
                        }
                        // удалить из локальной коллекции
                        GoalCollection.instance.goalsInProgressList.removeAt(position)
                        // обновить ресайклер
                        notifyItemRemoved(position)
                    }

                }
                .show()
        }
    }

    private fun removeSocialGoalFromAccount(goalKey: String) {
        val currentUserId = auth.currentUser!!.uid

        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userDatabaseSnapshot: DataSnapshot) {
                for (user in userDatabaseSnapshot.children) {
                    if (user.hasChild("socialGoals")) {
                        // если друг уже принял цель и она есть у него в socialGoals
                        if (user.child("socialGoals").hasChild(goalKey)) {
                            // установить наш прогресс у него равным 100
                            userDatabase.child(user.key!!).child("socialGoals").child(goalKey)
                                .child("friends").child(currentUserId).child("progress")
                                .setValue(100)
                                .addOnSuccessListener {
                                    // удаляем цель из нашего аккаунта
                                    userDatabase.child(currentUserId).child("socialGoals")
                                        .child(goalKey).removeValue().addOnSuccessListener {
                                            Toast.makeText(
                                                fragmentGoals.context,
                                                "Цель завершена!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                        }
                    }
                    // если друг ещё не принял цель - ищем в запросах
                    goalRequestDatabase.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(requests: DataSnapshot) {
                            if (requests.hasChild(user.key!!)) {
                                //если у кого-то друга ещё висит запрос от нас
                                if (requests.child(user.key!!).hasChild(currentUserId)) {
                                    // если в запросе есть конкретная цель
                                    if (requests.child(user.key!!).child(currentUserId).hasChild(
                                            goalKey
                                        )
                                    ) {
                                        // добавить поле нашим прогрессом к конкретной цели
                                        goalRequestDatabase.child(user.key!!)
                                            .child(currentUserId).child(goalKey)
                                            .child(currentUserId + "_progress")
                                            .setValue(100).addOnSuccessListener {
                                                // удаляем цель из нашего аккаунта
                                                userDatabase.child(currentUserId)
                                                    .child("socialGoals")
                                                    .child(goalKey).removeValue()
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            fragmentGoals.context,
                                                            "Цель завершена!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun deleteSocialGoal(position: Int) {
        val goalKey = SocialGoalCollection.instance.keysList[position]
        val currentUserId = auth.currentUser!!.uid

        // удаляем нашего пользователя из списка выполняющих у всех вовлечённых пользователей
        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userDatabaseSnapshot: DataSnapshot) {
                for (user in userDatabaseSnapshot.children) {
                    if (user.hasChild("socialGoals")) {
                        // если друг уже принял цель и она есть у него в socialGoals
                        if (user.child("socialGoals").hasChild(goalKey)) {
                            userDatabase.child(user.key!!).child("socialGoals").child(goalKey)
                                .child("friends").child(currentUserId).removeValue()
                                .addOnSuccessListener {
                                    // удаляем цель из нашего аккаунта
                                    userDatabase.child(currentUserId).child("socialGoals")
                                        .child(goalKey).removeValue().addOnSuccessListener {
                                            Toast.makeText(
                                                fragmentGoals.context,
                                                "Цель удалена!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                        }
                        // если друг ещё не принял цель - ищем в запросах
                        else {
//                            goalRequestDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
//                                override fun onDataChange(requests: DataSnapshot) {
//                                    if (requests.hasChild(user.key!!)) {
//                                        //если у кого-то друга ещё висит запрос от нас
//                                        if (requests.child(user.key!!).hasChild(currentUserId)) {
//                                            // добавить поле
//                                        }
//                                    }
//                                }
//                                override fun onCancelled(p0: DatabaseError) {
//                                }
//                            })
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        // удаляем цель из локальной коллекции
        SocialGoalCollection.instance.goalList.removeAt(position)
        SocialGoalCollection.instance.keysList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addGoalToRecycler(goal: Goal) {

        CoroutineScope(Dispatchers.IO).launch {
            fragmentGoals.context?.let {
                goal.id = DatabaseOperations.getInstance(it).addGoaltoDatabase(goal).await().toInt()
                Log.e(
                    "GOAL DATABASE SIZE: ",
                    DatabaseOperations.getInstance(it).database.getGoalDao().getAllGoals().size.toString()
                )

                val analytics = AnalyticsSingleton.instance.analytics
                analytics.goalsSet++
                analytics.tasksSet += goal.tasks?.size ?: 0
                DatabaseOperations.getInstance(it).updateAnalytics(analytics).await()
            }
            if (goal.id != null) {
                GoalCollection.instance.addGoal(goal)

                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
            }

        }
    }

    fun removeGoal(goal: Goal) {
        fragmentGoals.activity?.run {
            Log.e(
                "-----ENTRANCE------",
                "GOAL_ADAPTER removeGoal(): fragmentGoals.activity?.run{dbOperation}"
            )
            goal.id?.let { DatabaseOperations.getInstance(this).removeGoalfromDatabase(it) }
        }

        GoalCollection.instance.removeGoal(goal)
        notifyDataSetChanged()
    }

    fun updateGoalProgress(goalPosition: Int) {
        tasksVisible = true
        notifyItemChanged(goalPosition)
    }
}
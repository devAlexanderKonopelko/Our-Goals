package by.konopelko.ourgoals.goals.recyclerGoals

import android.annotation.SuppressLint
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Goal
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
        val view = holder.itemView

        // сеттим UI цели
        setViewElements(view, position)

        // обработка нажатия на чекбокс для цели без задач
        setSingleCheckBoxChangeListener(view, position)

        // обработка нажатия на крестик (удаление цели)
        setDeleteButtonClickListener(view, position)

        // обработка нажатия на "стрелку вниз" - развёртывание цели
        setDetailsButtonClickListener(view)
    }

    private fun setDetailsButtonClickListener(view: View) {
        view.itemGoalDetailsButton.setOnClickListener {
            // развёртывание цели
            if (view.itemGoalTasksRecycler.visibility == View.GONE) {
                view.itemGoalTasksRecycler.visibility = View.VISIBLE
                view.itemGoalDetailsButton.setImageResource(R.drawable.arrow_top)
            } else { // сворачивание цели
                view.itemGoalTasksRecycler.visibility = View.GONE
                view.itemGoalDetailsButton.setImageResource(R.drawable.arrow_down)
            }
        }
    }

    private fun setDeleteButtonClickListener(view: View, position: Int) {
        view.itemGoalDeleteButton.setOnClickListener {
            // inflating confirmation dialog
            MaterialAlertDialogBuilder(fragmentGoals.context)
                .setTitle(view.context.getString(R.string.dialog_confirmAction))
                .setMessage(view.context.getString(R.string.dialog_deleteGoal))
                .setNegativeButton(view.context.getString(R.string.dialog_cancel)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(view.context.getString(R.string.dialog_delete)) { dialog, which ->
                    // Respond to positive button press
                    if (list[position].isSocial) {
                        // deleting social goal

                        deleteSocialGoal(position, list[position])
                    } else {
                        //deleting personal goal from database a updating goal recycler view

                        removeGoal(list[position])
                    }

                }.show()
        }
    }

    private fun setSingleCheckBoxChangeListener(view: View, position: Int) {
        view.itemGoalSingleCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (GoalCollection.instance.visible) {
                    list[position].progress = 100
                } else {
                    SocialGoalCollection.instance.goalList[position].progress = 100
                    changeSocialGoalProgress(
                        100,
                        true,
                        SocialGoalCollection.instance.keysList[position]
                    )
                }

                view.itemGoalProgressBarIndicator.progress = 100
                view.itemGoalProgressBarValue.text = "100%"
                view.itemGoalCompleteButton.visibility = View.VISIBLE
                setCompleteClickListener(view, position)
            } else {
                if (GoalCollection.instance.visible) {
                    list[position].progress = 0
                } else {
                    SocialGoalCollection.instance.goalList[position].progress = 0
                    changeSocialGoalProgress(
                        0,
                        false,
                        SocialGoalCollection.instance.keysList[position]
                    )
                }
                view.itemGoalProgressBarIndicator.progress = 0
                view.itemGoalProgressBarValue.text = "0%"
                view.itemGoalCompleteButton.visibility = View.GONE
            }
            if (GoalCollection.instance.visible) {
                fragmentGoals.context?.let {
                    DatabaseOperations.getInstance(it)
                        .updateGoalInDatabase(list[position])
                }
            }
        }
    }

    private fun setViewElements(view: View, position: Int) {
        view.itemGoalCategory.text = list[position].category
        view.itemGoalText.text = list[position].text
        view.itemGoalProgressBarIndicator.progress = list[position].progress
        view.itemGoalProgressBarValue.text = "${list[position].progress}%"

        setTextScrollListener(view.itemGoalText);

        if (list[position].tasks == null || list[position].tasks?.size ?: 1 == 0) {
            view.itemGoalDetailsButton.visibility = View.GONE
            view.itemGoalSingleCheckBox.visibility = View.VISIBLE
        } else {
            view.itemGoalDetailsButton.visibility = View.VISIBLE
            view.itemGoalSingleCheckBox.visibility = View.INVISIBLE
        }

        if (list[position].progress == 100) {
            view.itemGoalCompleteButton.visibility = View.VISIBLE
            view.itemGoalSingleCheckBox.isChecked = true
            setCompleteClickListener(view, position)
        } else {
            view.itemGoalCompleteButton.visibility = View.GONE
            view.itemGoalSingleCheckBox.isChecked = false
        }

        if (list[position].isSocial) {
            view.itemGoalSocialButton.visibility = View.VISIBLE
            setSocialButtonClickListener(view, position)
        } else view.itemGoalSocialButton.visibility = View.INVISIBLE

        view.itemGoalTasksRecycler.adapter =
            AdapterTasksList(
                list[position].tasks,
                fragmentGoals,
                position
            )
        view.itemGoalTasksRecycler.layoutManager = LinearLayoutManager(fragmentGoals.context)
        view.itemGoalTasksRecycler.setHasFixedSize(true)

        if (tasksVisible) {
            view.itemGoalTasksRecycler.visibility = View.VISIBLE
            view.itemGoalDetailsButton.setImageResource(R.drawable.arrow_top)
        } else {
            view.itemGoalTasksRecycler.visibility = View.GONE
            view.itemGoalDetailsButton.setImageResource(R.drawable.arrow_down)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTextScrollListener(itemGoalText: TextView?) {
        itemGoalText?.setOnTouchListener { v, event ->
            itemGoalText.parent.requestDisallowInterceptTouchEvent(true)
            itemGoalText.movementMethod = ScrollingMovementMethod.getInstance()
            false
        }
    }

    private fun setSocialButtonClickListener(view: View, position: Int) {
        view.itemGoalSocialButton.setOnClickListener {
            // надуваем фрагмент просмотра прогресса друзей
            val friendsProgressDialog = FragmentFriendsProgress(
                SocialGoalCollection.instance.keysList[position],
                list[position].text
            )
            fragmentGoals.fragmentManager?.let { fm -> friendsProgressDialog.show(fm, "") }
        }
    }

    private fun changeSocialGoalProgress(progress: Int, isDone: Boolean, goalKey: String) {
        val currentUserId = CurrentSession.instance.currentUser.id

        // изменить прогресс цели, прогресс задачи у нас в аккаунте по ключу цели
        userDatabase.child(currentUserId).child("socialGoals").child(goalKey).child("done")
            .setValue(isDone).addOnSuccessListener {
                userDatabase.child(currentUserId).child("socialGoals").child(goalKey)
                    .child("progress").setValue(progress).addOnSuccessListener {
//                        Toast.makeText(
//                            fragmentGoals.context,
//                            "Наша Цель на сервере изменина",
//                            Toast.LENGTH_SHORT
//                        ).show()
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

    // обработка нажатия на кнопку "Завершить"
    private fun setCompleteClickListener(view: View, position: Int) {
        view.itemGoalCompleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(fragmentGoals.context)
                .setTitle(view.context.getString(R.string.dialog_confirmAction))
                .setMessage(view.context.getString(R.string.dialog_completeGoal))
                .setNegativeButton(view.context.getString(R.string.dialog_cancel)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(view.context.getString(R.string.dialog_completeGoalButton)) { dialog, which ->
                    // Respond to positive button press
                    if (list[position].isSocial) {
                        // completing social goal

                        // обновляем аналитику
                        fragmentGoals.context?.let { ctx ->
                            val analytics = AnalyticsSingleton.instance.analytics
                            analytics.goalsCompleted++
                            DatabaseOperations.getInstance(ctx).updateAnalytics(analytics)
                        }

                        // удалить цель из локальной коллекции
                        SocialGoalCollection.instance.goalList.removeAt(position)
                        val goalKey = SocialGoalCollection.instance.keysList[position]
                        SocialGoalCollection.instance.keysList.removeAt(position)

                        // обновляем ресайклер
                        notifyDataSetChanged()
                        // удалить цель из нашего аккаунта на сервере
                        removeSocialGoalFromAccount(goalKey)
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

                        // обновляем ресайклер
                        notifyDataSetChanged()
                    }

                }.show()
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
                                                fragmentGoals.context?.getString(R.string.toast_goalCompleted),
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
                                                            fragmentGoals.context?.getString(R.string.toast_goalCompleted),
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

    private fun deleteSocialGoal(position: Int, goal: Goal) {
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
                                                fragmentGoals.context?.getString(R.string.toast_goalDeleted),
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

        Log.e("POSITION", position.toString())
        // удаляем цель из локальной коллекции
        SocialGoalCollection.instance.goalList.remove(goal)
        SocialGoalCollection.instance.keysList.removeAt(position)
        notifyDataSetChanged()
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
                Log.e(
                    "GOALS LIST: ",
                    "${GoalCollection.instance.goalsInProgressList}"
                )

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

    fun updateGoalProgress(position: Int) {
        tasksVisible = true
        notifyItemChanged(position)
    }

    fun changeGoalProgress(position: Int, progress: Int): Goal {
        GoalCollection.instance.goalsInProgressList[position].progress = progress
        return list[position]
    }
}
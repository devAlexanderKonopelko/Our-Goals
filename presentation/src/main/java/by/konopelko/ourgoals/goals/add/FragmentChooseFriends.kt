package by.konopelko.ourgoals.goals.add

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.goals.add.recyclerChooseFriends.ChooseFriendsAdapter
import by.konopelko.ourgoals.goals.add.recyclerTasks.AddTaskSingleton
import by.konopelko.ourgoals.temporaryData.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_add_goal_friends.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentChooseFriends(val previousDialog: FragmentAddTasks) : DialogFragment() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    interface SocialGoalAddition {
        fun updateRecyclerWithSocialGoal()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_goal_friends, container, true)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // получаем список друзей текущего пользователя
        findFriends() // выполнится через какое-то время

        val friendsList = FriendsListCollection.instance.friendsList
        this.context?.let {
            chooseFriendsRecycler.adapter = ChooseFriendsAdapter(friendsList, it)
        }
        chooseFriendsRecycler.layoutManager = LinearLayoutManager(this.context)
        chooseFriendsRecycler.setHasFixedSize(true)

        // inflating previous fragment
        chooseFriendsBackButton.setOnClickListener {
            dismiss()
            fragmentManager?.let { fm -> previousDialog.show(fm, "") }
        }

        // finishing creating new goal
        // генерируем уникальный ключ
        val goalKey = FirebaseDatabase.getInstance().reference.push().key

        // обработчик кнопки Готово
        chooseFriendsFinishButton.setOnClickListener {
            if (goalKey != null) {
                // если мы выбрали хотя бы одного друга
                if (GoalReceiversCollection.instance.receiversIds.isNotEmpty()) {
                    for (receiverId in GoalReceiversCollection.instance.receiversIds) {
                        // отправка запроса на сервер
                        sendGoalRequestUserFriend(receiverId, goalKey)
                    }
                    // запись цели нам в аккаунт на сервере
                    sendSocialGoaltoAccount(goalKey)
                    addSocialGoalToCollection(goalKey)
                    updateAnalytics(NewGoal.instance.goal.tasks)

                    // очищаем временную коллекцию
                    GoalReceiversCollection.instance.receiversIds.clear()

                    if (SocialGoalCollection.instance.visible) {
                        activity?.run {
                            val refresh = this as SocialGoalAddition
                            refresh.updateRecyclerWithSocialGoal()
                        }
                    }
                    Toast.makeText(context, getString(R.string.toast_teamGoalAdded), Toast.LENGTH_LONG).show()
                    dismiss()
                } else {
                    Toast.makeText(context, getString(R.string.toast_chooseOneFriend), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateAnalytics(tasks: ArrayList<Task>?) {
        activity?.run {
            val analytics = AnalyticsSingleton.instance.analytics
            analytics.goalsSet++
            analytics.tasksSet += tasks?.size ?: 0

            CoroutineScope(Dispatchers.IO).launch {
                DatabaseOperations.getInstance(this@run).updateAnalytics(analytics).await()
            }
        }
    }

    private fun clearTempFriendsList() {
        FriendsListCollection.instance.friendsList.clear()
        FriendsListCollection.instance.keysList.clear()
    }

    private fun findFriends() {
        clearTempFriendsList()

        val currentUsedId = auth.currentUser!!.uid
        // добавление существующих друзей в коллекцию
        userDatabase.child(currentUsedId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(currentUser: DataSnapshot) {

                // если у нас уже есть друзья
                if (currentUser.hasChild("friends")) {
                    for (friend in currentUser.child("friends").children) {
                        userDatabase.child(friend.key!!).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(ourFriend: DataSnapshot) {
                                // добавлять друзей в коллекцию
                                val uid = ourFriend.child("uid").value.toString()
                                val name = ourFriend.child("login").value.toString()

                                FriendsListCollection.instance.friendsList.add(
                                    User(
                                        uid,
                                        name,
                                        ArrayList()
                                    )
                                )

                                if (this@FragmentChooseFriends.isVisible) {
//                                    Log.e("БД НА ДРУЗЕЙ: ", "TRUE")

                                    // обновляем ресайклер по ходу
                                    (chooseFriendsRecycler.adapter as ChooseFriendsAdapter).notifyDataSetChanged()
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                            }
                        })
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun sendGoalRequestUserFriend(receiverId: String, goalKey: String) {
        val currentUserId = auth.currentUser!!.uid

        Log.e("PARAMETERS", "UID: $currentUserId || sendGoalRequestUserFriend()")
        // Запись запроса (мы -> друг)
        firebaseDatabase.child("GoalRequests").child(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(ourUser: DataSnapshot) {
                    // записываем цель
                    firebaseDatabase.child("GoalRequests").child(currentUserId)
                        .child(receiverId)
                        .child(goalKey)
                        .child("text").setValue(NewGoal.instance.goal.text).addOnSuccessListener {
                            firebaseDatabase.child("GoalRequests").child(currentUserId)
                                .child(receiverId)
                                .child(goalKey)
                                .child("tasks").setValue(NewGoal.instance.goal.tasks)
                                .addOnSuccessListener {
                                    firebaseDatabase.child("GoalRequests").child(currentUserId)
                                        .child(receiverId)
                                        .child(goalKey)
                                        .child("request_status").setValue("sent")
                                        .addOnSuccessListener {
                                            sendGoalRequestFriendUser(receiverId, goalKey)
                                        }
                                }
                        }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })


        // и записываем нас, как одного из тех, кто выполняет эту цель
//        firebaseDatabase.child("GoalRequests").child(receiverId)
//            .child(currentUserId)
//            .child(goalKey)
//            .child("friends")
//            .child(currentUserId)
//            .child("progress")
//            .setValue(0)
    }

    private fun sendGoalRequestFriendUser(receiverId: String, goalKey: String) {
        val currentUserId = auth.currentUser!!.uid

        Log.e("PARAMETERS", "UID: $currentUserId || sendGoalRequestFriendUser()")
        Log.e("PARAMETERS", "RECEIVER ID: $receiverId || sendGoalRequestFriendUser()")
        // Запись запроса (друг -> мы)
        firebaseDatabase.child("GoalRequests")
            .child(receiverId)
            .child(currentUserId)
            .child(goalKey)
            .child("text")
            .setValue(NewGoal.instance.goal.text)
            .addOnSuccessListener {
                firebaseDatabase.child("GoalRequests")
                    .child(receiverId)
                    .child(currentUserId)
                    .child(goalKey)
                    .child("tasks")
                    .setValue(NewGoal.instance.goal.tasks)
                    .addOnSuccessListener {
                        //записываем в запрос всех остальных пользователей, которые выполняют эту цель
                        for (otherReceiverID in GoalReceiversCollection.instance.receiversIds) {
                            // записываем всех других пользователей получателю (всех, кроме данного получателя, receiverId)
                            if (otherReceiverID != receiverId) {
                                firebaseDatabase.child("GoalRequests")
                                    .child(receiverId)
                                    .child(currentUserId)
                                    .child(goalKey)
                                    .child("friends")
                                    .child(otherReceiverID)
                                    .child("progress")
                                    .setValue(0)
                            }
                        }
                        firebaseDatabase.child("GoalRequests")
                            .child(receiverId)
                            .child(currentUserId)
                            .child(goalKey)
                            .child("request_status")
                            .setValue("received")
                    }
            }
    }

    // добавляем текущему пользователю Социальную Цель в аккаунт на сервере
    private fun sendSocialGoaltoAccount(goalKey: String) {
        val currentUserId = auth.currentUser!!.uid

        Log.e("PARAMETERS", "UID: $currentUserId || sendSocialGoaltoAccount()")
        userDatabase.child(currentUserId).child("socialGoals")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(ourSocialGoals: DataSnapshot) {
                    //записываем НАМ новую общую цель
                    userDatabase.child(currentUserId).child("socialGoals")
                        .child(goalKey).setValue(NewGoal.instance.goal)
                    // + добавить друзей, которым эта цель была отправлена
                    markReceiversInSocialGoal(goalKey)

                }

                private fun markReceiversInSocialGoal(goalKey: String) {
                    for (receiverId in GoalReceiversCollection.instance.receiversIds) {
                        userDatabase.child(currentUserId).child("socialGoals")
                            .child(goalKey)
                            .child("friends")
                            .child(receiverId)
                            .child("progress").setValue(0)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

    private fun addSocialGoalToCollection(goalKey: String) {
        val ownerId = CurrentSession.instance.currentUser.id
        val category = NewGoal.instance.goal.category
        val isDone = NewGoal.instance.goal.isDone
        val isSocial = NewGoal.instance.goal.isSocial
        val progress = NewGoal.instance.goal.progress
        val tasks = AddTaskSingleton.instance.taskList
        val text = NewGoal.instance.goal.text

        Log.e(
            "PARAMETERS",
            "UID: ${CurrentSession.instance.currentUser.id} || addSocialGoalToCollection()"
        )

        val goal = Goal(
            ownerId,
            category,
            text,
            progress,
            tasks,
            isDone,
            isSocial
        )

        SocialGoalCollection.instance.goalList.add(goal)
        SocialGoalCollection.instance.keysList.add(goalKey)
        Log.e(
            "NEW SOCIAL GOAL:",
            "${SocialGoalCollection.instance.goalList[SocialGoalCollection.instance.goalList.size - 1]}, goalKey: $goalKey"
        )
    }

}
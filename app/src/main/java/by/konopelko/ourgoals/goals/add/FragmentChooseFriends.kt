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
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.goals.add.recyclerChooseFriends.ChooseFriendsAdapter
import by.konopelko.ourgoals.temporaryData.FriendsListCollection
import by.konopelko.ourgoals.temporaryData.GoalReceiversCollection
import by.konopelko.ourgoals.temporaryData.SocialGoalCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_add_goal_friends.*

class FragmentChooseFriends(val previousDialog: FragmentAddTasks) : DialogFragment() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

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
        chooseFriendsFinishButton.setOnClickListener {
            if (goalKey != null) {
                for (receiverId in GoalReceiversCollection.instance.receiversIds) {
                    // отправка запроса на сервер
                    sendGoalRequest(GoalReceiversCollection.instance.receiversIds, receiverId, goalKey)
                }
                // запись цели нам в аккаунт на сервере
                sendSocialGoaltoAccount(goalKey)
                addSocialGoalToCollection(goalKey)

                Toast.makeText(this.context, "Цель добавлена в Общие Цели!", Toast.LENGTH_SHORT).show()
                dismiss()
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

    private fun sendGoalRequest(receiversList: ArrayList<String>, receiverId: String, goalKey: String) {
        val currentUserId = auth.currentUser!!.uid

        // Запись запроса (мы -> друг)
        firebaseDatabase.child("GoalRequests").child(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(ourUser: DataSnapshot) {

                    // записываем цель
                    firebaseDatabase.child("GoalRequests").child(currentUserId)
                        .child(receiverId)
                        .child(goalKey)
                        .child("text").setValue(NewGoal.instance.goal.text)

                    firebaseDatabase.child("GoalRequests").child(currentUserId)
                        .child(receiverId)
                        .child(goalKey)
                        .child("tasks").setValue(NewGoal.instance.goal.tasks)

                    firebaseDatabase.child("GoalRequests").child(currentUserId)
                        .child(receiverId)
                        .child(goalKey)
                        .child("request_status").setValue("sent")
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })

        // Запись запроса (друг -> мы)
        firebaseDatabase.child("GoalRequests").child(receiverId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(receiver: DataSnapshot) {
                    // записываем цель
                    firebaseDatabase.child("GoalRequests").child(receiverId)
                        .child(currentUserId)
                        .child(goalKey)
                        .child("text").setValue(NewGoal.instance.goal.text)

                    firebaseDatabase.child("GoalRequests").child(receiverId)
                        .child(currentUserId)
                        .child(goalKey)
                        .child("tasks").setValue(NewGoal.instance.goal.tasks)

                    //записываем в запрос всех остальных пользователей, которые выполняют эту цель
                    for (otherReceiverID in GoalReceiversCollection.instance.receiversIds) {
                        // записываем всех других пользователей получателю (всех, кроме данного получателя, receiverId)
                        if (otherReceiverID != receiverId) {
                            firebaseDatabase.child("GoalRequests").child(receiverId)
                                .child(currentUserId)
                                .child(goalKey)
                                .child("friends")
                                .child(otherReceiverID)
                                .child("progress")
                                .setValue(0)
                        }
                    }
                    // и записываем нас, как одного из тех, кто выполняет эту цель
//                    firebaseDatabase.child("GoalRequests").child(receiverId)
//                        .child(currentUserId)
//                        .child(goalKey)
//                        .child("friends")
//                        .child(currentUserId)
//                        .child("progress")
//                        .setValue(0)

                    firebaseDatabase.child("GoalRequests").child(receiverId)
                        .child(currentUserId)
                        .child(goalKey)
                        .child("request_status").setValue("received")
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

    // добавляем текущему пользователю Социальную Цель в аккаунт на сервере
    private fun sendSocialGoaltoAccount(goalKey: String) {
        val currentUserId = auth.currentUser!!.uid

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
        SocialGoalCollection.instance.goalList.add(NewGoal.instance.goal)
        SocialGoalCollection.instance.keysList.add(goalKey)
        Log.e(
            "NEW SOCIAL GOAL:",
            "${SocialGoalCollection.instance.goalList[SocialGoalCollection.instance.goalList.size - 1]}"
        )
    }

}
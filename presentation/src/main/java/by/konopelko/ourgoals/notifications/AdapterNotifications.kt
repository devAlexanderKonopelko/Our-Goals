package by.konopelko.ourgoals.notifications

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.temporaryData.AnalyticsSingleton
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.NotificationsCollection
import by.konopelko.ourgoals.temporaryData.SocialGoalCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_recycler_friends.view.*
import kotlinx.android.synthetic.main.item_recycler_notifications_base.view.*
import kotlinx.android.synthetic.main.item_recycler_notifications_goals.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdapterNotifications(
    val friendsRequests: ArrayList<User>,
    val goalsRequests: ArrayList<Goal>,
    val goalsRequestsSenders: ArrayList<User>,
    val goalsRequestsGNumber: ArrayList<String>,
    val requestsKeys: ArrayList<String>,
    val fragmentManager: FragmentManager,
    val context: Context,
    val activity: FragmentActivity
) : RecyclerView.Adapter<AdapterNotifications.NotificationsViewHolder>() {
    class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface NotificationActions {
        fun notificationDeleted(listSize: Int)
    }

    private val friendRequestDatabase =
        FirebaseDatabase.getInstance().reference.child("FriendRequests")
    private val goalRequestDatabase = FirebaseDatabase.getInstance().reference.child("GoalRequests")
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_notifications_base, parent, false)

        return NotificationsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        // берём размер только одного списка, т.к. из-за заглушек размеры всех списков будут одинаковыми
        return friendsRequests.size
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val view = holder.itemView

        // показываем лэйаут в зависимости от ключа (типа) нотификации
        // если это связанно с друзьями
        if (requestsKeys[position].contains("friend")) {
            view.notifFriendLayout.visibility = View.VISIBLE
            view.itemFriendsNotificationText.visibility = View.VISIBLE
            when {
                // если это входящий запрос в друзья
                requestsKeys[position].contains("received") -> {
                    view.itemFriendsNotificationText.text = "Запрос в друзья"

                    view.itemFriendsName.text = friendsRequests[position].name
                    view.itemFriendsWaitingButton.setImageResource(R.drawable.icon_accept_request)
                    view.itemFriendsWaitingTitle.text = context.getString(R.string.notification_acceptRequest)
                    view.itemFriendsCancelTitle.text = context.getString(R.string.notification_declineRequest)
                    view.deleteNotificationButtonFriends.visibility = View.GONE
                    // обрабатывать нажатия на кнопки Принять/Отклонить запрос
                    view.itemFriendsWaitingButton.setOnClickListener {
                        // принятие запроса
                        acceptFriendRequest(friendsRequests[position].id)
                        // обновить ресайклер (удалить нотификацию)
                        updateRecycler(position)
                    }
                    view.itemFriendsCancelReqButton.setOnClickListener {
                        // отклонение запроса
                        declineFriendRequest(friendsRequests[position].id)
                        // обновить ресайклер (удалить нотификацию)
                        updateRecycler(position)
                    }

                }
                // если это уведомление о том, что пользователь подтвердил ваш запрос
                requestsKeys[position].contains("accepted") -> {
                    view.itemFriendsNotificationText.text = context.getString(R.string.notification_friendsReqAccepted)
                    view.itemFriendsName.text = friendsRequests[position].name
                    view.itemFriendsWaitingButton.setImageResource(R.drawable.icon_accept_request)
                    view.itemFriendsWaitingButton.isEnabled = false

                    view.itemFriendsCancelReqButton.visibility = View.GONE
                    view.itemFriendsWaitingTitle.visibility = View.INVISIBLE
                    view.itemFriendsCancelTitle.visibility = View.GONE

                    // удаление нотификации и соответствующего запроса на сервере
                    view.deleteNotificationButtonFriends.setOnClickListener {
                        deleteFriendRequest(friendsRequests[position].id)
                        // обновить ресайклер
                        updateRecycler(position)
                    }
                }
                // если это уведомление о том, что пользователь отклонил ваш запрос
                requestsKeys[position].contains("declined") -> {
                    view.itemFriendsNotificationText.text = context.getString(R.string.notification_friendsReqDeclined)
                    view.itemFriendsName.text = friendsRequests[position].name
                    view.itemFriendsCancelReqButton.setImageResource(R.drawable.icon_cancel_request)
                    view.itemFriendsCancelReqButton.isEnabled = false

                    view.itemFriendsWaitingButton.visibility = View.GONE
                    view.itemFriendsWaitingTitle.visibility = View.INVISIBLE
                    view.itemFriendsCancelTitle.visibility = View.GONE

                    // удаление нотификации и соответствующего запроса на сервере
                    view.deleteNotificationButtonFriends.setOnClickListener {
                        deleteFriendRequest(friendsRequests[position].id)
                        // обновить ресайклер
                        updateRecycler(position)
                    }
                }
            }
            // если это связанно с общими целями
        } else if (requestsKeys[position].contains("goal")) {
            view.notifGoalLayout.visibility = View.VISIBLE

            val goalText = goalsRequests[position].text
            val tasks = goalsRequests[position].tasks
            val sender = goalsRequestsSenders[position].name

            view.itemNotificationGoalText.append(goalText)
            view.itemNotificationGoalTitle.text = sender
            view.itemNotificationGoalTasksList.adapter = AdapterNotificationTasks(tasks)
            view.itemNotificationGoalTasksList.layoutManager = LinearLayoutManager(context)
            view.itemNotificationGoalTasksList.setHasFixedSize(true)

            // если это запрос на создание общей цели
            when {
                requestsKeys[position].contains("received") -> {
                    view.itemNotificationGoalTitle.append(context.getString(R.string.notification_goal_textPropose))
                    view.deleteNotificationButton.visibility = View.GONE

                    // обработчик нажатия на кнопки принять/отклонить
                    // принять общую цель
                    view.itemNotificationGoalAcceptButton.setOnClickListener {
                        acceptSocialGoal(
                            goalsRequestsSenders[position],
                            goalsRequestsGNumber[position],
                            position
                        )
                    }

                    // отклонить общую цель
                    view.itemNotificationGoalDeclineButton.setOnClickListener {
                        declineSocialGoal(
                            goalsRequestsSenders[position],
                            goalsRequestsGNumber[position],
                            position
                        )
                    }

                }
                // если это уведомление о принятии вашей общей цели
                requestsKeys[position].contains("accepted") -> {
                    view.itemNotificationGoalTitle.append(context.getString(R.string.notification_goal_accepted))
                    view.itemNotificationGoalDeclineButton.visibility = View.INVISIBLE
                    view.itemNotificationGoalDeclineTitle.visibility = View.INVISIBLE
                    view.itemNotificationGoalAcceptButton.isEnabled = false
                    view.itemNotificationGoalAcceptTitle.visibility = View.INVISIBLE

                    // удаление нотификации и соответствующего запроса на сервере
                    view.deleteNotificationButton.setOnClickListener {
                        deleteGoalRequest(
                            goalsRequestsSenders[position],
                            goalsRequestsGNumber[position],
                            position
                        )
                        // обновление ресайклера
                        updateRecycler(position)
                    }

                }
                // если это уведомление об отклонении вашей общей цели
                requestsKeys[position].contains("declined") -> {
                    view.itemNotificationGoalTitle.append(context.getString(R.string.notification_goal_declined))
                    view.itemNotificationGoalDeclineButton.isEnabled = false
                    view.itemNotificationGoalAcceptButton.visibility = View.INVISIBLE
                    view.itemNotificationGoalAcceptTitle.visibility = View.INVISIBLE
                    view.itemNotificationGoalDeclineTitle.visibility = View.INVISIBLE

                    // удаление нотификации и соответствующего запроса на сервере
                    view.deleteNotificationButton.setOnClickListener {
                        deleteGoalRequest(
                            goalsRequestsSenders[position],
                            goalsRequestsGNumber[position],
                            position
                        )
                        // обновить ресайклер
                        updateRecycler(position)
                    }
                }
            }

            // развёрнутая цель по нажанию на кнопку Подробнее
            view.itemNotificationGoalDetailsButton.setOnClickListener {
                val goalDetailsDialog = FragmentNotificationDetails(view)
                goalDetailsDialog.show(fragmentManager, "")
            }
        }
    }

    private fun acceptFriendRequest(senderId: String) {
        val currentUserId = auth.currentUser!!.uid

        // добавить друга НАМ
        userDatabase.child(currentUserId).child("friends").child(senderId).setValue("friend")
            .addOnSuccessListener {
                // добавить нас ДРУГУ
                userDatabase.child(senderId).child("friends").child(currentUserId)
                    .setValue("friend")
                    .addOnSuccessListener {
                        // у нас удалить ЗАПРОС
                        friendRequestDatabase.child(currentUserId).child(senderId).removeValue()
                            .addOnSuccessListener {
                                // у друга поменять ЗАПРОС на accepted
                                friendRequestDatabase.child(senderId).child(currentUserId)
                                    .child("request_type").setValue("accepted")
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_nowYouFriend),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                    }
            }
    }

    private fun declineFriendRequest(senderId: String) {
        val currentUserId = auth.currentUser!!.uid
        // у нас удалить ЗАПРОС
        friendRequestDatabase.child(currentUserId).child(senderId).removeValue()
            .addOnSuccessListener {
                // у друга поменять ЗАПРОС на declined
                friendRequestDatabase.child(senderId).child(currentUserId)
                    .child("request_type").setValue("declined")
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_reqDeclined),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

    }

    private fun deleteFriendRequest(senderId: String) {
        val currentUserId = auth.currentUser!!.uid
        // удалить запрос у НАС
        friendRequestDatabase.child(currentUserId).child(senderId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.toast_notifDeleted), Toast.LENGTH_SHORT).show()
            }
    }

    private fun acceptSocialGoal(sender: User, goalKey: String, position: Int) {
//        Log.e("GOAL KEY: ", goalKey)
        val currentUserId = auth.currentUser!!.uid
        Log.e("PARAMETERS", "UID: $currentUserId || acceptSocialGoal()")
        Log.e("PARAMETERS", "SENDER UID: ${sender.id} || acceptSocialGoal()")
        // у отправителя изменить цель на accepted
        goalRequestDatabase.child(sender.id).child(currentUserId).child(goalKey)
            .child("request_status").setValue("accepted").addOnSuccessListener {
                // добавить цель в НАШ список на сервере с пометкой ПРОГРЕССА у отправителя
                sendSocialGoaltoAccount(currentUserId, sender, goalKey, position)
            }


    }

    private fun sendSocialGoaltoAccount(
        currentUserId: String,
        sender: User,
        goalKey: String,
        position: Int
    ) {
        Log.e("PARAMETERS", "UID: $currentUserId || sendSocialGoaltoAccount()")
        Log.e("PARAMETERS", "SENDER UID: ${sender.id} || sendSocialGoaltoAccount()")

        val goal = Goal(
            ownerId = currentUserId,
            category = context.getString(R.string.category_name),
            text = goalsRequests[position].text,
            progress = 0,
            tasks = goalsRequests[position].tasks,
            isDone = false,
            isSocial = true
        )


        // добавляем цель в локальную КОЛЛЕКЦИЮ общих целей
        SocialGoalCollection.instance.goalList.add(goal)
        SocialGoalCollection.instance.keysList.add(goalKey)

        Toast.makeText(this.context, context.getString(R.string.toast_teamGoalAdded), Toast.LENGTH_LONG).show()

        // добавляем информацию в аналитику
        val analytics = AnalyticsSingleton.instance.analytics
        analytics.goalsSet++
        analytics.tasksSet += goal.tasks?.size ?: 0

        CoroutineScope(Dispatchers.IO).launch {
            DatabaseOperations.getInstance(context).updateAnalytics(analytics).await()
        }

        // записываем принятую цель в НАШ список соц. целей на сервере
        userDatabase.child(currentUserId).child("socialGoals")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(ourSocialGoals: DataSnapshot) {
                    // записываем нам новую общую цель
                    userDatabase.child(currentUserId).child("socialGoals")
                        .child(goalKey).setValue(goal)

                    // + добавить отправителя, который предложил эту цель
                    markReceiversInSocialGoal(goalKey, position)
                }

                // пометить всех пользователей, которые также выполняют эту цель
                private fun markReceiversInSocialGoal(goalKey: String, position: Int) {
                    Log.e("MARK RECEIVERS","START")
                    //пометить отправителя как пользователя, выполняющего цель
                    goalRequestDatabase.child(currentUserId).child(sender.id).child(goalKey).addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(acceptedGoal: DataSnapshot) {
                            // помечаем прогресс всех вовлечённых пользователей
                            for (otherUser in acceptedGoal.child("friends").children) {
                                userDatabase.child(currentUserId).child("socialGoals")
                                    .child(goalKey)
                                    .child("friends")
                                    .child(otherUser.key!!)
                                    .child("progress").setValue(0)
                            }

                            // если отправить уже завершил эту цель, то ставим ему прогресс 100
                            if (acceptedGoal.hasChild(sender.id + "_progress")) {
                                userDatabase.child(currentUserId).child("socialGoals")
                                    .child(goalKey)
                                    .child("friends")
                                    .child(sender.id)
                                    .child("progress").setValue(100)
                            }

                            // удалить цель по КЛЮЧУ_ЦЕЛИ из НАШЕГО ЗАПРОСА
                            goalRequestDatabase.child(currentUserId).child(sender.id).child(goalKey)
                                .removeValue().addOnSuccessListener {
                                    // обновление ресайклера (удаление нотификации)
                                    updateRecycler(position)
                                }
                        }
                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

    private fun declineSocialGoal(sender: User, goalKey: String, position: Int) {
        val currentUserId = auth.currentUser!!.uid

        // удалить цель по КЛЮЧУ_ЦЕЛИ из НАШЕГО ЗАПРОСА
        goalRequestDatabase.child(currentUserId).child(sender.id).child(goalKey).removeValue()
            .addOnSuccessListener {
                // у отправителя изменить цель на declined
                goalRequestDatabase.child(sender.id).child(currentUserId).child(goalKey)
                    .child("request_status").setValue("declined").addOnSuccessListener {
                        Toast.makeText(context, context.getString(R.string.toast_reqDeclined), Toast.LENGTH_SHORT).show()
                        // обновить ресайклер (удалить нотификацию)
                        updateRecycler(position)
                    }
            }

    }

    private fun deleteGoalRequest(sender: User, goalKey: String, position: Int) {
        val currentUserId = auth.currentUser!!.uid

        // удалить запрос у НАС с конкретным пользователем по КЛЮЧУ_ЦЕЛИ
        goalRequestDatabase.child(currentUserId).child(sender.id).child(goalKey).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.toast_notifDeleted), Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRecycler(position: Int) {
        // удалить item из коллекций
        NotificationsCollection.instance.goalsRequests.removeAt(position)
        NotificationsCollection.instance.goalsRequestsGoalKeys.removeAt(position)
        NotificationsCollection.instance.goalsRequestsSenders.removeAt(position)
        NotificationsCollection.instance.requestsKeys.removeAt(position)
        // также удаляем заглушку, чтобы в ресайклере индексы обновились корректно
        NotificationsCollection.instance.friendsRequests.removeAt(position)

        // обновить датасет ресайклера
        notifyDataSetChanged()

        activity.run {
            val notificationAction = this as NotificationActions
            notificationAction.notificationDeleted(NotificationsCollection.instance.requestsKeys.size)
        }
    }

}
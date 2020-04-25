package by.konopelko.ourgoals.temporaryData

import android.util.Log
import by.konopelko.ourgoals.ActivityMain
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.Task
import by.konopelko.ourgoals.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class NotificationOperations {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val friendRequestDatabase =
        FirebaseDatabase.getInstance().reference.child("FriendRequests")
    private val goalRequestDatabase = FirebaseDatabase.getInstance().reference.child("GoalRequests")

    companion object {
        val instance = NotificationOperations()
    }

    suspend fun loadNotifications(): Deferred<Boolean> {
        Log.e("INSIDE", "loadNotifications()")
        clearTempNotificationsList()

        val result = CoroutineScope(Dispatchers.IO).async {
            val done1 = findFriendsNotifications().await()
            val done2 = findGoalsNotifications().await()
            return@async done1 && done2
        }

        Log.e("INSIDE", "loadNotifications(): RETURN")

        return result
    }
    private fun clearTempNotificationsList() {
        NotificationsCollection.instance.friendsRequests.clear()
        NotificationsCollection.instance.goalsRequests.clear()
        NotificationsCollection.instance.goalsRequestsSenders.clear()
        NotificationsCollection.instance.requestsKeys.clear()
    }

    private suspend fun findFriendsNotifications(): Deferred<Boolean> {
        // Находить все ВХОДЯЩИЕ ЗАПРОСЫ дружбы +
        // Находить все ПРИНЯТЫЕ ЗАПРОСЫ дружбы +
        // Находить все ОТКЛОНЁННЫЕ ЗАПРОСЫ дружбы +

        val currentUsedId = auth.currentUser!!.uid
        val result = CoroutineScope(Dispatchers.IO).async {
            friendRequestDatabase.child(currentUsedId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(requests: DataSnapshot) {
                        if (requests.hasChildren()) {
                            for (request in requests.children) {
                                val uid = request.key!!
                                var name = ""
                                userDatabase.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(users: DataSnapshot) {
                                        name = users.child(uid).child("login").value.toString()

                                        if (request.child("request_type").value == "received" ||
                                            request.child("request_type").value == "accepted" ||
                                            request.child("request_type").value == "declined"
                                        ) {

                                            // записываем пользователя, который прислал запрос на дружбу
                                            val user = User(uid, name, ArrayList())
                                            NotificationsCollection.instance.friendsRequests.add(user)
                                            // и записываем ключ (тип) нотификации как "нотификация получения запроса на дружбу"
                                            when (request.child("request_type").value   ) {
                                                "received" -> {
                                                    NotificationsCollection.instance.requestsKeys.add("friend_received_request")
                                                }
                                                "accepted" -> {
                                                    NotificationsCollection.instance.requestsKeys.add("friend_accepted_request")
                                                }
                                                "declined" -> {
                                                    NotificationsCollection.instance.requestsKeys.add("friend_declined_request")
                                                }
                                            }


                                            // делаем заглушку по этому индексу для коллекции запросов целей
                                            // (это нужно для того, чтобы адаптер нотификаций брал корректные индексы)
                                            NotificationsCollection.instance.goalsRequests.add(
                                                Goal(
                                                    "", "", "", 0,
                                                    ArrayList(), isDone = false, isSocial = false
                                                )
                                            )
                                            NotificationsCollection.instance.goalsRequestsSenders.add(
                                                User("", "", ArrayList())
                                            )
                                            NotificationsCollection.instance.goalsRequestsGoalKeys.add("")
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

            true
        }

        return result
    }

    private suspend fun findGoalsNotifications(): Deferred<Boolean> {
        // Находить все ВХОДЯЩИЕ ЗАПРОСЫ на общие цели +
        // Находить все ПРИНЯТЫЕ ЗАПРОСЫ на общие цели +
        // Находить все ОТКЛОНЁННЫЕ ЗАПРОСЫ на общие цели +

        val currentUsedId = auth.currentUser!!.uid

        val result = CoroutineScope(Dispatchers.IO).async {
            goalRequestDatabase.child(currentUsedId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(ourUserGoalRequests: DataSnapshot) {
                        if (ourUserGoalRequests.hasChildren()) { // Если у нас есть запросы общих целей с другими пользователями
                            for (otherUser in ourUserGoalRequests.children) { // перебираем каждого пользователя с запросами
                                for (socialGoal in otherUser.children) { // для каждого пользователя перебираем цели
                                    // записываем все цели этого пользователя, где есть received/accepted/declined
                                    if (socialGoal.child("request_status").value == "received" ||
                                        socialGoal.child("request_status").value == "accepted" ||
                                        socialGoal.child("request_status").value == "declined"
                                    ) {
                                        val goalKey = socialGoal.key!!

                                        // записываем задачи для текущей цели
                                        val tasks = ArrayList<Task>()
                                        for (taskSnapshot in socialGoal.child("tasks").children) {
                                            val task = Task(
                                                taskSnapshot.child("text").value.toString(),
                                                taskSnapshot.child("finishDate").value.toString(),
                                                false
                                            )

                                            tasks.add(task)
                                        }

                                        val goal =
                                            Goal(
                                                "",
                                                "",
                                                socialGoal.child("text").value.toString(),
                                                0,
                                                tasks,
                                                isDone = false,
                                                isSocial = true
                                            )

                                        // записываем каждую received цель в коллекцию
                                        NotificationsCollection.instance.goalsRequests.add(goal)
                                        // записываем ключ цели
                                        NotificationsCollection.instance.goalsRequestsGoalKeys.add(
                                            goalKey
                                        )

                                        // также надо записать отправителя, чтобы вывести его имя в нотификации
                                        val senderId = otherUser.key.toString()
                                        userDatabase.child(senderId)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(sender: DataSnapshot) {
                                                    val user = User(
                                                        sender.child("uid").value.toString(),
                                                        sender.child("login").value.toString(),
                                                        ArrayList()
                                                    )
                                                    NotificationsCollection.instance.goalsRequestsSenders.add(
                                                        user
                                                    )

                                                    // записываем ключ (тип) к этой нотификации
                                                    when (socialGoal.child("request_status").value) {
                                                        "received" -> {
                                                            NotificationsCollection.instance.requestsKeys.add(
                                                                "goal_received_request"
                                                            )
                                                        }
                                                        "accepted" -> {
                                                            NotificationsCollection.instance.requestsKeys.add(
                                                                "goal_accepted_request"
                                                            )
                                                        }
                                                        "declined" -> {
                                                            NotificationsCollection.instance.requestsKeys.add(
                                                                "goal_declined_request"
                                                            )
                                                        }
                                                    }


                                                    // делаем заглушку по этому индексу для коллекции запросов дружбы.
                                                    // Это нужно для того, чтобы адаптер нотификаций брал корректные индексы.
                                                    NotificationsCollection.instance.friendsRequests.add(
                                                        User("", "", ArrayList())
                                                    )

                                                    Log.e(
                                                        "COLLECT WRITTEN GOAL:",
                                                        NotificationsCollection.instance.goalsRequests[NotificationsCollection.instance.goalsRequests.size - 1].toString()
                                                    )
                                                    Log.e(
                                                        "COLLECT WRITTEN USER:",
                                                        NotificationsCollection.instance.goalsRequestsSenders[NotificationsCollection.instance.goalsRequestsSenders.size - 1].toString()
                                                    )
                                                    Log.e(
                                                        "COLLECT WRITTEN KEY:",
                                                        NotificationsCollection.instance.requestsKeys[NotificationsCollection.instance.requestsKeys.size - 1]
                                                    )
                                                }
                                                override fun onCancelled(p0: DatabaseError) {
                                                }
                                            })
                                    }
                                }
                            }
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {

                    }
                })

            true
        }

        Log.e("INSIDE", "findGoalsNotifications(): DONE")

        return result
    }

}
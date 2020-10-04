package by.konopelko.data.repositories.firebase

import android.util.Log
import by.konopelko.data.session.NotificationData
import by.konopelko.data.session.UserData
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseNotificationRepositoryImpl {
    private val friendRequestDatabase = FirebaseDatabase.getInstance().reference.child("FriendRequests")
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val goalRequestDatabase = FirebaseDatabase.getInstance().reference.child("GoalRequests")

    // хранят список повешенных на данные слушателей изменений
    private val goalsRequestListeners = ArrayList<ValueEventListener>()

    suspend fun observeNotifications() {
        // очищаем коллекцию уведомлений
        NotificationData.instance.clearTempNotificationsList()

        val uid = UserData.instance.currentUser.id

        // подгружаем все существующие уведомления
        performFriendsNotificationsObservation(uid)
        performGoalsNotificationsObservation(uid)
    }

    private suspend fun performFriendsNotificationsObservation(uid: String) {
        // firebase work
        val currentUser = friendRequestDatabase.child(uid).getSnapshotFromEventListener()




        friendRequestDatabase.child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(requests: DataSnapshot) {
                    // если есть какие-либо запросы в друзья
                    if (requests.hasChildren()) {
                        // проходимся по списку запросов
                        for (request in requests.children) {
                            // записываем id пользователя, с которым есть запрос
                            val requestUid = request.key!!
                            var name = ""
                            userDatabase.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(users: DataSnapshot) {
                                    name = users.child(requestUid).child("login").value.toString()

                                    if (request.child("request_type").value == "received" ||
                                        request.child("request_type").value == "accepted" ||
                                        request.child("request_type").value == "declined"
                                    ) {

                                        // записываем пользователя, который прислал запрос на дружбу
                                        val user =
                                            User(
                                                requestUid,
                                                name,
                                                ArrayList()
                                            )

                                        var userExists = false

                                        for (collectionUser in NotificationsCollection.instance.friendsRequests) {
                                            if (collectionUser.equals(user)) {
                                                userExists = true
                                                break
                                            }
                                        }

                                        if (userExists) return
                                        else {
                                            NotificationsCollection.instance.friendsRequests.add(
                                                user
                                            )
                                            // и записываем ключ (тип) нотификации как "нотификация получения запроса на дружбу"
                                            when (request.child("request_type").value) {
                                                "received" -> {
                                                    NotificationsCollection.instance.requestsKeys.add(
                                                        "friend_received_request"
                                                    )
                                                }
                                                "accepted" -> {
                                                    NotificationsCollection.instance.requestsKeys.add(
                                                        "friend_accepted_request"
                                                    )
                                                }
                                                "declined" -> {
                                                    NotificationsCollection.instance.requestsKeys.add(
                                                        "friend_declined_request"
                                                    )
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
                                                User(
                                                    "",
                                                    "",
                                                    ArrayList()
                                                )
                                            )
                                            NotificationsCollection.instance.goalsRequestsGoalKeys.add(
                                                ""
                                            )

                                            onOperationListener.onNotificationsChanged(
                                                NotificationsCollection.instance.requestsKeys.size
                                            )
                                        }
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

    private fun performGoalsNotificationsObservation(uid: String) {
        // firebase goal notifications search
//        goalRequestDatabase = FirebaseDatabase.getInstance().reference.child("GoalRequests")

        val valueEventListener = goalRequestDatabase.child(CurrentSession.instance.currentUser.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(ourUserGoalRequests: DataSnapshot) {
                    Log.e("PARAMETERS","OBSERVED USER: ${ourUserGoalRequests.key} || performGoalsNotificationsObservation()")
                    if (ourUserGoalRequests.hasChildren()) { // Если у нас есть запросы общих целей с другими пользователями
                        for (otherUser in ourUserGoalRequests.children) { // перебираем каждого пользователя с запросами
                            for (socialGoal in otherUser.children) { // для каждого пользователя перебираем цели
                                // записываем все цели этого пользователя, где есть received/accepted/declined
                                if (socialGoal.child("request_status").value == "received" ||
                                    socialGoal.child("request_status").value == "accepted" ||
                                    socialGoal.child("request_status").value == "declined"
                                ) {
                                    Log.e("NEW NOTIFICATION", "GOAL " +
                                            "\nRECEIVER: ${ourUserGoalRequests.key} " +
                                            "\nSENDER: ${otherUser.key} " +
                                            "\nID: ${socialGoal.key} " +
                                            "\nSTATUS: ${socialGoal.child("request_status").value}")

                                    val goalKey = socialGoal.key!!

                                    // записываем задачи для текущей цели
                                    val tasks = ArrayList<Task>()
                                    for (taskSnapshot in socialGoal.child("tasks").children) {
                                        val task =
                                            Task(
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

                                    var goalExists = false

                                    for (collectionGoal in NotificationsCollection.instance.goalsRequests) {
                                        if (collectionGoal == goal) {
                                            Log.e("NOTIFICATIONS:","Найдено повторение уведомления.")
                                            goalExists = true
                                            break
                                        }
                                    }

                                    if (goalExists) return
                                    else {
                                        // записываем каждую цель в коллекцию
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
                                                    val user =
                                                        User(
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
                                                        User(
                                                            "",
                                                            "",
                                                            ArrayList()
                                                        )
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

                                                    onOperationListener.onNotificationsChanged(
                                                        NotificationsCollection.instance.requestsKeys.size
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
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        goalsRequestListeners.add(valueEventListener)
        Log.e("LISTENER","GOAL LISTENER ADDED: $valueEventListener \nARRAY: $goalsRequestListeners")
    }


    private suspend fun DatabaseReference.getSnapshotFromEventListener(): DataSnapshot {
        return withContext(Dispatchers.IO) {
            // приостанавливаем выполнение корутины с помощью suspendCoroutine
            suspendCoroutine<DataSnapshot> { continuation -> // с помощью continuation мы сможем продолжить выполнение
                addValueEventListener(MyValueEventListener( // запускаем кастомный слушатель
                    // в качестве параметров передаём две функции, которые продолжат выполнение корутины
                    onDataChange = {
                        continuation.resume(it)
                    },
                    onError = {
                        continuation.resumeWithException(it.toException())
                    }
                ))
            }
        }
    }

    class MyValueEventListener(
        val onDataChange: (DataSnapshot) -> Unit,
        val onError: (DatabaseError) -> Unit
    ) : ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            return onDataChange.invoke(data) // вызываем переданную функцию как только получим data
        }

        override fun onCancelled(error: DatabaseError) {
            return onError.invoke(error)
        }
    }

}
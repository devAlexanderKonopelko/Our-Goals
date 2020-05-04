package by.konopelko.ourgoals.authentication.core

import android.util.Log
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.temporaryData.NotificationsCollection
import by.konopelko.ourgoals.temporaryData.SocialGoalCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogInInteractor(val onOperationListener: LogInContract.OnOperationListener) :
    LogInContract.Interactor {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val friendRequestDatabase =
        FirebaseDatabase.getInstance().reference.child("FriendRequests")
    private val goalRequestDatabase = FirebaseDatabase.getInstance().reference.child("GoalRequests")

    override fun performLogIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnSuccessListener {
            if (auth.currentUser!!.isEmailVerified) {
                onOperationListener.onLogIn(0, auth.currentUser?.uid.toString())
            }
            else {
                onOperationListener.onLogIn(2, auth.currentUser?.uid.toString())
            }
        }.addOnFailureListener {
            onOperationListener.onLogIn(1, auth.currentUser?.uid.toString())
        }
    }

    override fun performUserDownLoad(uid: String) {
        userDatabase.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(currentUser: DataSnapshot) {
                    val login = currentUser.child("login").value.toString()
                    val friends = ArrayList<User>()
                    val user = User(
                        uid,
                        login,
                        friends
                    )

                    onOperationListener.onUserLoadedFromServer(user)
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

    override fun performSocialGoalsDownload(uid: String) {
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

                        SocialGoalCollection.instance.goalList.add(newGoal)
                        SocialGoalCollection.instance.keysList.add(goal.key.toString())
                    }
                    onOperationListener.onSocialGoalsLoaded(true)
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

    override fun performFriendsNotificationsDownload(uid: String) {
        friendRequestDatabase.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(requests: DataSnapshot) {
                    if (requests.hasChildren()) {
                        for (request in requests.children) {
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
                                        NotificationsCollection.instance.friendsRequests.add(user)
                                        // и записываем ключ (тип) нотификации как "нотификация получения запроса на дружбу"
                                        when (request.child("request_type").value) {
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
                                            User(
                                                "",
                                                "",
                                                ArrayList()
                                            )
                                        )
                                        NotificationsCollection.instance.goalsRequestsGoalKeys.add("")
                                    }

                                }

                                override fun onCancelled(p0: DatabaseError) {
                                }
                            })
                        }
                    }
                    Log.e("1 NOTIFICATIONS AMOUNT:", NotificationsCollection.instance.requestsKeys.size.toString())
                    onOperationListener.onFriendsNotificationsLoaded(true)
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

    override fun performGoalsNotificationsDownload(uid: String) {
        goalRequestDatabase.child(uid)
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
                                            }
                                            override fun onCancelled(p0: DatabaseError) {
                                            }
                                        })
                                }
                            }
                        }
                    }
                    Log.e("2 NOTIFICATIONS AMOUNT:", NotificationsCollection.instance.requestsKeys.size.toString())
                    onOperationListener.onGoalsNotificationsLoaded(true)
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }
}
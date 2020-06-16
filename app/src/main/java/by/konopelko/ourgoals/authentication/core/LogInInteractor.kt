package by.konopelko.ourgoals.authentication.core

import android.util.Log
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.temporaryData.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogInInteractor(val onOperationListener: LogInContract.OnOperationListener) :
    LogInContract.Interactor {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    override fun performLogIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnSuccessListener {
            if (auth.currentUser!!.isEmailVerified) {
                onOperationListener.onLogIn(0, auth.currentUser?.uid.toString())
            } else {
                onOperationListener.onLogIn(2, auth.currentUser?.uid.toString())
            }
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthException -> {
                    onOperationListener.onLogIn(1, auth.currentUser?.uid.toString())
                }
                is FirebaseNetworkException -> {
                    onOperationListener.onLogIn(3, auth.currentUser?.uid.toString())
                }
            }
        }
    }

    override fun performLogInWithGoogle(googleSignInAccount: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            googleSignInAccount.displayName?.let { it1 -> createAccountWithGoogle(it1) }
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthException -> {
                    onOperationListener.onLogIn(1, auth.currentUser?.uid.toString())
                }
                is FirebaseNetworkException -> {
                    onOperationListener.onLogIn(3, auth.currentUser?.uid.toString())
                }
            }
        }
    }

    private fun createAccountWithGoogle(name: String) {
        var nameExists = false
        // регистритуем пользователя в Firebase бд, если его ещё там нет
        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(users: DataSnapshot) {
                for (user in users.children) {
                    if (user.child("login").value.toString().equals(name)) {
                        nameExists = true
                        break
                    }
                }
                if (!nameExists) {
                    // adding user to UsersDatabase at Firebase
                    val currentUid = auth.currentUser?.uid
                    val firebaseDatabase =
                        currentUid?.let { cur_uid ->
                            FirebaseDatabase.getInstance().reference.child("Users")
                                .child(cur_uid)
                        }
                    val userMap = HashMap<String, String>()
                    userMap["uid"] = currentUid.toString()
                    userMap["login"] = name

                    firebaseDatabase?.setValue(userMap)?.addOnSuccessListener {
                        onOperationListener.onLogIn(0, auth.currentUser?.uid.toString())
                    }
                } else {
                    onOperationListener.onLogIn(0, auth.currentUser?.uid.toString())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun performUserDownLoad(uid: String) {
        this.userDatabase.child(uid)
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
        this.userDatabase.child(uid).child("socialGoals")
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
}
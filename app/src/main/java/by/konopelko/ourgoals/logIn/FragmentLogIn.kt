package by.konopelko.ourgoals.logIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.ActivityMain
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.Task
import by.konopelko.ourgoals.database.User
import by.konopelko.ourgoals.guide.ActivityGuide
import by.konopelko.ourgoals.temporaryData.CurrentSession
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.GoalCollection
import by.konopelko.ourgoals.temporaryData.SocialGoalCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_log_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentLogIn : Fragment() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        logInButton.setOnClickListener {
            //fields check and auth check

            if (logInEmailField.text.toString().isNotEmpty()
                && logInPasswordField.text.toString().isNotEmpty()
            ) {
                signIn()
            } else if (logInEmailField.text.toString().isEmpty()) {
                logInEmailField.error = "Email cannot be empty"
            } else if (logInPasswordField.text.toString().isEmpty()) {
                logInPasswordField.error = "Password cannot be empty"
            }
        }
    }

    private fun signIn() {
        // подгрузить локальную базу пользователя, если такой пользователь уже входил ранее
        // если новый - создать под него новую базу.
        auth.signInWithEmailAndPassword(
            logInEmailField.text.toString(),
            logInPasswordField.text.toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                activity?.run {
                    // setting current user in Current Session
                    var user: User?
                    CoroutineScope(Dispatchers.IO).launch {
                        user = auth.currentUser?.uid?.let { id ->
                            DatabaseOperations.getInstance(this@run).getUserById(id).await()
                        }
                        if (user != null) {
                            CurrentSession.instance.currentUser = user as User
                            Log.e("CURRENT SESSION USER: ", CurrentSession.instance.currentUser.toString())

                            // обновить локальную бд целей пользователя
                            val currentUserId = CurrentSession.instance.currentUser.id

                            // подгружаем список личных целей из локальной бд
                            val goalsDatabase =
                                DatabaseOperations.getInstance(this@run).loadGoalsDatabase(currentUserId)
                                    .await()
                            GoalCollection.instance.getGoalsDatabase(goalsDatabase)

                            //TODO: загружать в коллекцию SocialGoalCollection список общих целей с СЕРВЕРА
                            //
                            loadSocialGoals(currentUserId)

                            runMainActivity()
                        } else {
                            // если авторизация успешна, но в локальной базе этого пользователя ещё нет,
                            // то есть пользователь зарегистрировался, а потом через какое-то время удалил приложение,
                            // снова установил его и хочет войти обратно, то:

                            // загрузить пользователя с сервера по uid.

                            val uid = auth.currentUser!!.uid

                            userDatabase.child(uid).addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onDataChange(currentUser: DataSnapshot) {
                                    val login = currentUser.child("login").value.toString()
                                    val newUser = User(uid, login, ArrayList())
//                                    Log.e("NEW USER: ", newUser.toString())

                                    // задаём пользователя текущей сессии.
                                    CurrentSession.instance.currentUser = newUser
                                    Log.e("CURRENT SESSION USER: ", CurrentSession.instance.currentUser.toString())

                                    // добавляем пользователя в локальную бд.
                                    CoroutineScope(Dispatchers.IO).launch {
                                        DatabaseOperations.getInstance(this@run)
                                            .addUsertoDatabase(newUser)
                                            .await()

                                        // задаём локальную бд целей пользователя
                                        val currentUserId = CurrentSession.instance.currentUser.id
                                        val goalsDatabase =
                                            DatabaseOperations.getInstance(this@run).loadGoalsDatabase(currentUserId)
                                                .await()
                                        GoalCollection.instance.getGoalsDatabase(goalsDatabase)
                                    }

                                    //TODO: загружать в коллекцию SocialGoalCollection список общих целей с СЕРВЕРА
                                    //

                                    runMainActivity()
                                }
                                override fun onCancelled(p0: DatabaseError) {
                                }
                            })
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@run, "Вход выполнен!", Toast.LENGTH_SHORT).show()
                        }
                        // on Job complete
                    }
                }
            } else {
                activity?.run {
                    Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun loadSocialGoals(currentUserId: String) {
        userDatabase.child(currentUserId).child("socialGoals")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(socialGoals: DataSnapshot) {
                    for (goal in socialGoals.children) {

                        val tasks = ArrayList<Task>()
                        for (taskSnapshot in goal.child("tasks").children) {
                            val task = Task(
                                taskSnapshot.child("text").value.toString(),
                                taskSnapshot.child("finishDate").value.toString(),
                                false
                            )

                            tasks.add(task)
                        }

                        val newGoal = Goal(
                            goal.child("ownerId").value.toString(),
                            goal.child("category").value.toString(),
                            goal.child("text").value.toString(),
                            goal.child("progress").value.toString().toInt(),
                            tasks,
                            goal.child("done").value.toString().toBoolean(),
                            goal.child("social").value.toString().toBoolean()
                        )

                        SocialGoalCollection.instance.goalList.add(newGoal)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })

    }

    fun runMainActivity() {
        activity?.run {
            if (CurrentSession.instance.firstTimeRun) {
                startActivity(Intent(this, ActivityGuide::class.java))
                CurrentSession.instance.firstTimeRun = false
            } else {
                startActivity(Intent(this, ActivityMain::class.java))
            }
        }
    }

}
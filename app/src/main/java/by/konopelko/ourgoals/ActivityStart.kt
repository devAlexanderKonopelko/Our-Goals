package by.konopelko.ourgoals

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.authentication.ActivityLogIn
import by.konopelko.ourgoals.temporaryData.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*

const val PREFS_NAME = "shared-prefs"
const val PREFS_VERSION_CODE_KEY = "VERSION_CODE"
const val PREFS_CODE_DOESNT_EXIST = -1

class ActivityStart : AppCompatActivity() {
    private val currentVersionCode = BuildConfig.VERSION_CODE

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedVersionCode = prefs.getInt(PREFS_VERSION_CODE_KEY, PREFS_CODE_DOESNT_EXIST)

        CoroutineScope(Dispatchers.IO).launch {
            createGuest()
            setCurrentUser()

            when {
                currentVersionCode == savedVersionCode -> {
                    // Не первый запуск
                    CurrentSession.instance.firstTimeRun = false
                    if (auth.currentUser != null) {
                        if (auth.currentUser!!.isEmailVerified) {
                            waitAndTransitToMain()
                        }
                        else waitAndTransitToLogIn()
                    } else {
                        waitAndTransitToMain()
                    }
                }
                savedVersionCode == PREFS_CODE_DOESNT_EXIST -> {
                    // Первый запуск/очищены prefs

                    auth.signOut() // зачем?

                    CurrentSession.instance.firstTimeRun = true
                    waitAndTransitToLogIn()
                }
                currentVersionCode > savedVersionCode -> {
                    // TODO This is an application upgrade
                }
            }

//        В конце надо записывать в prefs текущую версию, чтобы сохранилась информация о версии.
//        Иначе постоянно будет первый запуск.
            prefs.edit().putInt(PREFS_VERSION_CODE_KEY, currentVersionCode).apply()
        }
    }

    private suspend fun createGuest() {
        val databaseSize = DatabaseOperations.getInstance(this).getUsersDatabaseSize().await()
        Log.e("USER_DATABASE_SIZE: ", databaseSize.toString())
        if (databaseSize == 0) {
            val guest = User(
                "0",
                getString(R.string.username_guest),
                ArrayList()
            )
            DatabaseOperations.getInstance(this).addUsertoDatabase(guest).await()

            val list = ArrayList<String>()
            list.addAll(resources.getStringArray(R.array.default_categories_titles))

            // загружаем в бд стандартные категории (со всеми разделами) для гостя
            DatabaseOperations.getInstance(this).setDefaultCategoriesList(guest.id, list).await()

            CategoryCollection.instance.setDefaultCategories(guest.id, list)

            Log.e("DEFAULT CATEGORIES: ", " LOADED: ${CategoryCollection.instance.categoryList}")

            // задаём изначальную аналитику
            DatabaseOperations.getInstance(this).setDefaultAnalytics(guest.id).await()
        }
    }

    // setting current session user
    private suspend fun setCurrentUser() {
        if (auth.currentUser != null) {
            val user =
                DatabaseOperations.getInstance(this).getUserById(auth.currentUser!!.uid).await()
            CurrentSession.instance.currentUser = user

            Log.e("CURRENT USER NAME: ", CurrentSession.instance.currentUser.name)
            Log.e("CURRENT USER ID: ", CurrentSession.instance.currentUser.id)
        }
    }

    private suspend fun waitAndTransitToMain() {
        val currentUserId = CurrentSession.instance.currentUser.id

        CategoryCollection.instance.categoryList.clear()
        // загружаем список категорий
        val categories =
            DatabaseOperations.getInstance(this).getCategoriesByUserId(currentUserId).await()
        CategoryCollection.instance.categoryList.addAll(categories)
        Log.e("----CATEGORIES----", " LOADED: ${CategoryCollection.instance.categoryList}")

        // загружаем список личных целей из локальной бд
        val goalsDatabase =
            DatabaseOperations.getInstance(this@ActivityStart).loadGoalsDatabase(currentUserId)
                .await()
        loadSocialGoals(currentUserId)

        val analytics = DatabaseOperations.getInstance(this).loadAnalytics(currentUserId).await()
        AnalyticsSingleton.instance.analytics = analytics

        // вместо delay сделать нормально
        delay(2000)

        Log.e("GOAL DATABASE SIZE: ", goalsDatabase.size.toString())
        GoalCollection.instance.goalsInProgressList.clear()
        GoalCollection.instance.setGoalsInProgress(goalsDatabase)
        GoalCollection.instance.visible = true
        Log.e("GOAL LOCAL SIZE:", GoalCollection.instance.goalsInProgressList.size.toString())
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityStart, ActivityMain::class.java))
        }
    }

    private suspend fun waitAndTransitToLogIn() {
        val currentUserId = CurrentSession.instance.currentUser.id
        Log.e("CURRENT SESSION UID: ", currentUserId)

        // вместо delay сделать нормально
        delay(2000)

        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityStart, ActivityLogIn::class.java))
        }
    }

    private fun loadSocialGoals(currentUserId: String) {
        SocialGoalCollection.instance.goalList.clear()

        userDatabase.child(currentUserId).child("socialGoals")
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
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

}

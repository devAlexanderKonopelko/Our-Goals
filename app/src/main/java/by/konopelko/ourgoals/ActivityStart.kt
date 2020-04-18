package by.konopelko.ourgoals

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import by.konopelko.ourgoals.database.User
import by.konopelko.ourgoals.logIn.ActivityLogIn
import by.konopelko.ourgoals.temporaryData.CurrentSession
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.GoalCollection
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

const val PREFS_NAME = "shared-prefs"
const val PREFS_VERSION_CODE_KEY = "VERSION_CODE"
const val PREFS_CODE_DOESNT_EXIST = -1

class ActivityStart : AppCompatActivity() {
    val currentVersionCode = BuildConfig.VERSION_CODE

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                    waitAndTransitToMain()
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
            val guest = User("0", "Гость", ArrayList())
            DatabaseOperations.getInstance(this).addUsertoDatabase(guest).await()
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
        val goalsDatabase =
            DatabaseOperations.getInstance(this@ActivityStart).loadGoalsDatabase(currentUserId)
                .await()
        delay(2000)
        Log.e("GOAL DATABASE SIZE: ", goalsDatabase.size.toString())
        GoalCollection.instance.getGoalsDatabase(goalsDatabase)
        Log.e("GOAL LOCAL SIZE:", GoalCollection.instance.goalsList.size.toString())
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityStart, ActivityMain::class.java))
        }
    }

    private suspend fun waitAndTransitToLogIn() {
        val currentUserId = CurrentSession.instance.currentUser.id
        Log.e("CURRENT SESSION UID: ", currentUserId)

        delay(2000)
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityStart, ActivityLogIn::class.java))
        }
    }
}

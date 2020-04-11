package by.konopelko.ourgoals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.konopelko.ourgoals.logIn.ActivityLogIn
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.GoalCollection
import kotlinx.coroutines.*

class ActivityStart : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        CoroutineScope(Dispatchers.IO).launch {
            waitAndTransitToLogIn()
        }
    }

    private suspend fun waitAndTransitToLogIn() {
        val database = DatabaseOperations.getInstance(this).loadDatabase().await()
        GoalCollection.instance.getDatabase(database)
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityStart, ActivityLogIn::class.java))
        }
    }
}

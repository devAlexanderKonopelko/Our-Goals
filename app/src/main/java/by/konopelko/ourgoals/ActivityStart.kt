package by.konopelko.ourgoals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.konopelko.ourgoals.logIn.ActivityLogIn
import kotlinx.coroutines.*

class ActivityStart : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        CoroutineScope(Dispatchers.IO).launch {
            waitAndTransitToLogIn()
        }
    }

    suspend fun waitAndTransitToLogIn() {
        delay(3000)
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityStart, ActivityLogIn::class.java))
        }
    }
}

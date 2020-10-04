package by.konopelko.ourgoals.view.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import by.konopelko.ourgoals.view.home.ActivityMain
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.view.guide.ActivityGuide
import by.konopelko.ourgoals.presenter.signin.LogInPresenterDefault
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.coroutines.*

class ActivityLogIn : AppCompatActivity(),
    LogInGeneralView, View.OnClickListener {
    private val logInFragment =
        FragmentLogIn()
    private val registerFragment =
        FragmentRegister()
    private val presenter =
        LogInPresenterDefault(
            this
        )

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        val model = ViewModelProvider(this).get(ViewModelLogIn::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        // when activity created user first sees logInFragment
        supportFragmentManager.beginTransaction().replace(logInFragmentLayout.id, logInFragment)
            .commit()
        model.activeFragment = model.LOG_IN_FRAGMENT

        registerButton.setOnClickListener(this)
        guestButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val model = ViewModelProvider(this).get(ViewModelLogIn::class.java)
        when (v?.id) {
            registerButton.id -> { // if user wants to register
                // inflating registerFragment
                supportFragmentManager.beginTransaction()
                    .replace(logInFragmentLayout.id, registerFragment).commit()
                // changing buttons visibility
                registerButton.visibility = View.GONE
                guestButton.visibility = View.GONE
                model.activeFragment = model.REGISTER_FRAGMENT
            }
            guestButton.id -> { // if user enters as a guest
                auth.signInAnonymously()
                CoroutineScope(Dispatchers.IO).launch {
                    loadCurrentUserData("0") // NEW устанавливает текущего пользователя Гость,
                                                    // и загружает категории, личные цели и аналитику Гостя
                    checkAndRunActivity() // NEW проверяет, первый ли это запуск и запускает нужную Activity
                }
            }
        }
    }

    private suspend fun checkAndRunActivity() {
        presenter.onCurrentSessionRunChecked()
    }

    private suspend fun loadCurrentUserData(uid: String): Boolean {
        return presenter.onCurrentUserDataLoaded(uid, this)
    }

    override fun onBackPressed() {
        val model = ViewModelProvider(this).get(ViewModelLogIn::class.java)

        if (model.activeFragment == model.REGISTER_FRAGMENT) {
            supportFragmentManager.beginTransaction().replace(logInFragmentLayout.id, logInFragment)
                .commit()

            model.activeFragment = model.LOG_IN_FRAGMENT
            registerButton.visibility = View.VISIBLE
            guestButton.visibility = View.VISIBLE
        } else {
            finishAffinity()
        }
    }

    override suspend fun startGuideActivity() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityLogIn, ActivityGuide::class.java))
        }
    }

    override suspend fun startMainActivity() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@ActivityLogIn, ActivityMain::class.java))
        }
    }
}

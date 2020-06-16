package by.konopelko.ourgoals.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import by.konopelko.ourgoals.ActivityMain
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.guide.ActivityGuide
import by.konopelko.ourgoals.temporaryData.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.coroutines.*

class ActivityLogIn : AppCompatActivity(), View.OnClickListener {
    private val logInFragment = FragmentLogIn()
    private val registerFragment = FragmentRegister()

    val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        val model = ViewModelProvider(this).get(ViewModelLogIn::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportFragmentManager.beginTransaction().replace(logInFragmentLayout.id, logInFragment)
            .commit()
        model.activeFragment = model.LOG_IN_FRAGMENT

        registerButton.setOnClickListener(this)
        guestButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val model = ViewModelProvider(this).get(ViewModelLogIn::class.java)
        when (v?.id) {
            registerButton.id -> {
                // if user wants to register
                supportFragmentManager.beginTransaction()
                    .replace(logInFragmentLayout.id, registerFragment).commit()

                registerButton.visibility = View.GONE
                guestButton.visibility = View.GONE
                model.activeFragment = model.REGISTER_FRAGMENT
            }
            guestButton.id -> {
                // if user enters as a guest
                auth.signInAnonymously()
                CurrentSession.instance.currentUser = User("0", getString(R.string.username_guest), ArrayList())

                CoroutineScope(Dispatchers.IO).launch {
                    loadUsersCategories("0")
                    loadUsersPersonalGoals("0")
                    loadUsersAnalytics("0")

                    withContext(Dispatchers.Main) {
                        if (CurrentSession.instance.firstTimeRun) {
                            startActivity(Intent(this@ActivityLogIn, ActivityGuide::class.java))
                            CurrentSession.instance.firstTimeRun = false
                        } else {
                            startActivity(Intent(this@ActivityLogIn, ActivityMain::class.java))
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadUsersAnalytics(id: String) {
        AnalyticsSingleton.instance.analytics = CoroutineScope(Dispatchers.IO).async {
            DatabaseOperations.getInstance(this@ActivityLogIn).loadAnalytics(id).await()
        }.await()
    }

    private suspend fun loadUsersCategories(id: String): Boolean {
        // очищаем локальную коллекцию пользовательских категорий
        CategoryCollection.instance.categoryList.clear()

        val categoriesList = CoroutineScope(Dispatchers.IO).async {
            DatabaseOperations.getInstance(this@ActivityLogIn).getCategoriesByUserId("0")
                .await()
        }.await()

        CategoryCollection.instance.categoryList.addAll(categoriesList)

        if (categoriesList != null) {
            Log.e("INSIDE", "loadUsersCategories(): FINISH $categoriesList")

            return true
        } else return false
    }

    private suspend fun loadUsersPersonalGoals(uid: String): Boolean {
        Log.e("INSIDE", "loadUsersPersonalGoals()")

        // подгружаем список личных целей из локальной бд
        val goalsDatabase = CoroutineScope(Dispatchers.IO).async {
            DatabaseOperations.getInstance(this@ActivityLogIn)
                .loadGoalsDatabase(uid)
                .await()
        }.await()

        // отчищается коллекция для хранения списка из бд
        GoalCollection.instance.goalsInProgressList.clear()

        if (goalsDatabase != null) {
            GoalCollection.instance.setGoalsInProgress(goalsDatabase)
            GoalCollection.instance.visible = true
            Log.e("INSIDE", "loadUsersPersonalGoals(): FINISH $goalsDatabase")

            return true
        } else return false
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
}

package by.konopelko.ourgoals.ui.startscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import by.konopelko.ourgoals.ActivityMain
import by.konopelko.ourgoals.BuildConfig
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.authentication.ActivityLogIn
import by.konopelko.ourgoals.mvp.startscreen.StartScreenPresenterDefault
import by.konopelko.ourgoals.mvp.startscreen.StartScreenView
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

class ActivityStart : AppCompatActivity(), StartScreenView {
    private val presenter = StartScreenPresenterDefault(this, this)
    private val currentVersionCode =
        BuildConfig.VERSION_CODE

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedVersionCode = prefs.getInt(
            PREFS_VERSION_CODE_KEY,
            PREFS_CODE_DOESNT_EXIST
        )

        CoroutineScope(Dispatchers.IO).launch {
            loadDatabaseInstance() // загрузка ссылки на БД NEW

//            createGuest() // OLD
//            setCurrentUser() // OLD

            when {
                // Не первый запуск
                savedVersionCode == currentVersionCode -> {
                    CurrentSession.instance.firstTimeRun = false
                    if (auth.currentUser != null) {
                        if (auth.currentUser!!.isEmailVerified) {
                            loadCurrentUserData() // NEW загрузка данных текущего пользователя
                            transitToMainScreen() // NEW переход к MainActivity

                            waitAndTransitToMain() // OLD

                        } else waitAndTransitToLogIn()
                    } else {
                        waitAndTransitToMain()
                    }
                }
                // Первый запуск/очищены prefs
                savedVersionCode == PREFS_CODE_DOESNT_EXIST -> {
                    loadUserGuestData() // загрузка данных Гостя NEW
                    loadCurrentUserData() // загрузка данных текущего пользователя NEW

                    CurrentSession.instance.firstTimeRun = true
                    waitAndTransitToLogIn()
                }
                currentVersionCode > savedVersionCode -> {
                    // TODO Обновление приложения
                }
            }

//        В конце надо записывать в prefs текущую версию, чтобы сохранилась информация о версии.
//        Иначе постоянно будет первый запуск.
            prefs.edit().putInt(PREFS_VERSION_CODE_KEY, currentVersionCode).apply()
        }
    }

    private fun loadDatabaseInstance(): Boolean {
        return presenter.onDatabaseInstanceLoaded(this)
    }

    private suspend fun loadCurrentUserData(): Boolean {
        var result = false
        auth.currentUser?.let { user ->
            result = presenter.onCurrentUserDataLoaded(user.uid, this) // загружаем данные текущего пользователя
        }
        return result
    }

    private suspend fun loadUserGuestData(): Boolean {
        val guestId = getString(R.string.guestId)
        val guestExist = presenter.onGuestUserExistenceChecked(guestId, this)
        var guestCreated = false
        return if (guestExist) {
            guestExist
        } else {
            // создание пользователя Гость в БД
            guestCreated =
                presenter.onGuestUserCreated(guestId, getString(R.string.username_guest), this)
            if (guestCreated) {
                // создание стандартных категорий для Гостя в БД
                val titles = ArrayList<String>()
                titles.addAll(resources.getStringArray(R.array.default_categories_titles))
                guestCreated = presenter.onDefaultCategoriesCreated(guestId, titles, this)
            }
            // создание аналитики Гостя в БД
            if (guestCreated) guestCreated = presenter.onDefaultAnalyticsCreated(guestId, this)
            guestCreated
        }
    }

    private suspend fun waitAndTransitToMain() {
        val currentUserId = CurrentSession.instance.currentUser.id

        // загружаем категории
        loadUsersCategories(currentUserId)

        // загружаем личные цели
        loadPersonalGoals(currentUserId)

        // загружаем командные цели
        loadSocialGoals(currentUserId)

        // загружаем аналитику
        loadAnalytics(currentUserId)

        // вместо delay сделать через последовательно через mvp
        delay(2000)

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

    private suspend fun createGuest() {
        if (!checkGuestExistence()) {
            val guest = User(
                "0",
                getString(R.string.username_guest),
                ArrayList()
            )
            DatabaseOperations.getInstance(this).addUsertoDatabase(guest).await()

            setDefaultCategories(guest.id)
            setDefaultAnalytics(guest.id)
        }
    }

    private suspend fun setDefaultAnalytics(guestId: String) {
        // задаём изначальную аналитику
        DatabaseOperations.getInstance(this).setDefaultAnalytics(guestId).await()
    }

    private suspend fun setDefaultCategories(guestId: String) {
        val list = ArrayList<String>()
        list.addAll(resources.getStringArray(R.array.default_categories_titles))

        // загружаем в бд стандартные категории (со всеми разделами) для гостя
        DatabaseOperations.getInstance(this).setDefaultCategoriesList(guestId, list).await()

        // добавляем стандартные категории в коллекцию
        CategoryCollection.instance.setDefaultCategories(guestId, list)

        Log.e("DEFAULT CATEGORIES: ", " LOADED: ${CategoryCollection.instance.categoryList}")
    }

    private suspend fun checkGuestExistence(): Boolean {
        val databaseSize = DatabaseOperations.getInstance(this).getUsersDatabaseSize().await()
        Log.e("USER_DATABASE_SIZE: ", databaseSize.toString())
        return databaseSize != 0
    }

    // setting current session user
    private suspend fun setCurrentUser() {
        if (auth.currentUser != null) {
            // определяем текущего пользователя
            val user =
                DatabaseOperations.getInstance(this).getUserById(auth.currentUser!!.uid).await()
            CurrentSession.instance.currentUser = user

            Log.e("CURRENT USER NAME: ", CurrentSession.instance.currentUser.name)
            Log.e("CURRENT USER ID: ", CurrentSession.instance.currentUser.id)
        }
    }

    private suspend fun loadAnalytics(uid: String) {
        val analytics = DatabaseOperations.getInstance(this).loadAnalytics(uid).await()
        AnalyticsSingleton.instance.analytics = analytics
    }

    private suspend fun loadPersonalGoals(uid: String) {
        // загружаем список личных целей из локальной бд
        val goalsDatabase =
            DatabaseOperations.getInstance(this@ActivityStart).loadGoalsDatabase(uid)
                .await()
        Log.e("GOAL DATABASE SIZE: ", goalsDatabase.size.toString())

        // загружаем цели в коллекцию
        GoalCollection.instance.goalsInProgressList.clear()
        GoalCollection.instance.setGoalsInProgress(goalsDatabase)
        GoalCollection.instance.visible = true
        Log.e("GOAL LOCAL SIZE:", GoalCollection.instance.goalsInProgressList.size.toString())
    }

    private suspend fun loadUsersCategories(uid: String) {
        CategoryCollection.instance.categoryList.clear()
        // загружаем список категорий
        val categories =
            DatabaseOperations.getInstance(this).getCategoriesByUserId(uid).await()
        CategoryCollection.instance.categoryList.addAll(categories)
        Log.e("----CATEGORIES----", " LOADED: ${CategoryCollection.instance.categoryList}")
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

    override fun transitToMainScreen() {
        // start activity Main
    }

    override fun transitToSignInScreen() {
        // start activity LogIn
    }

}

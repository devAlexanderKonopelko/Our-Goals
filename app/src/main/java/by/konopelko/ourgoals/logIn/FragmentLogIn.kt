package by.konopelko.ourgoals.logIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.ActivityMain
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.User
import by.konopelko.ourgoals.guide.ActivityGuide
import by.konopelko.ourgoals.logIn.core.LogInContract
import by.konopelko.ourgoals.logIn.core.LogInPresenter
import by.konopelko.ourgoals.temporaryData.*
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_log_in.*
import kotlinx.coroutines.*

class FragmentLogIn : Fragment(), LogInContract.View {

    private val USER_FRIENDS_NOTIFICATIONS_LOADED = "USER_FRIENDS_NOTIFICATIONS_LOADED"
    private val USER_GOALS_NOTIFICATIONS_LOADED = "USER_GOALS_NOTIFICATIONS_LOADED"
    private val USER_ALL_NOTIFICATIONS_LOADED = "USER_ALL_NOTIFICATIONS_LOADED"
    private val USER_GOALS_LOADED = "USER_GOALS_LOADED"
    private val USER_PROFILE_LOADED = "USER_PROFILE_LOADED"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: User? = null
    private val presenter = LogInPresenter(this)

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

                logInProgressBar.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    presenter.logIn(
                        logInEmailField.text.toString(),
                        logInPasswordField.text.toString()
                    )
                }
            } else if (logInEmailField.text.toString().isEmpty()) {
                logInEmailField.error = "Email cannot be empty"
            } else if (logInPasswordField.text.toString().isEmpty()) {
                logInPasswordField.error = "Password cannot be empty"
            }
        }
    }

    private suspend fun checkUserInDatabase(uid: String): User? {
        Log.e("INSIDE", "getUserFromDatabase()")

        val user = CoroutineScope(Dispatchers.IO).async {
            activity?.run {
                DatabaseOperations.getInstance(this@run).getUserById(uid).await()
            }
        }.await()

        Log.e("INSIDE", "getUserFromDatabase(): RETURN $user")
        return user
    }

    private fun setCurrentSessionUser(user: User) {
        Log.e("INSIDE", "setCurrentSessionUser()")

        CurrentSession.instance.currentUser = user

        Log.e("INSIDE", "setCurrentSessionUser(): FINISH ${CurrentSession.instance.currentUser}")

    }

    private suspend fun loadUsersPersonalGoals(uid: String): Boolean {
        Log.e("INSIDE", "loadUsersPersonalGoals()")

        // подгружаем список личных целей из локальной бд
        val goalsDatabase = CoroutineScope(Dispatchers.IO).async {
            activity?.run {
                DatabaseOperations.getInstance(this@run)
                    .loadGoalsDatabase(uid)
                    .await()
            }
        }.await()

        if (goalsDatabase != null) {
            GoalCollection.instance.getGoalsDatabase(goalsDatabase)
            Log.e("INSIDE", "loadUsersPersonalGoals(): FINISH $goalsDatabase")

            return true
        }
        else return false
    }

    private suspend fun addUserToDatabase(user: User?): Boolean {
        Log.e("INSIDE", "addUserToDatabase()")

        // добавляем пользователя в локальную бд.
        val result = CoroutineScope(Dispatchers.IO).async {
            activity?.run {
                if (user != null) {
                    DatabaseOperations.getInstance(this@run)
                        .addUsertoDatabase(user)
                        .await()

                    Log.e("INSIDE", "addUserToDatabase(): FINISH")
                }
            }

            true
        }.await()

        return result
    }

    private fun signIn(): com.google.android.gms.tasks.Task<AuthResult> {
        Log.e("INSIDE", "signIn()")

        val result = auth.signInWithEmailAndPassword(
            logInEmailField.text.toString(),
            logInPasswordField.text.toString()
        )

        Log.e("INSIDE", "signIn(): RETURN")
        return result
    }

    private fun runMainActivity() {
        Log.e("INSIDE", "runMainActivity()")
        logInProgressBar.visibility = View.INVISIBLE

        activity?.run {
            if (CurrentSession.instance.firstTimeRun) {
                startActivity(Intent(this, ActivityGuide::class.java))
                CurrentSession.instance.firstTimeRun = false
            } else {
                startActivity(Intent(this, ActivityMain::class.java))
            }
        }
    }

    override fun onLogIn(result: Boolean, uid: String) {
        Log.e("SIGN_IN:", "$result, $uid")

        CoroutineScope(Dispatchers.IO).launch {
            currentUser = withContext(coroutineContext) {
                checkUserInDatabase(uid)
            }
            if (currentUser == null) {
                Log.e("USER", "$currentUser. Loading from server...")
                presenter.loadUserFromServer(uid) // load user from server
            }
            else {
                proceedLoadingUsersData(USER_PROFILE_LOADED)
            }
        }
    }

    override fun onUserLoadedFromServer(user: User) {
        Log.e("USER LOADED:", "$user")
        currentUser = user

        CoroutineScope(Dispatchers.IO).launch {
            // add user to database
            addUserToDatabase(currentUser)
            proceedLoadingUsersData(USER_PROFILE_LOADED)
        }
    }

    override fun onSocialGoalsLoaded(result: Boolean) {
        Log.e("SOCIAL GOALS LOADED:", "$result")
        proceedLoadingUsersData(USER_GOALS_LOADED)
    }

    override fun onFriendsNotificationsLoaded(result: Boolean) {
        Log.e("NOTIFICATIONS LOADED:", "FRIENDS: $result")
        proceedLoadingUsersData(USER_FRIENDS_NOTIFICATIONS_LOADED)
    }

    override fun onGoalsNotificationsLoaded(result: Boolean) {
        Log.e("NOTIFICATIONS LOADED:", "GOALS: $result")
        proceedLoadingUsersData(USER_GOALS_NOTIFICATIONS_LOADED)
    }

    private fun proceedLoadingUsersData(tag: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (tag.equals(USER_PROFILE_LOADED)) {
                currentUser?.let { setCurrentSessionUser(it) }
                currentUser?.id?.let { loadUsersPersonalGoals(it) }
                currentUser?.id?.let { presenter.loadSocialGoalsFromServer(it) }
            }
            if (tag.equals(USER_GOALS_LOADED)) {
                clearTempNotificationsList()
                currentUser?.id?.let { presenter.loadFriendsNotifications(it) }
            }
            if(tag.equals(USER_FRIENDS_NOTIFICATIONS_LOADED)) {
                currentUser?.id?.let { presenter.loadGoalsNotifications(it) }
            }
            if (tag.equals(USER_GOALS_NOTIFICATIONS_LOADED)) {
                runMainActivity()
            }
        }
    }

    private fun clearTempNotificationsList() {
        NotificationsCollection.instance.friendsRequests.clear()
        NotificationsCollection.instance.goalsRequests.clear()
        NotificationsCollection.instance.goalsRequestsSenders.clear()
        NotificationsCollection.instance.requestsKeys.clear()
    }

}
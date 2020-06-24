package by.konopelko.ourgoals.ui.authentication

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
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.guide.ActivityGuide
import by.konopelko.ourgoals.mvp.authentication.LogInContract
import by.konopelko.ourgoals.mvp.authentication.LogInPresenterDefault
import by.konopelko.ourgoals.temporaryData.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.fragment_log_in.*
import kotlinx.coroutines.*

class FragmentLogIn : Fragment(), LogInContract.View {
    private val USER_GOALS_LOADED = "USER_GOALS_LOADED"
    private val USER_PROFILE_LOADED = "USER_PROFILE_LOADED"

    private var currentUser: User? = null
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val SIGN_IN_CODE = 1
    private val presenter =
        LogInPresenterDefault(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        createGoogleRequest()
        signInButton.setOnClickListener {
            registerWithGoogle()
        }

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
                logInEmailField.error = getString(R.string.toast_enterEmail)
            } else if (logInPasswordField.text.toString().isEmpty()) {
                logInPasswordField.error = getString(R.string.toast_enterPassword)
            }
        }
    }

    private fun registerWithGoogle() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, SIGN_IN_CODE)
    }

    private fun createGoogleRequest() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        activity?.let {
            googleSignInClient = GoogleSignIn.getClient(it, googleSignInOptions)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleSignInAccount = task.getResult(ApiException::class.java)
                if (googleSignInAccount != null) {
                    logInProgressBar.visibility = View.VISIBLE
                    presenter.logInWithGoogle(googleSignInAccount)
                }
            } catch (e: ApiException) {
                Toast.makeText(this.context, "ERROR: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
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

        // отчищаем коллекцию, которая хранит список из бд
        GoalCollection.instance.goalsInProgressList.clear()
        if (goalsDatabase != null) {
            GoalCollection.instance.setGoalsInProgress(goalsDatabase)
            GoalCollection.instance.visible = true
            Log.e("INSIDE", "loadUsersPersonalGoals(): FINISH $goalsDatabase")

            return true
        } else return false
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

    private suspend fun runMainActivity() {
        Log.e("INSIDE", "runMainActivity()")
        withContext(Dispatchers.Main) {
            logInProgressBar.visibility = View.INVISIBLE
        }
        activity?.run {
            if (CurrentSession.instance.firstTimeRun) {
                startActivity(Intent(this, ActivityGuide::class.java))
                CurrentSession.instance.firstTimeRun = false
            } else {
                startActivity(Intent(this, ActivityMain::class.java))
            }
        }
    }

    override fun onLogIn(result: Int, uid: String) {
        Log.e("SIGN_IN:", "$result, $uid")

        if (result == 0) {
            CoroutineScope(Dispatchers.IO).launch {
                currentUser = withContext(coroutineContext) {
                    checkUserInDatabase(uid)
                }
                if (currentUser == null) {
                    Log.e("USER", "$currentUser. Loading from server...")
                    presenter.loadUserFromServer(uid) // loading users data from server
                } else {
                    proceedLoadingUsersData(USER_PROFILE_LOADED)
                }
            }
        } else if (result == 1) {
            logInProgressBar.visibility = View.GONE
            Toast.makeText(this.context, getString(R.string.Toast_accountNotFound), Toast.LENGTH_SHORT).show()
        } else if (result == 2) {
            logInProgressBar.visibility = View.GONE
            Toast.makeText(this.context, getString(R.string.toast_accountNotConfError), Toast.LENGTH_SHORT).show()
        }
        else if (result == 3) {
            logInProgressBar.visibility = View.GONE
            Toast.makeText(this.context, getString(R.string.toast_networkError), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onUserLoadedFromServer(user: User) {
        Log.e("USER LOADED:", "$user")
        currentUser = user

        CoroutineScope(Dispatchers.IO).launch {
            // add user to database
            addUserToDatabase(currentUser)

            // setting default categories to users local database
            setDefaultCategories(currentUser!!.id)

            // setting default analytics to user
            setDefaultAnalytcs(currentUser!!.id)

            proceedLoadingUsersData(USER_PROFILE_LOADED)
        }
    }

    private suspend fun setDefaultAnalytcs(id: String): Boolean {
        Log.e("INSIDE", "setDefaultAnalytcs()")

        val result = CoroutineScope(Dispatchers.IO).async {
            activity?.run {
                DatabaseOperations.getInstance(this).setDefaultAnalytics(id).await()
            }

            true
        }.await()

        return result
    }

    private suspend fun setDefaultCategories(id: String): Boolean {
        Log.e("INSIDE", "setDefaultCategories()")

        val list = ArrayList<String>()
        list.addAll(resources.getStringArray(R.array.default_categories_titles))

        val result = CoroutineScope(Dispatchers.IO).async {
            activity?.run {
                DatabaseOperations.getInstance(this)
                    .setDefaultCategoriesList(id, list)
                    .await()
                Log.e("INSIDE", "addUserToDatabase(): FINISH")
            }

            true
        }.await()

        return result
    }

    override fun onSocialGoalsLoaded(result: Boolean) {
        Log.e("SOCIAL GOALS LOADED:", "$result")
        proceedLoadingUsersData(USER_GOALS_LOADED)
    }

    private fun proceedLoadingUsersData(tag: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (tag.equals(USER_PROFILE_LOADED)) {
                currentUser?.let { setCurrentSessionUser(it) }
                currentUser?.id?.let { loadUsersCategories(it) }
                currentUser?.id?.let { loadUsersAnalytics(it) }
                currentUser?.id?.let { loadUsersPersonalGoals(it) }
                currentUser?.id?.let { presenter.loadSocialGoalsFromServer(it) }
            }
            if (tag.equals(USER_GOALS_LOADED)) {
                runMainActivity()
            }
        }
    }

    private suspend fun loadUsersAnalytics(id: String): Boolean {
        Log.e("INSIDE", "loadUsersAnalytics()")

        val analytics = CoroutineScope(Dispatchers.IO).async {
            activity?.run {
                DatabaseOperations.getInstance(this).loadAnalytics(id).await()
            }
        }.await()

        if (analytics != null) {
            AnalyticsSingleton.instance.analytics = analytics
            Log.e("INSIDE", "loadUsersAnalytics(): FINISH ${AnalyticsSingleton.instance.analytics}")

            return true
        } else return false
    }

    private suspend fun loadUsersCategories(userId: String): Boolean {
        Log.e("INSIDE", "loadUsersCategories()")

        // очищаем локальную коллекцию пользовательских категорий
        CategoryCollection.instance.categoryList.clear()

        // подгружаем список личных целей из локальной бд
        val categoriesList = CoroutineScope(Dispatchers.IO).async {
            activity?.run {
                DatabaseOperations.getInstance(this@run).getCategoriesByUserId(userId).await()
            }
        }.await()

        if (categoriesList != null) {
            CategoryCollection.instance.categoryList.addAll(categoriesList)
            Log.e("INSIDE", "loadUsersCategories(): FINISH $categoriesList")

            return true
        } else return false
    }

}
package by.konopelko.ourgoals.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.temporaryData.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentRegister : Fragment() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val SIGN_IN_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerFragmentButton.setOnClickListener {
            // fields check and auth check

            if (registerEmailField.text.toString() != ""
                && registerLoginField.text.toString() != ""
                && registerPasswordField.text.toString() != ""
                && registerConfirmPasswordField.text.toString() != ""
            ) {
                if (!registerPasswordField.text.toString().equals(registerConfirmPasswordField.text.toString())) {
                    Toast.makeText(
                        this.context,
                        getString(R.string.toast_differPasswords),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    createAccount()
                }
            } else {
                if (registerEmailField.text.toString().isEmpty()) {
                    registerEmailField.error = getString(R.string.toast_enterEmail)
                }
                if (registerLoginField.text.toString().isEmpty()) {
                    registerLoginField.error = getString(R.string.toast_enterUsername)
                }
                if (registerPasswordField.text.toString().isEmpty()) {
                    registerPasswordField.error = getString(R.string.toast_enterPassword)
                }
                if (registerConfirmPasswordField.text.toString().isEmpty()) {
                    registerConfirmPasswordField.error = getString(R.string.toast_confirmPassword)
                }
            }
        }
    }


    // создаёт аккаунт пользователя в Firebase бд и авторизирует пользователя
    private fun createAccount() {
        registerFragmentProgressBar.visibility = View.VISIBLE
        var nameExists = false
        usersDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(users: DataSnapshot) {
                for (user in users.children) {
                    if (user.child("login").value.toString().equals(registerLoginField.text.toString())) {
                        nameExists = true
                        break
                    }
                }
                if (nameExists) {
                    Toast.makeText(
                        this@FragmentRegister.context,
                        getString(R.string.toast_usernameExists),
                        Toast.LENGTH_LONG
                    ).show()
                    registerFragmentProgressBar.visibility = View.INVISIBLE
                    return
                } else {
                    auth.createUserWithEmailAndPassword(
                        registerEmailField.text.toString(),
                        registerPasswordField.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    // adding user to UsersDatabase at firebase
                                    val currentUid = auth.currentUser?.uid
                                    val firebaseDatabase =
                                        currentUid?.let { cur_uid ->
                                            FirebaseDatabase.getInstance().reference.child("Users")
                                                .child(
                                                    cur_uid
                                                )
                                        }
                                    val userMap = HashMap<String, String>()
                                    userMap.put("uid", currentUid.toString())
                                    userMap.put("login", registerLoginField.text.toString())

                                    firebaseDatabase?.setValue(userMap)?.addOnSuccessListener {
                                        activity?.run {
                                            // adding user to Local Database and Current Session
                                            val user = User(
                                                currentUid,
                                                registerLoginField.text.toString(),
                                                ArrayList()
                                            )

                                            Log.e("CURRENT USER ID", user.id)
                                            Log.e("CURRENT USER NAME", user.name)
                                            Log.e(
                                                "CURRENT USER FRIENDS_DB",
                                                user.friendsList.toString()
                                            )

                                            CurrentSession.instance.currentUser = user
                                            CoroutineScope(Dispatchers.IO).launch {
                                                // добавление пользователя в локальную бд
                                                DatabaseOperations.getInstance(this@run)
                                                    .addUsertoDatabase(user).await()

                                                // добавление дефолтных категорий для пользователя в локальную бд
                                                val list = ArrayList<String>()
                                                list.addAll(resources.getStringArray(R.array.default_categories_titles))

                                                DatabaseOperations.getInstance(this@run)
                                                    .setDefaultCategoriesList(currentUid, list).await()

                                                //создание изначальной статистики
                                                DatabaseOperations.getInstance(this@run)
                                                    .setDefaultAnalytics(currentUid).await()
                                                // загрузка "новоиспечённой" аналитики в локальную коллекцию
                                                AnalyticsSingleton.instance.analytics =
                                                    DatabaseOperations.getInstance(this@run)
                                                        .loadAnalytics(currentUid).await()
                                            }

                                            // очищаем локальную коллекцию пользовательских категорий
                                            CategoryCollection.instance.categoryList.clear()

                                            val list = ArrayList<String>()
                                            list.addAll(resources.getStringArray(R.array.default_categories_titles))
                                            // загружаем дефолтные категории в локальную коллекцию
                                            CategoryCollection.instance.setDefaultCategories(currentUid, list)

                                            GoalCollection.instance.visible = true

                                            view?.let {
                                                val snackbar = Snackbar.make(
                                                    view!!,
                                                    getString(R.string.toast_userRegestered),
                                                    Snackbar.LENGTH_INDEFINITE
                                                )
                                                val snackbarTextView =
                                                    snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                                                snackbarTextView.maxLines = 3
                                                snackbar.show()
                                                snackbar.setAction("OK") {
                                                    snackbar.dismiss()
                                                }
                                            }



                                            registerFragmentProgressBar.visibility = View.INVISIBLE
                                            val logInFragment = FragmentLogIn()
                                            activity?.run {
                                                val model =
                                                    ViewModelProvider(this).get(ViewModelLogIn::class.java)
                                                model.activeFragment = model.LOG_IN_FRAGMENT
                                                supportFragmentManager.beginTransaction()
                                                    .replace(logInFragmentLayout.id, logInFragment)
                                                    .commit()
                                                registerButton.visibility = View.VISIBLE
                                                guestButton.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            registerFragmentProgressBar.visibility = View.INVISIBLE
                            activity?.run {
                                Toast.makeText(this, getString(R.string.toast_registrError), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
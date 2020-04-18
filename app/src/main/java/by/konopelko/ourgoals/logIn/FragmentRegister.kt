package by.konopelko.ourgoals.logIn

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
import by.konopelko.ourgoals.database.User
import by.konopelko.ourgoals.guide.ActivityGuide
import by.konopelko.ourgoals.temporaryData.CurrentSession
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentRegister : Fragment() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                createAccount()
            }

        }

    }

    private fun createAccount() {
        auth.createUserWithEmailAndPassword(
            registerEmailField.text.toString(),
            registerPasswordField.text.toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                // adding user to UsersDatabase at firebase
                val currentUid = auth.currentUser?.uid
                val firebaseDatabase =
                    currentUid?.let { cur_uid ->
                        FirebaseDatabase.getInstance().reference.child("Users").child(
                            cur_uid
                        )
                    }
                val userMap = HashMap<String, String>()
                userMap.put("uid", currentUid.toString())
                userMap.put("login", registerLoginField.text.toString())

                firebaseDatabase?.setValue(userMap)?.addOnSuccessListener {
                    activity?.run {
                        // adding user to Local Database and Current Session
                        val user = User(currentUid, registerLoginField.text.toString(), ArrayList())

                        Log.e("CURRENT USER ID", user.id)
                        Log.e("CURRENT USER ID", user.name)
                        Log.e("CURRENT USER ID", user.friendsList.toString())

                        CurrentSession.instance.currentUser = user
                        CoroutineScope(Dispatchers.IO).launch {
                            DatabaseOperations.getInstance(this@run).addUsertoDatabase(user).await()
                        }

                        Toast.makeText(this, "Пользователь Зарегистрирован!", Toast.LENGTH_SHORT)
                            .show()

                        if (CurrentSession.instance.firstTimeRun) {
                            startActivity(Intent(this, ActivityGuide::class.java))
                            CurrentSession.instance.firstTimeRun = false
                        } else {
                            startActivity(Intent(this, ActivityMain::class.java))
                        }
                    }
                }
            } else {
                activity?.run {
                    Toast.makeText(this, "User not created!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
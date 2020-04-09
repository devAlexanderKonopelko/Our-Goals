package by.konopelko.ourgoals.logIn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.guide.ActivityGuide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_log_in.*

class FragmentLogIn: Fragment() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                signIn()
            }
            else if (logInEmailField.text.toString().isEmpty()) {
                logInEmailField.error = "Email cannot be empty"
            }
            else if (logInPasswordField.text.toString().isEmpty()) {
                logInPasswordField.error = "Password cannot be empty"
            }
        }
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(logInEmailField.text.toString(), logInPasswordField.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    activity?.run {
                        Toast.makeText(this, "User signed in!", Toast.LENGTH_SHORT).show()
                        //
                        startActivity(Intent(this, ActivityGuide::class.java))
                    }
                }
                else {
                    activity?.run {
                        Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}
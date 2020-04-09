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
import kotlinx.android.synthetic.main.fragment_register.*

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
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    activity?.run {
                        Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, ActivityGuide::class.java))
                    }
                } else {
                    activity?.run {
                        Toast.makeText(this, "User not created!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
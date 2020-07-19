package by.konopelko.ourgoals.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.mvp.authentication.view.LogInFragmentView
import by.konopelko.ourgoals.mvp.authentication.presenter.LogInPresenterDefault
import by.konopelko.ourgoals.mvp.authentication.view.RegisterFragmentView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentRegister : Fragment(),
    LogInFragmentView,
    RegisterFragmentView {
    private val presenter =
        LogInPresenterDefault(
            this,
            this
        )

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
                if (!registerPasswordField.text.toString()
                        .equals(registerConfirmPasswordField.text.toString())
                ) {
                    Toast.makeText(
                        this.context,
                        getString(R.string.toast_differPasswords),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    registerFragmentProgressBar.visibility = View.VISIBLE
                    val email = registerEmailField.text.toString()
                    val name = registerLoginField.text.toString()
                    val password = registerPasswordField.text.toString()
                    createAccount(email, password, name)
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
    private fun createAccount(email: String, password: String, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            context?.let { ctx ->
                presenter.onRegisteredWithEmailPassword(email, password, name, ctx)
            }
        }
    }

    override fun transitToLogInFragment() {
        view?.let {
            val snackbar = Snackbar.make(
                view!!,
                getString(R.string.toast_userRegestered),
                Snackbar.LENGTH_INDEFINITE)
            val snackbarTextView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            snackbarTextView.maxLines = 3
            snackbar.show()
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
        }

        registerFragmentProgressBar.visibility = View.GONE
        val logInFragment = FragmentLogIn()
        activity?.run {
            val model = ViewModelProvider(this).get(ViewModelLogIn::class.java)
            model.activeFragment = model.LOG_IN_FRAGMENT
            supportFragmentManager.beginTransaction()
                .replace(logInFragmentLayout.id, logInFragment)
                .commit()
            registerButton.visibility = View.VISIBLE
            guestButton.visibility = View.VISIBLE
        }
    }

    override fun showAuthenticationError() {
        registerFragmentProgressBar.visibility = View.GONE
        Toast.makeText(
            this.context,
            getString(R.string.toast_registrError),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun showInternetError() {
        registerFragmentProgressBar.visibility = View.GONE
        Toast.makeText(this.context, getString(R.string.toast_networkError), Toast.LENGTH_SHORT)
            .show()
    }

    override fun showGeneralError() {
        registerFragmentProgressBar.visibility = View.GONE
        Toast.makeText(
            this.context,
            getString(R.string.toast_registrError),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun showUserExistError() {
        registerFragmentProgressBar.visibility = View.GONE
        Toast.makeText(
            this.context,
            getString(R.string.toast_usernameExists),
            Toast.LENGTH_LONG
        ).show()
    }
}
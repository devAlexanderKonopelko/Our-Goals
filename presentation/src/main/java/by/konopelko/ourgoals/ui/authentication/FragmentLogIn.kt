package by.konopelko.ourgoals.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.ActivityMain
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.guide.ActivityGuide
import by.konopelko.ourgoals.mvp.authentication.LogInFragmentView
import by.konopelko.ourgoals.mvp.authentication.LogInPresenterDefault
import by.konopelko.ourgoals.mvp.authentication.LogInGeneralView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.fragment_log_in.*
import kotlinx.coroutines.*

class FragmentLogIn : Fragment(), LogInGeneralView, LogInFragmentView {
    private val presenter = LogInPresenterDefault(this, this)

//    private var currentUser: User? = null
    private val SIGN_IN_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        createGoogleRequest() // UPDATED
        signInButton.setOnClickListener { // Нажатие на кнопку Вход через гугл
            registerWithGoogle()
        }

        logInButton.setOnClickListener { // Нажатие на кнопку Вход (по email и паролю)
            if (logInEmailField.text.toString().isNotEmpty()
                && logInPasswordField.text.toString().isNotEmpty()
            ) {
                logInProgressBar.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    context?.let { ctx ->
                        presenter.logIn(
                            logInEmailField.text.toString(),
                            logInPasswordField.text.toString(),
                            ctx)
                    }
                }
            } else if (logInEmailField.text.toString().isEmpty()) {
                logInEmailField.error = getString(R.string.toast_enterEmail)
            } else if (logInPasswordField.text.toString().isEmpty()) {
                logInPasswordField.error = getString(R.string.toast_enterPassword)
            }
        }
    }

    private fun registerWithGoogle() {
        val intent = presenter.onRegisteredWithGoogle()
        startActivityForResult(intent, SIGN_IN_CODE)
    }

    private fun createGoogleRequest() {
        activity?.let { activity ->
            presenter.onGoogleRequestCreated(activity, getString(R.string.default_web_client_id))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleSignInAccount = task.getResult(ApiException::class.java)
                if (googleSignInAccount != null) {
                    logInProgressBar.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        context?.let { ctx ->
                            presenter.onLoggedInWithGoogle(googleSignInAccount, ctx)
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "ERROR: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showAuthenticationError() {
        logInProgressBar.visibility = View.GONE
        Toast.makeText(
            this.context,
            getString(R.string.Toast_accountNotFound),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showInternetError() {
        logInProgressBar.visibility = View.GONE
        Toast.makeText(this.context, getString(R.string.toast_networkError), Toast.LENGTH_SHORT)
            .show()
    }

    override fun showGeneralError() {
        logInProgressBar.visibility = View.GONE
        Toast.makeText(
            this.context,
            getString(R.string.toast_accountNotConfError),
            Toast.LENGTH_SHORT
        ).show()
    }

    override suspend fun startGuideActivity() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(activity, ActivityGuide::class.java))
        }
    }

    override suspend fun startMainActivity() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(activity, ActivityMain::class.java))
        }
    }

}
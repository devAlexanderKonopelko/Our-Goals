package by.konopelko.data.repositories

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import by.konopelko.data.local.GoogleAuthData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthRepositoryImpl {
    fun createGoogleRequest(activity: FragmentActivity, webClientId: String) {
        GoogleAuthData.instance.googleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        GoogleAuthData.instance.googleSignInClient = GoogleSignIn.getClient(activity, GoogleAuthData.instance.googleSignInOptions)
    }

    fun getGoogleAuthIntent(): Intent? {
        return GoogleAuthData.instance.googleSignInClient.signInIntent
    }

    fun getAuthCredential(googleSignInAccount: GoogleSignInAccount): AuthCredential {
        return GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
    }
}
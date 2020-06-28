package by.konopelko.domain.repositories

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import by.konopelko.data.repositories.GoogleAuthRepositoryImpl
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential

class GoogleAuthRepository {
    lateinit var googleAuthRepositoryImpl: GoogleAuthRepositoryImpl
    fun createGoogleRequest(activity: FragmentActivity, webClientId: String) {
        googleAuthRepositoryImpl = GoogleAuthRepositoryImpl()
        googleAuthRepositoryImpl.createGoogleRequest(activity, webClientId)
    }

    fun getGoogleAuthIntent(): Intent? {
        googleAuthRepositoryImpl = GoogleAuthRepositoryImpl()
        return googleAuthRepositoryImpl.getGoogleAuthIntent()
    }

    fun getAuthCredential(googleSignInAccount: GoogleSignInAccount): AuthCredential {
        googleAuthRepositoryImpl = GoogleAuthRepositoryImpl()
        return googleAuthRepositoryImpl.getAuthCredential(googleSignInAccount)
    }
}
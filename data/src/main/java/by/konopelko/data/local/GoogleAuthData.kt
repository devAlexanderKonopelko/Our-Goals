package by.konopelko.data.local

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleAuthData {
    lateinit var googleSignInOptions: GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient
    val SIGN_IN_CODE = 1

    companion object {
        val instance = GoogleAuthData()
    }
}
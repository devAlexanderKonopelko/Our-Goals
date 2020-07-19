package by.konopelko.domain.repositories

import by.konopelko.data.repositories.firebase.FirebaseAuthRepositoryImpl
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential

class FirebaseAuthRepository {
    lateinit var firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl

    suspend fun logInWithGoogle(credential: AuthCredential, googleSignInAccount: GoogleSignInAccount): Int  {
        firebaseAuthRepositoryImpl = FirebaseAuthRepositoryImpl()
        return firebaseAuthRepositoryImpl.logInWithGoogle(credential, googleSignInAccount)
    }

    suspend fun logInWithEmailPassword(email: String, password: String): Int {
        firebaseAuthRepositoryImpl = FirebaseAuthRepositoryImpl()
        return firebaseAuthRepositoryImpl.logInWithEmailPassword(email, password)
    }


    suspend fun registerWithEmailPassword(email: String, password: String, name: String): Int {
        firebaseAuthRepositoryImpl = FirebaseAuthRepositoryImpl()
        return firebaseAuthRepositoryImpl.registerWithEmailPassword(email, password, name)
    }
}
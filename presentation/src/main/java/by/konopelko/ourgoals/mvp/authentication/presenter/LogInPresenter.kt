package by.konopelko.ourgoals.mvp.authentication.presenter

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LogInPresenter {
    suspend fun onCurrentUserDataLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersCategoriesLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersPersonalGoalsLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersAnalyticsLoaded(uid: String, context: Context): Boolean
    suspend fun onCurrentSessionRunChecked()
    fun onGoogleRequestCreated(activity: FragmentActivity, webClientId: String)
    suspend fun onLoggedInWithGoogle(googleSignInAccount: GoogleSignInAccount, context: Context)
    suspend fun logIn(email: String, password: String, context: Context)
    fun onRegisteredWithGoogle(): Intent?
    suspend fun onRegisteredWithEmailPassword(email: String, password: String, name: String, context: Context)
}
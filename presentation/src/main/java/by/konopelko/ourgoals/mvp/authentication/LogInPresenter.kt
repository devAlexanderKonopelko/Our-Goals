package by.konopelko.ourgoals.mvp.authentication

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

    // OLD
    suspend fun logIn(email: String, password: String, context: Context)
//    fun loadUserFromServer(uid: String)
//    fun loadSocialGoalsFromServer(uid: String)
    fun onRegisteredWithGoogle(): Intent?
}
package by.konopelko.ourgoals.mvp.authentication

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LogInPresenter {
    suspend fun onCurrentUserDataLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersCategoriesLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersPersonalGoalsLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersAnalyticsLoaded(uid: String, context: Context): Boolean
    suspend fun onCurrentSessionRunChecked()
    fun onGoogleRequestCreated(activity: FragmentActivity)

    // OLD
    fun logIn(email: String, password: String)
    fun logInWithGoogle(googleSignInAccount: GoogleSignInAccount)
    fun loadUserFromServer(uid: String)
    fun loadSocialGoalsFromServer(uid: String)
}
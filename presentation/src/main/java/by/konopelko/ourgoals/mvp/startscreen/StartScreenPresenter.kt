package by.konopelko.ourgoals.mvp.startscreen

import android.content.Context

interface StartScreenPresenter {
    fun onDatabaseInstanceLoaded(context: Context): Boolean
    suspend fun onGuestUserExistenceChecked(uid: String, context: Context): Boolean
    suspend fun onGuestUserCreated(uid: String, name: String, context: Context): Boolean
    suspend fun onDefaultCategoriesCreated(uid: String, titles: ArrayList<String>, context: Context): Boolean
    suspend fun onDefaultAnalyticsCreated(uid: String, context: Context): Boolean
    suspend fun onCurrentUserDataLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersCategoriesLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersPersonalGoalsLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersSocialGoalsLoaded(uid: String, context: Context): Boolean
    suspend fun onUsersAnalyticsLoaded(uid: String, context: Context): Boolean
    fun onCurrentSessionRunSet(state: Boolean)
}
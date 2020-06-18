package by.konopelko.ourgoals.mvp.startscreen

import android.content.Context

interface StartScreenPresenter {
    fun onDatabaseInstanceLoaded(context: Context): Boolean
    suspend fun onGuestUserExistenceChecked(uid: String, context: Context): Boolean
    suspend fun onGuestUserCreated(uid: String, name: String, context: Context): Boolean
    suspend fun onDefaultCategoriesCreated(uid: String, titles: ArrayList<String> ,context: Context): Boolean
    fun onDefaultAnalyticsCreated(): Boolean
    fun onCurrentUserDataLoaded(): Boolean
    fun onUsersCategoriesLoaded(): Boolean
    fun onUsersPersonalGoalsLoaded(): Boolean
    fun onUsersSocialGoalsLoaded(): Boolean
    fun onUsersAnalyticsLoaded(): Boolean
}
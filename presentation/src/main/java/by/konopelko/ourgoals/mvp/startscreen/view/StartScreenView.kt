package by.konopelko.ourgoals.mvp.startscreen.view

interface StartScreenView {
    suspend fun transitToMainScreen()
    suspend fun transitToSignInScreen()
}
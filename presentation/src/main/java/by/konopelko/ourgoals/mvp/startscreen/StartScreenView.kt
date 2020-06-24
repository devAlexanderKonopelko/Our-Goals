package by.konopelko.ourgoals.mvp.startscreen

interface StartScreenView {
    suspend fun transitToMainScreen()
    suspend fun transitToSignInScreen()
}
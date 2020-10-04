package by.konopelko.ourgoals.view.splash

interface StartScreenView {
    suspend fun transitToMainScreen()
    suspend fun transitToSignInScreen()
}
package by.konopelko.ourgoals.view.splash

interface SplashView {
    suspend fun transitToMainScreen()
    suspend fun transitToSignInScreen()
}
package by.konopelko.ourgoals.view.splash

interface SplashView {
    fun transitToMainScreen(lastUserId: String?)
    fun transitToSignInScreen()
}
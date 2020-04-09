package by.konopelko.ourgoals.logIn

import androidx.lifecycle.ViewModel

class ViewModelLogIn: ViewModel() {
    val LOG_IN_FRAGMENT = "logInFragment"
    val REGISTER_FRAGMENT = "registerFragment"

    var activeFragment = ""
}
package by.konopelko.ourgoals.authentication

import androidx.lifecycle.ViewModel

class ViewModelLogIn: ViewModel() {
    val LOG_IN_FRAGMENT = "logInFragment"
    val REGISTER_FRAGMENT = "registerFragment"

    var activeFragment = ""
}
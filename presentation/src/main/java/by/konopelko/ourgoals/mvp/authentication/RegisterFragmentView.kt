package by.konopelko.ourgoals.mvp.authentication

interface RegisterFragmentView {
    fun transitToLogInFragment()
    fun showUserExistError()
}
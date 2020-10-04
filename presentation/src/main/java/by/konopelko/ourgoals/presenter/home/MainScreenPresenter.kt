package by.konopelko.ourgoals.presenter.home

interface MainScreenPresenter {
    fun getToolbarCategoryList(allCategoriesString: String): ArrayList<String>
    fun getToolbarCategory(position: Int): String
    fun observeNotifications()
}
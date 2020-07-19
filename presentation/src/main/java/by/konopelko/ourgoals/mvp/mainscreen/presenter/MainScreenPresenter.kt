package by.konopelko.ourgoals.mvp.mainscreen.presenter

interface MainScreenPresenter {
    fun getToolbarCategoryList(allCategoriesString: String): ArrayList<String>
    fun getToolbarCategory(position: Int): String
}
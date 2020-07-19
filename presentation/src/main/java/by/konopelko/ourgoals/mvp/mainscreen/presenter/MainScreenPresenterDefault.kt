package by.konopelko.ourgoals.mvp.mainscreen.presenter

import by.konopelko.domain.interactors.mainscreen.MainScreenInteractor
import by.konopelko.ourgoals.mvp.mainscreen.view.MainScreenView

class MainScreenPresenterDefault(private val view: MainScreenView): MainScreenPresenter {
    private val interactor = MainScreenInteractor()

    override fun getToolbarCategoryList(allCategoriesString: String): ArrayList<String> {
        return interactor.getToolbarCategoryList(allCategoriesString)
    }

    override fun getToolbarCategory(position: Int): String {
        return interactor.getToolbarCategory(position)
    }
}

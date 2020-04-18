package by.konopelko.ourgoals.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.categories.recyclerCategories.CategoryAdapter
import by.konopelko.ourgoals.temporaryData.CategoryCollection
import kotlinx.android.synthetic.main.fragment_categories.*

class FragmentCategories : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CategoryCollection.instance.addcAtegories()
        val categoryList = CategoryCollection.instance.categoryList

        categoriesRecyclerView.adapter = CategoryAdapter(categoryList)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this.context)
        categoriesRecyclerView.setHasFixedSize(true)
    }
}
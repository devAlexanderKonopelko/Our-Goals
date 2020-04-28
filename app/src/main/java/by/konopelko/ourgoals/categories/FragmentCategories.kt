package by.konopelko.ourgoals.categories

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.categories.recyclerCategories.CategoryAdapter
import by.konopelko.ourgoals.database.Category
import by.konopelko.ourgoals.temporaryData.CategoryCollection
import kotlinx.android.synthetic.main.fragment_categories.*
import kotlinx.coroutines.*


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

        CoroutineScope(Dispatchers.IO).launch {
            checkPermissions().await()
        }


        val categoryList = CategoryCollection.instance.categoryList

        categoriesRecyclerView.adapter = this.context?.let {ctx ->
            CategoryAdapter(categoryList, this, ctx)
        }
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this.context)
        categoriesRecyclerView.setHasFixedSize(true)
    }

    fun addCategory(category: Category) {
        (categoriesRecyclerView.adapter as CategoryAdapter).addCategory(category)
    }

    private suspend fun checkPermissions(): Deferred<Unit?> {
        val result = CoroutineScope(Dispatchers.IO).async {
            this@FragmentCategories.context?.let {
                if (ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    activity?.let { it1 ->
                        ActivityCompat.requestPermissions(
                            it1,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1
                        )
                    }
                }
            }

        }
        return result
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //

                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery

                }
            }
            else -> return
        }
    }

    fun updateCategory(category: Category, position: Int) {
        (categoriesRecyclerView.adapter as CategoryAdapter).updateCategory(category, position)
    }
}
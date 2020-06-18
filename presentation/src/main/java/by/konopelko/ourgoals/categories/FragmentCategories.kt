package by.konopelko.ourgoals.categories

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.categories.motivations.recycler.MotivationsAdapter
import by.konopelko.ourgoals.categories.recycler.CategoryAdapter
import by.konopelko.ourgoals.database.entities.Category
import by.konopelko.ourgoals.database.motivations.Image
import by.konopelko.ourgoals.database.motivations.Link
import by.konopelko.ourgoals.database.motivations.Motivation
import by.konopelko.ourgoals.temporaryData.CategoryCollection
import by.konopelko.ourgoals.temporaryData.CurrentSession
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.MotivationsCollection
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_categories.*
import kotlinx.coroutines.*


class FragmentCategories : Fragment() {
    var categoriesVisible = true
    var motivationsVisible = false
    var materialsVisible = false
    var resultsVisible = false

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

        categoriesRecyclerView.adapter = this.context?.let { ctx ->
            CategoryAdapter(categoryList, this, ctx)
        }
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this.context)
        categoriesRecyclerView.setHasFixedSize(false)

        categoriesRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                activity?.run {
                    if (dy != 0 && goalsAddButton.isShown) {
                        goalsAddButton.hide()
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    activity?.run {
                        goalsAddButton.show()
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        categoriesVisible = true
        motivationsVisible = false
        materialsVisible = false
        resultsVisible = false
    }

    fun addCategory(category: Category) {
        (categoriesRecyclerView.adapter as CategoryAdapter).addCategory(category)
    }

    private fun checkPermissions(): Deferred<Unit?> {
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
                    //do something like displaying a message that he didn`t allow the app
                    // to access gallery and you wont be able to let him select from gallery
                }
            }
            else -> return
        }
    }

    fun updateCategory(category: Category, position: Int) {
        (categoriesRecyclerView.adapter as CategoryAdapter).updateCategory(category, position)
    }

    fun showMotivations(categoryName: String) {
        Log.e("MOTIVATIONS:", "showMotivations()")

        // загружаем список мотиваций из базы
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserId = CurrentSession.instance.currentUser.id
            val motivations = this.async {
                this@FragmentCategories.context?.let {
                    DatabaseOperations.getInstance(it)
                        .getMotivationsByCategory(categoryName, currentUserId).await()
                }
            }.await()

            if (motivations != null) {
                Log.e("MOTIVATIONS: ", "NOT NULL: $motivations")

                // сеттим список в локальную коллекцию
                MotivationsCollection.instance.motivations =
                    Motivation(motivations.ownerId, motivations.category, motivations.linkList?: ArrayList(),
                        motivations.imageList?: ArrayList(), motivations.noteList?:ArrayList())
                MotivationsCollection.instance.motivations.id = motivations.id

                val motivationsList = ArrayList<Any>()
                MotivationsCollection.instance.motivations.linkList?.let { motivationsList.addAll(it) }
                MotivationsCollection.instance.motivations.imageList?.let { motivationsList.addAll(it) }
                MotivationsCollection.instance.motivations.noteList?.let { motivationsList.addAll(it) }

                // меняем адаптер в ресайклере категорий
                withContext(Dispatchers.Main) {
                    categoriesRecyclerView.adapter =
                        this@FragmentCategories.context?.let {
                            MotivationsAdapter(
                                motivationsList,
                                it
                            )
                        }
                    categoriesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
                    categoriesRecyclerView.setHasFixedSize(false)
                }
            } else {
                Log.e("MOTIVATIONS: ", "NULL")
            }

            categoriesVisible = false
            motivationsVisible = true
        }
    }

    fun addMotivationLink(link: Link) {
        (categoriesRecyclerView.adapter as MotivationsAdapter).addMotivationLink(link)
    }

    fun addMotivationImage(image: Image) {
        (categoriesRecyclerView.adapter as MotivationsAdapter).addMotivationImage(image)
    }
}
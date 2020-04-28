package by.konopelko.ourgoals.categories.recyclerCategories

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.Context
import android.content.UriPermission
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.media.ImageReader
import android.net.Uri
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContentResolverCompat
import androidx.core.net.UriCompat
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.categories.FragmentCategories
import by.konopelko.ourgoals.categories.add.FragmentAddCategory
import by.konopelko.ourgoals.categories.edit.CategoryEdited
import by.konopelko.ourgoals.database.Category
import by.konopelko.ourgoals.temporaryData.CategoryCollection
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.item_recycler_category.view.*
import kotlinx.android.synthetic.main.item_recycler_goal.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class CategoryAdapter(
    val list: List<Category>,
    val fragment: FragmentCategories,
    val context: Context
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val view = holder.itemView

        view.itemCategoryTitle.text = list[position].title
        if (list[position].bgImageURI != null) {
            view.itemCategoryBgImage.visibility = View.VISIBLE
            val uri = Uri.parse(list[position].bgImageURI)
            view.itemCategoryBgImage.setImageURI(uri)
        } else {
            view.itemCategoryBgImage.visibility = View.GONE
        }
        list[position].bgColor?.let {
            if (list[position].bgImageURI == null) {
                view.itemCategoryBgImage.setBackgroundColor(it)
            }
            view.itemCategoryBgImage.setColorFilter(it, PorterDuff.Mode.MULTIPLY)
            (view as MaterialCardView).strokeColor = it
        }


//        -------------------BUTTONS------------------


//        MOTIVATIONS BUTTON
        view.itemCategoryMotivationsButton.setOnClickListener {
            // фрагмент мотиваций
        }

//        DELETE BUTTON
        view.itemCategoryDeleteButton.setOnClickListener {
            // inflating confirmation dialog
            MaterialAlertDialogBuilder(context)
                .setTitle("Подтвердите действие")
                .setMessage("Вы уверены, что хотите удалить данную категорию?")
                .setNegativeButton("Отмена") { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton("Удалить") { dialog, which ->
                    // Respond to positive button press
                    deleteCategory(list[position])
                }
                .show()
        }

//        EDIT BUTTON
        view.itemCategoryEditButton.setOnClickListener {
            CategoryEdited.instance.category =
                Category(list[position].ownerId, list[position].title, list[position].bgImageURI, list[position].bgColor)

            val addDialog = FragmentAddCategory(CategoryEdited.instance.category, position)
            fragment.fragmentManager.let { fm ->
                if (fm != null) {
                    addDialog.show(fm, "")
                }
            }
        }
    }

    private fun deleteCategory(category: Category) {
        fragment.activity?.run {
            Log.e("DELETING CATEGORY: ", "deleteCategory()")

            category.id?.let {
                DatabaseOperations.getInstance(context).removeCategoryFromDatabase(it)
            }
        }

        CategoryCollection.instance.removeCategory(category)
        notifyDataSetChanged()
    }

    fun addCategory(category: Category) {
        CoroutineScope(Dispatchers.IO).launch {
            category.id =
                DatabaseOperations.getInstance(context).addCategoryToDatabase(category).await()
                    .toInt()
            Log.e(
                "CATEGORY DB SIZE: ",
                DatabaseOperations.getInstance(context).database.getCategoryDao().getCategoriesByUsersId(
                    category.ownerId
                ).size.toString()
            )
            if (category.id != null) {
                CategoryCollection.instance.categoryList.add(category)
                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun updateCategory(category: Category, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseOperations.getInstance(context).updateCategoryInDatabase(category)
        }

        CategoryCollection.instance.categoryList[position] = category
        notifyItemChanged(position)
    }
}
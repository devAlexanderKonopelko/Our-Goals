package by.konopelko.ourgoals.categories.add

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Category
import by.konopelko.ourgoals.temporaryData.CurrentSession
import kotlinx.android.synthetic.main.fragment_add_category.*

const val GET_IMAGE_CODE = 111

class FragmentAddCategory() : DialogFragment() {
    constructor(category: Category?, position: Int) : this() {
        this.category = category
        if (category?.bgImageURI != null) {
            category.bgImageURI?.let {
                if (it.isNotEmpty()) {
                    imageURI = Uri.parse(category.bgImageURI)
                }
            }
        }
        this.position = position
    }

    private var category: Category? = null
    private var position = 0
    private var imageURI: Uri? = null
    private var bgColor = 0

    interface CategoryInterface {
        fun addCategory(category: Category)
        fun updateCategory(category: Category, position: Int)
        fun updateToolbarSort()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_category, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (category != null) {
            fragmentAddCategoryTitle.text = "Редактирование"
            fragmentAddCategoryNameInput.setText(category!!.title)

            if (category!!.bgImageURI != null && category!!.bgColor != 0) {
                val uri = Uri.parse(category!!.bgImageURI)

                fragmentAddCategorySwitchBackgroundImage.isChecked = true
                fragmentAddCategoryButtonBackgroundImage.isEnabled = true
                fragmentAddCategoryPreviewImage.setImageURI(uri)

                fragmentAddCategorySwitchBackgroundColor.isChecked = true
                fragmentAddCategoryButtonBackgroundColor.isEnabled = true
                fragmentAddCategoryPreviewImage.setColorFilter(
                    category!!.bgColor!!,
                    PorterDuff.Mode.MULTIPLY
                )
            } else if (category!!.bgColor != 0 && category!!.bgImageURI == null) {
                fragmentAddCategorySwitchBackgroundColor.isChecked = true
                fragmentAddCategoryButtonBackgroundColor.isEnabled = true
                fragmentAddCategoryPreviewImage.setBackgroundColor(category!!.bgColor!!)
            } else if (category!!.bgImageURI != null && category!!.bgColor == 0) {
                val uri = Uri.parse(category!!.bgImageURI)

                fragmentAddCategorySwitchBackgroundImage.isChecked = true
                fragmentAddCategoryButtonBackgroundImage.isEnabled = true
                fragmentAddCategoryPreviewImage.setImageURI(uri)
            }
        } else {
            fragmentAddCategoryTitle.text = "Новая Категория"
        }

        fragmentAddCategorySwitchBackgroundImage.setOnCheckedChangeListener { buttonView, isChecked ->
            fragmentAddCategoryButtonBackgroundImage.isEnabled = isChecked

            if (!isChecked) {
                imageURI = null
                category?.bgImageURI = null
                fragmentAddCategoryPreviewImage.setImageURI(imageURI)

                if (bgColor != 0) {
                    fragmentAddCategoryPreviewImage.setBackgroundColor(bgColor)
                }
            }
        }
        fragmentAddCategorySwitchBackgroundColor.setOnCheckedChangeListener { buttonView, isChecked ->
            fragmentAddCategoryButtonBackgroundColor.isEnabled = isChecked

            if (!isChecked) {
                bgColor = 0
                category?.bgColor = 0
                fragmentAddCategoryPreviewImage.colorFilter = null

                if (imageURI == null) {
                    fragmentAddCategoryPreviewImage.setBackgroundColor(0)
                }
            }
        }

        fragmentAddCategoryButtonBackgroundImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_OPEN_DOCUMENT

            startActivityForResult(
                Intent.createChooser(intent, "Выбери изображение"),
                GET_IMAGE_CODE
            )
        }

        fragmentAddCategoryButtonBackgroundColor.setOnClickListener {
            val colorPicker = FragmentColorPicker(this)

            fragmentManager?.let {
                colorPicker.show(it, "")
            }
        }

        fragmentAddCategoryCancelButton.setOnClickListener {
            dismiss()
        }

        fragmentAddCategoryCompleteButton.setOnClickListener {
            if (fragmentAddCategoryNameInput.text.toString().isNotEmpty()) {
                val ownerId = CurrentSession.instance.currentUser.id
                val title = fragmentAddCategoryNameInput.text.toString()
                val bgImageUri = imageURI.toString()
                val bgColor = bgColor

                Log.e(
                    "IMAGE URI:",
                    "$bgImageUri - ССЫЛКА НА КАРТИНКУ. \nEQUALS NULL - ${bgImageUri.equals(null)} \nIS NOT EMPTY ${bgImageUri.isNotEmpty()}"
                )

                val category = Category(
                    ownerId,
                    title,
                    bgImageUri,
                    bgColor
                )
                activity?.run {
                    // если категория была передана через конструктор, т.е. не равна null,
                    // то это обновление существующей категории
                    if (this@FragmentAddCategory.category != null) {
                        if (title.isNotEmpty()) {
                            this@FragmentAddCategory.category!!.title = title
                        }
                        if (!bgImageUri.equals("null")) {
                            this@FragmentAddCategory.category!!.bgImageURI = bgImageUri
                        }
                        if (bgColor != 0) {
                            this@FragmentAddCategory.category!!.bgColor = bgColor
                        }


                        Log.e("CATEGORY:", this@FragmentAddCategory.category!!.toString())
                        // обновление существующей категории
                        val updateCategory = this as CategoryInterface
                        updateCategory.updateCategory(this@FragmentAddCategory.category!!, position)
                    } else { // иначе, это создание новой категории
                        // добавление категории в локальную бд и в локальную коллекцию

                        val addCategory = this as CategoryInterface
                        addCategory.addCategory(category)
                    }
                }

                dismiss()
            } else {
                Toast.makeText(this.context, "Введите название категории!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageURI = data.data

            fragmentAddCategoryPreviewImage.setImageURI(imageURI)

            if (bgColor != 0) {
                fragmentAddCategoryPreviewImage.setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    fun setCategoryColor(color: Int) {
        bgColor = color
        if (imageURI != null) {
            fragmentAddCategoryPreviewImage.setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY)
        } else {
            fragmentAddCategoryPreviewImage.setBackgroundColor(bgColor)
        }
    }
}
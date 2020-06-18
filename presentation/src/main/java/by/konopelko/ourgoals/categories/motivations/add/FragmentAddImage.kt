package by.konopelko.ourgoals.categories.motivations.add

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.categories.add.GET_IMAGE_CODE
import by.konopelko.ourgoals.database.motivations.Image
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_motivation_image.*

class FragmentAddImage : DialogFragment() {
    interface AddMotivation {
        fun addImage(image: Image)
    }

    private var imageUrl = ""
    private var isWeb = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_motivation_image, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMotivationImageGalleryButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_OPEN_DOCUMENT

            startActivityForResult(
                Intent.createChooser(intent, "Выбери изображение"),
                GET_IMAGE_CODE
            )
        }
        addMotivationImageLoadButton.setOnClickListener {
            if (addMotivationImageUrl.text.toString().isNotEmpty() &&
                (addMotivationImageUrl.text.toString().startsWith("https://") ||
                        addMotivationImageUrl.text.toString().startsWith("http://"))
            ) {
                if (addMotivationImagePreview.visibility == View.GONE) {
                    addMotivationImagePreview.visibility = View.VISIBLE
                }
                Picasso.get().load(addMotivationImageUrl.text.toString()).into(addMotivationImagePreview)
                imageUrl = addMotivationImageUrl.text.toString()
                isWeb = true
            } else {
                Toast.makeText(context, "Неправильная ссылка. Ссылка должна начинаться с https:// либо http://", Toast.LENGTH_SHORT).show()
            }
        }

        addMotivationImageCompleteButton.setOnClickListener {
            if (imageUrl.isNotEmpty()) {
                val image = Image(imageUrl, isWeb)
                activity?.run {
                    val addMotivation = this as AddMotivation
                    addMotivation.addImage(image)
                }
                dismiss()
            } else {
                Toast.makeText(context, "Выберите изображение", Toast.LENGTH_SHORT).show()
            }
        }

        addMotivationImageCancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            imageUrl = imageUri.toString()
            isWeb = false

            if (addMotivationImagePreview.visibility == View.GONE) {
                addMotivationImagePreview.visibility = View.VISIBLE
            }
            addMotivationImagePreview.setImageURI(imageUri)
        }
    }
}
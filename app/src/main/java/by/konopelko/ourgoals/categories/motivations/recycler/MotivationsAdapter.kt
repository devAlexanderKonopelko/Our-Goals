package by.konopelko.ourgoals.categories.motivations.recycler

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.motivations.Image
import by.konopelko.ourgoals.database.motivations.Link
import by.konopelko.ourgoals.database.motivations.Motivation
import by.konopelko.ourgoals.database.motivations.Note
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.MotivationsCollection
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_recycler_motivations_image.view.*
import kotlinx.android.synthetic.main.item_recycler_motivations_link.view.*
import kotlinx.android.synthetic.main.item_recycler_motivations_note.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MotivationsAdapter(
    private val list: List<Any>,
    private val context: Context
) : RecyclerView.Adapter<MotivationsAdapter.BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_LINK = 0
        private const val TYPE_IMAGE = 1
        private const val TYPE_NOTE = 2
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }


    class LinkViewHolder(itemView: View, val context: Context, val adapter: MotivationsAdapter) :
        BaseViewHolder<Link>(itemView) {
        override fun bind(item: Link) {
            itemView.itemMotivationsLinkDescription.text = item.description

            itemView.itemMotivationsLinkShowButton.setOnClickListener {
                // переход по ссылке
                if (item.url.startsWith("https://") || item.url.startsWith("http://")) {
                    val uri = Uri.parse(item.url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(
                        context,
                        "Неправильная ссылка. Ссылка должна начинаться с http:// либо https://",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            itemView.itemMotivationsLinkDeleteButton.setOnClickListener {
                // удаление айтема-ссылки
                MaterialAlertDialogBuilder(context)
                    .setTitle("Подтвердите действие")
                    .setMessage("Вы уверены, что хотите удалить ссылку?")
                    .setNegativeButton("Отмена") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Завершить") { dialog, which ->
                        // удаление ссылки из коллекции
                        MotivationsCollection.instance.motivations.linkList?.remove(item)
                        adapter.notifyDataSetChanged()
                        // обновление объекта мотиваций в бд
                        adapter.updateMotivationInDatabase()
                    }
                    .show()
            }
        }
    }

    class ImageViewHolder(itemView: View, val context: Context, val adapter: MotivationsAdapter) :
        BaseViewHolder<Image>(itemView) {
        override fun bind(item: Image) {
            if (item.isWeb) {
                Log.e("----LOADING URL-----", item.imageUrl)
                Picasso.get().load(item.imageUrl).into(itemView.itemMotivationsImage)
            } else {
                val uri = Uri.parse(item.imageUrl)
                itemView.itemMotivationsImage.setImageURI(uri)
            }

            itemView.itemMotivationsImageDeleteButton.setOnClickListener {
                //удаление айтема-картинки
                MaterialAlertDialogBuilder(context)
                    .setTitle("Подтвердите действие")
                    .setMessage("Вы уверены, что хотите удалить изображение?")
                    .setNegativeButton("Отмена") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Завершить") { dialog, which ->
                        // удаление изображения из коллекции
                        MotivationsCollection.instance.motivations.imageList?.remove(item)
                        adapter.notifyDataSetChanged()
                        // обновление объекта мотиваций в бд
                        adapter.updateMotivationInDatabase()
                    }
                    .show()
            }
        }
    }

    class NoteViewHolder(itemView: View, val context: Context, val adapter: MotivationsAdapter) : BaseViewHolder<Note>(itemView) {
        override fun bind(item: Note) {
            itemView.itemMotivationsNoteTitle.text = item.title
            itemView.itemMotivationsNoteText.text = item.text
            itemView.setOnClickListener {
                // развёрнутая заметка
            }
            itemView.itemMotivationsNoteDeleteButton.setOnClickListener {
                // удаление айтема-заметки
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
//        Log.e("BIND", "onCreateViewHolder()")
        return when (viewType) {
            TYPE_LINK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_motivations_link, parent, false)
                LinkViewHolder(view, context, this)
            }
            TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_motivations_image, parent, false)
                ImageViewHolder(view, context, this)
            }
            TYPE_NOTE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler_motivations_note, parent, false)
                NoteViewHolder(view, context, this)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = list[position]
        when (holder) {
            is LinkViewHolder -> holder.bind(item as Link)
            is ImageViewHolder -> holder.bind(item as Image)
            is NoteViewHolder -> holder.bind(item as Note)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is Link -> TYPE_LINK
            is Image -> TYPE_IMAGE
            is Note -> TYPE_NOTE
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addMotivationLink(link: Link) {
        MotivationsCollection.instance.motivations.linkList?.add(link)
        Log.e("-----linkList------", "${MotivationsCollection.instance.motivations.linkList}")
        notifyDataSetChanged()
        updateMotivationInDatabase()
    }

    fun addMotivationImage(image: Image) {
        MotivationsCollection.instance.motivations.imageList?.add(image)
        Log.e("-----imageList------", "${MotivationsCollection.instance.motivations.imageList}")
        notifyDataSetChanged()
        updateMotivationInDatabase()
    }

    private fun updateMotivationInDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val motivation = Motivation(
                MotivationsCollection.instance.motivations.ownerId,
                MotivationsCollection.instance.motivations.category,
                MotivationsCollection.instance.motivations.linkList,
                MotivationsCollection.instance.motivations.imageList,
                MotivationsCollection.instance.motivations.noteList
            )
            motivation.id = MotivationsCollection.instance.motivations.id
            DatabaseOperations.getInstance(context).updateMotivationInDatabase(motivation)
        }
    }
}
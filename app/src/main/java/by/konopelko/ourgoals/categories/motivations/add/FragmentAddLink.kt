package by.konopelko.ourgoals.categories.motivations.add

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.categories.FragmentCategories
import by.konopelko.ourgoals.database.motivations.Link
import kotlinx.android.synthetic.main.fragment_add_motivation_link.*
import kotlinx.android.synthetic.main.fragment_categories.*
import java.util.*

class FragmentAddLink : DialogFragment() {
    interface AddMotivation {
        fun addLink(link: Link)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_motivation_link, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMotivationLinkCompleteButton.setOnClickListener {
            if (addMotivationLinkUrl.text.toString().isNotEmpty() && addMotivationLinkDescription.text.toString().isNotEmpty()) {
                // обновление мотиваций конекретной категории в бд, обновление ресайклера мотиваций
                val link = Link(
                    addMotivationLinkUrl.text.toString(),
                    addMotivationLinkDescription.text.toString()
                )
                activity?.run {
                    val addMotivation = this as AddMotivation
                    addMotivation.addLink(link)
                }
                dismiss()
            }
        }

        addMotivationLinkCancelButton.setOnClickListener {
            dismiss()
        }
    }
}

package by.konopelko.ourgoals.categories.motivations.add

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import kotlinx.android.synthetic.main.fragment_add_motivation.*

class FragmentAddMotivation: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_motivation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMotivationLinkButton.setOnClickListener {
            val linkDialog = FragmentAddLink()
            fragmentManager?.let { fm -> linkDialog.show(fm, "") }
            dismiss()
        }

        addMotivationImageButton.setOnClickListener {
            val imageDialog = FragmentAddImage()
            fragmentManager?.let { fm -> imageDialog.show(fm, "") }
            dismiss()
        }

        addMotivationNoteButton.setOnClickListener {
//            val noteDialog = FragmentAddNote()
//            fragmentManager?.let { fm -> imageDialog.show(fm, "") }
            dismiss()
        }
    }
}
package by.konopelko.ourgoals.categories.add

import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import com.skydoves.colorpickerview.listeners.ColorListener
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener
import kotlinx.android.synthetic.main.fragment_add_category_color_picker.*

class FragmentColorPicker(val parentFragment: FragmentAddCategory): DialogFragment() {
    private var colorPicked = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_category_color_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorPickerView.setColorListener(object: ColorListener {
            override fun onColorSelected(color: Int, fromUser: Boolean) {
//                Log.e("COLOR", color.toString())
                colorPickerPickedColor.setBackgroundColor(color)
                colorPicked = color
            }
        })

        colorPickerCancelButton.setOnClickListener {
            dismiss()
        }

        colorPickerCompleteButton.setOnClickListener {
            parentFragment.setCategoryColor(colorPicked)
            dismiss()
        }
    }
}
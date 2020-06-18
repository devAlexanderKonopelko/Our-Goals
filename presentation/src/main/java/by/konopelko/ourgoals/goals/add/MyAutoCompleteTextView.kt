package by.konopelko.ourgoals.goals.add

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.AutoCompleteTextView

@SuppressLint("AppCompatCustomView")
class MyAutoCompleteTextView: AutoCompleteTextView {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr)
    override fun performFiltering(text: CharSequence?, keyCode: Int) {
        super.performFiltering("", 0)
    }
}
package by.konopelko.ourgoals.help

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import by.konopelko.ourgoals.R
import kotlinx.android.synthetic.main.item_help.view.*

class ViewPagerAdapter(val context: Context) : PagerAdapter() {
    private val images = arrayOf(
        R.drawable.help_1,
        R.drawable.help_2,
        R.drawable.help_3,
        R.drawable.help_4
    )
    private val texts = arrayOf(R.string.help1, R.string.help2, R.string.help3, R.string.help4)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_help, null)
        view.itemHelpImage.setImageResource(images[position])
        view.itemHelpText.setText(texts[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
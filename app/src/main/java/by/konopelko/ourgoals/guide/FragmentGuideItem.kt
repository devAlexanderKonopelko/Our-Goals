package by.konopelko.ourgoals.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.R
import kotlinx.android.synthetic.main.fragment_guide_item.*

class FragmentGuideItem(val position: Int): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_guide_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val guideItem = GuideItemList.instance.itemlist[position]
        guideTitle1.text = guideItem.title1
        guideTitle2.text = guideItem.title2
        guideText1.text = guideItem.text1
        guideText2.text = guideItem.text2
        guideFraze.text = guideItem.fraze
    }
}
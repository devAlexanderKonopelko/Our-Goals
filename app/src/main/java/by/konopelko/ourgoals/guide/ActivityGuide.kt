package by.konopelko.ourgoals.guide

import by.konopelko.ourgoals.ActivityMain
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import by.konopelko.ourgoals.R
import kotlinx.android.synthetic.main.activity_guide.*

class ActivityGuide : AppCompatActivity(), View.OnClickListener {
    private val fragmentGuideStart =
        FragmentGuideStart()
    private val fragmentGuideItem1 =
        FragmentGuideItem(0)
    private val fragmentGuideItem2 =
        FragmentGuideItem(1)
    private var guidePage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        //setting the guide fragments
        GuideItemList.instance.itemlist.add(
            GuideItem(
                getString(R.string.test_title),
                getString(R.string.test_guide_text),
                getString(R.string.test_guide_fraze),
                getString(R.string.test_title),
                getString(R.string.test_guide_text), 0
            )
        )
        GuideItemList.instance.itemlist.add(
            GuideItem(
                getString(R.string.test_title2),
                getString(R.string.test_guide_text),
                getString(R.string.test_guide_fraze),
                getString(R.string.test_title),
                getString(R.string.test_guide_text), 1
            )
        )

        supportFragmentManager.beginTransaction()
            .replace(guideFragmentLayout.id, fragmentGuideStart).commit()

        skipButton.setOnClickListener(this)
        startButton.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        backButton.setOnClickListener(this)
        finishButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            skipButton.id, finishButton.id -> {
                startActivity(Intent(this, ActivityMain::class.java))
            }
            startButton.id -> {
                supportFragmentManager.beginTransaction()
                    .replace(guideFragmentLayout.id, fragmentGuideItem1).commit()

                skipButton.visibility = View.GONE
                backButton.visibility = View.VISIBLE

                startButton.visibility = View.GONE
                nextButton.visibility = View.VISIBLE

                guidePage = 1
            }
            nextButton.id -> {
                // предпоследняя страница гайда
                if (guidePage == 1) {
                    supportFragmentManager.beginTransaction()
                        .replace(guideFragmentLayout.id, fragmentGuideItem2).commit()

                    guidePage = 2

                    nextButton.visibility = View.GONE
                    finishButton.visibility = View.VISIBLE
                }
            }

            backButton.id -> {
                when(guidePage) {
                    1 -> {
                        supportFragmentManager.beginTransaction()
                            .replace(guideFragmentLayout.id, fragmentGuideStart).commit()

                        skipButton.visibility = View.VISIBLE
                        backButton.visibility = View.GONE

                        startButton.visibility = View.VISIBLE
                        nextButton.visibility = View.GONE

                        guidePage = 0
                    }
                    2 -> {
                        supportFragmentManager.beginTransaction()
                            .replace(guideFragmentLayout.id, fragmentGuideItem1).commit()

                        nextButton.visibility = View.VISIBLE
                        finishButton.visibility = View.GONE

                        guidePage = 1
                    }
                }
            }
        }
    }

    // works the same as skipButton if on fragmentGuideStart
    // else - as backButton
    //onBackPressed() override
}

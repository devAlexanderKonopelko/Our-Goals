package by.konopelko.ourgoals.guide

import by.konopelko.ourgoals.ActivityMain
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.help.FragmentHelp
import by.konopelko.ourgoals.help.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_guide.*

class ActivityGuide : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        firstRunViewPager.adapter = ViewPagerAdapter(this)
        firstRunTabLayout.setupWithViewPager(firstRunViewPager, true)

        finishButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            finishButton.id -> {
                startActivity(Intent(this, ActivityMain::class.java))
            }
        }
    }
}

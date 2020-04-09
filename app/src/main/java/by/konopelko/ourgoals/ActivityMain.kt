package by.konopelko.ourgoals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.analytics.FragmentAnalytics
import by.konopelko.ourgoals.categories.FragmentCategories
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.goals.FragmentGoals
import by.konopelko.ourgoals.goals.add.FragmentAddTasks
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_goals.*

class ActivityMain : AppCompatActivity(), FragmentAddTasks.RefreshGoalsListInterface {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val fragmentGoals = FragmentGoals()
    val fragmentCategories = FragmentCategories()
    val fragmentAnalytics = FragmentAnalytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  access users data like this:
        //  auth.currentUser?.email

        bottomNavigation.selectedItemId = R.id.nav_goals
        getFragment(fragmentGoals)

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_goals -> {
                    getFragment(fragmentGoals)
                }
                R.id.nav_categories -> {
                    getFragment(fragmentCategories)
                }
                R.id.nav_analytics -> {
                    getFragment(fragmentAnalytics)
                }
            }
            true
        }
    }

    private fun getFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fragmentContainer.id, fragment).commit()
    }

    override fun refreshGoalsRecyclerView(goal: Goal) {
        fragmentGoals.refreshRecycler(goal)
    }
}

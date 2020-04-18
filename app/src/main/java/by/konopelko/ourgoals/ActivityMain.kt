package by.konopelko.ourgoals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.analytics.FragmentAnalytics
import by.konopelko.ourgoals.categories.FragmentCategories
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.friends.add.FragmentAddFriends
import by.konopelko.ourgoals.friends.FragmentFriends
import by.konopelko.ourgoals.goals.FragmentGoals
import by.konopelko.ourgoals.goals.add.FragmentAddGoal
import by.konopelko.ourgoals.goals.add.FragmentAddTasks
import by.konopelko.ourgoals.goals.add.recyclerTasks.AddTaskSingleton
import by.konopelko.ourgoals.logIn.ActivityLogIn
import by.konopelko.ourgoals.temporaryData.CurrentSession
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_side_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// при добавлении друга, в базу на сервере в friends текущего пользователя
// должен заноситься uid добавленного друга

class ActivityMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FragmentAddTasks.RefreshGoalsListInterface {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val usersDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    val fragmentGoals = FragmentGoals()
    val fragmentCategories = FragmentCategories()
    val fragmentAnalytics = FragmentAnalytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, 0, 0)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        if (auth.currentUser == null) {
            nav_view.menu.findItem(R.id.nav_side_friends).isEnabled = false
            nav_view.menu.findItem(R.id.nav_side_add_friends).isEnabled = false
            nav_view.menu.findItem(R.id.nav_side_social_goal).isEnabled = false
            nav_view.menu.findItem(R.id.nav_side_log_out).title = "Войти в аккаунт"

            nav_view.getHeaderView(0).currentUserLogin.text =
                CurrentSession.instance.currentUser.name
            nav_view.getHeaderView(0).currentUserImage.setImageResource(R.drawable.icon_guest)
            nav_view.getHeaderView(0).currentUserEmail.visibility = View.INVISIBLE
        } else {
            nav_view.getHeaderView(0).currentUserLogin.text =
                CurrentSession.instance.currentUser.name
            // TODO: change user's icon (in future)
            nav_view.getHeaderView(0).currentUserImage.setImageResource(R.drawable.icon_guest)
            nav_view.getHeaderView(0).currentUserEmail.visibility = View.VISIBLE
            nav_view.getHeaderView(0).currentUserEmail.text = auth.currentUser!!.email
        }


        //setting bottom navigation menu
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

        // add goal button
        goalsAddButton.setOnClickListener {
            // clear tasks list for new goal
            AddTaskSingleton.instance.taskList.clear()

            // inflating dialog fragment
            val addDialog = FragmentAddGoal()
            supportFragmentManager.let { supportFM -> addDialog.show(supportFM, "") }
        }
    }

    //processing side navigation clicks
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_side_log_out -> {
                if (auth.currentUser == null) {
                    // переход на экран авторизации/регистрации
                    startActivity(Intent(this, ActivityLogIn::class.java))
                } else {
                    // выход из аккаунта
                    auth.signOut()
                    Toast.makeText(this, "Выход выполнен", Toast.LENGTH_SHORT).show()

                    CoroutineScope(Dispatchers.IO).launch {
                        CurrentSession.instance.currentUser =
                            DatabaseOperations.getInstance(this@ActivityMain).getUserById("0")
                                .await()
                    }

                    startActivity(Intent(this, ActivityLogIn::class.java))
                }
            }
            R.id.nav_side_add_friends -> {
                val addFriendsDialog = FragmentAddFriends()
                addFriendsDialog.show(supportFragmentManager, "")
            }
            R.id.nav_side_friends -> {
                val friendsDialog = FragmentFriends()
                friendsDialog.show(supportFragmentManager, "")
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        finishAffinity()
    }


    // -----------custom functions--------------

    private fun getFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fragmentContainer.id, fragment).commit()
    }

    override fun refreshGoalsRecyclerView(goal: Goal) {
        fragmentGoals.refreshRecycler(goal)
    }
}

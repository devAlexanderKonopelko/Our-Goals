package by.konopelko.ourgoals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.analytics.FragmentAnalytics
import by.konopelko.ourgoals.categories.FragmentCategories
import by.konopelko.ourgoals.categories.add.FragmentAddCategory
import by.konopelko.ourgoals.categories.motivations.add.FragmentAddImage
import by.konopelko.ourgoals.categories.motivations.add.FragmentAddLink
import by.konopelko.ourgoals.categories.motivations.add.FragmentAddMotivation
import by.konopelko.ourgoals.core.main.MainContract
import by.konopelko.ourgoals.core.main.MainPresenter
import by.konopelko.ourgoals.database.entities.Category
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.database.motivations.Image
import by.konopelko.ourgoals.database.motivations.Link
import by.konopelko.ourgoals.friends.add.FragmentAddFriends
import by.konopelko.ourgoals.friends.FragmentFriends
import by.konopelko.ourgoals.goals.FragmentGoals
import by.konopelko.ourgoals.goals.add.FragmentAddGoal
import by.konopelko.ourgoals.goals.add.FragmentAddTasks
import by.konopelko.ourgoals.authentication.ActivityLogIn
import by.konopelko.ourgoals.goals.add.FragmentChooseFriends
import by.konopelko.ourgoals.help.FragmentHelp
import by.konopelko.ourgoals.notifications.AdapterNotifications
import by.konopelko.ourgoals.notifications.FragmentNotifications
import by.konopelko.ourgoals.supportDeveloper.FragmentSupportDev
import by.konopelko.ourgoals.temporaryData.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.badge_notifications.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_side_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FragmentAddTasks.RefreshGoalsListInterface, AdapterNotifications.NotificationActions,
    MainContract.View, FragmentAddCategory.CategoryInterface, FragmentAddLink.AddMotivation,
    FragmentAddImage.AddMotivation, FragmentChooseFriends.SocialGoalAddition {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val presenter = MainPresenter(this)

    val fragmentGoals = FragmentGoals()
    val fragmentCategories = FragmentCategories()
    val fragmentAnalytics = FragmentAnalytics()

    private val categoriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // создаём список категорий для сортировки из верхнего тулбара
        categoriesList.add("Все категории")
        for (category in CategoryCollection.instance.categoryList) {
            categoriesList.add(category.title)
        }

        val arrayAdapter =
            ArrayAdapter<String>(this, R.layout.item_spinner_sort_collections, categoriesList)
        toolbarSort.setAdapter(arrayAdapter)
        setSupportActionBar(toolbar)

        //Сортировка целей по категориям
        setToolbarSort()

        // создание бокового меню
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, 0, 0)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.menu.findItem(R.id.nav_side_notifications)
            .setActionView(R.layout.badge_notifications)

        if (CurrentSession.instance.currentUser.name.equals("Гость")) {
            nav_view.menu.findItem(R.id.nav_side_friends).isEnabled = false
            nav_view.menu.findItem(R.id.nav_side_add_friends).isEnabled = false
            nav_view.menu.findItem(R.id.nav_side_social_goals).isEnabled = false
            nav_view.menu.findItem(R.id.nav_side_notifications).isEnabled = false
            nav_view.menu.findItem(R.id.nav_side_log_out).title = "Войти в аккаунт"

            nav_view.getHeaderView(0).currentUserLogin.text =
                CurrentSession.instance.currentUser.name
//            nav_view.getHeaderView(0).currentUserImage.setImageResource(R.drawable.icon_guest)
            nav_view.getHeaderView(0).currentUserEmail.visibility = View.INVISIBLE

            notificationBadge.visibility = View.INVISIBLE
            nav_view.menu.findItem(R.id.nav_side_notifications).actionView.visibility =
                View.INVISIBLE
        } else {
            Log.e("CURRENT SESSION USER: ", CurrentSession.instance.currentUser.toString())

            nav_view.getHeaderView(0).currentUserLogin.text =
                CurrentSession.instance.currentUser.name
            // TODO: change user's icon
//            nav_view.getHeaderView(0).currentUserImage.setImageResource(R.drawable.icon_guest)
            nav_view.getHeaderView(0).currentUserEmail.visibility = View.VISIBLE
            nav_view.getHeaderView(0).currentUserEmail.text = auth.currentUser!!.email

            // ------ загружаем нотификации для текущего пользователя -----------

            // постоянно следить за изменениями в уведомлениях:
            // добавлять их в локальную коллекцию
            // обновлять ui
            Log.e("NOTIFICATIONS", "Отслеживание уведомлений")
            presenter.observeNotifications(CurrentSession.instance.currentUser.id)

            Log.e(
                "NOTIFICATIONS AMOUNT:",
                NotificationsCollection.instance.requestsKeys.size.toString()
            )

            if (NotificationsCollection.instance.friendsRequests.size != 0 ||
                NotificationsCollection.instance.goalsRequests.size != 0
            ) {
                notificationBadge.visibility = View.VISIBLE
                nav_view.menu.findItem(R.id.nav_side_notifications).actionView.visibility =
                    View.VISIBLE
            } else {
                notificationBadge.visibility = View.INVISIBLE
                nav_view.menu.findItem(R.id.nav_side_notifications).actionView.visibility =
                    View.INVISIBLE
            }
        }


//        --------- setting bottom navigation menu ------------
        bottomNavigation.selectedItemId = R.id.nav_goals
        GoalCollection.instance.visible = true
        SocialGoalCollection.instance.visible = false
        getFragment(fragmentGoals)

        if (GoalCollection.instance.visible) {
            toolbarSectionTitle.text = "Мои цели"
        } else {
            toolbarSectionTitle.text = "Общие цели"
        }
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_goals -> {
                    if (GoalCollection.instance.visible) {
                        toolbarSectionTitle.text = "Мои цели"
                    } else {
                        toolbarSectionTitle.text = "Общие цели"
                    }
                    goalsAddButton.visibility = View.VISIBLE
                    toolbarSortContainer.visibility = View.VISIBLE
                    getFragment(fragmentGoals)
                }
                R.id.nav_categories -> {
                    goalsAddButton.visibility = View.VISIBLE
                    toolbarSortContainer.visibility = View.INVISIBLE
                    getFragment(fragmentCategories)
                    toolbarSectionTitle.text = "Категории"
                }
                R.id.nav_analytics -> {
                    goalsAddButton.visibility = View.INVISIBLE
                    toolbarSortContainer.visibility = View.INVISIBLE
                    getFragment(fragmentAnalytics)
                    toolbarSectionTitle.text = "Аналитика"
                }
            }
            true
        }

        // add goal button (ПЛЮСИК)
        goalsAddButton.setOnClickListener {
            if (fragmentGoals.isVisible) {
                // inflating dialog fragment

                val addDialog = FragmentAddGoal()
                supportFragmentManager.let { supportFM -> addDialog.show(supportFM, "") }
            }
            if (fragmentCategories.isVisible) {
                // show add category dialog

                if (fragmentCategories.categoriesVisible) {
                    val addDialog = FragmentAddCategory()
                    supportFragmentManager.let { supportFM -> addDialog.show(supportFM, "") }
                }
                if (fragmentCategories.motivationsVisible) {
                    //
                    val addDialog = FragmentAddMotivation()
                    supportFragmentManager.let { supportFM -> addDialog.show(supportFM, "") }
                }
            }
            if (fragmentAnalytics.isVisible) {
                // show add analytics dialog
            }
        }
    }

    private fun setToolbarSort() {
        toolbarSort.setOnItemClickListener { parent, view, position, id ->
            val category = categoriesList[position]
            if (category.equals("Все категории")) {
                // коллекции хранят цели со всех категорий
                if (GoalCollection.instance.visible) {
                    fragmentGoals.showLocalGoals()
                } else {
                    fragmentGoals.showSocialGoals()
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    if (SocialGoalCollection.instance.visible) {
                        val goals = ArrayList<Goal>()
                        val keys = ArrayList<String>()

                        //собираем подходящие цели
                        for (i in 0 until SocialGoalCollection.instance.goalList.size) {
                            if (SocialGoalCollection.instance.goalList[i].category.equals(category)) {
                                goals.add(SocialGoalCollection.instance.goalList[i])
                                keys.add(SocialGoalCollection.instance.keysList[i])
                            }
                        }
                        withContext(Dispatchers.Main) {
                            fragmentGoals.updateGoals(goals)
                        }
                    } else {
                        val goals = ArrayList<Goal>()

                        //собираем подходящие цели
                        for (i in 0 until GoalCollection.instance.goalsInProgressList.size) {
                            if (GoalCollection.instance.goalsInProgressList[i].category.equals(
                                    category
                                )
                            ) {
                                goals.add(GoalCollection.instance.goalsInProgressList[i])
                            }
                        }
                        // обновляем адаптер
                        fragmentGoals.updateGoals(goals)
                    }
                }
            }
        }
    }

    //    -------- processing side navigation clicks ----------------
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_side_my_goals -> {
                if (!fragmentGoals.isVisible) {
                    GoalCollection.instance.visible = true
                    SocialGoalCollection.instance.visible = false

                    bottomNavigation.selectedItemId = R.id.nav_goals
                    toolbarSortContainer.visibility = View.VISIBLE
                    getFragment(fragmentGoals)
                } else {
                    fragmentGoals.showLocalGoals()
                }
                toolbarSectionTitle.text = "Мои цели"
            }
            R.id.nav_side_social_goals -> {
                if (!fragmentGoals.isVisible) {
                    GoalCollection.instance.visible = false
                    SocialGoalCollection.instance.visible = true

                    bottomNavigation.selectedItemId = R.id.nav_goals
                    toolbarSortContainer.visibility = View.VISIBLE
                    getFragment(fragmentGoals)
                } else {
                    fragmentGoals.showSocialGoals()
                }
                toolbarSectionTitle.text = "Общие цели"
            }
            R.id.nav_side_notifications -> {
                val notifDialog = FragmentNotifications()
                notifDialog.show(supportFragmentManager, "")
            }
            R.id.nav_side_friends -> {
                val friendsDialog = FragmentFriends()
                friendsDialog.show(supportFragmentManager, "")
            }
            R.id.nav_side_add_friends -> {
                val addFriendsDialog = FragmentAddFriends()
                addFriendsDialog.show(supportFragmentManager, "")
            }
            R.id.nav_side_log_out -> {
                if (auth.currentUser == null) { // если текущий пользователь - гость
                    // переход на экран авторизации/регистрации
                    startActivity(Intent(this, ActivityLogIn::class.java))
                } else {
                    // выход из аккаунта
                    auth.signOut()

                    //очищаем локальную коллекцию соц. целей (они будут подгружаться заново для нового пользователя)
                    SocialGoalCollection.instance.goalList.clear()

                    Toast.makeText(this, "Выход выполнен", Toast.LENGTH_SHORT).show()

                    CoroutineScope(Dispatchers.IO).launch {
                        CurrentSession.instance.currentUser =
                            DatabaseOperations.getInstance(this@ActivityMain).getUserById("0")
                                .await()

                        presenter.removeNotificationsListeners()
                    }
                }
            }
            R.id.nav_side_help -> {
                val helpDialog = FragmentHelp()
                helpDialog.show(supportFragmentManager, "")
            }
            R.id.nav_side_support_dev -> {
                val supportDialog = FragmentSupportDev()
                supportDialog.show(supportFragmentManager, "")
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun getFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fragmentContainer.id, fragment).commit()
    }

    override fun refreshGoalsRecyclerView(goal: Goal) {
        fragmentGoals.refreshRecycler(goal)
    }

    override fun notificationDeleted(listSize: Int) {
        if (listSize == 0) {
            notificationBadge.visibility = View.INVISIBLE
            nav_view.menu.findItem(R.id.nav_side_notifications).actionView.visibility =
                View.INVISIBLE
        }
    }

    override fun onNotificationsChanged(listSize: Int) {
        Log.e("NOTIFICATIONS", "Изменение уведомлений")
        if (listSize != 0) {
            notificationBadge.visibility = View.VISIBLE
            nav_view.menu.findItem(R.id.nav_side_notifications).actionView.visibility = View.VISIBLE
        } else {
            notificationBadge.visibility = View.INVISIBLE
            nav_view.menu.findItem(R.id.nav_side_notifications).actionView.visibility =
                View.INVISIBLE
        }
    }

    override fun onNotificationsListenersRemoved() {
        // переход на экран авторизации/регистрации
        startActivity(Intent(this@ActivityMain, ActivityLogIn::class.java))
    }

    override fun addCategory(category: Category) {
        fragmentCategories.addCategory(category)

        categoriesList.add(category.title)
        val arrayAdapter =
            ArrayAdapter<String>(this, R.layout.item_spinner_sort_collections, categoriesList)
        toolbarSort.setAdapter(arrayAdapter)
    }

    override fun updateCategory(category: Category, position: Int) {
        fragmentCategories.updateCategory(category, position)

        categoriesList.clear()
        categoriesList.add("Все категории")
        for (category in CategoryCollection.instance.categoryList) {
            categoriesList.add(category.title)
        }
    }

    override fun updateToolbarSort() {
        categoriesList.clear()
        categoriesList.add("Все категории")
        for (category in CategoryCollection.instance.categoryList) {
            categoriesList.add(category.title)
        }
    }

    override fun addLink(link: Link) {
        fragmentCategories.addMotivationLink(link)
    }

    override fun addImage(image: Image) {
        fragmentCategories.addMotivationImage(image)
    }

    override fun updateRecyclerWithSocialGoal() {
        fragmentGoals.showSocialGoals()
    }
}

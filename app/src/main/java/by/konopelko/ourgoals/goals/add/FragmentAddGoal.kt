package by.konopelko.ourgoals.goals.add

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.temporaryData.CategoryCollection
import by.konopelko.ourgoals.temporaryData.CurrentSession
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_goal_main.*
import kotlinx.android.synthetic.main.fragment_add_goal_main.view.*

class FragmentAddGoal : DialogFragment() {
    private val addDialogTasks = FragmentAddTasks(this)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_goal_main, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.isCancelable = false
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            addGoalFragmentGoalText.setText(savedInstanceState.getString("GOAL_TEXT"))
            addGoalFragmentCategoryList.setText(savedInstanceState.getString("GOAL_CATEGORY"))
        }

        if (CurrentSession.instance.currentUser.id.equals("0")) {
            addGoalFragmentSocialLayout.visibility = View.GONE
        } else {
            addGoalFragmentSocialLayout.visibility = View.VISIBLE
        }

        // получать текущий список категорий из базы
        val items = ArrayList<String>()
        for (category in CategoryCollection.instance.categoryList) {
            items.add(category.title)
        }

        val adapter = ArrayAdapter(view.context, R.layout.item_add_goal_category, items)
        addGoalFragmentCategoryList.setAdapter(adapter)

        addGoalFragmentSocialInfo.setOnClickListener {
            // TODO: доделать снекбар - увеличить число строчек
            val snackbar = Snackbar.make(
                view,
                "При создании общей цели вы сможете подключать ваших друзей, чтобы выполнять цель вместе.",
                Snackbar.LENGTH_INDEFINITE)

            snackbar.show()
            snackbar.setAction("OK", View.OnClickListener {
                snackbar.dismiss()
            })
        }

        addGoalFragmentCancelButton.setOnClickListener {
            dismiss()
        }
        addGoalFragmentNextButton.setOnClickListener {
            if (addGoalFragmentGoalText.text.toString().isNotEmpty() &&
                addGoalFragmentCategoryList.text.toString().isNotEmpty()
            ) {

                NewGoal.instance.goal.text = addGoalFragmentGoalText.text.toString()
                NewGoal.instance.goal.category = addGoalFragmentCategoryList.text.toString()
                NewGoal.instance.goal.isSocial = addGoalFragmentSwitchSocial.isChecked

                //взять значение свича фона
//                addGoalFragmentSwitchBackground.isChecked

                fragmentManager?.let { it -> addDialogTasks.show(it, "") }

                savedInstanceState?.putString("GOAL_TEXT", addGoalFragmentGoalText.text.toString())
                savedInstanceState?.putString(
                    "GOAL_CATEGORY",
                    addGoalFragmentCategoryList.text.toString()
                )
                // + настройки фона и т.д.

                dismiss()
            }
        }
    }
}
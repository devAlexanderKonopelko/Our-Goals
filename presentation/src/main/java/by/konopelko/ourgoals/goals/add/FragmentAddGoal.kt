package by.konopelko.ourgoals.goals.add

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.goals.add.recyclerTasks.AddTaskSingleton
import by.konopelko.ourgoals.temporaryData.CategoryCollection
import by.konopelko.ourgoals.temporaryData.CurrentSession
import by.konopelko.ourgoals.temporaryData.GoalCollection
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_goal_main.*
import kotlinx.android.synthetic.main.fragment_add_goal_main.view.*
import javax.security.auth.login.LoginException

class FragmentAddGoal : DialogFragment() {
    private val addDialogTasks = FragmentAddTasks(this)

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

        // получаем список категорий из коллекции
        val items = ArrayList<String>()
        for (category in CategoryCollection.instance.categoryList) {
            items.add(category.title)
        }

        val adapter = ArrayAdapter(view.context, R.layout.item_add_goal_category, items)
        addGoalFragmentCategoryList.setAdapter(adapter)

        // если пользователь - Гость, то прятать опцию Общей цели
        if (CurrentSession.instance.currentUser.id.equals("0")) {
            addGoalFragmentSocialLayout.visibility = View.GONE
        } else {
            addGoalFragmentSocialLayout.visibility = View.VISIBLE
        }

        addGoalFragmentSocialInfo.setOnClickListener {
            val snackbar = Snackbar.make(
                view,
                getString(R.string.add_goal_teamInfoSnackbar),
                Snackbar.LENGTH_INDEFINITE
            )
            val snackbarTextView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            snackbarTextView.maxLines = 3
            snackbar.show()
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
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
                if (!addDialogTasks.isBack) {
                    AddTaskSingleton.instance.taskList = ArrayList()
                }
                //взять значение свича фона
//                addGoalFragmentSwitchBackground.isChecked
                addDialogTasks.isBack = true
                fragmentManager?.let { fm -> addDialogTasks.show(fm, "") }
                dismiss()
            } else if (addGoalFragmentGoalText.text.toString().isEmpty()) {
                Toast.makeText(context, getString(R.string.toast_enterGoalText), Toast.LENGTH_LONG).show()
            } else if (addGoalFragmentCategoryList.text.toString().isEmpty()) {
                Toast.makeText(context, getString(R.string.toast_chooseCategory), Toast.LENGTH_LONG).show()
            }
        }
    }
}

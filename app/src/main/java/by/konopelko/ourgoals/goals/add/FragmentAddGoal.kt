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
import kotlinx.android.synthetic.main.fragment_add_goal_main.*
import kotlinx.android.synthetic.main.fragment_add_goal_main.view.*

class FragmentAddGoal : DialogFragment() {
    val addDialogTasks = FragmentAddTasks(this)

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
            view.addGoalFragmentGoalText.setText(savedInstanceState.getString("GOAL_TEXT"))
            view.addGoalFragmentCategoryList.setText(savedInstanceState.getString("GOAL_CATEGORY"))
        }
        // получать текущий список категорий из базы
        val items = listOf("Material", "Design", "Components", "Android")

        val adapter = ArrayAdapter(view.context, R.layout.item_add_goal_category, items)
        view.addGoalFragmentCategoryList.setAdapter(adapter)

        addGoalFragmentCancelButton.setOnClickListener {
            dismiss()
        }
        addGoalFragmentNextButton.setOnClickListener {
            if (view.addGoalFragmentGoalText.text.toString().isNotEmpty() &&
                view.addGoalFragmentCategoryList.text.toString().isNotEmpty()) {


                NewGoal.instance.goal.text = view.addGoalFragmentGoalText.text.toString()
                NewGoal.instance.goal.category = view.addGoalFragmentCategoryList.text.toString()

                //взять значение свича фона и социальной цели
//                addGoalFragmentSwitchBackground.isChecked
//                addGoalFragmentSwitchSocial.isChecked

                fragmentManager?.let { it -> addDialogTasks.show(it,"") }

                savedInstanceState?.putString("GOAL_TEXT", view.addGoalFragmentGoalText.text.toString())
                savedInstanceState?.putString("GOAL_CATEGORY", view.addGoalFragmentCategoryList.text.toString())
                // + настройки фона и т.д.

                dismiss()
            }
        }
    }
}
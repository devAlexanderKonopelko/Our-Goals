package by.konopelko.ourgoals.goals.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.database.Task
import by.konopelko.ourgoals.temporaryData.GoalCollection
import by.konopelko.ourgoals.goals.add.recyclerTasks.AddTaskSingleton
import by.konopelko.ourgoals.goals.add.recyclerTasks.TaskAdapter
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import kotlinx.android.synthetic.main.fragment_add_goal_tasks.*

class FragmentAddTasks(val previousDialog: FragmentAddGoal) : DialogFragment() {

    interface RefreshGoalsListInterface {
        fun refreshGoalsRecyclerView(goal: Goal)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_goal_tasks, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        addTasksRecycler.adapter = TaskAdapter(AddTaskSingleton.instance.taskList)
        addTasksRecycler.layoutManager = LinearLayoutManager(view.context)
        addTasksRecycler.setHasFixedSize(true)

        AddTaskSingleton.instance.taskToComplete = 0

        addTaskFragmentAddButton.setOnClickListener {
            if (AddTaskSingleton.instance.taskToComplete == 0) {
                AddTaskSingleton.instance.taskList.add(Task("", "", false))

                (addTasksRecycler.adapter as TaskAdapter).notifyItemInserted(AddTaskSingleton.instance.taskList.size - 1)
                addTasksRecycler.scrollToPosition(AddTaskSingleton.instance.taskList.size - 1)

                AddTaskSingleton.instance.taskToComplete = 1
            }
            else {
                Toast.makeText(this.context, "Завершите редактирование задачи", Toast.LENGTH_SHORT).show()
            }
        }
        addGoalFragmentBackButton.setOnClickListener {
            if (AddTaskSingleton.instance.taskToComplete == 0) {
                dismiss()

                fragmentManager?.let { fm -> previousDialog.show(fm, "") }
            }
            else {
                Toast.makeText(this.context, "Завершите редактирование задачи", Toast.LENGTH_SHORT).show()
            }
        }
        addGoalFragmentFinishButton.setOnClickListener {
            val category = NewGoal.instance.goal.category
            val isDone = NewGoal.instance.goal.isDone
            val isSocial = NewGoal.instance.goal.isSocial
            val progress = NewGoal.instance.goal.progress
            val tasks = AddTaskSingleton.instance.taskList
            val text = NewGoal.instance.goal.text

            val goal = Goal(
                category,
                text,
                progress,
                tasks,
                isDone,
                isSocial
            )

            // refreshing recycler with goals and adding new goal to database
            activity?.run {
                val refresh = this as RefreshGoalsListInterface
                refresh.refreshGoalsRecyclerView(goal)
            }

            dismiss()
        }
    }

}
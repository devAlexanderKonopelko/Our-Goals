package by.konopelko.ourgoals.goals.add.recyclerTasks

import android.app.DatePickerDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Task
import by.konopelko.ourgoals.temporaryData.GoalCollection
import kotlinx.android.synthetic.main.item_recycler_add_task.view.*
import kotlinx.android.synthetic.main.item_recycler_complete_task.view.*
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapter(val list: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(), DatePickerDialog.OnDateSetListener {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_complete_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val view = holder.itemView

        view.itemTaskCompleteText.text =
            AddTaskSingleton.instance.taskList[position].text

        view.itemTaskCompleteDate.text =
            AddTaskSingleton.instance.taskList[position].finishDate

        if (view.itemTaskCompleteText.text.isNotEmpty()) {
            view.itemAddTaskLayout.visibility = View.GONE
            view.itemTaskCompleteLayout.visibility = View.VISIBLE
        }
        else {
            view.itemAddTaskLayout.visibility = View.VISIBLE
            view.itemTaskCompleteLayout.visibility = View.GONE
        }

        view.itemAddTaskText.text?.clear()
        view.itemAddTaskDate.text = ""

        var text: String
        var date: String

        view.itemAddTaskDatePickButton.setOnClickListener {
            val calendar= Calendar.getInstance()
            val yearCurrent = calendar.get(Calendar.YEAR)
            val monthCurrent = calendar.get(Calendar.MONTH)
            val dayCurrent = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(view.context, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                view.itemAddTaskDate.text =
                    "" + dayOfMonth + " - " + (monthOfYear + 1) + " - " + year

            }, yearCurrent, monthCurrent, dayCurrent)

            datePickerDialog.show()

        }

        view.itemAddTaskAddButton.setOnClickListener {
            if (view.itemAddTaskText.text.toString().isNotEmpty() &&
                view.itemAddTaskDate.text.toString().isNotEmpty()
            ) {
                text = view.itemAddTaskText.text.toString()
                date = view.itemAddTaskDate.text.toString()

                AddTaskSingleton.instance.taskList[AddTaskSingleton.instance.taskList.size - 1] =
                    Task(
                        text,
                        date,
                        false
                    )

                Log.e("GOALS IN PROGRESS", "${GoalCollection.instance.goalsInProgressList}")

                view.itemAddTaskLayout.visibility = View.GONE
                view.itemTaskCompleteLayout.visibility = View.VISIBLE
                view.itemTaskCompleteText.text =
                    AddTaskSingleton.instance.taskList[AddTaskSingleton.instance.taskList.size - 1].text
                view.itemTaskCompleteDate.text =
                    AddTaskSingleton.instance.taskList[AddTaskSingleton.instance.taskList.size - 1].finishDate

                AddTaskSingleton.instance.taskToComplete = 0
            }
        }
        view.itemAddTaskDeleteButton.setOnClickListener {
            // deleting new task
            view.itemAddTaskText.text?.clear()

            (list as ArrayList<Task>).removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

            AddTaskSingleton.instance.taskToComplete = 0
        }
        view.itemTaskCompleteDeleteButton.setOnClickListener {
            // deleting completed task

            (list as ArrayList<Task>).removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
        view.itemTaskCompleteEditButton.setOnClickListener {
            // editing completed task
            if (AddTaskSingleton.instance.taskToComplete == 1) {
                Toast.makeText(view.context, view.context.getString(R.string.toast_finishTastEdit), Toast.LENGTH_SHORT).show()
            }
            else {
                view.itemAddTaskLayout.visibility = View.VISIBLE
                view.itemTaskCompleteLayout.visibility = View.GONE

                view.itemAddTaskText.setText(view.itemTaskCompleteText.text)
                view.itemAddTaskDate.text = view.itemTaskCompleteDate.text

                AddTaskSingleton.instance.taskToComplete = 1
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

    }
}
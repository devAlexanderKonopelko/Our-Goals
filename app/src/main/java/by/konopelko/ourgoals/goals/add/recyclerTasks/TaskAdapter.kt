package by.konopelko.ourgoals.goals.add.recyclerTasks

import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Task
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
        val taskView = holder.itemView

        taskView.itemTaskCompleteText.text =
            AddTaskSingleton.instance.taskList[position].text

        taskView.itemTaskCompleteDate.text =
            AddTaskSingleton.instance.taskList[position].finishDate

        if (taskView.itemTaskCompleteText.text.isNotEmpty()) {
            taskView.itemAddTaskLayout.visibility = View.GONE
            taskView.itemTaskCompleteLayout.visibility = View.VISIBLE
        }
        else {
            taskView.itemAddTaskLayout.visibility = View.VISIBLE
            taskView.itemTaskCompleteLayout.visibility = View.GONE
        }

        taskView.itemAddTaskText.text?.clear()
        taskView.itemAddTaskDate.text = ""

        var text: String
        var date: String

        taskView.itemAddTaskDatePickButton.setOnClickListener {
            val calendar= Calendar.getInstance()
            val yearCurrent = calendar.get(Calendar.YEAR)
            val monthCurrent = calendar.get(Calendar.MONTH)
            val dayCurrent = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(taskView.context, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                taskView.itemAddTaskDate.text =
                    "" + dayOfMonth + " - " + (monthOfYear + 1) + " - " + year

            }, yearCurrent, monthCurrent, dayCurrent)

            datePickerDialog.show()

        }

        taskView.itemAddTaskAddButton.setOnClickListener {
            if (taskView.itemAddTaskText.text.toString().isNotEmpty() &&
                taskView.itemAddTaskDate.text.toString().isNotEmpty()
            ) {
                text = taskView.itemAddTaskText.text.toString()
                date = taskView.itemAddTaskDate.text.toString()

                AddTaskSingleton.instance.taskList[AddTaskSingleton.instance.taskList.size - 1] =
                    Task(text, date, false)

                taskView.itemAddTaskLayout.visibility = View.GONE
                taskView.itemTaskCompleteLayout.visibility = View.VISIBLE
                taskView.itemTaskCompleteText.text =
                    AddTaskSingleton.instance.taskList[AddTaskSingleton.instance.taskList.size - 1].text
                taskView.itemTaskCompleteDate.text =
                    AddTaskSingleton.instance.taskList[AddTaskSingleton.instance.taskList.size - 1].finishDate

                AddTaskSingleton.instance.taskToComplete = 0
            }
        }
        taskView.itemAddTaskDeleteButton.setOnClickListener {
            // deleting new task
            taskView.itemAddTaskText.text?.clear()

            (list as ArrayList<Task>).removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

            AddTaskSingleton.instance.taskToComplete = 0
        }
        taskView.itemTaskCompleteDeleteButton.setOnClickListener {
            // deleting completed task

            (list as ArrayList<Task>).removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
        taskView.itemTaskCompleteEditButton.setOnClickListener {
            // editing completed task
            if (AddTaskSingleton.instance.taskToComplete == 1) {
                Toast.makeText(taskView.context, "Завершите редактирование задачи", Toast.LENGTH_SHORT).show()
            }
            else {
                taskView.itemAddTaskLayout.visibility = View.VISIBLE
                taskView.itemTaskCompleteLayout.visibility = View.GONE

                taskView.itemAddTaskText.setText(taskView.itemTaskCompleteText.text)
                taskView.itemAddTaskDate.text = taskView.itemTaskCompleteDate.text

                AddTaskSingleton.instance.taskToComplete = 1
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

    }
}
package by.konopelko.ourgoals.goals.add.recyclerTasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Task
import kotlinx.android.synthetic.main.item_recycler_add_task.view.*
import kotlinx.android.synthetic.main.item_recycler_task.view.*

class TaskAdapter(val list: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_task, parent, false)
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

        taskView.itemAddTaskText.text?.clear()
        taskView.itemAddTaskDate.text = ""

        var text: String
        var date: String

        taskView.itemAddTaskDate.text = "21.12.2019"
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
    }
}
package by.konopelko.ourgoals.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Task
import kotlinx.android.synthetic.main.item_recycler_notification_tasks.view.*

class AdapterNotificationTasks(val tasksList: ArrayList<Task>) :
    RecyclerView.Adapter<AdapterNotificationTasks.NotificationTasksViewHolder>() {
    class NotificationTasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationTasksViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_notification_tasks, parent, false)

        return NotificationTasksViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: NotificationTasksViewHolder, position: Int) {
        // setting task's fields
        val view = holder.itemView
        val task = tasksList[position]
        view.itemNotificationRecyclerTaskText.text = task.text
        view.itemNotificationRecyclerTaskDate.text = task.finishDate

    }
}
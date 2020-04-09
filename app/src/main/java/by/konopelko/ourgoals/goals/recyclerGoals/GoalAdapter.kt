package by.konopelko.ourgoals.goals.recyclerGoals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Goal
import kotlinx.android.synthetic.main.item_recycler_goal.view.*

class GoalAdapter(val list: List<Goal>) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_goal, parent, false)
        return GoalViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goalView = holder.itemView

        goalView.itemGoalCategory.text = list[position].category
        goalView.itemGoalText.text = list[position].text
        goalView.itemGoalProgressBarIndicator.progress = list[position].progress
        goalView.itemGoalProgressBarValue.text = list[position].progress.toString() + "%"

        if (list[position].isDone) goalView.itemGoalCompleteButton.visibility = View.VISIBLE
        else goalView.itemGoalCompleteButton.visibility = View.GONE

        if (list[position].isSocial) goalView.itemGoalSocialButton.visibility = View.VISIBLE
        else goalView.itemGoalSocialButton.visibility = View.INVISIBLE
    }

    fun addGoalToRecycler(goal: Goal) {
        (list as ArrayList<Goal>).add(goal)
        notifyDataSetChanged()
    }
}
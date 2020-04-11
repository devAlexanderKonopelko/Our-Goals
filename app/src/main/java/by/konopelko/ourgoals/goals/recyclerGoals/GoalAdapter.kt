package by.konopelko.ourgoals.goals.recyclerGoals

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.goals.FragmentGoals
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.GoalCollection
import kotlinx.android.synthetic.main.item_recycler_goal.view.*
import kotlinx.coroutines.*

class GoalAdapter(val list: List<Goal>, val fragmentGoals: FragmentGoals) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

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

        goalView.itemGoalDeleteButton.setOnClickListener {
            //deleting a goal from database a updating goal recycler view
            //BUG. DELETED FROM THE SECOND TRY.
            removeGoal(list[position])
        }
    }

    fun addGoalToRecycler(goal: Goal) {

        CoroutineScope(Dispatchers.IO).launch {
            fragmentGoals.context?.let {
                goal.id = DatabaseOperations.getInstance(it).addGoaltoDatabase(goal).await().toInt()
            }
            if (goal.id != null) {
                GoalCollection.instance.addGoal(goal)

                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
            }

        }
    }

    fun removeGoal(goal: Goal) {
        fragmentGoals.activity?.run {
            Log.e("-----ENTRANCE------", "removeGoal(), fragmentGoals.activity?.run{...}")
            goal.id?.let { DatabaseOperations.getInstance(this).removeGoalfromDatabase(it) }
        }

        GoalCollection.instance.removeGoal(goal)
        notifyDataSetChanged()
    }
}
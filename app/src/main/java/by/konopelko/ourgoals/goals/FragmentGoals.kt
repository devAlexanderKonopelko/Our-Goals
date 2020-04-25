package by.konopelko.ourgoals.goals

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.goals.recyclerGoals.GoalAdapter
import by.konopelko.ourgoals.temporaryData.GoalCollection
import by.konopelko.ourgoals.temporaryData.SocialGoalCollection
import kotlinx.android.synthetic.main.fragment_goals.*

class FragmentGoals : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(GoalCollection.instance.visible) {
            val goalsList = GoalCollection.instance.goalsList
            goalsRecyclerView.adapter = GoalAdapter(goalsList, this@FragmentGoals)

            Log.e("ADAPTER LIST SIZE: ", " ${goalsList.size}")
        }
        if (SocialGoalCollection.instance.visible) {
            val socialGoals = SocialGoalCollection.instance.goalList
            goalsRecyclerView.adapter = GoalAdapter(socialGoals, this)

            Log.e("ADAPTER LIST SIZE: ", " ${socialGoals.size}")
        }


        (goalsRecyclerView.adapter as GoalAdapter).notifyDataSetChanged()

        goalsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        goalsRecyclerView.setHasFixedSize(true)
    }

    fun refreshRecycler(goal: Goal) {
        (goalsRecyclerView.adapter as GoalAdapter).addGoalToRecycler(goal)
    }

    fun showLocalGoals() {
        GoalCollection.instance.visible = true
        SocialGoalCollection.instance.visible = false

        val localGoals = GoalCollection.instance.goalsList
        goalsRecyclerView.adapter = GoalAdapter(localGoals, this)
        (goalsRecyclerView.adapter as GoalAdapter).notifyDataSetChanged()
    }

    fun showSocialGoals() {
        GoalCollection.instance.visible = false
        SocialGoalCollection.instance.visible = true

        val socialGoals = SocialGoalCollection.instance.goalList
        goalsRecyclerView.adapter = GoalAdapter(socialGoals, this)
        (goalsRecyclerView.adapter as GoalAdapter).notifyDataSetChanged()
    }

    fun updateRecyclerItemProgress(goalPosition: Int) {
        (goalsRecyclerView.adapter as GoalAdapter).updateGoalProgress(goalPosition)
    }
}
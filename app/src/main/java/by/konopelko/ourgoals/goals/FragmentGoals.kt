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
import by.konopelko.ourgoals.temporaryData.DatabaseOperations
import by.konopelko.ourgoals.temporaryData.GoalCollection
import kotlinx.android.synthetic.main.fragment_goals.*
import kotlinx.coroutines.*

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

        val goalsList = GoalCollection.instance.goalsList
        goalsRecyclerView.adapter = GoalAdapter(goalsList, this@FragmentGoals)
        goalsRecyclerView.adapter?.notifyDataSetChanged()

        goalsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        goalsRecyclerView.setHasFixedSize(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun refreshRecycler(goal: Goal) {
        (goalsRecyclerView.adapter as GoalAdapter).addGoalToRecycler(goal)
    }
}
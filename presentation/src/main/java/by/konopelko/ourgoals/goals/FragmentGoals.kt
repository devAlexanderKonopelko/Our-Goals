package by.konopelko.ourgoals.goals

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.Goal
import by.konopelko.ourgoals.goals.recyclerGoals.GoalAdapter
import by.konopelko.ourgoals.temporaryData.GoalCollection
import by.konopelko.ourgoals.temporaryData.SocialGoalCollection
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_goals.*
import java.util.ArrayList

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
            val goalsList = GoalCollection.instance.goalsInProgressList
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

        goalsRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                activity?.run {
                    if (dy != 0 && goalsAddButton.isShown) {
                        goalsAddButton.hide()
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    activity?.run {
                        goalsAddButton.show()
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    fun refreshRecycler(goal: Goal) {
        (goalsRecyclerView.adapter as GoalAdapter).addGoalToRecycler(goal)
    }

    fun showLocalGoals() {
        GoalCollection.instance.visible = true
        SocialGoalCollection.instance.visible = false

        val localGoals = GoalCollection.instance.goalsInProgressList
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

    fun updateGoals(goals: ArrayList<Goal>) {
        goalsRecyclerView.adapter = GoalAdapter(goals, this)
        (goalsRecyclerView.adapter as GoalAdapter).notifyDataSetChanged()
    }

    fun changeGoalProgress(goalPosition: Int, progress: Int): Goal {
        return (goalsRecyclerView.adapter as GoalAdapter).changeGoalProgress(goalPosition, progress)
    }
}
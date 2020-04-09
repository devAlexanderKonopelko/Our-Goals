package by.konopelko.ourgoals.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.Goal
import by.konopelko.ourgoals.goals.add.FragmentAddGoal
import by.konopelko.ourgoals.goals.add.FragmentAddTasks
import by.konopelko.ourgoals.goals.add.recyclerTasks.AddTaskSingleton
import by.konopelko.ourgoals.goals.recyclerGoals.GoalAdapter
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val database = this.context?.let { GoalSingleton.getInstance(it).database }

        CoroutineScope(Dispatchers.IO).launch {

            database?.clearAllTables() // clears database
            val goalsList = database?.getGoalDao()?.getAllGoals()

            withContext(Dispatchers.Main) {
                goalsRecyclerView.adapter = goalsList?.let { GoalAdapter(it) }
            }
        }

        goalsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        goalsRecyclerView.setHasFixedSize(true)

        goalsAddButton.setOnClickListener {
            // очищаем список задач для цели
            AddTaskSingleton.instance.taskList.clear()

            // надувание диалога
            val addDialog = FragmentAddGoal()
            activity?.supportFragmentManager?.let { supportFM -> addDialog.show(supportFM, "") }
        }
    }

    fun refreshRecycler(goal: Goal) {
        (goalsRecyclerView.adapter as GoalAdapter).addGoalToRecycler(goal)
    }

}
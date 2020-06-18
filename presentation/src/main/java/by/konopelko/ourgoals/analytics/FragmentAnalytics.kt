package by.konopelko.ourgoals.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.temporaryData.AnalyticsSingleton
import kotlinx.android.synthetic.main.fragment_analytics.*

class FragmentAnalytics: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goalsCompleted = AnalyticsSingleton.instance.analytics.goalsCompleted
        val goalsSet = AnalyticsSingleton.instance.analytics.goalsSet
        val tasksCompleted = AnalyticsSingleton.instance.analytics.tasksCompleted
        val tasksSet = AnalyticsSingleton.instance.analytics.tasksSet

        analyticsGoalsCompleted.text = goalsCompleted.toString()
        analyticsGoalsSet.text = goalsSet.toString()
        analyticsTasksCompleted.text = tasksCompleted.toString()
        analyticsTasksSet.text = tasksSet.toString()
    }
}
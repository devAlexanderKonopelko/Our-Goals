package by.konopelko.ourgoals.notifications

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import kotlinx.android.synthetic.main.item_recycler_notifications_goals.*
import kotlinx.android.synthetic.main.item_recycler_notifications_goals.view.*

class FragmentNotificationDetails(val viewInfo: View): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemNotificationGoalText.text = viewInfo.itemNotificationGoalText.text
        itemNotificationGoalTasksList.adapter = viewInfo.itemNotificationGoalTasksList.adapter
        itemNotificationGoalTasksList.layoutManager = LinearLayoutManager(context)
        itemNotificationGoalTasksList.setHasFixedSize(true)

    }
}
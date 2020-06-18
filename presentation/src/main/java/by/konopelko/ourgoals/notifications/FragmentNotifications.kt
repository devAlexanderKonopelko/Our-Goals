package by.konopelko.ourgoals.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.temporaryData.NotificationsCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_notifications.*

class FragmentNotifications : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendsRequests = NotificationsCollection.instance.friendsRequests
        val goalsRequests = NotificationsCollection.instance.goalsRequests
        val goalsRequestsSenders = NotificationsCollection.instance.goalsRequestsSenders
        val goalsRequestsGNumber = NotificationsCollection.instance.goalsRequestsGoalKeys
        val requestsKeys = NotificationsCollection.instance.requestsKeys

        this.context?.let { ctx ->
            fragmentManager?.let { fm ->
                fragmentNotifRecyclerView.adapter = activity?.let {
                    AdapterNotifications(
                        friendsRequests,
                        goalsRequests,
                        goalsRequestsSenders,
                        goalsRequestsGNumber,
                        requestsKeys,
                        fm,
                        ctx,
                        it
                    )
                }
            }

        }

        fragmentNotifRecyclerView.layoutManager = LinearLayoutManager(this.context)
        fragmentNotifRecyclerView.setHasFixedSize(true)

        fragmentNotifBackButton.setOnClickListener {
            dismiss()
        }
    }

}
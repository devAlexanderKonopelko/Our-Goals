package by.konopelko.ourgoals.goals.recyclerFriendsProgress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.User
import kotlinx.android.synthetic.main.item_recycler_friends_progress.view.*

class AdapterFriendsProgress(
    val usersList: ArrayList<User>,
    val progressList: ArrayList<Int>,
    val goalKey: String
): RecyclerView.Adapter<AdapterFriendsProgress.FriendsProgressViewHolder>() {
    class FriendsProgressViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsProgressViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_friends_progress, parent, false)
        return FriendsProgressViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: FriendsProgressViewHolder, position: Int) {
        val view = holder.itemView

        view.itemFriendsProgressName.text = usersList[position].name
        view.itemFriendsProgressValue.progress = progressList[position]

        if(progressList[position] < 100) {
            view.itemFriendsProgressImageStatus.setImageResource(R.drawable.icon_friend_in_progress)
        }
        else {
            view.itemFriendsProgressImageStatus.setImageResource(R.drawable.icon_friend_done)
        }
    }
}
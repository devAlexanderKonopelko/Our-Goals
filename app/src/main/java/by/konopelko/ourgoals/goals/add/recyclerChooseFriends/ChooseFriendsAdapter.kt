package by.konopelko.ourgoals.goals.add.recyclerChooseFriends

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.User
import by.konopelko.ourgoals.goals.add.NewGoal
import by.konopelko.ourgoals.temporaryData.GoalReceiversCollection
import kotlinx.android.synthetic.main.item_recycler_choose_friends.view.*

class ChooseFriendsAdapter(
    val friendsList: ArrayList<User>,
    val context: Context
) : RecyclerView.Adapter<ChooseFriendsAdapter.ChooseFriendsViewHolder>() {
    class ChooseFriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseFriendsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_choose_friends, parent, false)
        return ChooseFriendsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }


    override fun onBindViewHolder(holder: ChooseFriendsViewHolder, position: Int) {
        val view = holder.itemView

        view.itemChooseFriendsName.text = friendsList[position].name

        Log.e("NEW GOAL:", "${NewGoal.instance.goal}")

        view.itemChooseFriendsAddButton.setOnClickListener {
            Toast.makeText(context, "Запрос отправлен", Toast.LENGTH_SHORT).show()

            // меняем вид кнопки на "Ожидание" и показываем кнопку "отменить запрос"
            view.itemChooseFriendsAddButton.setImageResource(R.drawable.icon_waitnig_friend_response)
            view.itemChooseFriendsAddButton.isEnabled = false
            view.itemChooseFriendsWaitingTitle.visibility = View.VISIBLE

            view.itemChooseFriendsCancelReqButton.visibility = View.VISIBLE
            view.itemChooseFriendsCancelTitle.visibility = View.VISIBLE

            // добавляем в коллекцию id получателя
            addNewReceiverId(position)
        }
    }

    private fun addNewReceiverId(position: Int) {
        GoalReceiversCollection.instance.receiversIds.add(friendsList[position].id)
    }

}
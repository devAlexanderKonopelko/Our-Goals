package by.konopelko.ourgoals.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.User
import by.konopelko.ourgoals.temporaryData.FriendsListCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_recycler_friends.view.*

// TODO: убрать логику для received запросов (перенесена в нотификации)

class FriendsAdapter(
    val list: ArrayList<User>,
    val keysList: ArrayList<String>,
    val context: Context
) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {
    class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val friendRequestDatabase =
        FirebaseDatabase.getInstance().reference.child("FriendRequests")
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_friends, parent, false)

//        currentReqState = "not_friends"

        return FriendsViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val view = holder.itemView

        view.deleteNotificationButtonFriends.visibility = View.GONE

        // засеттить элемент в зависимостри от состояния: друг, запрос входящий/исходящий
        view.itemFriendsName.text = list[position].name

        if (keysList[position] == "sent") {
            view.itemFriendsWaitingButton.isEnabled = false
            view.itemFriendsCancelReqButton.setOnClickListener {
                Toast.makeText(context, "Запрос отменён", Toast.LENGTH_SHORT).show()

                cancelRequest(position)
            }
        }
//        else if (keysList[position] == "received") {
//            friendsView.itemFriendsWaitingButton.isEnabled = true
//            friendsView.itemFriendsWaitingButton.setImageResource(R.drawable.icon_accept_request)
//            friendsView.itemFriendsWaitingTitle.text = "Принять \n запрос"
//            friendsView.itemFriendsCancelTitle.text = "Отклонить \n запрос"
//
//            friendsView.itemFriendsWaitingButton.setOnClickListener {
//                acceptRequest(position)
//            }
//            friendsView.itemFriendsCancelReqButton.setOnClickListener {
//                declineRequest(position)
//            }
//        }
        else if (keysList[position] == "friends") {
            view.itemFriendsWaitingButton.visibility = View.GONE
            view.itemFriendsWaitingTitle.visibility = View.GONE
            view.itemFriendsCancelTitle.text = "Удалить из\n друзей"

            view.itemFriendsCancelReqButton.setOnClickListener {
                unfriendRequest(position)
            }
        }
    }

    private fun unfriendRequest(position: Int) {
        val ourId = auth.currentUser!!.uid
        val senderId = list[position].id

        userDatabase.child(ourId).child("friends").child(senderId).removeValue().addOnSuccessListener {
            userDatabase.child(senderId).child("friends").child(ourId).removeValue().addOnSuccessListener {
                Toast.makeText(context, "Удаление успешно!", Toast.LENGTH_SHORT).show()
                removeItem(position)
            }
        }
    }

//    private fun declineRequest(position: Int) {
//        val ourId = auth.currentUser!!.uid
//        val senderId = list[position].id
//        deleteRequest(position)
//    }
//
//    private fun acceptRequest(position: Int) {
//        val ourId = auth.currentUser!!.uid
//        val senderId = list[position].id
//
//        userDatabase.child(ourId).child("friends").child(senderId).setValue("friend").addOnSuccessListener {
//            userDatabase.child(senderId).child("friends").child(ourId).setValue("friend").addOnSuccessListener {
//                Toast.makeText(context, "Теперь вы друзья!", Toast.LENGTH_SHORT).show()
//                deleteRequest("friends", position)
//            }
//        }
//    }

//    private fun deleteRequest(state: String, position: Int) {
//        val ourId = auth.currentUser!!.uid
//        val senderId = list[position].id
//
//        friendRequestDatabase.child(ourId).child(senderId).removeValue().addOnSuccessListener {
//            friendRequestDatabase.child(senderId).child(ourId).removeValue().addOnSuccessListener {
//                changeItem(state, position)
//            }
//        }
//    }
//
//    private fun deleteRequest(position: Int) {
//        val ourId = auth.currentUser!!.uid
//        val senderId = list[position].id
//
//        friendRequestDatabase.child(ourId).child(senderId).removeValue().addOnSuccessListener {
//            friendRequestDatabase.child(senderId).child(ourId).removeValue().addOnSuccessListener {
//                removeItem(position)
//            }
//        }
//    }

//    private fun changeItem(state: String, position: Int) {
//        FriendsListCollection.instance.keysList[position] = state
//        notifyDataSetChanged()
//    }

    private fun cancelRequest(position: Int) {
        val senderId = auth.currentUser!!.uid
        val receiverId = list[position].id

        friendRequestDatabase.child(senderId).child(receiverId).removeValue().addOnSuccessListener {
                friendRequestDatabase.child(receiverId).child(senderId).removeValue().addOnSuccessListener {
                        Toast.makeText(context, "Запрос отменён", Toast.LENGTH_SHORT).show()
                        removeItem(position)
                    }
            }

    }

    private fun removeItem(position: Int) {
        FriendsListCollection.instance.friendsList.removeAt(position)
        FriendsListCollection.instance.keysList.removeAt(position)
        notifyDataSetChanged()
    }
}
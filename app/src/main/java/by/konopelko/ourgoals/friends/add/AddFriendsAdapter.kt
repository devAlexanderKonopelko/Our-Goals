package by.konopelko.ourgoals.friends.add

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_recycler_add_friends.view.*

class AddFriendsAdapter(val list: ArrayList<User>, val context: Context) :
    RecyclerView.Adapter<AddFriendsAdapter.AddFriendsViewHolder>() {
    class AddFriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private lateinit var currentReqState: String
    private val friendRequestDatabase =
        FirebaseDatabase.getInstance().reference.child("FriendRequests")
    val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_add_friends, parent, false)

        currentReqState = "not_friends"

        return AddFriendsViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AddFriendsViewHolder, position: Int) {
        val friendsView = holder.itemView

        checkIfFriends(friendsView, position)

        friendsView.itemAddFriendsName.text = list[position].name
        friendsView.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_request_friendship)
        friendsView.itemAddFriendsCancelReqButton.visibility = View.INVISIBLE
        friendsView.itemAddFriendsWaitingTitle.visibility = View.INVISIBLE
        friendsView.itemAddFriendsCancelTitle.visibility = View.INVISIBLE

        // sending request
        friendsView.itemAddFriendsSendReqButton.setOnClickListener {
            friendsView.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_waitnig_friend_response)
            friendsView.itemAddFriendsSendReqButton.isEnabled = false
            friendsView.itemAddFriendsCancelReqButton.visibility = View.VISIBLE
            friendsView.itemAddFriendsWaitingTitle.visibility = View.VISIBLE
            friendsView.itemAddFriendsCancelTitle.visibility = View.VISIBLE
            friendsView.itemAddFriendsCancelTitle.text = "Отменить \n запрос"

            //firebase request
            sendRequest(position)
        }

        // cancelling request
        friendsView.itemAddFriendsCancelReqButton.setOnClickListener {
            Toast.makeText(context, "Запрос отменён", Toast.LENGTH_SHORT).show()

            friendsView.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_request_friendship)
            friendsView.itemAddFriendsSendReqButton.isEnabled = true
            friendsView.itemAddFriendsCancelReqButton.visibility = View.INVISIBLE
            friendsView.itemAddFriendsWaitingTitle.visibility = View.INVISIBLE
            friendsView.itemAddFriendsCancelTitle.visibility = View.INVISIBLE

            //firebase cancelling request
            cancelRequest(position)
        }
    }

    private fun checkIfFriends(friendsView: View, position: Int) {
        val currentUserId = auth.currentUser!!.uid
        userDatabase.child(currentUserId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(currentUser: DataSnapshot) {
                if (currentUser.child("friends").hasChild(list[position].id)) {
                    friendsView.itemAddFriendsSendReqButton.visibility = View.INVISIBLE
                    friendsView.itemAddFriendsWaitingTitle.visibility = View.INVISIBLE

                    friendsView.itemAddFriendsCancelReqButton.visibility = View.INVISIBLE
                    friendsView.itemAddFriendsCancelTitle.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    // sending request via firebase database
    private fun sendRequest(position: Int) {
        val senderId = auth.currentUser!!.uid
        val receiverId = list[position].id

        if (currentReqState == "not_friends") {
            friendRequestDatabase.child(senderId).child(receiverId).child("request_type")
                .setValue("sent").addOnCompleteListener {
                    if (it.isSuccessful) {
                        friendRequestDatabase.child(receiverId).child(senderId)
                            .child("request_type").setValue("received").addOnSuccessListener {
                                Toast.makeText(context, "Запрос отправлен", Toast.LENGTH_SHORT)
                                    .show()
                                currentReqState = "request_sent"
                            }
                    } else {
                        Toast.makeText(context, "Ошибка выполнения запроса", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

        }
    }

    // cancelling request via firebase database
    private fun cancelRequest(position: Int) {
        val senderId = auth.currentUser!!.uid
        val receiverId = list[position].id

        if (currentReqState == "request_sent") {
            friendRequestDatabase.child(senderId).child(receiverId).removeValue()
                .addOnSuccessListener {
                    friendRequestDatabase.child(receiverId).child(senderId).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Запрос отменён", Toast.LENGTH_SHORT).show()
                            currentReqState = "not_friends"
                        }
                }
        }
    }

}
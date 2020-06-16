package by.konopelko.ourgoals.friends.add

import android.content.Context
import android.util.Log
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
        val view = holder.itemView

        view.itemAddFriendsName.text = list[position].name
        view.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_request_friendship)
        view.itemAddFriendsCancelReqButton.visibility = View.GONE
        view.itemAddFriendsWaitingTitle.visibility = View.GONE
        view.itemAddFriendsCancelTitle.visibility = View.GONE

        checkIfFriends(view, position)
        checkIfRequestSent(view, position)

        // sending request
        view.itemAddFriendsSendReqButton.setOnClickListener {
            view.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_waitnig_friend_response)
            view.itemAddFriendsSendReqButton.isEnabled = false
            view.itemAddFriendsCancelReqButton.visibility = View.VISIBLE
            view.itemAddFriendsWaitingTitle.visibility = View.VISIBLE
            view.itemAddFriendsWaitingTitle.text = context.getString(R.string.add_friends_waitingResponse)
            view.itemAddFriendsCancelTitle.visibility = View.VISIBLE
            view.itemAddFriendsCancelTitle.text = context.getString(R.string.add_friends_cancelRequest)

            //firebase request
            sendRequest(position)
        }

        // cancelling request
        view.itemAddFriendsCancelReqButton.setOnClickListener {
            view.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_request_friendship)
            view.itemAddFriendsSendReqButton.isEnabled = true
            view.itemAddFriendsCancelReqButton.visibility = View.GONE
            view.itemAddFriendsCancelTitle.visibility = View.GONE
            view.itemAddFriendsWaitingTitle.visibility = View.VISIBLE
            view.itemAddFriendsWaitingTitle.text = context.getString(R.string.add_friend_sendReq)

            //firebase cancelling request
            cancelRequest(position)
        }
    }

    private fun checkIfRequestSent(view: View, position: Int) {
        val currentUserId = auth.currentUser!!.uid

        friendRequestDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(requests: DataSnapshot) {
                if (requests.hasChild(currentUserId)) {
                    // если мы уже отправили запрос на дружбу, то меняем UI
                    if (requests.child(currentUserId).hasChild(list[position].id)) {
                        Log.e("FRIENDS STATUS", ": REQUEST SENT")
                        view.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_waitnig_friend_response)
                        view.itemAddFriendsSendReqButton.isEnabled = false
                        view.itemAddFriendsCancelReqButton.visibility = View.VISIBLE
                        view.itemAddFriendsWaitingTitle.visibility = View.VISIBLE
                        view.itemAddFriendsWaitingTitle.text = context.getString(R.string.add_friends_waitingResponse)
                        view.itemAddFriendsCancelTitle.visibility = View.VISIBLE
                        view.itemAddFriendsCancelTitle.text = context.getString(R.string.add_friends_cancelRequest)

                        currentReqState = "request_sent"
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun checkIfFriends(view: View, position: Int) {
        val currentUserId = auth.currentUser!!.uid
        userDatabase.child(currentUserId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(currentUser: DataSnapshot) {
                if (currentUser.child("friends").hasChild(list[position].id)) {
                    Log.e("FRIENDS STATUS", ": FRIENDS")
                    view.itemAddFriendsSendReqButton.visibility = View.GONE
                    view.itemAddFriendsWaitingTitle.visibility = View.GONE

                    view.itemAddFriendsCancelReqButton.visibility = View.GONE
                    view.itemAddFriendsCancelTitle.visibility = View.GONE
                }
                else {
                    Log.e("FRIENDS STATUS", ": NOT FRIENDS")
                    view.itemAddFriendsSendReqButton.setImageResource(R.drawable.icon_request_friendship)
                    view.itemAddFriendsSendReqButton.visibility = View.VISIBLE
                    view.itemAddFriendsWaitingTitle.visibility = View.VISIBLE
                    view.itemAddFriendsWaitingTitle.text = context.getString(R.string.add_friend_sendReq)
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
                                Toast.makeText(context, context.getString(R.string.toast_requestSent), Toast.LENGTH_SHORT)
                                    .show()
                                currentReqState = "request_sent"
                            }
                    } else {
                        Toast.makeText(context, context.getString(R.string.toast_reqError), Toast.LENGTH_SHORT)
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
                            Toast.makeText(context, context.getString(R.string.toast_reqCanceled), Toast.LENGTH_SHORT).show()
                            currentReqState = "not_friends"
                        }
                }
        }
    }

}
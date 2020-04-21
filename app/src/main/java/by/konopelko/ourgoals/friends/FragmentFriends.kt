package by.konopelko.ourgoals.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.User
import by.konopelko.ourgoals.temporaryData.FriendsListCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.coroutines.*

class FragmentFriends : DialogFragment() {
    private val friendRequestDatabase =
        FirebaseDatabase.getInstance().reference.child("FriendRequests")
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        получить все запросы в друзья исходящие для текущего пользователя + список друзей.
        findRequests() // выполнится через какое-то время

        val friendsList = FriendsListCollection.instance.friendsList
        val keysList = FriendsListCollection.instance.keysList

        this@FragmentFriends.context?.let {
            friendsFragmentRecyclerView.adapter = FriendsAdapter(friendsList, keysList, it)
        }

        friendsFragmentRecyclerView.layoutManager = LinearLayoutManager(this.context)
        friendsFragmentRecyclerView.setHasFixedSize(true)

        friendsFragmentBackButton.setOnClickListener {
            dismiss()
        }
    }

    private fun clearTempFriendsList() {
        FriendsListCollection.instance.friendsList.clear()
        FriendsListCollection.instance.keysList.clear()
    }

    private fun findRequests() {
        clearTempFriendsList()

        val currentUsedId = auth.currentUser!!.uid
        // добавление существующих друзей в коллекцию
        userDatabase.child(currentUsedId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(currentUser: DataSnapshot) {

                // если у нас уже есть друзья
                if (currentUser.hasChild("friends")) {
                    for (friend in currentUser.child("friends").children) {
                        userDatabase.child(friend.key!!).addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(ourFriend: DataSnapshot) {
                                // добавлять друзей в коллекцию
                                val uid = ourFriend.child("uid").value.toString()
                                val name = ourFriend.child("login").value.toString()

                                FriendsListCollection.instance.friendsList.add(User(uid, name, ArrayList()))

                                // Добавляем в коллекцию ключ о состоянии friends
                                FriendsListCollection.instance.keysList.add("friends")

                                if (this@FragmentFriends.isVisible) {
                                    Log.e("БД НА ДРУЗЕЙ: ", "TRUE")

                                    // обновляем ресайклер по ходу
                                    (friendsFragmentRecyclerView.adapter as FriendsAdapter).notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(p0: DatabaseError) {
                            }
                        })
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

        // добавление запросов в коллекцию и обновление ресайклера
        friendRequestDatabase.child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(requests: DataSnapshot) {
                    if (requests.hasChildren()) {
                        for (request in requests.children) {
                            val uid = request.key!!
                            var name = ""
                            userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(users: DataSnapshot) {
                                    name = users.child(uid).child("login").value.toString()

                                    // записываем пользователей, к которым есть запросы входящие/исходящие

//                                    Log.e("ADD_FRIEND: ", FriendsListCollection.instance.friendsList.size.toString())

                                    // записываем тип запроса к этому пользователю
                                    if (request.child("request_type").value == "sent") {
                                        FriendsListCollection.instance.friendsList.add(
                                            User(
                                                uid,
                                                name,
                                                ArrayList()
                                            )
                                        )
                                        FriendsListCollection.instance.keysList.add("sent")
                                    }

                                    if (this@FragmentFriends.isVisible) {
                                        Log.e("СЕРВЕР ЗАПРОСЫ ДРУЖБЫ: ", "TRUE")

                                        // обновляем ресайклер по ходу
                                        (friendsFragmentRecyclerView.adapter as FriendsAdapter).notifyDataSetChanged()
                                    }
                                }
                                override fun onCancelled(p0: DatabaseError) {
                                }
                            })
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }
}
package by.konopelko.ourgoals.friends.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_add_friends.*

class FragmentAddFriends : DialogFragment() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        AddFriendsCollection.instance.foundFriendsList.clear()

        val friendsList = AddFriendsCollection.instance.foundFriendsList
        this.context?.let {
            addFriendsFragmentRecyclerView.adapter =
                AddFriendsAdapter(
                    friendsList,
                    it
                )
        }
        addFriendsFragmentRecyclerView.layoutManager = LinearLayoutManager(this.context)
        addFriendsFragmentRecyclerView.setHasFixedSize(true)

        searchFriendsButton.setOnClickListener {
            if (!logInEmailField.text.isNullOrEmpty()) {
                // clearing last search results
                AddFriendsCollection.instance.foundFriendsList.clear()

                val loginToFind = logInEmailField.text.toString()

                userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (user in dataSnapshot.children) {
                            if (user.child("login").value.toString().equals(loginToFind)) {
                                val foundUserLogin = user.child("login").value.toString()
                                val foundFriend = User(user.child("uid").value.toString(), foundUserLogin, ArrayList())

                                AddFriendsCollection.instance.foundFriendsList.add(foundFriend)
                                (addFriendsFragmentRecyclerView.adapter as AddFriendsAdapter).notifyDataSetChanged()
                            }
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }
        }

        addFriendsFragmentBackButton.setOnClickListener {
            dismiss()
        }
    }
}
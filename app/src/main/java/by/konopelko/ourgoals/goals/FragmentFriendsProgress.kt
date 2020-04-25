package by.konopelko.ourgoals.goals

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.konopelko.ourgoals.R
import by.konopelko.ourgoals.database.User
import by.konopelko.ourgoals.goals.recyclerFriendsProgress.AdapterFriendsProgress
import by.konopelko.ourgoals.temporaryData.FriendsProgressCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_goals_friends_progress.*

class FragmentFriendsProgress(val goalKey: String, val goalText: String) : DialogFragment() {
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals_friends_progress, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fragmentGoalFriendsProgressText.append(goalText)

        // загрузка пользователей и их прогресса в локальную коллекцию
        setUsersAndProgress(goalKey)

        val usersList = FriendsProgressCollection.instance.userList
        val progressList = FriendsProgressCollection.instance.progressList

        // задавать ресайклер
        fragmentGoalFriendsProgressRecyclerView.adapter =
            AdapterFriendsProgress(usersList, progressList, goalKey)
        fragmentGoalFriendsProgressRecyclerView.layoutManager = LinearLayoutManager(this.context)
        fragmentGoalFriendsProgressRecyclerView.setHasFixedSize(true)

        fragmentGoalFriendsProgressCloseButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        FriendsProgressCollection.instance.userList.clear()
        FriendsProgressCollection.instance.progressList.clear()
        super.onDismiss(dialog)
    }

    private fun setUsersAndProgress(goalKey: String) {
        val currentUserId = auth.currentUser!!.uid
        // загрузка пользователей и их прогресса в локальную коллекцию
        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userDatabaseSnapshot: DataSnapshot) {
                userDatabase.child(currentUserId).child("socialGoals").child(goalKey)
                    .child("friends").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(friends: DataSnapshot) {
                            for (user in friends.children) {

                                userDatabase.child(user.key!!).addListenerForSingleValueEvent(object: ValueEventListener {
                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                        // загружаем данные с Сервера в локальные коллекции
                                        FriendsProgressCollection.instance.userList.add(
                                            User(
                                                userSnapshot.key!!,
                                                userSnapshot.child("login").value.toString(),
                                                ArrayList()
                                            )
                                        )

                                        FriendsProgressCollection.instance.progressList.add(
                                            user.child("progress").value.toString().toInt()
                                        )

                                        // обновляем ресайклер по ходу
                                        (fragmentGoalFriendsProgressRecyclerView.adapter as
                                                AdapterFriendsProgress).notifyDataSetChanged()
                                    }
                                    override fun onCancelled(p0: DatabaseError) {
                                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                    }
                                })
                            }
                        }
                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
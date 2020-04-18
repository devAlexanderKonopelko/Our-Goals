package by.konopelko.ourgoals.friends.add

import by.konopelko.ourgoals.database.User

class AddFriendsCollection {
    val foundFriendsList = ArrayList<User>()

    // коллекция сетится в ресайклер динамически при поиске пользователей.
    companion object {
        val instance = AddFriendsCollection()
    }
}
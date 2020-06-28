package by.konopelko.data.repositories.firebase

import by.konopelko.data.database.entities.User
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepositoryImpl {
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    suspend fun getUserById(uid: String): User {
        val user = userDatabase.child(uid).getSnapshotValue()
        return User(uid, user.child("login").value.toString(), ArrayList())
    }


    private suspend fun DatabaseReference.getSnapshotValue(): DataSnapshot {
        return withContext(Dispatchers.IO) {
            // приостанавливаем выполнение корутины с помощью suspendCoroutine
            suspendCoroutine<DataSnapshot> { continuation -> // с помощью continuation мы сможем продолжить выполнение
                addListenerForSingleValueEvent(MyValueEventListener( // запускаем кастомный слушатель
                    // в качестве параметров передаём две функции, которые продолжат выполнение корутины
                    onDataChange = {
                        continuation.resume(it)
                    },
                    onError = {
                        continuation.resumeWithException(it.toException())
                    }
                ))
            }
        }
    }

    class MyValueEventListener(
        val onDataChange: (DataSnapshot) -> Unit,
        val onError: (DatabaseError) -> Unit
    ) : ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            return onDataChange.invoke(data) // вызываем переданную функцию как только получим data
        }

        override fun onCancelled(error: DatabaseError) {
            return onError.invoke(error)
        }
    }
}
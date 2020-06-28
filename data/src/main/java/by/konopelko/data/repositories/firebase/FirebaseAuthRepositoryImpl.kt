package by.konopelko.data.repositories.firebase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthRepositoryImpl {
    private val auth = FirebaseAuth.getInstance()
    private val userDatabase = FirebaseDatabase.getInstance().reference.child("Users")

    suspend fun logInWithGoogle(
        credential: AuthCredential,
        googleSignInAccount: GoogleSignInAccount
    ): Int {
        var code: Int
        code = signInWithCredential(credential, auth)
        if (code == 0) { // если пользователь успешно зарегистрирован
            // создаётся профиль в Firebase Database
            code = createAccountWithGoogle(googleSignInAccount.displayName)
        }
        return code
    }

    private suspend fun signInWithCredential(credential: AuthCredential, auth: FirebaseAuth): Int {
        return try {
            auth.signInWithCredential(credential).await()
            0
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthException -> 1
                is FirebaseNetworkException -> 2
                else -> 3
            }
        }
    }

    // Доделать вызов этой функции в domain
    private suspend fun createAccountWithGoogle(name: String?): Int {
        var nameExists = false
        val code: Int
        val users = userDatabase.getSnapshotValue()

        // регистритуем пользователя в Firebase бд, если его ещё там нет
        for (user in users.children) {
            if (user.child("login").value.toString().equals(name)) {
                nameExists = true
                break
            }
        }
        if (!nameExists) {
            // adding user to UsersDatabase at Firebase
            val currentUid = auth.currentUser?.uid
            val firebaseDatabaseUser =
                currentUid?.let { uid ->
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(uid)
                }
            val userMap = HashMap<String, String>()
            userMap["uid"] = currentUid.toString()
            name?.let { userMap["login"] = name }

            code = addUserToFirebaseDatabase(firebaseDatabaseUser, userMap)
        } else {
            code = 0
//            onLoggedIn(0, auth.currentUser?.uid)
        }
        return code
    }

    private suspend fun addUserToFirebaseDatabase(
        firebaseDatabaseUser: DatabaseReference?,
        userMap: HashMap<String, String>
    ): Int {
        return try {
            firebaseDatabaseUser?.setValue(userMap)?.await()
            0
        } catch (e: Exception) {
            3
        }
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

    fun getAuthorizedUserId(): String {
        return auth.currentUser?.uid ?: "0"
    }

    suspend fun loInWithEmailPassword(email: String, password: String): Int {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            0
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthException -> 1
                is FirebaseNetworkException -> 2
                else -> 3
            }
        }
    }

}
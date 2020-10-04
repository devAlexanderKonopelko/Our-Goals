package by.konopelko.domain.repositories.firebase

import by.konopelko.data.repositories.firebase.FirebaseNotificationRepositoryImpl

class FirebaseNotificationRepository {
    lateinit var firebaseNotificationRepositoryImpl: FirebaseNotificationRepositoryImpl

    fun observeNotifications() {
        firebaseNotificationRepositoryImpl = FirebaseNotificationRepositoryImpl()
        firebaseNotificationRepositoryImpl.observeNotifications()
    }
}
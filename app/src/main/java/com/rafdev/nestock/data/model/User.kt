package com.rafdev.nestock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val householdIds: List<String> = emptyList(),
    val fcmToken: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

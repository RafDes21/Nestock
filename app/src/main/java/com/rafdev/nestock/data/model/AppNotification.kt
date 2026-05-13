package com.rafdev.nestock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class AppNotification(
    @DocumentId val id: String = "",
    val type: String = "",
    val title: String = "",
    val body: String = "",
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val payload: Map<String, Any> = emptyMap()
)

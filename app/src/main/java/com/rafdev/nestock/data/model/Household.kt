package com.rafdev.nestock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Household(
    @DocumentId val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val createdBy: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val members: Map<String, String> = emptyMap()
)

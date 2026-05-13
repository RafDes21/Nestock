package com.rafdev.nestock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ShoppingEntry(
    @DocumentId val id: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val quantityNeeded: Double = 1.0,
    val unit: String = "und",
    val isPurchased: Boolean = false,
    val addedBy: String = "",
    val addedAt: Timestamp = Timestamp.now()
)

package com.rafdev.nestock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Item(
    @DocumentId val id: String = "",
    val name: String = "",
    val categoryId: String = "",
    val barcode: String? = null,
    val quantity: Double = 0.0,
    val minQuantity: Double = 1.0,
    val optimalQuantity: Double = 5.0,
    val unit: String = "und",
    val imageUrl: String? = null,
    val updatedBy: String = "",
    val updatedAt: Timestamp = Timestamp.now(),
    val isLowStock: Boolean = false,
    val expirationDate: com.google.firebase.Timestamp? = null
)

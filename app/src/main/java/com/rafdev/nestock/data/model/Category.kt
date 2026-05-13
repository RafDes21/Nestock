package com.rafdev.nestock.data.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId val id: String = "",
    val name: String = "",
    val icon: String = "📦",
    val color: String = "#2D6A4F"
)

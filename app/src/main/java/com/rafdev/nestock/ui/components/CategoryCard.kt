package com.rafdev.nestock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafdev.nestock.data.model.Category
import com.rafdev.nestock.ui.theme.*

@Composable
fun CategoryCard(category: Category, itemCount: Int, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Border, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(11.dp)
    ) {
        Text(category.icon, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(category.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Text("$itemCount items", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Border)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((itemCount.toFloat() / 30f).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(GreenLight)
            )
        }
    }
}

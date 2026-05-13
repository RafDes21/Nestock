package com.rafdev.nestock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafdev.nestock.data.model.Item
import com.rafdev.nestock.ui.theme.*

@Composable
fun ItemRow(item: Item, onClick: () -> Unit) {
    val isLow = item.isLowStock
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isLow) OrangePale else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (isLow) OrangeAlert.copy(alpha = 0.35f) else Border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(if (isLow) OrangePale else GreenPale),
            contentAlignment = Alignment.Center
        ) {
            Text("📦", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("${item.categoryId.ifEmpty { "Sin categoría" }} · mín. ${item.minQuantity.fmt()} ${item.unit}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                item.quantity.fmt(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isLow) OrangeAlert else GreenDark,
                fontFamily = FrauncesFamily
            )
            Text(item.unit, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

private fun Double.fmt(): String = if (this == toLong().toDouble()) toLong().toString() else toString()

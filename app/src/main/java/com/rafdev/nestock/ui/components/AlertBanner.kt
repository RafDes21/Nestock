package com.rafdev.nestock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpirationBanner(expiringItems: List<Item>) {
    if (expiringItems.isEmpty()) return
    val now = System.currentTimeMillis()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(OrangePale)
            .border(1.dp, OrangeAlert.copy(alpha = 0.25f), RoundedCornerShape(13.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp)
    ) {
        Text(
            "⏰  Por vencer · ${expiringItems.size} ${if (expiringItems.size == 1) "item" else "items"}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = OrangeAlert
        )
        Spacer(Modifier.height(7.dp))
        expiringItems.take(3).forEach { item ->
            val daysLeft = ((item.expirationDate!!.toDate().time - now) / (1000L * 60 * 60 * 24)).toInt()
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Box(
                    Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(OrangeAlert)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        if (daysLeft == 0) "Hoy" else "En ${daysLeft}d",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Surface
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
fun AlertBanner(lowStockItems: List<Item>) {
    if (lowStockItems.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(OrangePale)
            .border(1.dp, OrangeAlert.copy(alpha = 0.25f), RoundedCornerShape(13.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp)
    ) {
        Text(
            "⚠️  Bajo stock · ${lowStockItems.size} items",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = OrangeAlert
        )
        Spacer(Modifier.height(7.dp))
        lowStockItems.take(3).forEach { item ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Box(
                    Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(OrangeAlert)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        if (item.quantity == 0.0) "Agotado" else "${item.quantity.toLong()} ${item.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Surface
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}

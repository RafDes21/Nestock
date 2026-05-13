package com.rafdev.nestock.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.data.model.AppNotification
import com.rafdev.nestock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount   by viewModel.unreadCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", style = MaterialTheme.typography.headlineSmall, fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold, color = TextPrimary) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Volver") } },
                actions = {
                    if (unreadCount > 0) TextButton(onClick = { viewModel.markAllAsRead() }) { Text("Marcar leídas", style = MaterialTheme.typography.labelMedium, color = GreenDark) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().background(Background).padding(padding),
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(notifications, key = { it.id }) { notif ->
                NotificationItem(notif = notif, onRead = { viewModel.markAsRead(notif.id) })
            }
            if (notifications.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Sin notificaciones", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notif: AppNotification, onRead: () -> Unit) {
    val isLowStock = notif.type == "low_stock"
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(
                width = if (!notif.isRead) 2.dp else 1.dp,
                color = if (!notif.isRead) GreenLight else Border,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(11.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(9.dp))
                .background(if (isLowStock) OrangePale else GreenPale),
            contentAlignment = Alignment.Center
        ) { Text(if (isLowStock) "⚠️" else "📦", style = MaterialTheme.typography.bodyMedium) }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text(notif.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(notif.body, style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
        }
        if (!notif.isRead) {
            Box(Modifier.size(7.dp).clip(RoundedCornerShape(50)).background(GreenLight))
        }
    }
}

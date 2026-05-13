package com.rafdev.nestock.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.rafdev.nestock.ui.theme.*

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        },
        text = {
            Text(message, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = OrangeAlert, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GreenDark)
            }
        }
    )
}

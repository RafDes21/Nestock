package com.rafdev.nestock.ui.screens.household

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteScreen(
    onNavigateBack: () -> Unit,
    viewModel: HouseholdViewModel = hiltViewModel()
) {
    val household by viewModel.household.collectAsState()
    val code = household?.inviteCode ?: "------"
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invitar al hogar", style = MaterialTheme.typography.headlineSmall, fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold, color = TextPrimary) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().background(Background).padding(padding).padding(horizontal = 15.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // QR placeholder
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(16.dp)).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Escanear QR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier.size(110.dp).clip(RoundedCornerShape(11.dp)).background(Background).border(1.dp, Border, RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("⬛", fontSize = 48.sp) }
                Spacer(Modifier.height(10.dp))
                Text(household?.name ?: "", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }

            // Código
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(13.dp)).padding(13.dp)
            ) {
                Text("Código de invitación", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(bottom = 8.dp))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(GreenPale).padding(horizontal = 13.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(code, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = GreenDark, fontFamily = FrauncesFamily, letterSpacing = 4.sp)
                    Row {
                        TextButton(
                            onClick = { clipboard.setText(AnnotatedString(code)); copied = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = GreenDark)
                        ) { Text(if (copied) "✓ Copiado" else "Copiar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }

            // Link
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(13.dp)).padding(13.dp)
            ) {
                Text("Link compartible", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(bottom = 8.dp))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(9.dp)).background(Background).padding(horizontal = 11.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "nestock.app/join/$code",
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1
                    )
                    Spacer(Modifier.width(7.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Únete a mi hogar en Nestock: nestock.app/join/$code")
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                        shape = RoundedCornerShape(7.dp),
                        contentPadding = PaddingValues(horizontal = 9.dp, vertical = 5.dp)
                    ) { Text("Compartir", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold) }
                }
            }

        }
    }
}

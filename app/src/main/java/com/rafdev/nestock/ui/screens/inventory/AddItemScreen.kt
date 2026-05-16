package com.rafdev.nestock.ui.screens.inventory

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.data.model.Category
import com.rafdev.nestock.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    itemId: String = "",
    onNavigateBack: () -> Unit,
    viewModel: AddItemViewModel = hiltViewModel()
) {
    LaunchedEffect(itemId) {
        if (itemId.isNotBlank()) viewModel.loadForEdit(itemId)
    }

    val categories by viewModel.categories.collectAsState()
    var showUnitMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (viewModel.isEditMode) "Editar insumo" else "Agregar insumo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FrauncesFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // TODO: Scanner de código de barras — descomentar cuando se retome
        // if (!viewModel.isEditMode) {
        //     ScannerButton(
        //         barcodeValue = viewModel.barcode,
        //         onClick = {
        //             val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        //             if (granted) showScanner = true
        //             else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        //         }
        //     )
        // }

        Column(Modifier.padding(horizontal = 15.dp, vertical = 16.dp)) {
                viewModel.error?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = OrangeAlert, modifier = Modifier.padding(bottom = 8.dp))
                }

                // Nombre
                FormField(label = "Nombre del producto") {
                    OutlinedTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.name = it },
                        placeholder = { Text("Ej: Arroz blanco 1kg", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))

                // Categoría
                FormField(label = "Categoría") {
                    CategoryDropdown(
                        label = categories.find { it.id == viewModel.selectedCategoryId }
                            ?.let { "${it.icon} ${it.name}" }
                            ?: "Seleccionar categoría",
                        isPlaceholder = viewModel.selectedCategoryId.isEmpty(),
                        categories = categories,
                        onSelect = { viewModel.selectedCategoryId = it }
                    )
                }
                Spacer(Modifier.height(10.dp))

                // Cantidad y Unidad
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FormField(label = "Cantidad", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = viewModel.quantity,
                            onValueChange = { viewModel.quantity = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    FormField(label = "Unidad", modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = showUnitMenu,
                            onExpandedChange = { showUnitMenu = it }
                        ) {
                            OutlinedTextField(
                                value = viewModel.unit,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUnitMenu) }
                            )
                            ExposedDropdownMenu(
                                expanded = showUnitMenu,
                                onDismissRequest = { showUnitMenu = false }
                            ) {
                                viewModel.units.forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u, style = MaterialTheme.typography.bodySmall) },
                                        onClick = { viewModel.unit = u; showUnitMenu = false }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))

                // Stock mínimo y óptimo
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FormField(label = "Stock mínimo", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = viewModel.minQuantity,
                            onValueChange = { viewModel.minQuantity = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    FormField(label = "Stock óptimo", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = viewModel.optimalQuantity,
                            onValueChange = { viewModel.optimalQuantity = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))

                // Fecha de vencimiento (opcional)
                FormField(label = "Fecha de vencimiento (opcional)") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Border, RoundedCornerShape(10.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { showDatePicker = true }
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextMuted)
                            Text(
                                viewModel.expirationDateMillis?.let { formatDate(it) } ?: "Sin fecha",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (viewModel.expirationDateMillis != null) TextPrimary else TextMuted
                            )
                        }
                        if (viewModel.expirationDateMillis != null) {
                            IconButton(onClick = { viewModel.expirationDateMillis = null }) {
                                Icon(Icons.Filled.Close, contentDescription = "Quitar fecha", modifier = Modifier.size(16.dp), tint = TextMuted)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.save(onNavigateBack) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !viewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            if (viewModel.isEditMode) "Guardar cambios" else "Guardar insumo",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = viewModel.expirationDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.expirationDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    label: String,
    isPlaceholder: Boolean,
    categories: List<Category>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isPlaceholder) TextMuted else TextPrimary,
                unfocusedTextColor = if (isPlaceholder) TextMuted else TextPrimary
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (categories.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Sin categorías. Creá una en Mi Hogar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    },
                    onClick = { expanded = false }
                )
            } else {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(cat.icon, style = MaterialTheme.typography.bodyMedium)
                                Text(cat.name, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                        },
                        onClick = { onSelect(cat.id); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun FormField(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content()
    }
}

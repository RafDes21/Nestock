package com.rafdev.nestock.ui.components

import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.rafdev.nestock.ui.theme.*
import kotlinx.coroutines.guava.await
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun BarcodeScanner(
    onBarcodeScanned: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasScanned = remember { AtomicBoolean(false) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    // Inicia la cámara cuando el PreviewView esté listo
    LaunchedEffect(previewView) {
        val pv = previewView ?: return@LaunchedEffect

        val cameraProvider = ProcessCameraProvider.getInstance(context).await()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(pv.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    executor,
                    BarcodeAnalyzer(BarcodeScanning.getClient(), hasScanned, onBarcodeScanned)
                )
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (_: Exception) {}
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // Preview de cámara a pantalla completa
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView = it }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay oscuro con ventana de escaneo
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxWidth().weight(1.2f)
                    .background(Color.Black.copy(alpha = 0.58f))
            )
            Row(Modifier.fillMaxWidth().height(230.dp)) {
                Box(Modifier.weight(1f).fillMaxHeight().background(Color.Black.copy(alpha = 0.58f)))
                Box(
                    Modifier.size(230.dp)
                        .border(2.5.dp, GreenLight, RoundedCornerShape(14.dp))
                )
                Box(Modifier.weight(1f).fillMaxHeight().background(Color.Black.copy(alpha = 0.58f)))
            }
            Box(
                Modifier.fillMaxWidth().weight(1f)
                    .background(Color.Black.copy(alpha = 0.58f))
            ) {
                Text(
                    "Apuntá la cámara al código de barras",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)
                )
            }
        }

        // TopBar
        Row(
            Modifier.fillMaxWidth().align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(start = 4.dp, top = 36.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Color.White)
            }
            Text(
                "Escanear producto",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private class BarcodeAnalyzer(
    private val scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    private val hasScanned: AtomicBoolean,
    private val onScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || hasScanned.get()) {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { barcode ->
                    if (hasScanned.compareAndSet(false, true)) {
                        onScanned(barcode)
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}

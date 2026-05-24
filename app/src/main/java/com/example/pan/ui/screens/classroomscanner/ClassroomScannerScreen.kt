package com.example.pan.ui.screens.classroomscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pan.viewmodel.ClassroomScannerViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

// --------------------------------------------------
// ENTRY POINT
// --------------------------------------------------

@Composable
fun ClassroomScannerScreen(
    onBack: () -> Unit,
    viewModel: ClassroomScannerViewModel = viewModel()
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        PermissionRequiredContent(
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onBack              = onBack
        )
        return
    }

    ScannerContent(onBack = onBack, viewModel = viewModel)
}

// --------------------------------------------------
// PERMISSION REQUIRED SCREEN
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionRequiredContent(
    onRequestPermission: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Σάρωση Αίθουσας", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = MaterialTheme.colorScheme.primary,
                    titleContentColor          = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint     = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text       = "Απαιτείται πρόσβαση στην κάμερα",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text      = "Για να σαρώσετε την πινακίδα αίθουσας, η εφαρμογή χρειάζεται πρόσβαση στην κάμερά σας.",
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color     = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = onRequestPermission) {
                Text("Παραχώρηση Άδειας")
            }
        }
    }
}

// --------------------------------------------------
// SCANNER CONTENT (full-screen camera overlay)
// --------------------------------------------------

@Composable
private fun ScannerContent(
    onBack: () -> Unit,
    viewModel: ClassroomScannerViewModel
) {
    val detectedRoom    = viewModel.detectedRoom
    val scheduleMessage = viewModel.scheduleMessage
    val isScanning      = viewModel.isScanning

    val infiniteTransition = rememberInfiniteTransition(label = "scan_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.35f,
        targetValue   = 1.0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bracket_alpha"
    )

    val bracketAlpha = when {
        isScanning           -> pulseAlpha
        detectedRoom != null -> 1f
        else                 -> 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Layer 1: live camera preview
        CameraPreviewView(
            onTextDetected = viewModel::onTextDetected,
            modifier       = Modifier.fillMaxSize()
        )

        // Layer 2: scanning-frame corner brackets
        ScanningFrame(
            alpha    = bracketAlpha,
            locked   = detectedRoom != null,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 3: top controls — back button + room badge
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick  = onBack,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Πίσω",
                    tint = Color.White
                )
            }

            if (detectedRoom != null) {
                RoomBadge(room = detectedRoom)
            }
        }

        // Layer 4: scanning hint text, centered just below the viewfinder frame
        if (isScanning) {
            Text(
                text      = "Στρέψτε την κάμερα στην πινακίδα αίθουσας",
                color     = Color.White,
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .align(Alignment.Center)
                    .offset(y = 110.dp)
                    .padding(horizontal = 32.dp)
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Layer 5: schedule result card at the bottom
        if (scheduleMessage != null && detectedRoom != null) {
            ScheduleResultCard(
                room     = detectedRoom,
                message  = scheduleMessage,
                onRescan = viewModel::reset,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}

// --------------------------------------------------
// CAMERA PREVIEW (CameraX via AndroidView)
// --------------------------------------------------

@Composable
private fun CameraPreviewView(
    onTextDetected: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val onTextState    = rememberUpdatedState(onTextDetected)
    val executor       = remember { Executors.newSingleThreadExecutor() }
    val recognizer     = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    val providerHolder = remember { arrayOf<ProcessCameraProvider?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            providerHolder[0]?.unbindAll()
            executor.shutdown()
            recognizer.close()
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            val lastAnalysisMs = AtomicLong(0L)
            val cameraFuture   = ProcessCameraProvider.getInstance(ctx)

            cameraFuture.addListener({
                val cameraProvider     = cameraFuture.get()
                providerHolder[0]      = cameraProvider

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(executor) { proxy ->
                    val now = System.currentTimeMillis()
                    if (now - lastAnalysisMs.get() < 500L) {
                        proxy.close()
                        return@setAnalyzer
                    }
                    lastAnalysisMs.set(now)

                    val media = proxy.image
                    if (media == null) { proxy.close(); return@setAnalyzer }

                    val inputImage = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
                    recognizer.process(inputImage)
                        .addOnSuccessListener(ContextCompat.getMainExecutor(ctx)) { result ->
                            val texts = buildList {
                                // Add the full combined text first to catch split blocks (like "ΑΜΦΙΘΕΑΤΡΟ" and "Β")
                                if (result.text.isNotBlank()) add(result.text)
                                
                                for (block in result.textBlocks) {
                                    add(block.text)
                                    for (line in block.lines) {
                                        add(line.text)
                                    }
                                }
                            }
                            onTextState.value(texts)
                        }
                        .addOnCompleteListener { proxy.close() }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                    )
                } catch (_: Exception) { }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

// --------------------------------------------------
// SCANNING FRAME (corner brackets overlay)
// --------------------------------------------------

@Composable
private fun ScanningFrame(
    alpha: Float,
    locked: Boolean,
    modifier: Modifier = Modifier
) {
    val lockedColor   = Color(0xFF4CAF50)
    val bracketColor  = if (locked) lockedColor else MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        if (alpha == 0f) return@Canvas

        val w      = size.width
        val h      = size.height
        val frameW = w * 0.78f
        val frameH = minOf(h * 0.22f, 180.dp.toPx())
        val left   = (w - frameW) / 2f
        val top    = (h - frameH) / 2f
        val right  = left + frameW
        val bottom = top + frameH
        val arm    = 36.dp.toPx()
        val stroke = 3.dp.toPx()
        val c      = bracketColor.copy(alpha = alpha)

        fun corner(ax: Float, ay: Float, bx: Float, by: Float, cx: Float, cy: Float) {
            drawLine(c, Offset(ax, ay), Offset(bx, by), stroke, cap = StrokeCap.Round)
            drawLine(c, Offset(bx, by), Offset(cx, cy), stroke, cap = StrokeCap.Round)
        }

        corner(left,          top + arm,    left,  top,    left + arm,  top   )  // top-left
        corner(right - arm,   top,          right, top,    right,       top + arm)  // top-right
        corner(left,          bottom - arm, left,  bottom, left + arm,  bottom)  // bottom-left
        corner(right - arm,   bottom,       right, bottom, right,       bottom - arm)  // bottom-right
    }
}

// --------------------------------------------------
// ROOM BADGE (top overlay chip)
// --------------------------------------------------

@Composable
private fun RoomBadge(room: String) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint     = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text       = "Αίθουσα: $room  ✓",
            color      = Color.White,
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// --------------------------------------------------
// SCHEDULE RESULT CARD (bottom sheet-style card)
// --------------------------------------------------

@Composable
private fun ScheduleResultCard(
    room: String,
    message: String,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier            = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text       = "Αίθουσα: $room",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            OutlinedButton(
                onClick  = onRescan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Σάρωση Ξανά")
            }
        }
    }
}
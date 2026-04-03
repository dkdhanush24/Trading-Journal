package com.tradingjournal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tradingjournal.ui.theme.*
import com.tradingjournal.ui.viewmodel.RecordViewModel
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    strategyId: Long,
    onTranscriptionComplete: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RecordViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }
    
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }
    var statusText by remember { mutableStateOf("Tap to start recording") }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingTime++
            }
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isRecording) pulseScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "buttonScale"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Trade") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!hasPermission) {
                PermissionRequired(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) {
                                Brush.radialGradient(
                                    colors = listOf(AccentRed, AccentRed.copy(alpha = 0.3f))
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(PrimaryGold, PrimaryGoldDark)
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (isRecording) {
                                try {
                                    mediaRecorder?.stop()
                                    mediaRecorder?.release()
                                    mediaRecorder = null
                                    
                                    statusText = "Processing..."
                                    
                                    viewModel.processRecording(
                                        audioFile = audioFile!!,
                                        onTranscriptionComplete = onTranscriptionComplete
                                    )
                                } catch (e: Exception) {
                                    statusText = "Recording failed"
                                }
                                isRecording = false
                            } else {
                                try {
                                    val file = File(context.cacheDir, "recording_${System.currentTimeMillis()}.m4a")
                                    audioFile = file
                                    
                                    mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        MediaRecorder(context)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        android.media.MediaRecorder()
                                    }.apply {
                                        setAudioSource(MediaRecorder.AudioSource.MIC)
                                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                        setOutputFile(file.absolutePath)
                                        prepare()
                                        start()
                                    }
                                    
                                    recordingTime = 0
                                    isRecording = true
                                    statusText = "Recording... Speak now"
                                } catch (e: Exception) {
                                    statusText = "Failed to start recording"
                                }
                            }
                        },
                        modifier = Modifier.size(180.dp)
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop" else "Record",
                            modifier = Modifier.size(80.dp),
                            tint = DarkBackground
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (isRecording) {
                    Text(
                        text = formatTime(recordingTime),
                        style = MaterialTheme.typography.displayMedium,
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isRecording) PrimaryGold else TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "Tips for better recognition:",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TipRow("Speak clearly and at a natural pace")
                        TipRow("Mention timeframe (5m, 15m, 1h, 4h)")
                        TipRow("Include setups (MSS, FVG, Breakout)")
                        TipRow("Note confluences if any")
                    }
                }
            }
        }
    }
}

@Composable
fun TipRow(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = PrimaryGold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
fun PermissionRequired(onRequestPermission: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Microphone permission required",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "To record your trade reasoning, we need access to your microphone.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
        ) {
            Text("Grant Permission")
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

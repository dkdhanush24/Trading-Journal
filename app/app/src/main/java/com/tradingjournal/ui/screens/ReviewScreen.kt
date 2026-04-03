package com.tradingjournal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradingjournal.ui.theme.*
import com.tradingjournal.ui.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    strategyId: Long,
    rawText: String,
    onSaveComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: ReviewViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(strategyId, rawText) {
        viewModel.initialize(strategyId, rawText)
    }
    
    LaunchedEffect(uiState.saveComplete) {
        if (uiState.saveComplete) {
            onSaveComplete()
        }
    }
    
    var pair by remember { mutableStateOf("") }
    var entryTimeframe by remember { mutableStateOf("") }
    var htfBias by remember { mutableStateOf("") }
    var setup by remember { mutableStateOf("") }
    var confluencesText by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    
    LaunchedEffect(uiState.parsedData) {
        uiState.parsedData?.let { data ->
            entryTimeframe = data.entryTimeframe ?: ""
            htfBias = data.htfBias ?: ""
            setup = data.setup ?: ""
            confluencesText = data.confluences.joinToString(", ")
            summary = data.summary ?: ""
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Trade") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                uiState.isProcessing -> {
                    ProcessingState()
                }
                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error!!,
                        onRetry = { viewModel.retry() }
                    )
                }
                else -> {
                    RawTextCard(rawText = rawText)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    EditableFields(
                        pair = pair,
                        onPairChange = { pair = it },
                        entryTimeframe = entryTimeframe,
                        onEntryTimeframeChange = { entryTimeframe = it },
                        htfBias = htfBias,
                        onHtfBiasChange = { htfBias = it },
                        setup = setup,
                        onSetupChange = { setup = it },
                        confluencesText = confluencesText,
                        onConfluencesChange = { confluencesText = it },
                        summary = summary,
                        onSummaryChange = { summary = it }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    SaveButton(
                        enabled = !uiState.isSaving,
                        onClick = {
                            viewModel.saveTrade(
                                pair = pair,
                                entryTimeframe = entryTimeframe,
                                htfBias = htfBias,
                                setup = setup,
                                confluences = confluencesText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                summary = summary,
                                rawText = rawText
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RawTextCard(rawText: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TextFields,
                    contentDescription = null,
                    tint = PrimaryGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Raw Transcription",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                rawText,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun EditableFields(
    pair: String,
    onPairChange: (String) -> Unit,
    entryTimeframe: String,
    onEntryTimeframeChange: (String) -> Unit,
    htfBias: String,
    onHtfBiasChange: (String) -> Unit,
    setup: String,
    onSetupChange: (String) -> Unit,
    confluencesText: String,
    onConfluencesChange: (String) -> Unit,
    summary: String,
    onSummaryChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Trade Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryGold
            )
            
            OutlinedTextField(
                value = pair,
                onValueChange = onPairChange,
                label = { Text("Trading Pair") },
                placeholder = { Text("e.g., XAUUSD, EURUSD") },
                leadingIcon = {
                    Icon(Icons.Default.CurrencyExchange, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = TextMuted
                )
            )
            
            OutlinedTextField(
                value = entryTimeframe,
                onValueChange = onEntryTimeframeChange,
                label = { Text("Entry Timeframe") },
                placeholder = { Text("e.g., 5m, 15m, 1h") },
                leadingIcon = {
                    Icon(Icons.Default.Timeline, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = TextMuted
                )
            )
            
            OutlinedTextField(
                value = htfBias,
                onValueChange = onHtfBiasChange,
                label = { Text("HTF Bias") },
                placeholder = { Text("Bullish, Bearish, Neutral") },
                leadingIcon = {
                    Icon(Icons.Default.TrendingUp, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = TextMuted
                )
            )
            
            OutlinedTextField(
                value = setup,
                onValueChange = onSetupChange,
                label = { Text("Setup") },
                placeholder = { Text("e.g., MSS, Breakout, FVG") },
                leadingIcon = {
                    Icon(Icons.Default.FlashOn, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = TextMuted
                )
            )
            
            OutlinedTextField(
                value = confluencesText,
                onValueChange = onConfluencesChange,
                label = { Text("Confluences") },
                placeholder = { Text("Comma separated") },
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = TextMuted
                )
            )
            
            OutlinedTextField(
                value = summary,
                onValueChange = onSummaryChange,
                label = { Text("Summary") },
                placeholder = { Text("Brief trade summary") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = TextMuted
                )
            )
        }
    }
}

@Composable
fun ProcessingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = PrimaryGold,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Processing...",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Transcribing and parsing your trade",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = AccentRed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                error,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun SaveButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGold,
            disabledContainerColor = PrimaryGold.copy(alpha = 0.3f)
        )
    ) {
        Icon(Icons.Default.Save, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Save Trade",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

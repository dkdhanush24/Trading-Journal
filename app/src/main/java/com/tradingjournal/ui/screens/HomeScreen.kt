package com.tradingjournal.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradingjournal.model.TradeResult
import com.tradingjournal.ui.theme.*
import com.tradingjournal.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartRecording: (Long) -> Unit,
    onOpenJournal: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val strategies by viewModel.strategies.collectAsStateWithLifecycle()
    val allTrades by viewModel.allTrades.collectAsStateWithLifecycle()
    val selectedStrategyId by viewModel.selectedStrategyId.collectAsStateWithLifecycle()
    
    var showAddStrategyDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.initializeDefaultStrategies()
    }
    
    val wins = allTrades.count { it.result == TradeResult.WIN }
    val losses = allTrades.count { it.result == TradeResult.LOSS }
    val totalDecided = wins + losses
    val winRate = if (totalDecided > 0) (wins.toFloat() / totalDecided) * 100 else 0f
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Trading Journal",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    // Start of Dashboard - No back button needed
                },
                actions = {
                    IconButton(onClick = onOpenJournal) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Journal Calendar",
                            tint = PrimaryBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val idToUse = selectedStrategyId ?: strategies.firstOrNull()?.id
                    if (idToUse != null) {
                        onStartRecording(idToUse)
                    }
                },
                containerColor = PrimaryBlue,
                contentColor = TextPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Record Trade")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }
            
            // Strategy Selector Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Strategies",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showAddStrategyDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Strategy", tint = PrimaryBlue)
                    }
                }
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        TabPill(
                            text = "All",
                            selected = selectedStrategyId == null,
                            onClick = { viewModel.selectStrategy(null) }
                        )
                    }
                    items(strategies.size) { index ->
                        val strategy = strategies[index]
                        TabPill(
                            text = strategy.name,
                            selected = selectedStrategyId == strategy.id,
                            onClick = { viewModel.selectStrategy(strategy.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = PrimaryBlue),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "CURRENT PERFORMANCE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Stats Grid (2x3)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatBox(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.PieChart,
                                iconTint = PrimaryBlue,
                                title = "Win Rate",
                                value = "${String.format("%.1f", winRate)}%"
                            )
                            StatBox(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.ThumbUp,
                                iconTint = ProfitGreen,
                                title = "Wins",
                                value = "$wins"
                            )
                            StatBox(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.ThumbDown,
                                iconTint = LossRed,
                                title = "Losses",
                                value = "$losses"
                            )
                        }
                        
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) } // Padding for FAB
        }
        
        if (showAddStrategyDialog) {
            var newStrategyName by remember { mutableStateOf("") }
            var newStrategyDesc by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showAddStrategyDialog = false },
                title = { Text("Add Strategy") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newStrategyName,
                            onValueChange = { newStrategyName = it },
                            label = { Text("Strategy Name") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newStrategyDesc,
                            onValueChange = { newStrategyDesc = it },
                            label = { Text("Description") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addStrategy(newStrategyName, "Any", newStrategyDesc)
                        showAddStrategyDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStrategyDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StatBox(modifier: Modifier = Modifier, icon: ImageVector, iconTint: Color, title: String, value: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = DarkBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun TabPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) PrimaryBlue else TextMuted
        )
    }
}


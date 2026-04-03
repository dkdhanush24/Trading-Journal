package com.tradingjournal.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradingjournal.model.Strategy
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
    var showAddDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.initializeDefaultStrategies()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Voice Trading Journal",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onOpenJournal) {
                        Icon(
                            Icons.Default.Book,
                            contentDescription = "Journal",
                            tint = PrimaryGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryGold,
                contentColor = DarkBackground
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Strategy")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Select Strategy",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (strategies.isEmpty()) {
                EmptyState(
                    onAddStrategy = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(strategies, key = { it.id }) { strategy ->
                        StrategyCard(
                            strategy = strategy,
                            onClick = { onStartRecording(strategy.id) }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddStrategyDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, market, description ->
                viewModel.addStrategy(name, market, description)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StrategyCard(
    strategy: Strategy,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(GradientGold)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (strategy.market) {
                        "Gold" -> Icons.Default.TrendingUp
                        "Forex" -> Icons.Default.CurrencyExchange
                        else -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = DarkBackground
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    strategy.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    strategy.market,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Icon(
                Icons.Default.Mic,
                contentDescription = "Record",
                tint = PrimaryGold,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun EmptyState(onAddStrategy: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.LibraryBooks,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No strategies yet",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add a strategy to start logging trades",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddStrategy,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Strategy")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStrategyDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, market: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("Gold") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    val markets = listOf("Gold", "Forex", "Crypto", "Indices")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Strategy") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Strategy Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = market,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Market") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        markets.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = {
                                    market = m
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, market, description) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

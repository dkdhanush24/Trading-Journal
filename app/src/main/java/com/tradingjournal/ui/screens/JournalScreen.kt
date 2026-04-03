package com.tradingjournal.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradingjournal.model.Trade
import com.tradingjournal.ui.components.FlashCard
import com.tradingjournal.ui.theme.*
import com.tradingjournal.ui.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onBack: () -> Unit,
    viewModel: JournalViewModel = viewModel()
) {
    val trades by viewModel.trades.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trading Journal") },
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
        ) {
            DateSelector(
                selectedDate = selectedDate,
                formattedDate = dateFormat.format(Date(selectedDate)),
                onDateChange = { viewModel.selectDate(it) },
                onPreviousDay = { viewModel.previousDay() },
                onNextDay = { viewModel.nextDay() },
                onCalendarClick = { showDatePicker = true }
            )
            
            if (trades.isEmpty()) {
                EmptyJournalState()
            } else {
                Text(
                    "${trades.size} trade${if (trades.size > 1) "s" else ""} on this day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(trades, key = { _, trade -> trade.id }) { index, trade ->
                        FlashCard(
                            trade = trade,
                            index = index,
                            total = trades.size
                        )
                    }
                }
            }
        }
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                viewModel.selectDate(date)
                showDatePicker = false
            },
            initialDate = selectedDate
        )
    }
}

@Composable
fun DateSelector(
    selectedDate: Long,
    formattedDate: String,
    onDateChange: (Long) -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous day",
                    tint = PrimaryGold
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onCalendarClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    formattedDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Tap to change",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
            
            IconButton(
                onClick = onNextDay,
                enabled = selectedDate < System.currentTimeMillis()
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next day",
                    tint = if (selectedDate < System.currentTimeMillis()) PrimaryGold else TextMuted
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
    initialDate: Long
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(it)
                    }
                }
            ) {
                Text("OK", color = PrimaryGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = PrimaryGold,
                selectedDayContentColor = DarkBackground,
                todayContentColor = PrimaryGold,
                todayDateBorderColor = PrimaryGold
            )
        )
    }
}

@Composable
fun EmptyJournalState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No trades logged",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Start recording trades to see them here",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

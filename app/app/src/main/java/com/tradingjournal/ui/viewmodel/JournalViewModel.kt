package com.tradingjournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradingjournal.VoiceTradingApp
import com.tradingjournal.model.Trade
import kotlinx.coroutines.flow.*
import java.util.Calendar

class JournalViewModel : ViewModel() {
    
    private val repository = VoiceTradingApp.instance.repository
    
    private val _selectedDate = MutableStateFlow(getStartOfDay(System.currentTimeMillis()))
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()
    
    val trades: StateFlow<List<Trade>> = _selectedDate
        .flatMapLatest { date ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = date
            }
            repository.getTradesForDay(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun selectDate(timestamp: Long) {
        _selectedDate.value = getStartOfDay(timestamp)
    }
    
    fun previousDay() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _selectedDate.value
            add(Calendar.DAY_OF_MONTH, -1)
        }
        _selectedDate.value = calendar.timeInMillis
    }
    
    fun nextDay() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _selectedDate.value
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val today = getStartOfDay(System.currentTimeMillis())
        if (calendar.timeInMillis <= today) {
            _selectedDate.value = calendar.timeInMillis
        }
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}

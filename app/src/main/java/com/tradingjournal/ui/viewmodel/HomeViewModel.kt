package com.tradingjournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradingjournal.VoiceTradingApp
import com.tradingjournal.model.Strategy
import com.tradingjournal.model.Trade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val repository = VoiceTradingApp.instance.repository
    
    private val _selectedStrategyId = MutableStateFlow<Long?>(null)
    val selectedStrategyId: StateFlow<Long?> = _selectedStrategyId.asStateFlow()

    val strategies: StateFlow<List<Strategy>> = repository.allStrategies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val allTrades: StateFlow<List<Trade>> = combine(
        repository.allTrades,
        _selectedStrategyId
    ) { trades, strategyId ->
        if (strategyId == null) trades else trades.filter { it.strategyId == strategyId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun selectStrategy(id: Long?) {
        _selectedStrategyId.value = id
    }
    
    fun initializeDefaultStrategies() {
        viewModelScope.launch {
            val count = repository.getStrategyCount()
            if (count == 0) {
                val defaultStrategies = listOf(
                    Strategy(name = "Breakout", market = "Gold", description = "HTF breakout entries"),
                    Strategy(name = "Pullback", market = "Gold", description = "Retest entries"),
                    Strategy(name = "MSS", market = "Gold", description = "Market Structure Shift"),
                    Strategy(name = "OTE", market = "Gold", description = "Optimal Trading Entry")
                )
                defaultStrategies.forEach { strategy ->
                    repository.insertStrategy(strategy)
                }
            }
        }
    }
    
    fun addStrategy(name: String, market: String, description: String) {
        viewModelScope.launch {
            repository.insertStrategy(
                Strategy(
                    name = name,
                    market = market,
                    description = description
                )
            )
        }
    }
    
    fun deleteStrategy(strategy: Strategy) {
        viewModelScope.launch {
            repository.deleteStrategy(strategy)
        }
    }
}

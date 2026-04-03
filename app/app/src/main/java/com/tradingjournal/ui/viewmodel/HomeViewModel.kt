package com.tradingjournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradingjournal.VoiceTradingApp
import com.tradingjournal.model.Strategy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val repository = VoiceTradingApp.instance.repository
    
    val strategies: StateFlow<List<Strategy>> = repository.allStrategies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
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

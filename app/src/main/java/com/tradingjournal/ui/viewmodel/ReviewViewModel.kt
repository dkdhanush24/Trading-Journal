package com.tradingjournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradingjournal.VoiceTradingApp
import com.tradingjournal.data.remote.ApiService
import com.tradingjournal.model.ParsedTrade
import com.tradingjournal.model.SessionType
import com.tradingjournal.model.Trade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewUiState(
    val isProcessing: Boolean = false,
    val isSaving: Boolean = false,
    val parsedData: ParsedTrade? = null,
    val error: String? = null,
    val saveComplete: Boolean = false
)

class ReviewViewModel : ViewModel() {
    
    private val apiService = VoiceTradingApp.instance.apiService
    private val repository = VoiceTradingApp.instance.repository
    
    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()
    
    private var strategyId: Long = 0
    private var rawText: String = ""
    
    fun initialize(strategyId: Long, rawText: String) {
        this.strategyId = strategyId
        this.rawText = rawText
        processText(rawText)
    }
    
    fun retry() {
        processText(rawText)
    }
    
    private fun processText(text: String) {
        viewModelScope.launch {
            _uiState.value = ReviewUiState(isProcessing = true)
            
            val parseResult = apiService.parse(text)
            
            parseResult.fold(
                onSuccess = { response ->
                    _uiState.value = ReviewUiState(
                        parsedData = ParsedTrade(
                            pair = response.pair,
                            entryTimeframe = response.entry_timeframe,
                            htfBias = response.htf_bias,
                            setup = response.setup,
                            confluences = response.confluences ?: emptyList(),
                            summary = response.summary
                        )
                    )
                },
                onFailure = { error ->
                    _uiState.value = ReviewUiState(
                        error = error.message ?: "Failed to parse trade"
                    )
                }
            )
        }
    }
    
    fun saveTrade(
        pair: String,
        entryTimeframe: String,
        htfBias: String,
        setup: String,
        confluences: List<String>,
        summary: String,
        rawText: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            val trade = Trade(
                strategyId = if (strategyId > 0) strategyId else null,
                pair = pair,
                entryTimeframe = entryTimeframe,
                htfBias = htfBias,
                setup = setup,
                confluences = confluences,
                summary = summary,
                rawText = rawText,
                sessionType = SessionType.LIVE,
                timestamp = System.currentTimeMillis()
            )
            
            repository.insertTrade(trade)
            
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                saveComplete = true
            )
        }
    }
}

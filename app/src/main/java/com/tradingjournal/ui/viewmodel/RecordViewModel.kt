package com.tradingjournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradingjournal.VoiceTradingApp
import com.tradingjournal.data.remote.ApiService
import kotlinx.coroutines.launch
import java.io.File

class RecordViewModel : ViewModel() {
    
    private val apiService = VoiceTradingApp.instance.apiService
    
    fun processRecording(
        audioFile: File,
        onTranscriptionComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            val transcriptionResult = apiService.transcribe(audioFile)
            
            transcriptionResult.fold(
                onSuccess = { response ->
                    onTranscriptionComplete(response.text)
                },
                onFailure = { error ->
                    onTranscriptionComplete("Transcription failed: ${error.message}")
                }
            )
        }
    }
}

package com.tradingjournal.data.remote

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiService(private val baseUrl: String = "https://trading-journal-backend-production-d66c.up.railway.app") {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    suspend fun transcribe(audioFile: File): Result<TranscribeResponse> = withContext(Dispatchers.IO) {
        try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    audioFile.name,
                    audioFile.asRequestBody("audio/*".toMediaType())
                )
                .build()
            
            val request = Request.Builder()
                .url("$baseUrl/api/transcribe")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string()
                val transcribeResponse = gson.fromJson(body, TranscribeResponse::class.java)
                Result.success(transcribeResponse)
            } else {
                Result.failure(IOException("Transcription failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun parse(text: String): Result<ParseResponse> = withContext(Dispatchers.IO) {
        try {
            val jsonBody = gson.toJson(mapOf("text" to text))
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("$baseUrl/api/parse")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string()
                val parseResponse = gson.fromJson(body, ParseResponse::class.java)
                Result.success(parseResponse)
            } else {
                Result.failure(IOException("Parse failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun healthCheck(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/health")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class TranscribeResponse(
    val text: String,
    val error: String? = null
)

data class ParseResponse(
    val pair: String? = null,
    val entry_timeframe: String?,
    val htf_bias: String?,
    val setup: String?,
    val confluences: List<String>?,
    val date: String?,
    val summary: String?,
    val rr_ratio: Float? = null,
    val result: String? = null,
    val error: String? = null
)

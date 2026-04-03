package com.tradingjournal.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(
    tableName = "trades",
    foreignKeys = [
        ForeignKey(
            entity = Strategy::class,
            parentColumns = ["id"],
            childColumns = ["strategyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Trade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val strategyId: Long?,
    val pair: String = "",
    val entryTimeframe: String = "",
    val htfBias: String = "",
    val setup: String = "",
    val confluences: List<String> = emptyList(),
    val summary: String = "",
    val rawText: String = "",
    val sessionType: SessionType = SessionType.LIVE,
    val result: TradeResult? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SessionType {
    LIVE, BACKTEST
}

enum class TradeResult {
    WIN, LOSS, BE
}

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

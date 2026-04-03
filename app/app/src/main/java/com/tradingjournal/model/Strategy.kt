package com.tradingjournal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "strategies")
data class Strategy(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val market: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

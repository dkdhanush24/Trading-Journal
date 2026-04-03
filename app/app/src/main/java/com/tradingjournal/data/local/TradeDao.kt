package com.tradingjournal.data.local

import androidx.room.*
import com.tradingjournal.model.Trade
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<Trade>>
    
    @Query("SELECT * FROM trades WHERE strategyId = :strategyId ORDER BY timestamp DESC")
    fun getTradesByStrategy(strategyId: Long): Flow<List<Trade>>
    
    @Query("SELECT * FROM trades WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getTradesByDateRange(startTime: Long, endTime: Long): Flow<List<Trade>>
    
    @Query("SELECT * FROM trades WHERE timestamp BETWEEN :startOfDay AND :endOfDay ORDER BY timestamp DESC")
    fun getTradesForDay(startOfDay: Long, endOfDay: Long): Flow<List<Trade>>
    
    @Query("SELECT * FROM trades WHERE id = :id")
    suspend fun getTradeById(id: Long): Trade?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: Trade): Long
    
    @Update
    suspend fun updateTrade(trade: Trade)
    
    @Delete
    suspend fun deleteTrade(trade: Trade)
    
    @Query("SELECT COUNT(*) FROM trades")
    suspend fun getTradeCount(): Int
}

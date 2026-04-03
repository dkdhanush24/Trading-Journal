package com.tradingjournal.data.local

import androidx.room.*
import com.tradingjournal.model.Strategy
import kotlinx.coroutines.flow.Flow

@Dao
interface StrategyDao {
    @Query("SELECT * FROM strategies ORDER BY createdAt DESC")
    fun getAllStrategies(): Flow<List<Strategy>>
    
    @Query("SELECT * FROM strategies WHERE id = :id")
    suspend fun getStrategyById(id: Long): Strategy?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrategy(strategy: Strategy): Long
    
    @Update
    suspend fun updateStrategy(strategy: Strategy)
    
    @Delete
    suspend fun deleteStrategy(strategy: Strategy)
    
    @Query("SELECT COUNT(*) FROM strategies")
    suspend fun getStrategyCount(): Int
}

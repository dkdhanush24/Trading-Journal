package com.tradingjournal.data.repository

import com.tradingjournal.data.local.StrategyDao
import com.tradingjournal.data.local.TradeDao
import com.tradingjournal.model.Strategy
import com.tradingjournal.model.Trade
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TradeRepository(
    private val strategyDao: StrategyDao,
    private val tradeDao: TradeDao
) {
    val allStrategies: Flow<List<Strategy>> = strategyDao.getAllStrategies()
    val allTrades: Flow<List<Trade>> = tradeDao.getAllTrades()
    
    suspend fun getStrategyById(id: Long): Strategy? = strategyDao.getStrategyById(id)
    
    suspend fun insertStrategy(strategy: Strategy): Long = strategyDao.insertStrategy(strategy)
    
    suspend fun updateStrategy(strategy: Strategy) = strategyDao.updateStrategy(strategy)
    
    suspend fun deleteStrategy(strategy: Strategy) = strategyDao.deleteStrategy(strategy)
    
    suspend fun insertTrade(trade: Trade): Long = tradeDao.insertTrade(trade)
    
    suspend fun updateTrade(trade: Trade) = tradeDao.updateTrade(trade)
    
    suspend fun deleteTrade(trade: Trade) = tradeDao.deleteTrade(trade)
    
    fun getTradesForDay(year: Int, month: Int, day: Int): Flow<List<Trade>> {
        val calendar = Calendar.getInstance().apply {
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis - 1
        
        return tradeDao.getTradesForDay(startOfDay, endOfDay)
    }
    
    fun getTradesByStrategy(strategyId: Long): Flow<List<Trade>> {
        return tradeDao.getTradesByStrategy(strategyId)
    }
    
    suspend fun getStrategyCount(): Int = strategyDao.getStrategyCount()
}

package com.tradingjournal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tradingjournal.model.Converters
import com.tradingjournal.model.Strategy
import com.tradingjournal.model.Trade

@Database(
    entities = [Strategy::class, Trade::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun strategyDao(): StrategyDao
    abstract fun tradeDao(): TradeDao
}

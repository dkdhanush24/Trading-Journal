package com.tradingjournal

import android.app.Application
import androidx.room.Room
import com.tradingjournal.data.local.AppDatabase
import com.tradingjournal.data.remote.ApiService
import com.tradingjournal.data.repository.TradeRepository

class VoiceTradingApp : Application() {
    
    lateinit var database: AppDatabase
        private set
    
    lateinit var repository: TradeRepository
        private set
    
    lateinit var apiService: ApiService
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "voice_trading_journal"
        )
        .fallbackToDestructiveMigration()
        .build()
        
        repository = TradeRepository(
            strategyDao = database.strategyDao(),
            tradeDao = database.tradeDao()
        )
        
        apiService = ApiService()
    }
    
    companion object {
        lateinit var instance: VoiceTradingApp
            private set
    }
}

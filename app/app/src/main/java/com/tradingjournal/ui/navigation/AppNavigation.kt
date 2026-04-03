package com.tradingjournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tradingjournal.ui.screens.HomeScreen
import com.tradingjournal.ui.screens.JournalScreen
import com.tradingjournal.ui.screens.RecordScreen
import com.tradingjournal.ui.screens.ReviewScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Record : Screen("record/{strategyId}") {
        fun createRoute(strategyId: Long) = "record/$strategyId"
    }
    data object Review : Screen("review/{strategyId}/{rawText}") {
        fun createRoute(strategyId: Long, rawText: String) = "review/$strategyId/${java.net.URLEncoder.encode(rawText, "UTF-8")}"
    }
    data object Journal : Screen("journal")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartRecording = { strategyId ->
                    navController.navigate(Screen.Record.createRoute(strategyId))
                },
                onOpenJournal = {
                    navController.navigate(Screen.Journal.route)
                }
            )
        }
        
        composable(
            route = Screen.Record.route,
            arguments = listOf(navArgument("strategyId") { type = NavType.LongType })
        ) { backStackEntry ->
            val strategyId = backStackEntry.arguments?.getLong("strategyId") ?: 0L
            RecordScreen(
                strategyId = strategyId,
                onTranscriptionComplete = { rawText ->
                    navController.navigate(Screen.Review.createRoute(strategyId, rawText)) {
                        popUpTo(Screen.Record.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.Review.route,
            arguments = listOf(
                navArgument("strategyId") { type = NavType.LongType },
                navArgument("rawText") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val strategyId = backStackEntry.arguments?.getLong("strategyId") ?: 0L
            val rawText = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("rawText") ?: "",
                "UTF-8"
            )
            ReviewScreen(
                strategyId = strategyId,
                rawText = rawText,
                onSaveComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Journal.route) {
            JournalScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

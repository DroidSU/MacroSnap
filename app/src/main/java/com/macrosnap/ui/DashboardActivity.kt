package com.macrosnap.ui

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.macrosnap.data.local.MealDatabase
import com.macrosnap.data.remote.GeminiService
import com.macrosnap.data.repository.MealRepository
import com.macrosnap.ui.screen.CameraScreen
import com.macrosnap.ui.screen.HistoryScreen
import com.macrosnap.ui.screen.ResultScreen
import com.macrosnap.ui.theme.MacroSnapTheme
import com.macrosnap.ui.viewmodel.MacroSnapViewModelFactory
import com.macrosnap.ui.viewmodel.MealUiState
import com.macrosnap.ui.viewmodel.MealViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MealDatabase.getDatabase(this)
        val geminiService = GeminiService()
        val repository = MealRepository(geminiService, database.mealDao())
        val viewModelFactory = MacroSnapViewModelFactory(repository)

        setContent {
            MacroSnapTheme {
                val navController = rememberNavController()
                val mealViewModel: MealViewModel = viewModel(factory = viewModelFactory)
                val uiState by mealViewModel.uiState.collectAsState()
                var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val showBottomBar = currentDestination?.route in listOf("camera", "history")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.PhotoCamera,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Scanner") },
                                    selected = currentDestination?.hierarchy?.any { it.route == "camera" } == true,
                                    onClick = {
                                        navController.navigate("camera") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.History,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("History") },
                                    selected = currentDestination?.hierarchy?.any { it.route == "history" } == true,
                                    onClick = {
                                        navController.navigate("history") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "camera",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("camera") {
                            CameraScreen(onImageCaptured = { bitmap ->
                                capturedBitmap = bitmap
                                mealViewModel.analyzeMeal(bitmap)
                                runOnUiThread {
                                    navController.navigate("result")
                                }
                            })
                        }
                        composable("result") {
                            ResultScreen(
                                uiState = uiState,
                                capturedImage = capturedBitmap,
                                onBack = {
                                    mealViewModel.resetState()
                                    navController.popBackStack()
                                },
                                onSave = {
                                    val currentState = uiState
                                    if (currentState is MealUiState.Success) {
                                        mealViewModel.saveMeal(currentState.analysis)
                                        navController.navigate("history") {
                                            popUpTo("camera") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                        composable("history") {
                            HistoryScreen(mealViewModel)
                        }
                    }
                }
            }
        }
    }
}

package com.macrosnap.ui.screen

import SettingsScreen
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.macrosnap.data.local.MealEntity
import com.macrosnap.data.model.MealAnalysis
import com.macrosnap.ui.theme.MacroSnapTheme
import com.macrosnap.ui.viewmodel.MealUiState
import com.macrosnap.ui.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: MealUiState,
    history: List<MealEntity>,
    weeklyMeals: List<MealEntity>,
    capturedImage: Bitmap?,
    sortOrder: SortOrder,
    onAnalyzeMeal: (Bitmap) -> Unit,
    onSaveMeal: (MealAnalysis) -> Unit,
    onDeleteMeal: (MealEntity) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onResetState: () -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    Triple("history", Icons.Default.History, "History"),
                    Triple("camera", Icons.Default.PhotoCamera, "Scanner"),
                    Triple("settings", Icons.Default.Settings, "Settings")
                )

                items.forEach { (route, icon, label) ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == route } == true
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "camera",
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                composable("camera") {
                    CameraScreen(
                        onImageCaptured = { bitmap ->
                            onAnalyzeMeal(bitmap)
                            navController.navigate("result")
                        }
                    )
                }
                composable("result") {
                    ResultScreen(
                        uiState = uiState,
                        capturedImage = capturedImage,
                        onBack = {
                            onResetState()
                            navController.popBackStack()
                        },
                        onSave = {
                            if (uiState is MealUiState.Success) {
                                onSaveMeal(uiState.analysis)
                                navController.navigate("history") {
                                    popUpTo("camera") { inclusive = true }
                                }
                            }
                        }
                    )
                }
                composable("history") {
                    HistoryScreen(
                        history = history,
                        weeklyMeals = weeklyMeals,
                        sortOrder = sortOrder,
                        onSortOrderChange = onSortOrderChange,
                        onDeleteMeal = onDeleteMeal
                    )
                }
                composable("settings") {
                    SettingsScreen(onSignOut = onSignOut)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    MacroSnapTheme {
        DashboardScreen(
            uiState = MealUiState.Loading,
            history = emptyList(),
            weeklyMeals = emptyList(),
            capturedImage = null,
            sortOrder = SortOrder.DATE_DESC,
            onAnalyzeMeal = {},
            onSaveMeal = {},
            onDeleteMeal = {},
            onSortOrderChange = {},
            onResetState = {},
            onSignOut = {}
        )
    }
}

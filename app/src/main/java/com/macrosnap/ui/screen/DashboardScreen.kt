package com.macrosnap.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: MealUiState,
    history: List<MealEntity>,
    weeklyMeals: List<MealEntity>,
    capturedImage: Bitmap?,
    onAnalyzeMeal: (Bitmap) -> Unit,
    onStartLoading: () -> Unit,
    onSaveMeal: (MealAnalysis) -> Unit,
    onResetState: () -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var showMenu by remember { mutableStateOf(false) }

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
                            onStartLoading()
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
                    HistoryScreen(history = history, weeklyMeals = weeklyMeals)
                }
                composable("settings") {
                    SettingsScreen(onSignOut = onSignOut)
                }
            }

            TopAppBar(
                title = { },
                actions = {
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = if (currentDestination?.route == "camera") Color.White else MaterialTheme.colorScheme.primary
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Profile") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("App Settings") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Help & Support") },
                                onClick = { showMenu = false }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onSignOut()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MacroSnapTheme {
        DashboardScreen(
            uiState = MealUiState.Idle,
            history = emptyList(),
            weeklyMeals = emptyList(),
            capturedImage = null,
            onAnalyzeMeal = {},
            onStartLoading = {},
            onSaveMeal = {},
            onResetState = {},
            onSignOut = {}
        )
    }
}

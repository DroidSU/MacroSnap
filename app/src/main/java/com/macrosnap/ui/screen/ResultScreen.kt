package com.macrosnap.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.macrosnap.data.model.MealAnalysis
import com.macrosnap.ui.components.MacroCard
import com.macrosnap.ui.theme.MacroSnapTheme
import com.macrosnap.ui.viewmodel.MealUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    uiState: MealUiState,
    capturedImage: Bitmap?,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Analysis") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Retake")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            capturedImage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Captured Meal",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            when (uiState) {
                is MealUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                    Text("Analyzing your meal...")
                }
                is MealUiState.Success -> {
                    MacroCard(analysis = uiState.analysis)
                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save to Daily Log")
                    }
                }
                is MealUiState.Error -> {
                    Text(
                        text = "Error: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = onBack) {
                        Text("Try Again")
                    }
                }
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    MacroSnapTheme {
        ResultScreen(
            uiState = MealUiState.Success(
                MealAnalysis(
                    dishName = "Chicken Pasta",
                    calories = 550,
                    protein = 30,
                    carbs = 60,
                    fats = 20,
                )
            ),
            capturedImage = null,
            onBack = {},
            onSave = {}
        )
    }
}

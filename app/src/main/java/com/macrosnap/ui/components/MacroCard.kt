package com.macrosnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.macrosnap.data.model.MealAnalysis

@Composable
fun MacroCard(analysis: MealAnalysis, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = analysis.dishName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroItem(label = "Calories", value = "${analysis.calories} kcal")
                MacroItem(label = "Protein", value = "${analysis.protein}g")
                MacroItem(label = "Carbs", value = "${analysis.carbs}g")
                MacroItem(label = "Fats", value = "${analysis.fats}g")
            }

            Divider()

            if (!analysis.healthierSwap.isNullOrBlank()) {
                RecommendationItem(label = "Healthier Swap", text = analysis.healthierSwap)
            }

            if (!analysis.portionTweak.isNullOrBlank()) {
                RecommendationItem(label = "Portion Tweak", text = analysis.portionTweak)
            }
        }
    }
}

@Composable
private fun MacroItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RecommendationItem(label: String, text: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

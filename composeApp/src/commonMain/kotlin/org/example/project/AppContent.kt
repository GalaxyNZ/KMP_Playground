package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.project.scoring.ScoringScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun AppContent() {
    MaterialTheme {
        ScoringScreen()
    }
}
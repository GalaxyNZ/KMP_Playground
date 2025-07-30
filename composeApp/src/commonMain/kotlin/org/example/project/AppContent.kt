package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

import org.example.project.scoring.ScoringScreen

@Composable
@Preview
fun AppContent() {
    MaterialTheme {
        ScoringScreen()
    }
}
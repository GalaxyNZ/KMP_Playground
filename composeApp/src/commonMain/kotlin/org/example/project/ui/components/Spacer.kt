package org.example.project.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Spacer(horizontal: Dp = 0.dp, vertical: Dp = 0.dp) {
    androidx.compose.foundation.layout.Spacer(
        Modifier.size(width = horizontal, height = vertical)
    )
}
package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSeparator(
    padding: PaddingValues = PaddingValues(0.dp),
    width: Dp = 0.5.dp,
    backgroundColor: Color = Color.DarkGray,
) {
    Box(
        Modifier
            .padding(padding)
            .fillMaxHeight()
            .width(width)
            .background(backgroundColor)
    )
}

@Composable
fun HorizontalSeparator(
    padding: PaddingValues = PaddingValues(0.dp),
    height: Dp = 0.5.dp,
    backgroundColor: Color = Color.DarkGray,
) {
    Box(
        Modifier
            .padding(padding)
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor)
    )
}
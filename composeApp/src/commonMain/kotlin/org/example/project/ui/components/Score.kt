package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.squareSize
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Score(
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Box(
        Modifier
            .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .clickable {
                onClick?.invoke()
            }
            .padding(5.dp)
            .squareSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 120.sp,
        )
    }
}

@Preview
@Composable
private fun ScorePreview() {
    Score(
        text = "1",
        onClick = null
    )
}
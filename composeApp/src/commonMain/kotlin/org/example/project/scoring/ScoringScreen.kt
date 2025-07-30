package org.example.project.scoring

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.P
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScoringScreen() {
    ScoringScreenContent(
        ScoringScreenModel(
        teamAName = "",
        teamBName = "",
        teamAScore = 0,
        teamBScore = 0,
        timeRemaining = "00:00:00"
    ), updateScore1 = {}, updateScore2 = {})
}

@Composable
internal fun ScoringScreenContent(
    model: ScoringScreenModel,
    updateScore1: (Int) -> Unit,
    updateScore2: (Int) -> Unit,
) {
    Box(Modifier.background(Color.LightGray).fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    teamName = model.teamAName,
                    teamScore = model.teamAScore,
                    updateScore = updateScore1
                )
            }

            VerticalSeparator()

            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    teamName = model.teamBName,
                    teamScore = model.teamBScore,
                    updateScore = updateScore2
                )
            }
        }
    }
}

@Composable
fun TeamScoringSection(
    teamName: String,
    teamScore: Int,
    updateScore: (Int) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = teamName, fontSize = 18.sp
        )

        Text(
            text = teamScore.toString(), fontSize = 32.sp
        )

        Button(
            onClick = { updateScore(teamScore + 1) },
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { updateScore(teamScore - 1) }
                )
            }) {
            Text(text = "Increment Score 2")
        }
    }
}

@Composable
fun VerticalSeparator() {
    Box(
        Modifier.fillMaxHeight().width(0.5.dp).background(Color.DarkGray)
    )
}

@Preview
@Composable
fun ScoringScreenPreview() {
    ScoringScreenContent(
        ScoringScreenModel(
            "Team A",
            "Team B",
            0,
            0,
            "00:00:00"
        ),
        {},{}
    )
}
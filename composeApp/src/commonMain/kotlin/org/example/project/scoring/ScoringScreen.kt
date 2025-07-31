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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.scoring.ScoringViewModel.Team
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun ScoringScreen(
    viewModel: ScoringViewModel = koinInject(),
) {
    val screenModel by viewModel.screenModel.collectAsState(
        initial = ScoringScreenModel(TeamScoringData("", 0), TeamScoringData("", 0))
    )

    ScoringScreenContent(
        screenModel,
        updateScore = viewModel::updateScore,
    )
}

@Composable
internal fun ScoringScreenContent(
    model: ScoringScreenModel,
    updateScore: (Team, Int) -> Unit,
) {
    Box(Modifier.background(Color.LightGray).fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    team = Team.A,
                    teamName = model.team1Data.teamName,
                    teamScore = model.team1Data.score,
                    updateScore = updateScore
                )
            }

            VerticalSeparator()

            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    Team.B,
                    teamName = model.team2Data.teamName,
                    teamScore = model.team2Data.score,
                    updateScore = updateScore
                )
            }
        }
    }
}

@Composable
fun TeamScoringSection(
    team: Team,
    teamName: String,
    teamScore: Int,
    updateScore: (Team, Int) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = teamName, fontSize = 18.sp
        )

        Text(
            text = teamScore.toString(), fontSize = 32.sp
        )

        Button(
            onClick = { updateScore(team, teamScore + 1) },
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { updateScore(team, teamScore - 1) }
                )
            }) {
            Text(text = "Increment Score ${team.name}")
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
            TeamScoringData(
                "Team A",
                0
            ),
            TeamScoringData(
                "Team B",
                0
            )
        ),
    ) { _, _ -> }
}
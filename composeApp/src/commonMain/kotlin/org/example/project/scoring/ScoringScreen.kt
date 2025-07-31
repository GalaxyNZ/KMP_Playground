package org.example.project.scoring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.scoring.ScoringViewModel.Team
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import kotlin.math.pow
import kotlin.math.round

@Composable
fun ScoringScreen(
    viewModel: ScoringViewModel = koinInject(),
) {
    val screenModel by viewModel.screenModel.collectAsState(
        initial = ScoringScreenModel(
            TeamScoringData("", 0, 0, 0, 0f),
            TeamScoringData("", 0, 0, 0, 0f),
            0
        )
    )

    ScoringScreenContent(
        screenModel,
        startTimer = viewModel::timerPressed,
        addGoal = viewModel::addGoal,
        addShot = viewModel::addShot,
    )
}

@Composable
internal fun ScoringScreenContent(
    model: ScoringScreenModel,
    startTimer: () -> Unit,
    addGoal: (Team) -> Unit,
    addShot: (Team) -> Unit,
) {
    Column(Modifier.background(Color.LightGray).fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            val totalSeconds = model.timer / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            val time = if (minutes == 0L) {
                "${seconds.padWith0()}:${((model.timer % 1000)).padWith0().take(2)}"
            } else {
                "${minutes.padWith0()}:${seconds.padWith0()}"
            }

            Text(
                text = time, fontSize = 48.sp,
                modifier = Modifier.clickable {
                    startTimer()
                }
            )
        }
        Row(Modifier.weight(1f)) {
            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    team = Team.A,
                    teamData = model.team1Data,
                    addGoal = addGoal,
                    addShot = addShot,
                )
            }

            VerticalSeparator()

            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    Team.B,
                    teamData = model.team2Data,
                    addGoal = addGoal,
                    addShot = addShot,
                )
            }
        }
    }
}

@Composable
fun TeamScoringSection(
    team: Team,
    teamData: TeamScoringData,
    addGoal: (Team) -> Unit,
    addShot: (Team) -> Unit,
) = with(teamData) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name, fontSize = 18.sp
        )

        Text(
            text = score.toString(), fontSize = 32.sp
        )

        Button(
            onClick = { addGoal(team) },
        ) {
            Text(text = "Increment Score ${team.name}")
        }

        Text(
            text = "Shots: $shots", fontSize = 16.sp
        )

        Button(
            onClick = { addShot(team) },
        ) {
            Text(text = "Add Shot")
        }

        Text(
            text = "Saves: $saves", fontSize = 16.sp
        )
        Text(
            text = "Save %: ${formatTwoDecimals(savePercentage)}", fontSize = 16.sp
        )
    }
}

fun Long.padWith0(length: Int = 2): String {
    return this.toString().padWith0(length)
}

fun String.padWith0(length: Int = 2): String {
    return this.padStart(length, '0')
}

fun formatTwoDecimals(value: Float): String {
    val factor = 10.0.pow(2)
    return (round(value * factor) / factor).toString()
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
                0,
                0,
                0,
                0f
            ),
            TeamScoringData(
                "Team B",
                0,
                0,
                0,
                0f
            ),
            0
        ),
        {},
        {},
        {}
    )
}
package org.example.project.scoring

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        addPenalty = viewModel::addPenalty,
    )
}

@Composable
internal fun ScoringScreenContent(
    model: ScoringScreenModel,
    startTimer: () -> Unit,
    addGoal: (Team) -> Unit,
    addShot: (Team) -> Unit,
    addPenalty: (Team) -> Unit,
) {
    Column(Modifier.background(Color.LightGray).fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Text(
                text = formatTimer(model.timer), fontSize = 48.sp,
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
                    addPenalty = addPenalty,
                )
            }

            VerticalSeparator()

            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    Team.B,
                    teamData = model.team2Data,
                    addGoal = addGoal,
                    addShot = addShot,
                    addPenalty = addPenalty,
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
    addPenalty: (Team) -> Unit,
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


        Button(
            onClick = { addPenalty(team) },
        ) {
            Text(text = "Add Penalty")
        }

        Column(
            Modifier.fillMaxWidth().padding(10.dp).border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp)).verticalScroll(rememberScrollState()),
        ) {
            for (i in penalties.filter { !it.penaltyServed }) {
                Column(
                    Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                        .padding(10.dp)
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Text(
                        text = "Penalty ${i.playerName}", fontSize = 16.sp
                    )
                    Text(
                        text = "at ${formatTimeToMinute(i.timeOfPenalty)}", fontSize = 16.sp
                    )
                    Text(
                        text = "Time left: ${formatTimer(i.penaltyTimeRemaining)}", fontSize = 16.sp
                    )
                }
            }
        }
    }
}

fun formatTimer(time: Long): String {
    val totalSeconds = time / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return if (minutes == 0L) {
        "${seconds.padWith0()}:${((time % 1000)).padWith0().take(2)}"
    } else {
        "${minutes.padWith0()}:${seconds.padWith0()}"
    }
}

fun formatTimeToMinute(time: Long): String {
    val totalSeconds = time / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "${minutes.padWith0()}:${seconds.padWith0()}"
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
        {},
        {}
    )
}
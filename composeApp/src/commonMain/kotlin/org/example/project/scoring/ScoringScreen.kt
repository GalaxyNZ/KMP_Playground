package org.example.project.scoring

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import org.example.project.helpers.formatTimeToMinute
import org.example.project.helpers.formatTimer
import org.example.project.scoring.ScoringViewModel.Team
import org.example.project.scoring.prompts.EditNamePrompt
import org.example.project.ui.components.Score
import org.example.project.ui.components.VerticalSeparator
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun ScoringScreen(
    viewModel: ScoringViewModel = koinInject(),
) {
    val screenModel by viewModel.screenModel.collectAsState(
        initial = ScoringScreenModel(
            TeamScoringData("", 0, 0),
            TeamScoringData("", 0, 0),
            0,
            2,
            null
        )
    )

    ScoringScreenContent(
        screenModel,
        timerPressed = viewModel::timerPressed,
        addGoal = viewModel::addGoal,
        addShot = viewModel::addShot,
        addPenalty = viewModel::addPenalty,
        updateName = viewModel::editTeamName,
        showPrompt = viewModel::showEditNamePrompt,
        dismissPrompt = viewModel::dismissPrompt
    )
}

@Composable
internal fun ScoringScreenContent(
    model: ScoringScreenModel,
    timerPressed: () -> Unit,
    addGoal: (Team) -> Unit,
    addShot: (Team) -> Unit,
    addPenalty: (Team) -> Unit,
    updateName: (Team, String) -> Unit,
    showPrompt: (Team) -> Unit,
    dismissPrompt: () -> Unit,
) {
    Column(Modifier.background(Color.White).fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTimer(model.timer), fontSize = 48.sp,
                modifier = Modifier.clickable {
                    timerPressed()
                }
            )

            Text(
                text = "Period ${model.period}", fontSize = 24.sp,
            )
        }
        Row(Modifier.weight(1f)) {
            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    team = Team.Home,
                    teamData = model.team1Data,
                    addGoal = addGoal,
                    addShot = addShot,
                    addPenalty = addPenalty,
                    editTeamName = showPrompt,
                )
            }

            VerticalSeparator()

            Box(Modifier.weight(1f).fillMaxHeight()) {
                TeamScoringSection(
                    Team.Away,
                    teamData = model.team2Data,
                    addGoal = addGoal,
                    addShot = addShot,
                    addPenalty = addPenalty,
                    editTeamName = showPrompt
                )
            }
        }

        model.editNamePromptDetails?.let { promptDetails ->
            EditNamePrompt(
                team = promptDetails.team,
                teamName = promptDetails.teamName,
                onNameChange = { team, newName ->
                    updateName(team, newName)
                },
                onDismiss = {
                    dismissPrompt()
                }
            )
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
    editTeamName: (Team) -> Unit,
) = with(teamData) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name, fontSize = 18.sp, modifier = Modifier.clickable {
                editTeamName(team)
            }
        )

        Score(
            text = score.toString()
        ) {
            addGoal(team)
        }

        Text(
            text = "$shots shots", fontSize = 16.sp
        )

        Button(
            onClick = { addShot(team) },
        ) {
            Text(text = "Add Shot")
        }

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
                        text = "Penalty ${i.playerName}", fontSize = 12.sp
                    )
                    Text(
                        text = "at ${formatTimeToMinute(i.timeOfPenalty)}", fontSize = 8.sp
                    )
                    Text(
                        text = "Time left: ${formatTimer(i.penaltyTimeRemaining)}", fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ScoringScreenPreview() {
    ScoringScreen(
        ScoringViewModel()
    )
}
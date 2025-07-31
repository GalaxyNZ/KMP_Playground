package org.example.project.scoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ScoringViewModel: ViewModel() {

    private val periodDuration = 1.5.minutes.inWholeMilliseconds

    //    private val startTime: MutableStateFlow<Long> = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
    private val elapsedTime: MutableStateFlow<Long> = MutableStateFlow(periodDuration)
    private val endTime: MutableStateFlow<Long> = MutableStateFlow(elapsedTime.value + periodDuration)

    private var timerState = TimerState.STOPPED

    enum class TimerState {
        RUNNING,
        PAUSED,
        STOPPED
    }

    fun timerPressed() {
        when (timerState) {
            TimerState.STOPPED -> startTimer()
            TimerState.RUNNING -> pauseTimer()
            TimerState.PAUSED -> runTimer()
        }
    }

    private fun startTimer() {
        endTime.value = Clock.System.now().toEpochMilliseconds() + periodDuration - elapsedTime.value
        runTimer()
    }

    private fun pauseTimer() {
        timerState = TimerState.PAUSED
    }

    private fun runTimer() {
        timerState = TimerState.RUNNING
        viewModelScope.launch {
            while (timerState == TimerState.RUNNING) {
                elapsedTime.value = Clock.System.now().toEpochMilliseconds() - endTime.value

                //                if (elapsedTime.value < 0) {
                //                    elapsedTime.value = 0
                //                    timerState = TimerState.STOPPED
                //                }

                delay(50) // Wait for 1 second
            }
        }
    }

    private val team1Name = MutableStateFlow("Team A")
    private val team1Goals = MutableStateFlow(0)
    private val team1Shots = MutableStateFlow(0)

    private val team2Name = MutableStateFlow("Team B")
    private val team2Goals = MutableStateFlow(0)
    private val team2Shots = MutableStateFlow(0)

    private val team1Data = combine(
        team1Name,
        team1Goals,
        team1Shots,
        team2Shots,
        team2Goals,
    ) { name, score, shots, oppShots, oppGoals ->
        TeamScoringData(
            name = name,
            score = score,
            shots = shots,
            saves = oppShots - oppGoals,
            savePercentage = if (oppShots == 0) 1f else ((oppShots.toFloat() - oppGoals) / oppShots)
        )
    }
    private val team2Data = combine(
        team2Name,
        team2Goals,
        team2Shots,
        team1Shots,
        team1Goals
    ) { name, score, shots, oppShots, oppGoals ->
        TeamScoringData(
            name = name,
            score = score,
            shots = shots,
            saves = oppShots - oppGoals,
            savePercentage = if (oppShots == 0 || oppGoals == 0) 1f else ((oppShots.toFloat() - oppGoals) / oppShots)
        )
    }

    val screenModel = combine(
        team1Data,
        team2Data,
        elapsedTime,
    ) { team1, team2, timer ->
        ScoringScreenModel(
            team1,
            team2,
            timer
        )
    }

    fun addGoal(team: Team) {
        when (team) {
            Team.A -> team1Goals.value += 1
            Team.B -> team2Goals.value += 1
        }

        addShot(team)
    }

    fun addShot(team: Team) {
        when (team) {
            Team.A -> team1Shots.value += 1
            Team.B -> team2Shots.value += 1
        }
    }

    enum class Team {
        A,
        B
    }
}

data class TeamScoringData(
    val name: String,
    val score: Int,
    val shots: Int,
    val saves: Int,
    val savePercentage: Float,
)
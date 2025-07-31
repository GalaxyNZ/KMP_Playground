package org.example.project.scoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ScoringViewModel: ViewModel() {

    private val periodDuration = 20.minutes.inWholeMilliseconds

    // Timer state management
    private var timerJob: Job? = null
    private var startTimestamp: Long = 0L
    private var pausedRemainingTime: Long = periodDuration

    // Changed: remainingTime is now the primary timer value
    private val remainingTime = MutableStateFlow(periodDuration)

    private val timerState = MutableStateFlow(TimerState.STOPPED)

    // Optional: Keep elapsedTime for backward compatibility if needed
    private val elapsedTime = MutableStateFlow(0L)

    enum class TimerState {
        RUNNING,
        PAUSED,
        STOPPED
    }

    fun timerPressed() {
        when (timerState.value) {
            TimerState.STOPPED -> startTimer()
            TimerState.RUNNING -> pauseTimer()
            TimerState.PAUSED -> resumeTimer()
        }
    }

    private fun startTimer() {
        startTimestamp = Clock.System.now().toEpochMilliseconds()
        pausedRemainingTime = periodDuration
        remainingTime.value = periodDuration
        elapsedTime.value = 0L
        timerState.value = TimerState.RUNNING
        startTimerJob()
    }

    private fun pauseTimer() {
        timerState.value = TimerState.PAUSED
        timerJob?.cancel()
        timerJob = null
        // Save the remaining time when pausing
        pausedRemainingTime = remainingTime.value
    }

    private fun resumeTimer() {
        // When resuming, adjust the start timestamp to account for paused time
        val timeAlreadyElapsed = periodDuration - pausedRemainingTime
        startTimestamp = Clock.System.now().toEpochMilliseconds() - timeAlreadyElapsed
        timerState.value = TimerState.RUNNING
        startTimerJob()
    }

    private fun stopTimer() {
        timerState.value = TimerState.STOPPED
        timerJob?.cancel()
        timerJob = null
        remainingTime.value = periodDuration
        elapsedTime.value = 0L
        pausedRemainingTime = periodDuration
    }

    private fun startTimerJob() {
        timerJob?.cancel() // Cancel any existing job
        timerJob = viewModelScope.launch {
            while (timerState.value == TimerState.RUNNING) {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                val elapsed = currentTime - startTimestamp
                elapsedTime.value = elapsed

                val remaining = periodDuration - elapsed
                remainingTime.value = maxOf(0L, remaining)

                // Stop timer when time is up
                if (remaining <= 0) {
                    stopTimer()
                    break
                }

                delay(100) // Update every 100ms for smooth display
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private val team1Name = MutableStateFlow("Team A")
    private val team1Goals = MutableStateFlow(0)
    private val team1Shots = MutableStateFlow(0)
    private val team1Penalties = MutableStateFlow(emptyList<Penalty>())

    private val team1PenaltyState = combine(
        team1Penalties,
        elapsedTime
    ) { penalties, elapsedTime ->
        penalties.map { penalty ->
            if (penalty.penaltyServed) {
                return@map penalty // Skip served penalties
            }
            val timeRemaining = (penalty.timeOfPenalty + (penalty.penaltyLengthSeconds * 1000)) - elapsedTime
            Penalty(
                playerName = penalty.playerName,
                timeOfPenalty = penalty.timeOfPenalty,
                penaltyTimeRemaining = timeRemaining,
                penaltyServed = timeRemaining <= 0,
            )
        }
    }

    private val team2Name = MutableStateFlow("Team B")
    private val team2Goals = MutableStateFlow(0)
    private val team2Shots = MutableStateFlow(0)

    private val team1Data = combine(
        team1Name,
        team1Goals,
        team1Shots,
        team1PenaltyState,
        //        team2Shots,
        //        team2Goals,
    ) { name, score, shots, penalties -> //oppShots, oppGoals ->
        TeamScoringData(
            name = name,
            score = score,
            shots = shots,
            saves = 0,
            savePercentage = 1f, // Default to 1f until we have opponent data
            //            saves = oppShots - oppGoals,
            //            savePercentage = if (oppShots == 0) 1f else ((oppShots.toFloat() - oppGoals) / oppShots),
            penalties = penalties
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
        remainingTime,
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

    fun addPenalty(team: Team) {
        team1Penalties.value +=
            Penalty(
                "Player ${Random.nextInt(0, 9)}",
                elapsedTime.value,
                penaltyLengthSeconds = 10

            )
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
    val penalties: List<Penalty> = emptyList(),
)

data class Penalty(
    val playerName: String,
    val timeOfPenalty: Long,
    val penaltyTimeRemaining: Long = 120,
    val penaltyLengthSeconds: Long = 120,
    val penaltyServed: Boolean = false,
)
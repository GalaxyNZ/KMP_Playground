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

    private val periods = 3
    private val currentPeriod = MutableStateFlow(1)
    private val periodDuration = 15.minutes.inWholeMilliseconds

    // Timer state management
    private var timerJob: Job? = null
    private var startTimestamp: Long = 0L
    private var pausedRemainingTime: Long = periodDuration

    private val remainingTime = MutableStateFlow(periodDuration * periods) // Count down
    private val elapsedTime = MutableStateFlow(0L) // Count up

    private val timerState = MutableStateFlow(TimerState.STOPPED)

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
        pausedRemainingTime = periodDuration * periods
        remainingTime.value = periodDuration * periods
        elapsedTime.value = 0L
        currentPeriod.value = 1
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
        val timeAlreadyElapsed = periodDuration * periods - pausedRemainingTime
        startTimestamp = Clock.System.now().toEpochMilliseconds() - timeAlreadyElapsed
        timerState.value = TimerState.RUNNING
        startTimerJob()
    }

    private fun stopTimer() {
        timerState.value = TimerState.STOPPED
        timerJob?.cancel()
        timerJob = null
        remainingTime.value = periodDuration * periods
        elapsedTime.value = 0L
        pausedRemainingTime = periodDuration * periods
    }

    private fun startTimerJob() {
        timerJob?.cancel() // Cancel any existing job
        timerJob = viewModelScope.launch {
            while (timerState.value == TimerState.RUNNING) {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                val elapsed = currentTime - startTimestamp
                elapsedTime.value = elapsed

                val remaining = periodDuration * periods - elapsed
                remainingTime.value = maxOf(0L, remaining)

                // Stop timer when time is up
                if (remaining - ((periods - currentPeriod.value) * periodDuration) <= 0) {
                    pauseTimer()
                    currentPeriod.value += 1
                    if (currentPeriod.value > periods) {
                        stopTimer()
                        remainingTime.value = 0
                        currentPeriod.value = periods
                    }
                    // TODO: game over check
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

    private val homeName = MutableStateFlow("Team A")
    private val homeGoals = MutableStateFlow(0)
    private val homeShots = MutableStateFlow(0)
    private val homePenalties = MutableStateFlow(emptyList<Penalty>())

    private val homePenaltyState = combine(
        homePenalties,
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

    private val awayName = MutableStateFlow("Team B")
    private val awayGoals = MutableStateFlow(0)
    private val awayShots = MutableStateFlow(0)
    private val awayPenalties = MutableStateFlow(emptyList<Penalty>())

    private val awayPenaltyState = combine(
        awayPenalties,
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

    private val homeData = combine(
        homeName,
        homeGoals,
        homeShots,
        homePenaltyState,
    ) { name, score, shots, penalties -> //oppShots, oppGoals ->
        TeamScoringData(
            name = name,
            score = score,
            shots = shots,
            penalties = penalties,
        )
    }
    private val awayData = combine(
        awayName,
        awayGoals,
        awayShots,
        awayPenaltyState
    ) { name, score, shots, penalties ->
        TeamScoringData(
            name = name,
            score = score,
            shots = shots,
            penalties = penalties,
        )
    }

    val editNamePrompt = MutableStateFlow<EditNamePromptDetails?>(null)

    val screenModel = combine(
        homeData,
        awayData,
        remainingTime,
        currentPeriod,
        editNamePrompt
    ) { team1, team2, timer, period, editNamePrompt ->
        ScoringScreenModel(
            team1,
            team2,
            timer - ((periods - period) * periodDuration),
            period,
            editNamePrompt
        )
    }

    fun addGoal(team: Team) {
        when (team) {
            Team.Home -> homeGoals.value += 1
            Team.Away -> awayGoals.value += 1
        }

        addShot(team)
    }

    fun addShot(team: Team) {
        when (team) {
            Team.Home -> homeShots.value += 1
            Team.Away -> awayShots.value += 1
        }
    }

    fun addPenalty(team: Team) {
        val penalty = Penalty(
            "Player ${Random.nextInt(0, 9)}",
            elapsedTime.value,
        )
        when (team) {
            Team.Home -> homePenalties.value += penalty
            Team.Away -> awayPenalties.value += penalty
        }
    }

    fun editTeamName(team: Team, newName: String) {
        when (team) {
            Team.Home -> homeName.value = newName
            Team.Away -> awayName.value = newName
        }
    }

    fun showEditNamePrompt(team: Team) {
        editNamePrompt.value = EditNamePromptDetails(team, if (team == Team.Home) homeName.value else awayName.value)
    }

    fun dismissPrompt() {
        editNamePrompt.value = null
    }

    enum class Team {
        Home,
        Away
    }
}

data class TeamScoringData(
    val name: String,
    val score: Int,
    val shots: Int,
    val penalties: List<Penalty> = emptyList(),
)

data class Penalty(
    val playerName: String,
    val timeOfPenalty: Long,
    val penaltyTimeRemaining: Long = 120,
    val penaltyLengthSeconds: Long = 120,
    val penaltyServed: Boolean = false,
)
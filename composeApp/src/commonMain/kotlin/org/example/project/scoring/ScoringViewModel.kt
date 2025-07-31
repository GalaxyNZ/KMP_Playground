package org.example.project.scoring

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class ScoringViewModel: ViewModel() {

    private val team1Name = MutableStateFlow("Team A")
    private val team1Score = MutableStateFlow(0)
    private val team1Data = combine(
        team1Name,
        team1Score
    ) { name, score ->
        TeamScoringData(
            teamName = name,
            score = score,
        )
    }

    private val team2Name = MutableStateFlow("Team B")
    private val team2Score = MutableStateFlow(10)
    private val team2Data = combine(
        team2Name,
        team2Score
    ) { name, score ->
        TeamScoringData(
            teamName = name,
            score = score,
        )
    }

    val screenModel = combine(
        team1Data,
        team2Data
    ) { team1, team2 ->
        ScoringScreenModel(
            team1,
            team2
        )
    }

    fun updateScore(team: Team, newScore: Int) {
        when (team) {
            Team.A -> team1Score.value = newScore
            Team.B -> team2Score.value = newScore
        }
    }

    enum class Team {
        A,
        B
    }
}

data class TeamScoringData(
    val teamName: String,
    val score: Int,
)
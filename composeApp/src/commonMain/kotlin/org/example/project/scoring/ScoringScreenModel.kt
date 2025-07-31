package org.example.project.scoring

data class ScoringScreenModel(
    val team1Data: TeamScoringData,
    val team2Data: TeamScoringData,
    val timer: Long,
    val period: Int,
)
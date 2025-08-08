package org.example.project.scoring

data class ScoringScreenModel(
    val team1Data: TeamScoringData,
    val team2Data: TeamScoringData,
    val timer: Long,
    val period: Int,
    val editNamePromptDetails: EditNamePromptDetails?,
)

data class EditNamePromptDetails(
    val team: ScoringViewModel.Team,
    val teamName: String,
)
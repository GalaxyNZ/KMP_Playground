package org.example.project.scoring

data class ScoringScreenModel(
    val teamAName: String,
    val teamBName: String,
    val teamAScore: Int,
    val teamBScore: Int,
    val timeRemaining: String,
)
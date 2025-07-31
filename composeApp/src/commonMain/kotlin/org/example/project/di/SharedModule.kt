package org.example.project.di

import org.example.project.scoring.ScoringViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    viewModel { ScoringViewModel() }
}
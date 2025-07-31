package org.example.project.di

import org.example.project.scoring.ScoringViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val commonModule = module {
    viewModelOf(::ScoringViewModel)
}
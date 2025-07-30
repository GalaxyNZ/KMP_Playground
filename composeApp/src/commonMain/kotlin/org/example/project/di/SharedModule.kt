package org.example.project.di

import org.koin.dsl.module

val commonModule = module {
    single { SharedRepo() }
}

class SharedRepo {
    // This is a placeholder for shared repository logic.
    // You can add functions and properties as needed.
    fun getData(): String {
        return "Shared data"
    }
}
package org.example.project.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(vararg modules: Module) = startKoin {
    modules(commonModule, *modules)
}
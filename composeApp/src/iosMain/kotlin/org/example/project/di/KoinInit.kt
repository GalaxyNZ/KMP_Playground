package org.example.project.di

import org.koin.dsl.module

fun startKoinIos() = initKoin(iosModule)

val iosModule = module {
//    single { IOSDep() }
}
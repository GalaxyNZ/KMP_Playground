package org.example.project.di

import org.koin.dsl.module

fun initKoinIos() = initKoin(iosModule)

val iosModule = module {
//    single { IOSDep() }
}
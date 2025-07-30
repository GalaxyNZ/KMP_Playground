package org.example.project

import android.app.Application
import org.example.project.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global resources or configurations here

        initKoin(androidModule)
    }
}

val androidModule = module {
//    single { AndroidDep(androidContext()) }
}
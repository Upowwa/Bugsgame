package com.example.bugsgame

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class BugsGameApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BugsGameApplication)
            modules(
                module {
                    viewModel { GameSessionViewModel() }
                }
            )
        }
    }
}

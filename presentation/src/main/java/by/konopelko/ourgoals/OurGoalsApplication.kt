package by.konopelko.ourgoals

import android.app.Application
import by.konopelko.data.database.DatabaseInstance
import by.konopelko.ourgoals.di.module.appModule
import by.konopelko.ourgoals.di.module.splashModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OurGoalsApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        initializeDatabase()

        startKoin {
            androidContext(this@OurGoalsApplication)
            modules(listOf(appModule, splashModule))
        }
    }

    private fun initializeDatabase() {
        DatabaseInstance.getInstance(applicationContext)
    }

    companion object {
        const val DI_SCOPE_NAME = "ApplicationScope"
    }
}
package by.konopelko.data.di.module

import by.konopelko.data.manager.SharedPreferencesManager
import org.koin.dsl.module

val appModule = module {
    single {
        SharedPreferencesManager()
    }
}
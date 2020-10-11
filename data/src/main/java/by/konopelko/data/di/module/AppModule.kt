package by.konopelko.data.di.module

import by.konopelko.data.sharedpreferences.SharedPreferences
import by.konopelko.data.sharedpreferences.SharedPreferencesImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single<SharedPreferences> {
        SharedPreferencesImpl(androidContext())
    }
}
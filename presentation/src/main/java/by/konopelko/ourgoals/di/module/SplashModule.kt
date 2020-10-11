package by.konopelko.ourgoals.di.module

import by.konopelko.data.repositories.sharedpreferences.SharedPreferencesRepositoryImpl
import by.konopelko.ourgoals.domain.repositories.sharedpreferences.SharedPreferencesRepository
import by.konopelko.ourgoals.domain.usecases.checkfirstrun.CheckFirstRunUseCase
import by.konopelko.ourgoals.domain.usecases.checkfirstrun.CheckFirstRunUseCaseImpl
import by.konopelko.ourgoals.domain.usecases.updateversioncode.UpdateVersionCodeUseCase
import by.konopelko.ourgoals.domain.usecases.updateversioncode.UpdateVersionCodeUseCaseImpl
import by.konopelko.ourgoals.view.splash.SplashActivity
import org.koin.core.qualifier.named
import org.koin.dsl.module

val splashModule = module {

    scope(named(SplashActivity.DI_SCOPE_NAME)) {

        scoped<SharedPreferencesRepository> {
            SharedPreferencesRepositoryImpl(get())
        }

        scoped<CheckFirstRunUseCase> {
            CheckFirstRunUseCaseImpl(get())
        }

        scoped<UpdateVersionCodeUseCase> {
            UpdateVersionCodeUseCaseImpl(get())
        }
    }
}
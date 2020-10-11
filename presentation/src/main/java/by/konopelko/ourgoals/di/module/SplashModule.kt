package by.konopelko.ourgoals.di.module

import by.konopelko.data.repositories.sharedpreferences.SharedPreferencesRepositoryImpl
import by.konopelko.ourgoals.domain.repositories.sharedpreferences.SharedPreferencesRepository
import by.konopelko.ourgoals.domain.usecases.getcurrentversioncode.GetCurrentVersionCodeUseCase
import by.konopelko.ourgoals.domain.usecases.getcurrentversioncode.GetCurrentVersionCodeUseCaseImpl
import by.konopelko.ourgoals.domain.usecases.getsavedversioncode.GetSavedVersionCodeUseCase
import by.konopelko.ourgoals.domain.usecases.getsavedversioncode.GetSavedVersionCodeUseCaseImpl
import by.konopelko.ourgoals.view.splash.SplashActivity
import org.koin.core.qualifier.named
import org.koin.dsl.module

val splashModule = module {

    scope(named(SplashActivity.DI_SCOPE_NAME)) {
        scoped<SharedPreferencesRepository> {
            SharedPreferencesRepositoryImpl(get())
        }

        scoped<GetSavedVersionCodeUseCase> {
            GetSavedVersionCodeUseCaseImpl(get())
        }

        scoped<GetCurrentVersionCodeUseCase> {
            GetCurrentVersionCodeUseCaseImpl(get())
        }
    }
}
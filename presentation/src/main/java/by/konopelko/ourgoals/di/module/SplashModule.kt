package by.konopelko.ourgoals.di.module

import by.konopelko.ourgoals.domain.usecases.getversioncode.GetVersionCodeUseCase
import by.konopelko.ourgoals.domain.usecases.getversioncode.GetVersionCodeUseCaseImpl
import by.konopelko.ourgoals.view.splash.SplashActivity
import org.koin.core.qualifier.named
import org.koin.dsl.module

val splashModule = module {

    scope(named(SplashActivity.DI_SCOPE_NAME)) {
        scoped<GetVersionCodeUseCase> {
            GetVersionCodeUseCaseImpl()
        }
    }
}
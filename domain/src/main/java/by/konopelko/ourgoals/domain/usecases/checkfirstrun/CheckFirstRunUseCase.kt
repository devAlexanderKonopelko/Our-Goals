package by.konopelko.ourgoals.domain.usecases.checkfirstrun

import by.konopelko.ourgoals.domain.entity.AppState

interface CheckFirstRunUseCase {

    operator fun invoke(): AppState
}
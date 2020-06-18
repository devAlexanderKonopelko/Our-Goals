package by.konopelko.ourgoals.goals.add.recyclerTasks

import by.konopelko.ourgoals.database.entities.Task

class AddTaskSingleton {
    var taskList = ArrayList<Task>()
    var taskToComplete = 0

    companion object {
        val instance = AddTaskSingleton()
    }
}
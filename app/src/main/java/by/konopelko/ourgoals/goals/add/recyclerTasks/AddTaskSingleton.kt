package by.konopelko.ourgoals.goals.add.recyclerTasks

import by.konopelko.ourgoals.database.Task

class AddTaskSingleton {
    val taskList = ArrayList<Task>()
    var taskToComplete = 0

    companion object {
        val instance = AddTaskSingleton()
    }
}
package com.ccormor392.room.addtasks.domain

import com.ccormor392.room.addtasks.data.TaskRepository
import com.ccormor392.room.addtasks.ui.model.TaskModel
import javax.inject.Inject

/**
 * Caso de uso para añadir una tarea
 */
class AddTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskModel: TaskModel) {
        taskRepository.add(taskModel)
    }
}
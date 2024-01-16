package com.ccormor392.room.addtasks.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ccormor392.room.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(): ViewModel() {
    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _myTaskText = MutableLiveData<String>()
    val myTaskText: LiveData<String> = _myTaskText
    //Los LiveData no van bien con los listados que se tienen que ir actualizando...
    //Para solucionarlo, podemos utilizar un mutableStateListOf porque se lleva mejor con LazyColumn a la hora de refrescar la información en la vista...
    private val _tasks = mutableStateListOf<TaskModel>()
    val tasks: List<TaskModel> = _tasks

    fun onDialogClose() {
        _showDialog.value = false
    }


    fun onTaskCreated() {
        onDialogClose()
        _tasks.add(TaskModel(task = _myTaskText.value ?: ""))
        _myTaskText.value = ""
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onTaskTextChanged(taskText: String) {
        _myTaskText.value = taskText
    }
    fun onItemRemove(taskModel: TaskModel) {
        //No podemos usar directamente _tasks.remove(taskModel) porque no es posible por el uso de let con copy para modificar el checkbox...
        //Para hacerlo correctamente, debemos previamente buscar la tarea en la lista por el id y después eliminarla
        val task = _tasks.find { it.id == taskModel.id }
        _tasks.remove(task)
    }
    fun onCheckBoxSelected(taskModel: TaskModel) {
        val index = _tasks.indexOf(taskModel)
        //esto sirve para que la vista se actualice, copia el objeto cambiandole solo la propiedad que queremos cambiar y reasigna el original
        _tasks[index] = _tasks[index].let { it.copy(selected = !it.selected) }
    }
}
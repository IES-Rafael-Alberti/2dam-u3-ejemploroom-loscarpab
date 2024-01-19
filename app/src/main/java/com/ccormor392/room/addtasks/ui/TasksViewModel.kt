package com.ccormor392.room.addtasks.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.room.addtasks.domain.AddTaskUseCase
import com.ccormor392.room.addtasks.domain.GetTasksUseCase
import com.ccormor392.room.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.ccormor392.room.addtasks.ui.TaskUiState.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//El parámetro getTasksUseCase del constructor se inyecta sin private val porque no nos hace falta
// ya que lo vamos a utilizar directamente en la variable uiState que gestionará los estados de la ui.
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    getTasksUseCase: GetTasksUseCase
): ViewModel() {
    //El caso de uso getTasksUseCase() nos devuelve el Flow continuo y cada vez que actualice
//los datos va a pasarlo a Success (ver data class en TaskUiState).
//Si por algún motivo falla y existe algún error, lo vamos a capturar y enviar al estado Error
//con el parámetro de la excepción que ha generado.
//El último modificador hará que cuando mi app o la pantalla esté en segundo plano hasta que no
//pasen 5 segundos no bloqueará o cancelará el Flow (por defecto es 0)
//Por ejemplo nuestra app pasará a segundo plano si nos llaman, o si desplegamos el menú superior
//para ver una notificación o un whatsapp, etc.
//Con stateIn, en el último argumento, también estamos asignando el estado inicial a Loading.
    val uiState: StateFlow<TaskUiState> = getTasksUseCase().map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _myTaskText = MutableLiveData<String>()
    val myTaskText: LiveData<String> = _myTaskText
    //Los LiveData no van bien con los listados que se tienen que ir actualizando...
    //Para solucionarlo, podemos utilizar un mutableStateListOf porque se lleva mejor con LazyColumn a la hora de refrescar la información en la vista...
    //private val _tasks = mutableStateListOf<TaskModel>()
    //val tasks: List<TaskModel> = _tasks

    fun onDialogClose() {
        _showDialog.value = false
    }


    fun onTaskCreated() {
        onDialogClose()
        //Un viewModelScope es una corutina.
        viewModelScope.launch {
            addTaskUseCase(TaskModel(task = _myTaskText.value ?: ""))
        }
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
        //val task = _tasks.find { it.id == taskModel.id }
        //_tasks.remove(task)
    }
    fun onCheckBoxSelected(taskModel: TaskModel) {
        //val index = _tasks.indexOf(taskModel)
        //esto sirve para que la vista se actualice, copia el objeto cambiandole solo la propiedad que queremos cambiar y reasigna el original
        //_tasks[index] = _tasks[index].let { it.copy(selected = !it.selected) }
    }
}
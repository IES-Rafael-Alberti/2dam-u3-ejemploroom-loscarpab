package com.ccormor392.room.addtasks.data.di

import android.content.Context
import androidx.room.Room
import com.ccormor392.room.addtasks.data.TaskDao
import com.ccormor392.room.addtasks.data.TasksManageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Debe ser un Singleton	para que la base de datos sea única en nuestro proyecto.
//Utilizaremos la notación de Hilt con @Provides.
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    //Provides es para la inyeccion de dependencias
    @Provides
    fun provideTaskDao(tasksManageDatabase: TasksManageDatabase): TaskDao {
        //Por eso en TasksManageDatabase estaba esta función abstract fun taskDao():TaskDao
        //para que esto automáticamente me devuelva el DAO (objeto de tipo TaskDao)
        return tasksManageDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun provideTasksManageDatabase(@ApplicationContext appContext: Context): TasksManageDatabase {
        //Esto crea la base de datos
        return Room.databaseBuilder(appContext, TasksManageDatabase::class.java, "TaskDatabase").build()
    }
}
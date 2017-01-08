/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource

import com.google.common.base.Preconditions.checkNotNull

/**
 * Listens to user actions from the UI ([AddEditTaskFragment]), retrieves the data and updates
 * the UI as required.
 */
class AddEditTaskPresenter
/**
 * Creates a presenter for the add/edit view.

 * @param taskId ID of the task to edit or null for a new task
 * *
 * @param tasksRepository a repository of data for tasks
 * *
 * @param addTaskView the add/edit view
 */
(private val mTaskId: String?, tasksRepository: TasksDataSource,
 addTaskView: AddEditTaskContract.View) : AddEditTaskContract.Presenter, TasksDataSource.GetTaskCallback {

    private val mTasksRepository: TasksDataSource

    private val mAddTaskView: AddEditTaskContract.View

    init {
        mTasksRepository = checkNotNull(tasksRepository)
        mAddTaskView = checkNotNull(addTaskView)

        mAddTaskView.setPresenter(this)
    }

    override fun start() {
        if (!isNewTask) {
            populateTask()
        }
    }

    override fun saveTask(title: String, description: String) {
        if (isNewTask) {
            createTask(title, description)
        } else {
            updateTask(title, description)
        }
    }

    override fun populateTask() {
        if (isNewTask) {
            throw RuntimeException("populateTask() was called but task is new.")
        }
        mTasksRepository.getTask(mTaskId!!, this)
    }

    override fun onTaskLoaded(task: Task) {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive) {
            mAddTaskView.setTitle(task.title)
            mAddTaskView.setDescription(task.description)
        }
    }

    override fun onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive) {
            mAddTaskView.showEmptyTaskError()
        }
    }

    private val isNewTask: Boolean
        get() = mTaskId == null

    private fun createTask(title: String, description: String) {
        val newTask = Task(title, description)
        if (newTask.isEmpty) {
            mAddTaskView.showEmptyTaskError()
        } else {
            mTasksRepository.saveTask(newTask)
            mAddTaskView.showTasksList()
        }
    }

    private fun updateTask(title: String, description: String) {
        if (isNewTask) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        mTasksRepository.saveTask(Task(title, description, mTaskId!!))
        mAddTaskView.showTasksList() // After an edit, go back to the list.
    }
}

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

package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource.LoadTasksCallback
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.google.common.collect.Lists
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the implementation of [TasksPresenter]
 */
class TasksPresenterTest {

    @Mock
    private val mTasksRepository: TasksRepository? = null

    @Mock
    private val mTasksView: TasksContract.View? = null

    /**
     * [ArgumentCaptor] is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private val mLoadTasksCallbackCaptor: ArgumentCaptor<LoadTasksCallback>? = null

    private var mTasksPresenter: TasksPresenter? = null

    @Before
    fun setupTasksPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        mTasksPresenter = TasksPresenter(mTasksRepository!!, mTasksView!!)

        // The presenter won't update the view unless it's active.
        `when`(mTasksView.isActive).thenReturn(true)

        // We start the tasks to 3, with one active and two completed
        TASKS = Lists.newArrayList(Task("Title1", "Description1"),
                Task("Title2", "Description2", true), Task("Title3", "Description3", true))
    }

    @Test
    fun loadAllTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksPresenter with initialized tasks
        // When loading of Tasks is requested
//        mTasksPresenter!!.filtering = TasksFilterType.ALL_TASKS
//        mTasksPresenter!!.loadTasks(true)
//
//        // Callback is captured and invoked with stubbed tasks
//        verify<TasksRepository>(mTasksRepository).getTasks(mLoadTasksCallbackCaptor!!.capture())
//        mLoadTasksCallbackCaptor.value.onTasksLoaded(TASKS!!)
//
//        // Then progress indicator is shown
//        val inOrder = inOrder(mTasksView)
//        inOrder.verify<TasksContract.View>(mTasksView).setLoadingIndicator(true)
//        // Then progress indicator is hidden and all tasks are shown in UI
//        inOrder.verify<TasksContract.View>(mTasksView).setLoadingIndicator(false)
//        val showTasksArgumentCaptor = ArgumentCaptor.forClass<List<*>>()
//        verify<TasksContract.View>(mTasksView).showTasks(showTasksArgumentCaptor.capture() as List<Task>)
//        assertTrue(showTasksArgumentCaptor.value.size == 3)
    }

    @Test
    fun loadActiveTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksPresenter with initialized tasks
        // When loading of Tasks is requested
        mTasksPresenter!!.filtering = TasksFilterType.ACTIVE_TASKS
        mTasksPresenter!!.loadTasks(true)

        // Callback is captured and invoked with stubbed tasks
        verify<TasksRepository>(mTasksRepository).getTasks(mLoadTasksCallbackCaptor!!.capture())
        mLoadTasksCallbackCaptor.value.onTasksLoaded(TASKS!!)

        // Then progress indicator is hidden and active tasks are shown in UI
//        verify<View>(mTasksView).setLoadingIndicator(false)
//        val showTasksArgumentCaptor = ArgumentCaptor.forClass<List<*>>(List<*>::class.java)
//        verify<View>(mTasksView).showTasks(showTasksArgumentCaptor.capture())
//        assertTrue(showTasksArgumentCaptor.value.size == 1)
    }

    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksPresenter with initialized tasks
        // When loading of Tasks is requested
        mTasksPresenter!!.filtering = TasksFilterType.COMPLETED_TASKS
        mTasksPresenter!!.loadTasks(true)

        // Callback is captured and invoked with stubbed tasks
        verify<TasksRepository>(mTasksRepository).getTasks(mLoadTasksCallbackCaptor!!.capture())
        mLoadTasksCallbackCaptor.value.onTasksLoaded(TASKS!!)

        // Then progress indicator is hidden and completed tasks are shown in UI
//        verify<View>(mTasksView).setLoadingIndicator(false)
//        val showTasksArgumentCaptor = ArgumentCaptor.forClass<List<*>>(List<*>::class.java)
//        verify<View>(mTasksView).showTasks(showTasksArgumentCaptor.capture())
//        assertTrue(showTasksArgumentCaptor.value.size == 2)
    }

    @Test
    fun clickOnFab_ShowsAddTaskUi() {
        // When adding a new task
        mTasksPresenter!!.addNewTask()

        // Then add task UI is shown
        verify<TasksContract.View>(mTasksView).showAddTask()
    }

    @Test
    fun clickOnTask_ShowsDetailUi() {
        // Given a stubbed active task
        val requestedTask = Task("Details Requested", "For this task")

        // When open task details is requested
        mTasksPresenter!!.openTaskDetails(requestedTask)

        // Then task detail UI is shown
        verify<TasksContract.View>(mTasksView).showTaskDetailsUi(any(String::class.java))
    }

    @Test
    fun completeTask_ShowsTaskMarkedComplete() {
        // Given a stubbed task
        val task = Task("Details Requested", "For this task")

        // When task is marked as complete
        mTasksPresenter!!.completeTask(task)

        // Then repository is called and task marked complete UI is shown
        verify<TasksRepository>(mTasksRepository).completeTask(task)
        verify<TasksContract.View>(mTasksView).showTaskMarkedComplete()
    }

    @Test
    fun activateTask_ShowsTaskMarkedActive() {
        // Given a stubbed completed task
        val task = Task("Details Requested", "For this task", true)
        mTasksPresenter!!.loadTasks(true)

        // When task is marked as activated
        mTasksPresenter!!.activateTask(task)

        // Then repository is called and task marked active UI is shown
        verify<TasksRepository>(mTasksRepository).activateTask(task)
        verify<TasksContract.View>(mTasksView).showTaskMarkedActive()
    }

    @Test
    fun unavailableTasks_ShowsError() {
        // When tasks are loaded
        mTasksPresenter!!.filtering = TasksFilterType.ALL_TASKS
        mTasksPresenter!!.loadTasks(true)

        // And the tasks aren't available in the repository
        verify<TasksRepository>(mTasksRepository).getTasks(mLoadTasksCallbackCaptor!!.capture())
        mLoadTasksCallbackCaptor.value.onDataNotAvailable()

        // Then an error message is shown
        verify<TasksContract.View>(mTasksView).showLoadingTasksError()
    }

    companion object {

        private var TASKS: List<Task>? = null
    }
}

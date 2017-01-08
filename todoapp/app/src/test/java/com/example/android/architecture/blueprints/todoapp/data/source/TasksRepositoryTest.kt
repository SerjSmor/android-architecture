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

package com.example.android.architecture.blueprints.todoapp.data.source

import android.content.Context
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.google.common.collect.Lists
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Matchers.any
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
class TasksRepositoryTest {

    private var mTasksRepository: TasksRepository? = null

    @Mock
    private val mTasksRemoteDataSource: TasksDataSource? = null

    @Mock
    private val mTasksLocalDataSource: TasksDataSource? = null

    @Mock
    private val mContext: Context? = null

    @Mock
    private val mGetTaskCallback: TasksDataSource.GetTaskCallback? = null

    @Mock
    private val mLoadTasksCallback: TasksDataSource.LoadTasksCallback? = null

    /**
     * [ArgumentCaptor] is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private val mTasksCallbackCaptor: ArgumentCaptor<TasksDataSource.LoadTasksCallback>? = null

    /**
     * [ArgumentCaptor] is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private val mTaskCallbackCaptor: ArgumentCaptor<TasksDataSource.GetTaskCallback>? = null

    @Before
    fun setupTasksRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        mTasksRepository = TasksRepository.getInstance(
                mTasksRemoteDataSource, mTasksLocalDataSource)
    }

    @After
    fun destroyRepositoryInstance() {
        TasksRepository.destroyInstance()
    }

    @Test
    fun getTasks_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the tasks repository
        twoTasksLoadCallsToRepository(mLoadTasksCallback!!)

        // Then tasks were only requested once from Service API
        verify<TasksDataSource>(mTasksRemoteDataSource).getTasks(any(TasksDataSource.LoadTasksCallback::class.java))
    }

    @Test
    fun getTasks_requestsAllTasksFromLocalDataSource() {
        // When tasks are requested from the tasks repository
        mTasksRepository!!.getTasks(mLoadTasksCallback!!)

        // Then tasks are loaded from the local data source
        verify<TasksDataSource>(mTasksLocalDataSource).getTasks(any(TasksDataSource.LoadTasksCallback::class.java))
    }

    @Test
    fun saveTask_savesTaskToServiceAPI() {
        // Given a stub task with title and description
        val newTask = Task(TASK_TITLE, "Some Task Description")

        // When a task is saved to the tasks repository
        mTasksRepository!!.saveTask(newTask)

        // Then the service API and persistent repository are called and the cache is updated
        verify<TasksDataSource>(mTasksRemoteDataSource).saveTask(newTask)
        verify<TasksDataSource>(mTasksLocalDataSource).saveTask(newTask)
        assertThat(mTasksRepository!!.mCachedTasks!!.size, `is`(1))
    }

    @Test
    fun completeTask_completesTaskToServiceAPIUpdatesCache() {
        // Given a stub active task with title and description added in the repository
        val newTask = Task(TASK_TITLE, "Some Task Description")
        mTasksRepository!!.saveTask(newTask)

        // When a task is completed to the tasks repository
        mTasksRepository!!.completeTask(newTask)

        // Then the service API and persistent repository are called and the cache is updated
        verify<TasksDataSource>(mTasksRemoteDataSource).completeTask(newTask)
        verify<TasksDataSource>(mTasksLocalDataSource).completeTask(newTask)
        assertThat(mTasksRepository!!.mCachedTasks!!.size, `is`(1))
        assertThat(mTasksRepository!!.mCachedTasks!![newTask.id]!!.isActive, `is`(false))
    }

    @Test
    fun completeTaskId_completesTaskToServiceAPIUpdatesCache() {
        // Given a stub active task with title and description added in the repository
        val newTask = Task(TASK_TITLE, "Some Task Description")
        mTasksRepository!!.saveTask(newTask)

        // When a task is completed using its id to the tasks repository
        mTasksRepository!!.completeTask(newTask.id)

        // Then the service API and persistent repository are called and the cache is updated
        verify<TasksDataSource>(mTasksRemoteDataSource).completeTask(newTask)
        verify<TasksDataSource>(mTasksLocalDataSource).completeTask(newTask)
        assertThat(mTasksRepository!!.mCachedTasks!!.size, `is`(1))
        assertThat(mTasksRepository!!.mCachedTasks!![newTask.id]?.isActive, `is`(false))
    }

    @Test
    fun activateTask_activatesTaskToServiceAPIUpdatesCache() {
        // Given a stub completed task with title and description in the repository
        val newTask = Task(TASK_TITLE, "Some Task Description", true)
        mTasksRepository!!.saveTask(newTask)

        // When a completed task is activated to the tasks repository
        mTasksRepository!!.activateTask(newTask)

        // Then the service API and persistent repository are called and the cache is updated
        verify<TasksDataSource>(mTasksRemoteDataSource).activateTask(newTask)
        verify<TasksDataSource>(mTasksLocalDataSource).activateTask(newTask)
        assertThat(mTasksRepository!!.mCachedTasks!!.size, `is`(1))
        assertThat(mTasksRepository!!.mCachedTasks!![newTask.id]?.isActive, `is`(true))
    }

    @Test
    fun activateTaskId_activatesTaskToServiceAPIUpdatesCache() {
        // Given a stub completed task with title and description in the repository
        val newTask = Task(TASK_TITLE, "Some Task Description", true)
        mTasksRepository!!.saveTask(newTask)

        // When a completed task is activated with its id to the tasks repository
        mTasksRepository!!.activateTask(newTask.id)

        // Then the service API and persistent repository are called and the cache is updated
        verify<TasksDataSource>(mTasksRemoteDataSource).activateTask(newTask)
        verify<TasksDataSource>(mTasksLocalDataSource).activateTask(newTask)
        assertThat(mTasksRepository!!.mCachedTasks!!.size, `is`(1))
        assertThat(mTasksRepository!!.mCachedTasks!![newTask.id]?.isActive, `is`(true))
    }

    @Test
    fun getTask_requestsSingleTaskFromLocalDataSource() {
        // When a task is requested from the tasks repository
        mTasksRepository!!.getTask(TASK_TITLE, mGetTaskCallback!!)

        // Then the task is loaded from the database
        verify<TasksDataSource>(mTasksLocalDataSource).getTask(eq(TASK_TITLE), any(TasksDataSource.GetTaskCallback::class.java))
    }

    @Test
    fun deleteCompletedTasks_deleteCompletedTasksToServiceAPIUpdatesCache() {
        // Given 2 stub completed tasks and 1 stub active tasks in the repository
        val newTask = Task(TASK_TITLE, "Some Task Description", true)
        mTasksRepository!!.saveTask(newTask)
        val newTask2 = Task(TASK_TITLE2, "Some Task Description")
        mTasksRepository!!.saveTask(newTask2)
        val newTask3 = Task(TASK_TITLE3, "Some Task Description", true)
        mTasksRepository!!.saveTask(newTask3)

        // When a completed tasks are cleared to the tasks repository
        mTasksRepository!!.clearCompletedTasks()


        // Then the service API and persistent repository are called and the cache is updated
        verify<TasksDataSource>(mTasksRemoteDataSource).clearCompletedTasks()
        verify<TasksDataSource>(mTasksLocalDataSource).clearCompletedTasks()

        assertThat(mTasksRepository!!.mCachedTasks!!.size, `is`(1))
        assertTrue(mTasksRepository!!.mCachedTasks!![newTask2.id]?.isActive!!)
        assertThat(mTasksRepository!!.mCachedTasks!![newTask2.id]?.title, `is`(TASK_TITLE2))
    }

    @Test
    fun deleteAllTasks_deleteTasksToServiceAPIUpdatesCache() {
        // Given 2 stub completed tasks and 1 stub active tasks in the repository
        val newTask = Task(TASK_TITLE, "Some Task Description", true)
        mTasksRepository!!.saveTask(newTask)
        val newTask2 = Task(TASK_TITLE2, "Some Task Description")
        mTasksRepository!!.saveTask(newTask2)
        val newTask3 = Task(TASK_TITLE3, "Some Task Description", true)
        mTasksRepository!!.saveTask(newTask3)

        // When all tasks are deleted to the tasks repository
        mTasksRepository!!.deleteAllTasks()

        // Verify the data sources were called
        verify<TasksDataSource>(mTasksRemoteDataSource).deleteAllTasks()
        verify<TasksDataSource>(mTasksLocalDataSource).deleteAllTasks()

        assertThat(mTasksRepository!!.mCachedTasks!!.size, `is`(0))
    }

    @Test
    fun deleteTask_deleteTaskToServiceAPIRemovedFromCache() {
        // Given a task in the repository
        val newTask = Task(TASK_TITLE, "Some Task Description", true)
        mTasksRepository!!.saveTask(newTask)
        assertThat(mTasksRepository!!.mCachedTasks!!.containsKey(newTask.id), `is`(true))

        // When deleted
        mTasksRepository!!.deleteTask(newTask.id)

        // Verify the data sources were called
        verify<TasksDataSource>(mTasksRemoteDataSource).deleteTask(newTask.id)
        verify<TasksDataSource>(mTasksLocalDataSource).deleteTask(newTask.id)

        // Verify it's removed from repository
        assertThat(mTasksRepository!!.mCachedTasks!!.containsKey(newTask.id), `is`(false))
    }

    @Test
    fun getTasksWithDirtyCache_tasksAreRetrievedFromRemote() {
        // When calling getTasks in the repository with dirty cache
        mTasksRepository!!.refreshTasks()
        mTasksRepository!!.getTasks(mLoadTasksCallback!!)

        // And the remote data source has data available
        setTasksAvailable(mTasksRemoteDataSource!!, TASKS)

        // Verify the tasks from the remote data source are returned, not the local
        verify<TasksDataSource>(mTasksLocalDataSource, never()).getTasks(mLoadTasksCallback)
        verify(mLoadTasksCallback).onTasksLoaded(TASKS)
    }

    @Test
    fun getTasksWithLocalDataSourceUnavailable_tasksAreRetrievedFromRemote() {
        // When calling getTasks in the repository
        mTasksRepository!!.getTasks(mLoadTasksCallback!!)

        // And the local data source has no data available
        setTasksNotAvailable(mTasksLocalDataSource!!)

        // And the remote data source has data available
        setTasksAvailable(mTasksRemoteDataSource!!, TASKS)

        // Verify the tasks from the local data source are returned
        verify(mLoadTasksCallback).onTasksLoaded(TASKS)
    }

    @Test
    fun getTasksWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getTasks in the repository
        mTasksRepository!!.getTasks(mLoadTasksCallback!!)

        // And the local data source has no data available
        setTasksNotAvailable(mTasksLocalDataSource!!)

        // And the remote data source has no data available
        setTasksNotAvailable(mTasksRemoteDataSource!!)

        // Verify no data is returned
        verify(mLoadTasksCallback).onDataNotAvailable()
    }

    @Test
    fun getTaskWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // Given a task id
        val taskId = "123"

        // When calling getTask in the repository
        mTasksRepository!!.getTask(taskId, mGetTaskCallback!!)

        // And the local data source has no data available
        setTaskNotAvailable(mTasksLocalDataSource!!, taskId)

        // And the remote data source has no data available
        setTaskNotAvailable(mTasksRemoteDataSource!!, taskId)

        // Verify no data is returned
        verify(mGetTaskCallback).onDataNotAvailable()
    }

    @Test
    fun getTasks_refreshesLocalDataSource() {
        // Mark cache as dirty to force a reload of data from remote data source.
        mTasksRepository!!.refreshTasks()

        // When calling getTasks in the repository
        mTasksRepository!!.getTasks(mLoadTasksCallback!!)

        // Make the remote data source return data
        setTasksAvailable(mTasksRemoteDataSource!!, TASKS)

        // Verify that the data fetched from the remote data source was saved in local.
        verify<TasksDataSource>(mTasksLocalDataSource, times(TASKS.size)).saveTask(any(Task::class.java))
    }

    /**
     * Convenience method that issues two calls to the tasks repository
     */
    private fun twoTasksLoadCallsToRepository(callback: TasksDataSource.LoadTasksCallback) {
        // When tasks are requested from repository
        mTasksRepository!!.getTasks(callback) // First call to API

        // Use the Mockito Captor to capture the callback
        verify<TasksDataSource>(mTasksLocalDataSource).getTasks(mTasksCallbackCaptor!!.capture())

        // Local data source doesn't have data yet
        mTasksCallbackCaptor.value.onDataNotAvailable()


        // Verify the remote data source is queried
        verify<TasksDataSource>(mTasksRemoteDataSource).getTasks(mTasksCallbackCaptor.capture())

        // Trigger callback so tasks are cached
        mTasksCallbackCaptor.value.onTasksLoaded(TASKS)

        mTasksRepository!!.getTasks(callback) // Second call to API
    }

    private fun setTasksNotAvailable(dataSource: TasksDataSource) {
        verify(dataSource).getTasks(mTasksCallbackCaptor!!.capture())
        mTasksCallbackCaptor.value.onDataNotAvailable()
    }

    private fun setTasksAvailable(dataSource: TasksDataSource, tasks: List<Task>) {
        verify(dataSource).getTasks(mTasksCallbackCaptor!!.capture())
        mTasksCallbackCaptor.value.onTasksLoaded(tasks)
    }

    private fun setTaskNotAvailable(dataSource: TasksDataSource, taskId: String) {
        verify(dataSource).getTask(eq(taskId), mTaskCallbackCaptor!!.capture())
        mTaskCallbackCaptor.value.onDataNotAvailable()
    }

    private fun setTaskAvailable(dataSource: TasksDataSource, task: Task) {
        verify(dataSource).getTask(eq(task.id), mTaskCallbackCaptor!!.capture())
        mTaskCallbackCaptor.value.onTaskLoaded(task)
    }

    companion object {

        private val TASK_TITLE = "title"

        private val TASK_TITLE2 = "title2"

        private val TASK_TITLE3 = "title3"

        private val TASKS = Lists.newArrayList(Task("Title1", "Description1"),
                Task("Title2", "Description2"))
    }
}

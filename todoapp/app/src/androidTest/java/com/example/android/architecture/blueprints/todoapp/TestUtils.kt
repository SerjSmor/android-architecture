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

package com.example.android.architecture.blueprints.todoapp

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.lifecycle.Stage.RESUMED

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.annotation.IdRes
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.v7.widget.Toolbar

/**
 * Useful test methods common to all activities
 */
object TestUtils {

    private fun rotateToLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun rotateToPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun rotateOrientation(activity: Activity) {
        val currentOrientation = activity.resources.configuration.orientation

        when (currentOrientation) {
            Configuration.ORIENTATION_LANDSCAPE -> rotateToPortrait(activity)
            Configuration.ORIENTATION_PORTRAIT -> rotateToLandscape(activity)
            else -> rotateToLandscape(activity)
        }
    }

    /**
     * Returns the content description for the navigation button view in the toolbar.
     */
    fun getToolbarNavigationContentDescription(
            activity: Activity, @IdRes toolbar1: Int): String {
        val toolbar = activity.findViewById(toolbar1) as Toolbar
        if (toolbar != null) {
            return toolbar.navigationContentDescription as String?
        } else {
            throw RuntimeException("No toolbar found.")
        }
    }

    /**
     * Gets an Activity in the RESUMED stage.
     *
     *
     * This method should never be called from the Main thread. In certain situations there might
     * be more than one Activities in RESUMED stage, but only one is returned.
     * See [ActivityLifecycleMonitor].
     */
    // The array is just to wrap the Activity and be able to access it from the Runnable.
    val currentActivity: Activity
        @Throws(IllegalStateException::class)
        get() {
            val resumedActivity = arrayOfNulls<Activity>(1)

            getInstrumentation().runOnMainSync {
                val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED)
                if (resumedActivities.iterator().hasNext()) {
                    resumedActivity[0] = resumedActivities.iterator().next() as Activity
                } else {
                    throw IllegalStateException("No Activity in stage RESUMED")
                }
            }
            return resumedActivity[0]
        }
}

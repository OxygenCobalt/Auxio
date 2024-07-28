/*
 * Copyright (c) 2024 Auxio Project
 * Start.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.tasker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import dagger.hilt.EntryPoints
import kotlinx.coroutines.runBlocking
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.playback.state.DeferredPlayback

class StartHelper(config: TaskerPluginConfig<Unit>) :
    TaskerPluginConfigHelperNoOutputOrInput<StartStateRunner>(config) {
    override val runnerClass: Class<StartStateRunner>
        get() = StartStateRunner::class.java

    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("Shuffles All Songs Once the Service is Available")
    }
}

class StartConfigBasicAction : Activity(), TaskerPluginConfigNoInput {
    override val context: Context
        get() = applicationContext

    private val taskerHelper by lazy { StartHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskerHelper.finishForTasker()
    }
}

class StartStateRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        ContextCompat.startForegroundService(
            context,
            Intent(context, AuxioService::class.java)
                .putExtra(AuxioService.INTENT_KEY_INTERNAL_START, true)
        )
        val entryPoint = EntryPoints.get(context.applicationContext, TaskerEntryPoint::class.java)
        val playbackManager = entryPoint.playbackManager()
        runBlocking {
            playbackManager.playDeferred(DeferredPlayback.RestoreState(sessionRequired = true))
        }
        while (!playbackManager.sessionOngoing) {
            if (!playbackManager.awaitingDeferredPlayback) {
                return TaskerPluginResultError(
                    IntegerTable.TASKER_ERROR_NOT_RESTORED,
                    "No state to restore, did not restart playback."
                )
            }
        }
        Thread.sleep(100)
        return TaskerPluginResultSucess()
    }
}

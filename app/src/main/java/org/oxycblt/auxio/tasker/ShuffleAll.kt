/*
 * Copyright (c) 2024 Auxio Project
 * ShuffleAll.kt is part of Auxio.
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
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import dagger.hilt.EntryPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.playback.state.DeferredPlayback

class ShuffleAllHelper(config: TaskerPluginConfig<Unit>) :
    TaskerPluginConfigHelperNoOutputOrInput<ShuffleAllRunner>(config) {
    override val runnerClass: Class<ShuffleAllRunner>
        get() = ShuffleAllRunner::class.java

    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("Shuffles All Songs Once the Service is Available")
    }
}

class ShuffleAllConfigBasicAction : Activity(), TaskerPluginConfigNoInput {
    override val context: Context
        get() = applicationContext

    private val taskerHelper by lazy { ShuffleAllHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskerHelper.finishForTasker()
    }
}

class ShuffleAllRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        ContextCompat.startForegroundService(
            context,
            Intent(context, AuxioService::class.java)
                .putExtra(AuxioService.INTENT_KEY_INTERNAL_START, true))
        val entryPoint = EntryPoints.get(context.applicationContext, TaskerEntryPoint::class.java)
        val playbackManager = entryPoint.playbackManager()
        runBlocking(Dispatchers.Main) { playbackManager.playDeferred(DeferredPlayback.ShuffleAll) }
        while (!playbackManager.sessionOngoing) {}
        Thread.sleep(100)
        return TaskerPluginResultSucess()
    }
}

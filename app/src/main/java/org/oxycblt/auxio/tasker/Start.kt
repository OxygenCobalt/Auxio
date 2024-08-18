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
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.IntegerTable

class StartActionHelper(config: TaskerPluginConfig<Unit>) :
    TaskerPluginConfigHelperNoOutputOrInput<StartActionRunner>(config) {
    override val runnerClass: Class<StartActionRunner>
        get() = StartActionRunner::class.java

    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append(
            "Starts Auxio using the previously saved state. If no saved state is available, all songs will be shuffled. Playback will start immediately. Be careful controlling this service, if you close it and then try to use it again, you will probably crash the app.")
    }
}

class ActivityConfigStartAction : Activity(), TaskerPluginConfigNoInput {
    override val context
        get() = applicationContext

    private val taskerHelper by lazy { StartActionHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskerHelper.finishForTasker()
    }
}

class StartActionRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        ContextCompat.startForegroundService(
            context,
            Intent(context, AuxioService::class.java)
                .putExtra(AuxioService.INTENT_KEY_START_ID, IntegerTable.START_ID_TASKER))
        while (!AuxioService.isForeground) {
            Thread.sleep(100)
        }
        return TaskerPluginResultSucess()
    }
}

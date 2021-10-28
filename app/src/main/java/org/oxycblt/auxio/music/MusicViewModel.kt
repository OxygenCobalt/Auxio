/*
 * Copyright (c) 2021 Auxio Project
 * MusicViewModel.kt is part of Auxio.
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

package org.oxycblt.auxio.music

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {
    private val mLoaderResponse = MutableLiveData<MusicStore.Response?>(null)
    val loaderResponse: LiveData<MusicStore.Response?> = mLoaderResponse

    private var isBusy = false

    /**
     * Initiate the loading process. This is done here since HomeFragment will be the first
     * fragment navigated to and because SnackBars will have the best UX here.
     */
    fun loadMusic(context: Context) {
        if (mLoaderResponse.value != null || isBusy) {
            return
        }

        isBusy = true
        mLoaderResponse.value = null

        viewModelScope.launch {
            val result = MusicStore.initInstance(context)

            isBusy = false
            mLoaderResponse.value = result
        }
    }

    fun reloadMusic(context: Context) {
        mLoaderResponse.value = null

        loadMusic(context)
    }
}

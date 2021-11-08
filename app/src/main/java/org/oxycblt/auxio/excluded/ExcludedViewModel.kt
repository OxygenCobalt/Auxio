/*
 * Copyright (c) 2021 Auxio Project
 * BlacklistViewModel.kt is part of Auxio.
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

package org.oxycblt.auxio.excluded

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel that acts as a wrapper around [ExcludedDatabase], allowing for the addition/removal
 * of paths. Use [Factory] to instantiate this.
 * @author OxygenCobalt
 */
class ExcludedViewModel(private val excludedDatabase: ExcludedDatabase) : ViewModel() {
    private val mPaths = MutableLiveData(mutableListOf<String>())
    val paths: LiveData<MutableList<String>> get() = mPaths

    private var dbPaths = listOf<String>()

    /**
     * Check if changes have been made to the ViewModel's paths.
     */
    val isModified: Boolean get() = dbPaths != paths.value

    init {
        loadDatabasePaths()
    }

    /**
     * Add a path to this viewmodel. It will not write the path to the database unless
     * [save] is called.
     */
    fun addPath(path: String) {
        if (!mPaths.value!!.contains(path)) {
            mPaths.value!!.add(path)
            mPaths.value = mPaths.value
        }
    }

    /**
     * Remove a path from this viewmodel, it will not remove this path from the database unless
     * [save] is called.
     */
    fun removePath(path: String) {
        mPaths.value!!.remove(path)
        mPaths.value = mPaths.value
    }

    /**
     * Save the pending paths to the database. [onDone] will be called on completion.
     */
    fun save(onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            excludedDatabase.writePaths(mPaths.value!!)
            dbPaths = mPaths.value!!

            onDone()
        }
    }

    /**
     * Load the paths stored in the database to this ViewModel, will erase any pending changes.
     */
    private fun loadDatabasePaths() {
        viewModelScope.launch(Dispatchers.IO) {
            dbPaths = excludedDatabase.readPaths()

            withContext(Dispatchers.Main) {
                mPaths.value = dbPaths.toMutableList()
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            check(modelClass.isAssignableFrom(ExcludedViewModel::class.java)) {
                "ExcludedViewModel.Factory does not support this class"
            }

            @Suppress("UNCHECKED_CAST")
            return ExcludedViewModel(ExcludedDatabase.getInstance(context)) as T
        }
    }
}

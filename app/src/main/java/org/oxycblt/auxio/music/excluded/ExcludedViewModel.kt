/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.music.excluded

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * ViewModel that acts as a wrapper around [ExcludedDatabase], allowing for the addition/removal of
 * paths. Use [Factory] to instantiate this.
 * @author OxygenCobalt
 *
 * TODO: Unify with MusicViewModel
 */
class ExcludedViewModel(private val excludedDatabase: ExcludedDatabase) : ViewModel() {
    private val _paths = MutableLiveData(mutableListOf<String>())
    val paths: LiveData<MutableList<String>>
        get() = _paths

    var isModified: Boolean = false
        private set

    init {
        loadDatabasePaths()
    }

    /**
     * Add a path to this ViewModel. It will not write the path to the database unless [save] is
     * called.
     */
    fun addPath(path: String) {
        val paths = unlikelyToBeNull(_paths.value)
        if (!paths.contains(path)) {
            paths.add(path)
            _paths.value = _paths.value
            isModified = true
        }
    }

    /**
     * Remove a path from this ViewModel, it will not remove this path from the database unless
     * [save] is called.
     */
    fun removePath(path: String) {
        unlikelyToBeNull(_paths.value).remove(path)
        _paths.value = _paths.value
        isModified = true
    }

    /** Save the pending paths to the database. [onDone] will be called on completion. */
    fun save(onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val start = System.currentTimeMillis()
            excludedDatabase.writePaths(unlikelyToBeNull(_paths.value))
            isModified = false
            onDone()
            this@ExcludedViewModel.logD(
                "Path save completed successfully in ${System.currentTimeMillis() - start}ms")
        }
    }

    /** Load the paths stored in the database to this ViewModel, will erase any pending changes. */
    private fun loadDatabasePaths() {
        viewModelScope.launch(Dispatchers.IO) {
            val start = System.currentTimeMillis()
            isModified = false

            val dbPaths = excludedDatabase.readPaths()
            withContext(Dispatchers.Main) { _paths.value = dbPaths.toMutableList() }

            this@ExcludedViewModel.logD(
                "Path load completed successfully in ${System.currentTimeMillis() - start}ms")
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

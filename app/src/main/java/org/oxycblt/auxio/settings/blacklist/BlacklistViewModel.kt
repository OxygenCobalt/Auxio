package org.oxycblt.auxio.settings.blacklist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.database.BlacklistDatabase

/**
 * ViewModel that acts as a wrapper around [BlacklistDatabase], allowing for the addition/removal
 * of paths. Use [Factory] to instantiate this.
 * @author OxygenCobalt
 */
class BlacklistViewModel(context: Context) : ViewModel() {
    private val mPaths = MutableLiveData(mutableListOf<String>())
    val paths: LiveData<MutableList<String>> get() = mPaths

    private val blacklistDatabase = BlacklistDatabase.getInstance(context)
    var modified = false
        private set

    init {
        loadDatabasePaths()
    }

    /**
     * Add a path to this viewmodel. It will not write the path to the database unless
     * [save] is called.
     */
    fun addPath(path: String) {
        if (mPaths.value!!.contains(path)) return

        mPaths.value!!.add(path)
        mPaths.value = mPaths.value

        modified = true
    }

    /**
     * Remove a path from this viewmodel, it will not remove this path from the database unless
     * [save] is called.
     */
    fun removePath(path: String) {
        mPaths.value!!.remove(path)
        mPaths.value = mPaths.value

        modified = true
    }

    /**
     * Save the pending paths to the database. [onDone] will be called on completion.
     */
    fun save(onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            blacklistDatabase.writePaths(mPaths.value!!)
            modified = false
            onDone()
        }
    }

    /**
     * Load the paths stored in the database to this ViewModel, will erase any pending changes.
     */
    fun loadDatabasePaths() {
        viewModelScope.launch(Dispatchers.IO) {
            val paths = blacklistDatabase.readPaths().toMutableList()

            withContext(Dispatchers.Main) {
                // Can only change LiveData on the main thread.
                mPaths.value = paths
            }

            modified = false
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            check(modelClass.isAssignableFrom(BlacklistViewModel::class.java)) {
                "BlacklistViewModel.Factory does not support this class"
            }

            @Suppress("UNCHECKED_CAST")
            return BlacklistViewModel(context) as T
        }
    }
}

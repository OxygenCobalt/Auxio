package org.oxycblt.auxio.library

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.MusicRepository

class LibraryViewModel(private val app: Application) : ViewModel() {

    private val viewModelJob = Job()
    private val ioScope = CoroutineScope(
        Dispatchers.IO
    )

    init {
        startMusicRepo()

        Log.d(this::class.simpleName, "ViewModel created.")
    }

    // TODO: Temp function, remove when LoadingFragment is added
    private fun startMusicRepo() {
        ioScope.launch {
            MusicRepository.getInstance().init(app)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                return LibraryViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

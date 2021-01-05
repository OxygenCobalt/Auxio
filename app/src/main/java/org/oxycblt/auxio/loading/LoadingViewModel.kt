package org.oxycblt.auxio.loading

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.processing.MusicLoader

/**
 * A [ViewModel] responsible for getting the music loading process going and managing the response
 * returned.
 * @author OxygenCobalt
 */
class LoadingViewModel(private val app: Application) : ViewModel() {
    private val mResponse = MutableLiveData<MusicLoader.Response>()
    val response: LiveData<MusicLoader.Response> get() = mResponse

    private val mRedo = MutableLiveData<Boolean>()
    val doReload: LiveData<Boolean> get() = mRedo

    private val mDoGrant = MutableLiveData<Boolean>()
    val doGrant: LiveData<Boolean> get() = mDoGrant

    private var started = false

    /**
     * Start the music loading sequence.
     * This should only be ran once, use reload() for all other loads.
     */
    fun go() {
        if (!started) {
            started = true
            doLoad()
        }
    }

    private fun doLoad() {
        viewModelScope.launch {
            val musicStore = MusicStore.getInstance()

            val response = musicStore.load(app)

            withContext(Dispatchers.Main) {
                mResponse.value = response
            }
        }
    }

    /**
     * Reload the music
     */
    fun reload() {
        mRedo.value = true

        doLoad()
    }

    /**
     * Mark that the UI is done with the reload call
     */
    fun doneWithReload() {
        mRedo.value = false
    }

    /**
     * Mark to start the grant process
     */
    fun grant() {
        mDoGrant.value = true
    }

    /**
     * Mark that the UI is done with the grant process.
     */
    fun doneWithGrant() {
        mDoGrant.value = false
    }

    /**
     * Factory for [LoadingViewModel] instances.
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoadingViewModel::class.java)) {
                return LoadingViewModel(application) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class.")
        }
    }
}

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
import org.oxycblt.auxio.music.processing.MusicLoaderResponse

class LoadingViewModel(private val app: Application) : ViewModel() {
    // UI control
    private val mResponse = MutableLiveData<MusicLoaderResponse>()
    val response: LiveData<MusicLoaderResponse> get() = mResponse

    private val mRedo = MutableLiveData<Boolean>()
    val doReload: LiveData<Boolean> get() = mRedo

    private val mDoGrant = MutableLiveData<Boolean>()
    val doGrant: LiveData<Boolean> get() = mDoGrant

    private var started = false

    // Start the music loading sequence.
    // This should only be ran once, use reload() for all other loads.
    fun go() {
        if (!started) {
            started = true
            doLoad()
        }
    }

    private fun doLoad() {
        viewModelScope.launch {
            val musicStore = MusicStore.getInstance()

            val response = withContext(Dispatchers.IO) {
                return@withContext musicStore.load(app)
            }

            withContext(Dispatchers.Main) {
                mResponse.value = response
            }
        }
    }

    // UI communication functions
    // LoadingFragment uses these so that button presses can update the ViewModel.
    // all doneWithX functions are to reset the value so that LoadingFragment doesn't
    // repeat commands if the view is recreated.
    fun reload() {
        mRedo.value = true

        doLoad()
    }

    fun doneWithReload() {
        mRedo.value = false
    }

    fun grant() {
        mDoGrant.value = true
    }

    fun doneWithGrant() {
        mDoGrant.value = false
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoadingViewModel::class.java)) {
                return LoadingViewModel(application) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class.")
        }
    }
}

package org.oxycblt.auxio.loading

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.MusicStore

/**
 * ViewModel responsible for the loading UI and beginning the loading process overall.
 * @author OxygenCobalt
 */
class LoadingViewModel(private val app: Application) : ViewModel() {
    private val mResponse = MutableLiveData<MusicStore.Response?>(null)
    private val mDoGrant = MutableLiveData(false)

    private var isBusy = false

    /** The last response from [MusicStore]. Null if the loading process is occurring. */
    val response: LiveData<MusicStore.Response?> = mResponse
    val doGrant: LiveData<Boolean> = mDoGrant
    val loaded: Boolean get() = musicStore.loaded

    private val musicStore = MusicStore.getInstance()

    /**
     * Begin the music loading process. The response is pushed to [response]
     */
    fun load() {
        // Dont start a new load if the last one hasnt finished
        if (isBusy) return

        isBusy = true
        mResponse.value = null

        viewModelScope.launch {
            mResponse.value = musicStore.load(app)
            isBusy = false
        }
    }

    /**
     * Show the grant prompt.
     */
    fun grant() {
        mDoGrant.value = true
    }

    /**
     * Mark that the grant prompt is now shown.
     */
    fun doneWithGrant() {
        mDoGrant.value = false
    }

    /**
     * Notify this viewmodel that there are no permissions
     */
    fun notifyNoPermissions() {
        mResponse.value = MusicStore.Response.NO_PERMS
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoadingViewModel::class.java)) {
                return LoadingViewModel(application) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class.")
        }
    }
}

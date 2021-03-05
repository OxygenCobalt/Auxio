package org.oxycblt.auxio.loading

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.MusicStore

/**
 * ViewModel responsible for the loading UI and beginning the loading process overall.
 * @author OxygenCobalt
 */
class LoadingViewModel : ViewModel() {
    private val mResponse = MutableLiveData<MusicStore.Response?>(null)
    private val mDoGrant = MutableLiveData(false)

    private var isBusy = false

    /** The last response from [MusicStore]. Null if the loading process is occurring. */
    val response: LiveData<MusicStore.Response?> = mResponse
    val doGrant: LiveData<Boolean> = mDoGrant
    val loaded: Boolean get() = musicStore.loaded

    private val musicStore = MusicStore.getInstance()

    /**
     * Begin the music loading process. The response from MusicStore is pushed to [response]
     */
    fun load(context: Context) {
        // Dont start a new load if the last one hasnt finished
        if (isBusy) return

        isBusy = true
        mResponse.value = null

        viewModelScope.launch {
            mResponse.value = musicStore.load(context)
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
}

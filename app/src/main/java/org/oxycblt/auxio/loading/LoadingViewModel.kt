package org.oxycblt.auxio.loading

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.MusicStore

class LoadingViewModel(private val app: Application) : ViewModel() {
    private val mResponse = MutableLiveData<MusicStore.Response?>(null)
    val response: LiveData<MusicStore.Response?> = mResponse

    private val mDoGrant = MutableLiveData(false)
    val doGrant: LiveData<Boolean> = mDoGrant

    private var isBusy = false

    private val musicStore = MusicStore.getInstance()

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

    fun grant() {
        mDoGrant.value = true
    }

    fun doneWithGrant() {
        mDoGrant.value = false
    }

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

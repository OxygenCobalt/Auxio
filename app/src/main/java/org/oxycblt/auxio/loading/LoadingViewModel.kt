package org.oxycblt.auxio.loading

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.processing.MusicLoaderResponse

class LoadingViewModel(private val app: Application) : ViewModel() {

    private val loadingJob = Job()
    private val ioScope = CoroutineScope(
        loadingJob + Dispatchers.IO
    )

    private val mMusicRepoResponse = MutableLiveData<MusicLoaderResponse>()
    val musicRepoResponse: LiveData<MusicLoaderResponse> get() = mMusicRepoResponse

    private val mDoRetry = MutableLiveData<Boolean>()
    val doRetry: LiveData<Boolean> get() = mDoRetry

    private val mDoGrant = MutableLiveData<Boolean>()
    val doGrant: LiveData<Boolean> get() = mDoGrant

    private var started = false

    // Start the music loading. It has already been called, one needs to call retry() instead.
    fun go() {
        if (!started) {
            startMusicRepo()
        }
    }

    // Start the music loading sequence.
    private fun startMusicRepo() {
        val repo = MusicRepository.getInstance()

        // Allow MusicRepository to scan the file system on the IO thread
        ioScope.launch {
            val response = repo.init(app)

            // Then actually notify listeners of the response in the Main thread
            withContext(Dispatchers.Main) {
                mMusicRepoResponse.value = response

                started = true
            }
        }
    }

    // Functions for communicating between LoadingFragment & LoadingViewModel

    fun doneWithResponse() {
        mMusicRepoResponse.value = null
    }

    fun retry() {
        startMusicRepo()

        mDoRetry.value = true
    }

    fun doneWithRetry() {
        mDoRetry.value = false
    }

    fun grant() {
        mDoGrant.value = true
    }

    fun doneWithGrant() {
        mDoGrant.value = false
    }

    override fun onCleared() {
        super.onCleared()

        // Cancel the current loading job if the app has been stopped
        loadingJob.cancel()
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

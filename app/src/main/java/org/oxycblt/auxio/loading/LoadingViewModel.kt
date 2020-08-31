package org.oxycblt.auxio.loading

import android.app.Application
import android.util.Log
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

    init {
        startMusicRepo()

        Log.d(this::class.simpleName, "ViewModel created.")
    }

    private fun startMusicRepo() {
        val repo = MusicRepository.getInstance()

        // Allow MusicRepository to scan the file system on the IO thread
        ioScope.launch {
            val response = repo.init(app)

            // Then actually notify listeners of the response in the Main thread
            withContext(Dispatchers.Main) {
                mMusicRepoResponse.value = response
            }
        }
    }

    fun doneWithResponse() {
        mMusicRepoResponse.value = null
    }

    fun retry() {
        mDoRetry.value = true

        startMusicRepo()
    }

    fun doneWithRetry() {
        mDoRetry.value = false
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

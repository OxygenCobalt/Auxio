package org.oxycblt.auxio.settings.blacklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BlacklistViewModel : ViewModel() {
    private val mPaths = MutableLiveData(mutableListOf<String>())
    val paths: LiveData<MutableList<String>> get() = mPaths

    fun addPath(path: String) {
        if (mPaths.value!!.contains(path)) {
            return
        }

        mPaths.value!!.add(path)

        mPaths.value = mPaths.value
    }

    fun removePath(path: String) {
        mPaths.value!!.remove(path)

        mPaths.value = mPaths.value
    }
}

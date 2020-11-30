package org.oxycblt.auxio.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel(), SettingsManager.Callback {
    private val mTheme = MutableLiveData<Int?>()
    val theme: LiveData<Int?> get() = mTheme

    private val mAccent = MutableLiveData<Pair<Int, Int>?>()
    val accent: LiveData<Pair<Int, Int>?> get() = mAccent

    private val mEdge = MutableLiveData<Boolean?>()
    val edge: LiveData<Boolean?> = mEdge

    private val settingsManager = SettingsManager.getInstance()

    init {
        settingsManager.addCallback(this)
    }

    fun doneWithThemeUpdate() {
        mTheme.value = null
    }

    fun doneWithAccentUpdate() {
        mAccent.value = null
    }

    fun doneWithEdgeUpdate() {
        mEdge.value = null
    }

    override fun onThemeUpdate(newTheme: Int) {
        mTheme.value = newTheme
    }

    override fun onAccentUpdate(newAccent: Pair<Int, Int>) {
        mAccent.value = newAccent
    }

    override fun onEdgeToEdgeUpdate(isEdgeToEdge: Boolean) {
        mEdge.value = isEdgeToEdge
    }

    override fun onCleared() {
        super.onCleared()

        settingsManager.removeCallback(this)
    }
}

package org.oxycblt.auxio.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel(), SettingsManager.Callback {
    private val mTheme = MutableLiveData<Int?>()
    val theme: LiveData<Int?> get() = mTheme

    private val mAccent = MutableLiveData<Pair<Int, Int>?>()
    val accent: LiveData<Pair<Int, Int>?> get() = mAccent

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

    override fun onThemeUpdate(newTheme: Int) {
        super.onThemeUpdate(newTheme)

        mTheme.value = newTheme
    }

    override fun onAccentUpdate(newAccent: Pair<Int, Int>) {
        super.onAccentUpdate(newAccent)

        mAccent.value = newAccent
    }

    override fun onCleared() {
        super.onCleared()

        settingsManager.removeCallback(this)
    }
}

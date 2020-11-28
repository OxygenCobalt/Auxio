package org.oxycblt.auxio.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.oxycblt.auxio.R

class SettingListFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_main, rootKey)
    }
}

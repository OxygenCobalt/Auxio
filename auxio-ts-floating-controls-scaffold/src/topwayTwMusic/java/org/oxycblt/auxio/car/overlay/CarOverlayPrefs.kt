package org.oxycblt.auxio.car.overlay

import android.content.Context
import android.content.SharedPreferences

internal class CarOverlayPrefs(context: Context) {
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE,
    )

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var alwaysShow: Boolean
        get() = prefs.getBoolean(KEY_ALWAYS_SHOW, true)
        set(value) = prefs.edit().putBoolean(KEY_ALWAYS_SHOW, value).apply()

    var hideWhileAuxioForeground: Boolean
        get() = prefs.getBoolean(KEY_HIDE_WHILE_AUXIO_FOREGROUND, true)
        set(value) = prefs.edit().putBoolean(KEY_HIDE_WHILE_AUXIO_FOREGROUND, value).apply()

    var x: Int
        get() = prefs.getInt(KEY_X, DEFAULT_X)
        set(value) = prefs.edit().putInt(KEY_X, value).apply()

    var y: Int
        get() = prefs.getInt(KEY_Y, DEFAULT_Y)
        set(value) = prefs.edit().putInt(KEY_Y, value).apply()

    var opacityPercent: Int
        get() = prefs.getInt(KEY_OPACITY_PERCENT, 86).coerceIn(30, 100)
        set(value) = prefs.edit().putInt(KEY_OPACITY_PERCENT, value.coerceIn(30, 100)).apply()

    var buttonDp: Int
        get() = prefs.getInt(KEY_BUTTON_DP, 64).coerceIn(48, 96)
        set(value) = prefs.edit().putInt(KEY_BUTTON_DP, value.coerceIn(48, 96)).apply()

    fun resetPosition() {
        prefs.edit().putInt(KEY_X, DEFAULT_X).putInt(KEY_Y, DEFAULT_Y).apply()
    }

    companion object {
        private const val PREFS_NAME = "auxio_car_floating_controls"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_ALWAYS_SHOW = "always_show"
        private const val KEY_HIDE_WHILE_AUXIO_FOREGROUND = "hide_while_auxio_foreground"
        private const val KEY_X = "x"
        private const val KEY_Y = "y"
        private const val KEY_OPACITY_PERCENT = "opacity_percent"
        private const val KEY_BUTTON_DP = "button_dp"

        const val DEFAULT_X = 24
        const val DEFAULT_Y = 320
    }
}

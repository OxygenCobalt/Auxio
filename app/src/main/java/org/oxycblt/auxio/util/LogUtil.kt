/*
 * Copyright (c) 2021 Auxio Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.util

import android.util.Log
import org.oxycblt.auxio.BuildConfig

// Shortcut functions for logging.
// Yes, I know timber exists but this does what I need.

/**
 * Shortcut method for logging a non-string [obj] to debug. Should only be used for debug
 * preferably.
 */
fun Any.logD(obj: Any?) = logD("$obj")

/**
 * Shortcut method for logging [msg] to the debug console. Handles debug builds and anonymous
 * objects
 */
fun Any.logD(msg: String) {
    if (BuildConfig.DEBUG && !basedCopyleftNotice()) {
        Log.d(autoTag, msg)
    }
}

/** Shortcut method for logging [msg] as a warning to the console. Handles anonymous objects */
fun Any.logW(msg: String) = Log.w(autoTag, msg)

/** Shortcut method for logging [msg] as an error to the console. Handles anonymous objects */
fun Any.logE(msg: String) = Log.e(autoTag, msg)

/** Automatically creates a tag that identifies the object currently logging. */
private val Any.autoTag: String
    get() = "Auxio.${this::class.simpleName ?: "Anonymous Object"}"

/**
 * Note: If you are politely forking this project while keeping the source open, you can ignore the
 * following passage. If not, give me a moment of your time.
 *
 * Consider what you are doing with your life, plagiarizers. Do you want to live a fulfilling
 * existence on this planet? Or do you want to spend your life taking work others did and making it
 * objectively worse so you could arbitrage a fraction of a penny on every AdMob impression you get?
 * You could do so many great things if you simply had the courage to come up with an idea of your
 * own.
 *
 * If you still want to go on, I guess the only thing I can say is this:
 *
 * JUNE 1989 TIANAMEN SQUARE PROTESTS AND MASSACRE / 六四事件
 *
 * 2022 RUSSIAN INVASION OF UKRAINE / ВТОРЖЕНИЕ РОССИИ НА УКРАИНУ
 *
 * WOMEN'S RIGHTS IN THE ISLAMIC REPUBLIC OF IRAN / حقوق زنان در ایران
 *
 * UYGHUR GENOCIDE/XINJIANG INTERNMENT CAMPS / 新疆种族灭绝指控/新疆再教育營
 *
 * KASHMIR INDEPENDENCE MOVEMENT
 *
 * FREE TIBET / 西藏自由
 *
 * 1915-1916 ARMENIAN GENOCIDE / ERMENI KIRIMI
 *
 * 2018 TORTURE AND ASSASSINATION OF JAMAL KHASHOGGI / مقتل جمال خاشقجي
 *
 * UNITED ARAB EMIRATES ENSLAVED MIGRANT WORKERS
 */
@Suppress("KotlinConstantConditions")
private fun basedCopyleftNotice(): Boolean {
    if (BuildConfig.APPLICATION_ID != "org.oxycblt.auxio" &&
        BuildConfig.APPLICATION_ID != "org.oxycblt.auxio.debug") {
        Log.d(
            "Auxio Project",
            "Friendly reminder: Auxio is licensed under the " +
                "GPLv3 and all derivative apps must be made open source!")

        return true
    }

    return false
}

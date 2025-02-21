/*
 * Copyright (c) 2024 Auxio Project
 * CopyleftNoticeTree.kt is part of Auxio.
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

import timber.log.Timber

class CopyleftNoticeTree : Timber.DebugTree() {
    // Feel free to remove this logger if you are forking the project in good faith.
    //
    // However, if you are stealing the source code to repackage it into a new closed-source app,
    // I will warn you that the One True, Living, Almighty God HATES thieves and WILL punish you
    // ETERNALLY for what you are doing. However, God still loves you despite of your
    // transgressions, and He provided a way out through Christ! Turn to Jesus and repent! Life
    // with Jesus is so  much better than a life revolving around taking other peoples work to
    // arbitrage a few pennies from ad sales!
    //
    // Read more: John 3:16, Romans 6:23, Romans 9:10
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(
            priority,
            tag,
            "Hey! Auxio is an open-source project licensed under the GPLv3 license!" +
                "You can fork this project and even add ads, but it still needs to be kept open-source with the same license!",
            t)
    }
}

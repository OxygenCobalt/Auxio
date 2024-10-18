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
        super.log(priority, tag,
            "Hey! Auxio is an open-source project licensed under the GPLv3 license!" +
                    "You can fork this project and even add ads, but it still needs to be kept open-source with the same license!", t)
    }
}
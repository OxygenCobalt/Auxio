/*
 * Copyright (c) 2026 Auxio Project
 * UsbModule.kt is part of Auxio.
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

package org.oxycblt.auxio.usb

import android.content.Context
import androidx.media3.common.Format
import androidx.media3.decoder.ffmpeg.RawSampleBitsData
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.ostrichbat2k.usbdac.UsbAudioManagerImpl
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.UsbDacPreferences

@Module
@InstallIn(SingletonComponent::class)
interface UsbModule {
    @Binds fun settings(usbSettings: UsbSettingsImpl): UsbDacPreferences

    companion object {
        // libusbdac has no DI-framework dependency of its own, so UsbAudioManager takes a plain
        // constructor -- this is the bridge from Hilt's graph to it.
        @Provides
        @Singleton
        fun provideUsbAudioManager(
            @ApplicationContext context: Context,
            settings: UsbDacPreferences,
        ): UsbAudioManager =
            UsbAudioManagerImpl(context, settings, realBitsPerSample = ::realBitsPerSample)

        /**
         * A genuine 24-bit FLAC/ALAC source and a genuinely 32-bit-precision source both decode
         * to the same [Format.pcmEncoding] (FFmpeg's `AVSampleFormat` has no native 24-bit
         * container) -- [RawSampleBitsData] is [FfmpegAudioRenderer][androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer]'s
         * side-channel for telling the two apart, see its own doc comment. Without this wired in,
         * [UsbAudioManagerImpl] can't tell the difference either, so every wide-container track
         * -- 24-bit real content included -- always lands on the device's 32-bit alt-setting.
         */
        private fun realBitsPerSample(format: Format): Int? =
            format.metadata?.getFirstEntryOfType(RawSampleBitsData::class.java)?.bitsPerRawSample
    }
}

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
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.UsbAudioManagerImpl
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
        ): UsbAudioManager = UsbAudioManagerImpl(context, settings)
    }
}

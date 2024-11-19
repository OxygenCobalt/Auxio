package org.oxycblt.auxio.music.stack.interpreter

import kotlinx.coroutines.flow.Flow
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.device.RawSong

interface Interpreter {
    suspend fun interpret(rawSong: Flow<RawSong>): DeviceLibrary
}
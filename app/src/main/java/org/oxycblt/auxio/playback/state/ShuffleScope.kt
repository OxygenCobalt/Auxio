/*
 * Copyright (c) 2026 Auxio Project
 * ShuffleScope.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.state

/**
 * Persistent UI-facing shuffle scope for playback controls.
 *
 * This is intentionally separate from [ShuffleMode], which is command-time intent used when
 * constructing a new playback command.
 */
enum class ShuffleScope {
    OFF,
    ALL,
    GENRE,
}
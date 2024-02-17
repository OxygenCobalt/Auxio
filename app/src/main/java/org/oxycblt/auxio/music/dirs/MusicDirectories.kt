/*
 * Copyright (c) 2023 Auxio Project
 * MusicDirectories.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.dirs

import org.oxycblt.auxio.music.fs.Path

/**
 * Represents the configuration for specific directories to filter to/from when loading music.
 *
 * @param dirs A list of directory [Path] instances. How these are interpreted depends on
 *   [shouldInclude]
 * @param shouldInclude True if the library should only load from the [Path] instances, false if the
 *   library should not load from the [Path] instances.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class MusicDirectories(val dirs: List<Path>, val shouldInclude: Boolean)

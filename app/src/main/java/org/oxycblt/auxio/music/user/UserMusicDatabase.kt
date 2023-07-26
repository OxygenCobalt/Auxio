/*
 * Copyright (c) 2023 Auxio Project
 * UserMusicDatabase.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.user

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import org.oxycblt.auxio.music.Music

/**
 * Allows persistence of all user-created music information.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Database(
    entities = [PlaylistInfo::class, PlaylistSong::class, PlaylistSongCrossRef::class],
    version = 30,
    exportSchema = false)
@TypeConverters(Music.UID.TypeConverters::class)
abstract class UserMusicDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}

// TODO: Handle playlist defragmentation? I really don't want dead songs to accumulate in this
//  database.

/**
 * The DAO for persisted playlist information.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Dao
abstract class PlaylistDao {
    /**
     * Read out all playlists stored in the database.
     *
     * @return A list of [RawPlaylist] representing each playlist stored.
     */
    @Transaction
    @Query("SELECT * FROM PlaylistInfo")
    abstract suspend fun readRawPlaylists(): List<RawPlaylist>

    /**
     * Create a new playlist.
     *
     * @param rawPlaylist The [RawPlaylist] to create.
     */
    @Transaction
    open suspend fun insertPlaylist(rawPlaylist: RawPlaylist) {
        insertInfo(rawPlaylist.playlistInfo)
        insertSongs(rawPlaylist.songs)
        insertRefs(
            rawPlaylist.songs.map {
                PlaylistSongCrossRef(
                    playlistUid = rawPlaylist.playlistInfo.playlistUid, songUid = it.songUid)
            })
    }

    /**
     * Replace the currently-stored [PlaylistInfo] for a playlist entry.
     *
     * @param playlistInfo The new [PlaylistInfo] to store.
     */
    @Transaction
    open suspend fun replacePlaylistInfo(playlistInfo: PlaylistInfo) {
        deleteInfo(playlistInfo.playlistUid)
        insertInfo(playlistInfo)
    }

    /**
     * Delete a playlist entry's [PlaylistInfo] and [PlaylistSong].
     *
     * @param playlistUid The [Music.UID] of the playlist to delete.
     */
    @Transaction
    open suspend fun deletePlaylist(playlistUid: Music.UID) {
        deleteInfo(playlistUid)
        deleteRefs(playlistUid)
    }

    /**
     * Insert new song entries into a playlist.
     *
     * @param playlistUid The [Music.UID] of the playlist to insert into.
     * @param songs The [PlaylistSong] representing each song to put into the playlist.
     */
    @Transaction
    open suspend fun insertPlaylistSongs(playlistUid: Music.UID, songs: List<PlaylistSong>) {
        insertSongs(songs)
        insertRefs(
            songs.map { PlaylistSongCrossRef(playlistUid = playlistUid, songUid = it.songUid) })
    }

    /**
     * Replace the currently stored songs of the given playlist entry.
     *
     * @param playlistUid The [Music.UID] of the playlist to update.
     * @param songs The [PlaylistSong] representing the new list of songs to be placed in the
     *   playlist.
     */
    @Transaction
    open suspend fun replacePlaylistSongs(playlistUid: Music.UID, songs: List<PlaylistSong>) {
        deleteRefs(playlistUid)
        insertSongs(songs)
        insertRefs(
            songs.map { PlaylistSongCrossRef(playlistUid = playlistUid, songUid = it.songUid) })
    }

    /** Internal, do not use. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertInfo(info: PlaylistInfo)

    /** Internal, do not use. */
    @Query("DELETE FROM PlaylistInfo where playlistUid = :playlistUid")
    abstract suspend fun deleteInfo(playlistUid: Music.UID)

    /** Internal, do not use. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSongs(songs: List<PlaylistSong>)

    /** Internal, do not use. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertRefs(refs: List<PlaylistSongCrossRef>)

    /** Internal, do not use. */
    @Query("DELETE FROM PlaylistSongCrossRef where playlistUid = :playlistUid")
    abstract suspend fun deleteRefs(playlistUid: Music.UID)
}

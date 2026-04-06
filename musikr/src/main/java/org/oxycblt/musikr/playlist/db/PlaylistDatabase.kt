/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDatabase.kt is part of Auxio.
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
 
package org.oxycblt.musikr.playlist.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.oxycblt.musikr.Music

/**
 * Allows persistence of all user-created music information.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Database(
    entities =
        [
            PlaylistInfo::class,
            PlaylistSong::class,
            PlaylistSongCrossRef::class,
            SongUriRecord::class,
        ],
    version = 31,
    exportSchema = false,
)
@TypeConverters(Music.UID.TypeConverters::class)
internal abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao

    companion object {
        fun from(context: Context) =
            Room.databaseBuilder(
                    context.applicationContext,
                    PlaylistDatabase::class.java,
                    "user_music.db",
                )
                .addMigrations(MIGRATION_30_31)
                .fallbackToDestructiveMigration(true)
                .build()
    }
}

private val MIGRATION_30_31 =
    object : Migration(30, 31) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `SongUriRecord` " +
                    "(`uri` TEXT NOT NULL, `songUid` TEXT NOT NULL, PRIMARY KEY(`uri`))"
            )
        }
    }

// TODO: Handle playlist defragmentation? I really don't want dead songs to accumulate in this
//  database.

/**
 * The DAO for persisted playlist information.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@Dao
internal abstract class PlaylistDao {
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
                    playlistUid = rawPlaylist.playlistInfo.playlistUid,
                    songUid = it.songUid,
                )
            }
        )
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
            songs.map { PlaylistSongCrossRef(playlistUid = playlistUid, songUid = it.songUid) }
        )
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
            songs.map { PlaylistSongCrossRef(playlistUid = playlistUid, songUid = it.songUid) }
        )
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

    /**
     * Migrate all playlist references from [oldUid] to [newUid]. Used when a song gains a
     * MusicBrainz ID and its canonical UID changes from a hash-based UID to a MusicBrainz UID.
     *
     * @param oldUid The hash-based [Music.UID] previously stored in playlists.
     * @param newUid The MusicBrainz [Music.UID] that now identifies the same song.
     */
    @Transaction
    open suspend fun migrateSongUid(oldUid: Music.UID, newUid: Music.UID) {
        insertSongs(listOf(PlaylistSong(newUid)))
        updateCrossRefSongUid(oldUid, newUid)
        deleteSong(oldUid)
    }

    /** Internal, do not use. */
    @Query("UPDATE PlaylistSongCrossRef SET songUid = :newUid WHERE songUid = :oldUid")
    abstract suspend fun updateCrossRefSongUid(oldUid: Music.UID, newUid: Music.UID)

    /** Internal, do not use. */
    @Query("DELETE FROM PlaylistSong WHERE songUid = :songUid")
    abstract suspend fun deleteSong(songUid: Music.UID)

    /**
     * Read the full URI → UID index recorded after the last index run.
     *
     * @return All [SongUriRecord] entries currently stored.
     */
    @Query("SELECT * FROM SongUriRecord") abstract suspend fun readUriRecords(): List<SongUriRecord>

    /**
     * Replace the entire URI → UID index with the current song set.
     *
     * @param records The new [SongUriRecord] entries representing every indexed song.
     */
    @Transaction
    open suspend fun replaceUriIndex(records: List<SongUriRecord>) {
        clearUriRecords()
        insertUriRecords(records)
    }

    /** Internal, do not use. */
    @Query("DELETE FROM SongUriRecord") abstract suspend fun clearUriRecords()

    /** Internal, do not use. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUriRecords(records: List<SongUriRecord>)
}

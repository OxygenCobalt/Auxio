package org.oxycblt.auxio.headunit.compat

data class HeadUnitMetadataSnapshot(
    val displayTitle: String,
    val displaySubtitle: String,
    val artist: String,
    val albumArtist: String,
    val albumTitle: String,
    val displayDescription: String,
    val durationMs: Long,
    val mediaId: String,
    val mediaUri: String,
    val hasArtwork: Boolean,
    val artworkUri: String?,
)

object HeadUnitMetadataPolicy {
    fun fromRaw(
        title: String?,
        artist: String?,
        albumArtist: String?,
        albumTitle: String?,
        durationMs: Long?,
        mediaId: String?,
        mediaUri: String?,
        artworkUri: String?,
        hasArtwork: Boolean,
    ): HeadUnitMetadataSnapshot? {
        val safeTitle = title?.trim().orEmpty()
        if (safeTitle.isBlank()) return null
        val safeArtist = artist?.trim().orEmpty()
        val safeAlbumArtist = albumArtist?.trim().orEmpty()
        val safeAlbum = albumTitle?.trim().orEmpty()
        val subtitle = listOf(safeArtist, safeAlbumArtist).filter { it.isNotBlank() }.distinct().joinToString(" • ")
        val description = if (safeAlbum.isNotBlank()) safeAlbum else subtitle
        return HeadUnitMetadataSnapshot(
            displayTitle = safeTitle,
            displaySubtitle = subtitle,
            artist = safeArtist,
            albumArtist = safeAlbumArtist,
            albumTitle = safeAlbum,
            displayDescription = description,
            durationMs = durationMs ?: 0L,
            mediaId = mediaId.orEmpty(),
            mediaUri = mediaUri.orEmpty(),
            hasArtwork = hasArtwork || !artworkUri.isNullOrBlank(),
            artworkUri = artworkUri?.takeIf { it.isNotBlank() },
        )
    }
}

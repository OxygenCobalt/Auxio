.class public final Lcom/tw/music/media/MediaMetadataMapper;
.super Ljava/lang/Object;
.source "MediaMetadataMapper.java"


# direct methods
.method private constructor <init>()V
    .registers 1

    .line 8
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static map(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLandroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/media/MediaMetadataCompat;
    .registers 10

    .line 18
    new-instance v0, Landroid/support/v4/media/MediaMetadataCompat$Builder;

    invoke-direct {v0}, Landroid/support/v4/media/MediaMetadataCompat$Builder;-><init>()V

    .line 19
    if-eqz p0, :cond_c

    const-string v1, "android.media.metadata.TITLE"

    invoke-virtual {v0, v1, p0}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/media/MediaMetadataCompat$Builder;

    .line 20
    :cond_c
    if-eqz p1, :cond_13

    const-string p0, "android.media.metadata.ARTIST"

    invoke-virtual {v0, p0, p1}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/media/MediaMetadataCompat$Builder;

    .line 21
    :cond_13
    if-eqz p2, :cond_1a

    const-string p0, "android.media.metadata.ALBUM"

    invoke-virtual {v0, p0, p2}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/media/MediaMetadataCompat$Builder;

    .line 22
    :cond_1a
    const-wide/16 p0, 0x0

    cmp-long p0, p3, p0

    if-lez p0, :cond_25

    const-string p0, "android.media.metadata.DURATION"

    invoke-virtual {v0, p0, p3, p4}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->putLong(Ljava/lang/String;J)Landroid/support/v4/media/MediaMetadataCompat$Builder;

    .line 23
    :cond_25
    if-eqz p5, :cond_2c

    const-string p0, "android.media.metadata.ALBUM_ART"

    invoke-virtual {v0, p0, p5}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->putBitmap(Ljava/lang/String;Landroid/graphics/Bitmap;)Landroid/support/v4/media/MediaMetadataCompat$Builder;

    .line 24
    :cond_2c
    if-eqz p6, :cond_33

    const-string p0, "android.media.metadata.MEDIA_ID"

    invoke-virtual {v0, p0, p6}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/media/MediaMetadataCompat$Builder;

    .line 25
    :cond_33
    if-eqz p7, :cond_3a

    .line 26
    const-string p0, "android.media.metadata.MEDIA_URI"

    invoke-virtual {v0, p0, p7}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/media/MediaMetadataCompat$Builder;

    .line 28
    :cond_3a
    invoke-virtual {v0}, Landroid/support/v4/media/MediaMetadataCompat$Builder;->build()Landroid/support/v4/media/MediaMetadataCompat;

    move-result-object p0

    return-object p0
.end method

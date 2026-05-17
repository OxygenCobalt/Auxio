.class public final Lcom/tw/music/media/PlaybackStateMapper;
.super Ljava/lang/Object;
.source "PlaybackStateMapper.java"


# direct methods
.method private constructor <init>()V
    .registers 1

    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static map(ZZZJFZ)Landroid/support/v4/media/session/PlaybackStateCompat;
    .registers 16

    .line 27
    nop

    .line 28
    const-wide/16 v0, 0x0

    if-eqz p2, :cond_8

    .line 29
    const/4 p0, 0x7

    move v3, p0

    goto :goto_1b

    .line 30
    :cond_8
    if-eqz p1, :cond_d

    .line 31
    const/4 p0, 0x6

    move v3, p0

    goto :goto_1b

    .line 32
    :cond_d
    if-eqz p0, :cond_12

    .line 33
    const/4 p0, 0x3

    move v3, p0

    goto :goto_1b

    .line 34
    :cond_12
    cmp-long p0, p3, v0

    if-lez p0, :cond_19

    .line 35
    const/4 p0, 0x2

    move v3, p0

    goto :goto_1b

    .line 34
    :cond_19
    const/4 p0, 0x1

    move v3, p0

    .line 38
    :goto_1b
    nop

    .line 43
    if-eqz p6, :cond_21

    .line 44
    const-wide/16 p0, 0x336

    goto :goto_23

    .line 43
    :cond_21
    const-wide/16 p0, 0x236

    .line 47
    :goto_23
    new-instance p2, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;

    invoke-direct {p2}, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;-><init>()V

    .line 48
    invoke-virtual {p2, p0, p1}, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->setActions(J)Landroid/support/v4/media/session/PlaybackStateCompat$Builder;

    move-result-object v2

    .line 51
    invoke-static {p3, p4, v0, v1}, Ljava/lang/Math;->max(JJ)J

    move-result-wide v4

    invoke-static {}, Landroid/os/SystemClock;->elapsedRealtime()J

    move-result-wide v7

    move v6, p5

    invoke-virtual/range {v2 .. v8}, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->setState(IJFJ)Landroid/support/v4/media/session/PlaybackStateCompat$Builder;

    move-result-object p0

    .line 52
    invoke-virtual {p0}, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->build()Landroid/support/v4/media/session/PlaybackStateCompat;

    move-result-object p0

    .line 47
    return-object p0
.end method

.class public Lcom/eckom/xtlibrary/b/f/d/U;
.super Lcom/eckom/xtlibrary/b/f/d/a;
.source "MusicIjkModel.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Lcom/eckom/xtlibrary/b/f/d/a;"
    }
.end annotation


# static fields
.field public static Gd:Z

.field private static hi:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/g/a;",
            ">;"
        }
    .end annotation
.end field

.field private static jd:Lcom/eckom/xtlibrary/b/f/f/s;

.field private static ji:Lcom/eckom/xtlibrary/b/f/d/U;


# instance fields
.field private Ai:Z

.field private Cg:Z

.field private Eh:I

.field private Qh:Z

.field private Rh:Z

.field private Sh:Z

.field Th:Z

.field private Uh:I

.field private Vh:Ljava/lang/String;

.field private fileName:Ljava/lang/String;

.field private mContext:Landroid/content/Context;

.field private mHandler:Landroid/os/Handler;

.field private mHints:[J

.field public mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

.field private qi:F

.field r:Lcom/eckom/xtlibrary/b/f/b/g;

.field private ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

.field private ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

.field private wg:Z

.field private wi:I

.field private xi:Z

.field private yi:I

.field private zi:Z


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    const/4 v0, 0x0

    .line 2
    sput-boolean v0, Lcom/eckom/xtlibrary/b/f/d/U;->Gd:Z

    const/4 v0, 0x0

    .line 3
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->ji:Lcom/eckom/xtlibrary/b/f/d/U;

    return-void
.end method

.method private constructor <init>()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/a;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->wi:I

    .line 3
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->xi:Z

    const/4 v1, 0x7

    .line 4
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->yi:I

    .line 5
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Qh:Z

    .line 6
    iget v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->yi:I

    new-array v1, v1, [J

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHints:[J

    .line 7
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->zi:Z

    .line 8
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Rh:Z

    .line 9
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Sh:Z

    .line 10
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Cg:Z

    .line 11
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Eh:I

    const/high16 v1, 0x3f800000    # 1.0f

    .line 12
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    .line 13
    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/M;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/M;-><init>(Lcom/eckom/xtlibrary/b/f/d/U;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

    .line 14
    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/N;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/N;-><init>(Lcom/eckom/xtlibrary/b/f/d/U;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

    .line 15
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Th:Z

    .line 16
    new-instance v1, Landroid/os/Handler;

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/O;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/O;-><init>(Lcom/eckom/xtlibrary/b/f/d/U;)V

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const/4 v1, -0x1

    .line 17
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Uh:I

    .line 18
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Ai:Z

    const-string v0, ""

    .line 19
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Vh:Ljava/lang/String;

    return-void
.end method

.method private Ab(Ljava/lang/String;)V
    .locals 7

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const-string v1, ""

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 3
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 4
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fb:Landroid/graphics/Bitmap;

    if-eqz v1, :cond_0

    const/4 v1, 0x0

    .line 5
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fb:Landroid/graphics/Bitmap;

    .line 6
    :cond_0
    new-instance v0, Landroid/media/MediaMetadataRetriever;

    invoke-direct {v0}, Landroid/media/MediaMetadataRetriever;-><init>()V

    const v1, 0x104000e

    .line 7
    :try_start_0
    invoke-virtual {v0, p1}, Landroid/media/MediaMetadataRetriever;->setDataSource(Ljava/lang/String;)V

    const/16 v2, 0xc

    .line 8
    invoke-virtual {v0, v2}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v2

    if-eqz v2, :cond_4

    .line 9
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v3, 0x2

    invoke-virtual {v0, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 10
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v3, 0x1

    invoke-virtual {v0, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 11
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v3, 0x7

    invoke-virtual {v0, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const/4 v2, 0x0

    .line 12
    :try_start_1
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_1

    .line 13
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getFileName()Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 14
    :cond_1
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    const-string v4, " "

    if-eqz v3, :cond_2

    .line 15
    :try_start_2
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 16
    :cond_2
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_3

    .line 17
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 18
    :cond_3
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    sget-object v4, Ljava/nio/charset/StandardCharsets;->UTF_16LE:Ljava/nio/charset/Charset;

    invoke-virtual {v3, v4}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    .line 19
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    sget-object v4, Ljava/nio/charset/StandardCharsets;->UTF_16LE:Ljava/nio/charset/Charset;

    invoke-virtual {v3, v4}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    .line 20
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    sget-object v4, Ljava/nio/charset/StandardCharsets;->UTF_16LE:Ljava/nio/charset/Charset;

    invoke-virtual {v3, v4}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    .line 21
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    invoke-direct {p0, v2, v3}, Lcom/eckom/xtlibrary/b/f/d/U;->b(ILjava/lang/String;)V

    .line 22
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/P;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/P;-><init>(Lcom/eckom/xtlibrary/b/f/d/U;)V

    const-wide/16 v5, 0x64

    invoke-virtual {v3, v4, v5, v6}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 23
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/Q;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/Q;-><init>(Lcom/eckom/xtlibrary/b/f/d/U;)V

    const-wide/16 v5, 0xc8

    invoke-virtual {v3, v4, v5, v6}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_0

    :catch_0
    move-exception v3

    :try_start_3
    const-string v4, "MusicModel"

    .line 24
    invoke-static {v3}, Landroid/util/Log;->getStackTraceString(Ljava/lang/Throwable;)Ljava/lang/String;

    move-result-object v3

    invoke-static {v4, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 25
    :goto_0
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    sget-object v4, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    invoke-direct {p0, v3, v4, v5, p1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 26
    invoke-virtual {v0}, Landroid/media/MediaMetadataRetriever;->getEmbeddedPicture()[B

    move-result-object p1

    if-eqz p1, :cond_5

    .line 27
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    array-length v4, p1

    invoke-static {p1, v2, v4}, Landroid/graphics/BitmapFactory;->decodeByteArray([BII)Landroid/graphics/Bitmap;

    move-result-object p1

    iput-object p1, v3, Lcom/eckom/xtlibrary/b/f/f/s;->Fb:Landroid/graphics/Bitmap;

    goto :goto_1

    .line 28
    :cond_4
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getFileName()Ljava/lang/String;

    move-result-object v2

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 29
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    invoke-virtual {v2, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 30
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    invoke-virtual {v2, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 31
    :cond_5
    :goto_1
    invoke-virtual {v0}, Landroid/media/MediaMetadataRetriever;->release()V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    goto :goto_2

    .line 32
    :catch_1
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getFileName()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 33
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    invoke-virtual {v0, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 34
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object p0

    iput-object p0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    :goto_2
    return-void
.end method

.method private Bb(Ljava/lang/String;)V
    .locals 6

    .line 5
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 6
    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    :cond_0
    const-string v0, "/storage/emulated/0/"

    .line 7
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const-string v0, "/storage/emulated/0"

    const-string v1, "mnt/sdcard"

    .line 8
    invoke-virtual {p1, v0, v1}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;

    .line 9
    :cond_1
    sput-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    const-string v0, "/"

    .line 10
    invoke-virtual {p1, v0}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v1

    const/4 v2, 0x0

    invoke-virtual {p1, v2, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v1

    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    .line 11
    new-instance v1, Lcom/eckom/xtlibrary/b/f/b/g;

    const-string v3, "Playlist"

    invoke-direct {v1, v3, v2, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    .line 12
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {v3, v4, v1, v5}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 13
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v3, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 14
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput-object v1, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 15
    iget-object v1, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iput v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    .line 16
    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 17
    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v1, v0}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v0

    add-int/lit8 v0, v0, 0x1

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    const-string v3, "."

    invoke-virtual {v1, v3}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v1

    invoke-virtual {p1, v0, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object p1

    move v0, v2

    move v1, v0

    .line 18
    :goto_0
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v4, v3, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge v0, v4, :cond_3

    .line 19
    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v3, v3, v0

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    invoke-virtual {p1, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_2

    move v1, v0

    :cond_2
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 20
    :cond_3
    sput v1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 21
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p1, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 22
    invoke-direct {p0, v2, v2}, Lcom/eckom/xtlibrary/b/f/d/U;->c(IZ)V

    .line 23
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_4
    :goto_1
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result p1

    if-eqz p1, :cond_5

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 24
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v0, :cond_4

    .line 25
    invoke-interface {p1, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_1

    :cond_5
    return-void
.end method

.method private L(Z)V
    .locals 1

    if-eqz p1, :cond_0

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/high16 v0, 0x3f000000    # 0.5f

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    goto :goto_0

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/high16 v0, 0x3f800000    # 1.0f

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 4
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    :goto_0
    return-void
.end method

.method private Pa(I)Z
    .locals 1

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Uh:I

    if-ne v0, p1, :cond_0

    const/4 p0, 0x1

    return p0

    .line 2
    :cond_0
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Uh:I

    const/4 p0, 0x0

    return p0
.end method

.method private Re()V
    .locals 3

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    if-eqz v1, :cond_1

    array-length v1, v1

    if-lez v1, :cond_1

    .line 2
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    const/4 v2, 0x2

    if-eq v1, v2, :cond_0

    .line 3
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    add-int/lit8 v1, v1, 0x1

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    :cond_0
    const/4 v0, 0x0

    .line 4
    invoke-direct {p0, v0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Sa()V
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/f/s;->Sa()V

    return-void
.end method

.method private Se()V
    .locals 4

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    if-eqz v1, :cond_1

    array-length v1, v1

    if-lez v1, :cond_1

    .line 2
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    const/4 v2, 0x2

    const/4 v3, 0x1

    if-eq v1, v2, :cond_0

    .line 3
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    sub-int/2addr v1, v3

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    :cond_0
    const/4 v0, 0x0

    .line 4
    invoke-direct {p0, v0, v3}, Lcom/eckom/xtlibrary/b/f/d/U;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Ue()Ljava/lang/String;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    return-object p0
.end method

.method private Ve()Z
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff02

    invoke-virtual {v0, v1}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v0

    if-nez v0, :cond_1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v0, 0xff03

    invoke-virtual {p0, v0}, Landroid/os/Handler;->hasMessages(I)Z

    move-result p0

    if-eqz p0, :cond_0

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    goto :goto_1

    :cond_1
    :goto_0
    const/4 p0, 0x1

    :goto_1
    return p0
.end method

.method private Xe()V
    .locals 8

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getDuration()I

    move-result v0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getCurrentPosition()I

    move-result v1

    const/4 v2, 0x0

    if-gez v0, :cond_0

    move v0, v2

    :cond_0
    if-gez v1, :cond_1

    move v1, v2

    :cond_1
    if-lez v0, :cond_2

    if-gt v1, v0, :cond_2

    mul-int/lit8 v1, v1, 0x64

    .line 3
    div-int v0, v1, v0

    goto :goto_0

    :cond_2
    move v0, v2

    .line 4
    :goto_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Nb()Ljava/lang/String;

    move-result-object v1

    .line 5
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_3

    const-string v1, ""

    .line 6
    :cond_3
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const v4, 0x9f00

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result v5

    const/16 v6, 0x80

    if-eqz v5, :cond_4

    move v5, v6

    goto :goto_1

    :cond_4
    move v5, v2

    :goto_1
    and-int/lit8 v0, v0, 0x7f

    or-int/2addr v5, v0

    const/4 v7, 0x3

    invoke-virtual {v3, v4, v7, v5, v1}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    .line 7
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v4, 0x303

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result v5

    if-eqz v5, :cond_5

    move v2, v6

    :cond_5
    or-int/2addr v0, v2

    invoke-virtual {v3, v4, v7, v0, v1}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v0, 0x9e06

    const-wide/16 v1, 0x1f4

    invoke-virtual {p0, v0, v1, v2}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/U;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Eh:I

    return p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    return-object p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/U;ILjava/lang/String;)V
    .locals 0

    .line 4
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/d/U;->b(ILjava/lang/String;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/U;Ljava/lang/String;)V
    .locals 0

    .line 5
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/U;->Bb(Ljava/lang/String;)V

    return-void
.end method

.method private a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 2

    .line 19
    new-instance v0, Landroid/content/Intent;

    const-string v1, "com.tw.music.info"

    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v1, "musicTitle"

    .line 20
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicaArtist"

    .line 21
    invoke-virtual {v0, p1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicAlbum"

    .line 22
    invoke-virtual {v0, p1, p3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicPath"

    .line 23
    invoke-virtual {v0, p1, p4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 24
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Rh:Z

    return p1
.end method

.method static synthetic access$200()Lcom/eckom/xtlibrary/b/f/f/s;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    return-object v0
.end method

.method static synthetic access$300()Ljava/util/ArrayList;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    return-object v0
.end method

.method private b(ILjava/lang/String;)V
    .locals 12

    const/4 v0, 0x0

    const/16 v1, 0x510

    const/4 v2, 0x4

    const/4 v3, 0x0

    if-nez p2, :cond_0

    .line 15
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    shl-int/2addr p1, v2

    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_5

    .line 16
    :cond_0
    iget v4, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Eh:I

    and-int/lit8 v5, v4, 0x1

    const-string v6, "UTF-16"

    const/4 v7, 0x1

    if-ne v5, v7, :cond_2

    .line 17
    :try_start_0
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 18
    :catch_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    shl-int/2addr p1, v2

    or-int/2addr p1, v3

    if-nez v0, :cond_1

    goto :goto_0

    :cond_1
    array-length v3, v0

    :goto_0
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_5

    :cond_2
    and-int/lit8 v5, v4, 0x2

    const/4 v8, 0x2

    if-ne v5, v8, :cond_4

    :try_start_1
    const-string p0, "Unicode"

    .line 19
    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    .line 20
    :catch_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    shl-int/2addr p1, v2

    or-int/2addr p1, v7

    if-nez v0, :cond_3

    goto :goto_1

    :cond_3
    array-length v3, v0

    :goto_1
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto :goto_5

    :cond_4
    and-int/lit8 v5, v4, 0x4

    const/4 v7, 0x3

    const-string v9, "GB2312"

    const-string v10, "GBK"

    const/16 v11, 0x80

    if-ne v5, v2, :cond_8

    .line 21
    :try_start_2
    invoke-virtual {p2, v10}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    :catch_2
    if-nez v0, :cond_5

    .line 22
    :try_start_3
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_3

    goto :goto_2

    :cond_5
    move v7, v8

    :catch_3
    :goto_2
    if-nez v0, :cond_6

    .line 23
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Eh:I

    and-int/2addr p0, v11

    if-ne p0, v11, :cond_6

    .line 24
    :try_start_4
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_4

    :catch_4
    move v7, v3

    .line 25
    :cond_6
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    shl-int/2addr p1, v2

    or-int/2addr p1, v7

    if-nez v0, :cond_7

    goto :goto_3

    :cond_7
    array-length v3, v0

    :goto_3
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto :goto_5

    :cond_8
    const/16 v5, 0x8

    and-int/2addr v4, v5

    if-ne v4, v5, :cond_c

    .line 26
    :try_start_5
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_5

    :catch_5
    if-nez v0, :cond_9

    .line 27
    :try_start_6
    invoke-virtual {p2, v10}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_6

    :catch_6
    move v7, v8

    :cond_9
    if-nez v0, :cond_a

    .line 28
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Eh:I

    and-int/2addr p0, v11

    if-ne p0, v11, :cond_a

    .line 29
    :try_start_7
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_7

    :catch_7
    move v7, v3

    .line 30
    :cond_a
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    shl-int/2addr p1, v2

    or-int/2addr p1, v7

    if-nez v0, :cond_b

    goto :goto_4

    :cond_b
    array-length v3, v0

    :goto_4
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    :cond_c
    :goto_5
    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/U;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Rh:Z

    return p0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Cg:Z

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/U;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Eh:I

    return p0
.end method

.method private c(IZ)V
    .locals 7

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    monitor-enter v0

    .line 4
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    if-eqz v1, :cond_d

    .line 5
    array-length v2, v1

    if-lez v2, :cond_d

    .line 6
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    const/4 v4, 0x0

    if-eqz p2, :cond_6

    const/4 p2, -0x1

    if-ge v3, p2, :cond_0

    move v3, p2

    :cond_0
    move v5, p1

    move p1, v3

    :goto_0
    if-le p1, p2, :cond_2

    .line 7
    aget v6, v1, p1

    sput v6, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 8
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/U;->play(I)Z

    move-result v5

    if-eqz v5, :cond_1

    .line 9
    sget-object v5, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput p1, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    move v5, v4

    goto :goto_1

    :cond_1
    add-int/lit8 p1, p1, -0x1

    move v5, v4

    goto :goto_0

    .line 10
    :cond_2
    :goto_1
    sget-object v6, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v6, v6, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-eqz v6, :cond_5

    if-ne p1, p2, :cond_5

    add-int/lit8 v2, v2, -0x1

    :goto_2
    if-le v2, v3, :cond_4

    .line 11
    aget p1, v1, v2

    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 12
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/U;->play(I)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 13
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    goto :goto_3

    :cond_3
    add-int/lit8 v2, v2, -0x1

    move v5, v4

    goto :goto_2

    :cond_4
    :goto_3
    if-ne v2, v3, :cond_5

    .line 14
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Tb()V

    .line 15
    :cond_5
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    if-ne p1, p2, :cond_d

    .line 16
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v4, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    .line 17
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    aget p1, v1, p1

    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 18
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Tb()V

    goto :goto_8

    :cond_6
    if-le v3, v2, :cond_7

    move v3, v2

    :cond_7
    move p2, p1

    move p1, v3

    :goto_4
    if-ge p1, v2, :cond_9

    .line 19
    aget v5, v1, p1

    sput v5, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 20
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/b/f/d/U;->play(I)Z

    move-result p2

    if-eqz p2, :cond_8

    .line 21
    sget-object p2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput p1, p2, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    move p2, v4

    goto :goto_5

    :cond_8
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_4

    .line 22
    :cond_9
    :goto_5
    sget-object v5, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-eqz v5, :cond_c

    if-ne p1, v2, :cond_c

    move p1, v4

    :goto_6
    if-ge p1, v3, :cond_b

    .line 23
    aget v5, v1, p1

    sput v5, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 24
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/b/f/d/U;->play(I)Z

    move-result p2

    if-eqz p2, :cond_a

    .line 25
    sget-object p2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput p1, p2, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    goto :goto_7

    :cond_a
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_6

    :cond_b
    :goto_7
    if-ne p1, v3, :cond_c

    .line 26
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Tb()V

    .line 27
    :cond_c
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    if-ne p1, v2, :cond_d

    .line 28
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    add-int/lit8 v2, v2, -0x1

    iput v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    .line 29
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    aget p1, v1, p1

    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 30
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Tb()V

    .line 31
    :cond_d
    :goto_8
    monitor-exit v0

    return-void

    :catchall_0
    move-exception p0

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw p0
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Sh:Z

    return p1
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/U;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/U;->L(Z)V

    return-void
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/U;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->wg:Z

    return p0
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/U;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/U;->mute(Z)V

    return-void
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/U;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->xi:Z

    return p0
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/f/d/U;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Sa()V

    return-void
.end method

.method private fd()Ljava/lang/String;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    return-object p0
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/b/f/d/U;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Xe()V

    return-void
.end method

.method private getCurrentPosition()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getCurrentPosition()I

    move-result p0

    return p0
.end method

.method private getDuration()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getDuration()I

    move-result p0

    return p0
.end method

.method public static getInstant()Lcom/eckom/xtlibrary/b/f/d/U;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->ji:Lcom/eckom/xtlibrary/b/f/d/U;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/d/U;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->ji:Lcom/eckom/xtlibrary/b/f/d/U;

    .line 3
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->ji:Lcom/eckom/xtlibrary/b/f/d/U;

    return-object v0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/b/f/d/U;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Cg:Z

    return p0
.end method

.method private hd()I
    .locals 3

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    const/4 v1, 0x1

    if-nez v0, :cond_0

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-ne p0, v1, :cond_0

    const/4 p0, 0x0

    return p0

    .line 2
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    const/4 v2, 0x2

    if-nez v0, :cond_1

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-ne p0, v2, :cond_1

    return v1

    .line 3
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    if-ne v0, v1, :cond_2

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-ne p0, v1, :cond_2

    return v2

    :cond_2
    const/4 p0, -0x1

    return p0
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/b/f/d/U;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Sh:Z

    return p0
.end method

.method private isPlaying()Z
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->isPlaying()Z

    move-result p0

    return p0
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/b/f/d/U;)Z
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result p0

    return p0
.end method

.method static synthetic k(Lcom/eckom/xtlibrary/b/f/d/U;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Re()V

    return-void
.end method

.method static synthetic l(Lcom/eckom/xtlibrary/b/f/d/U;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Se()V

    return-void
.end method

.method static synthetic m(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    return-object p0
.end method

.method private mute(Z)V
    .locals 1

    if-eqz p1, :cond_0

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/4 v0, 0x0

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    goto :goto_0

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/high16 v0, 0x3f800000    # 1.0f

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 4
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    :goto_0
    return-void
.end method

.method private onCreate()V
    .locals 3

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Th:Z

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-nez v0, :cond_0

    .line 3
    sget-boolean v0, Lcom/eckom/xtlibrary/b/f/d/U;->Gd:Z

    const/4 v1, 0x1

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    invoke-static {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/s;->a(ZZLandroid/content/Context;)Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v0

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const-string v2, "MusicModel"

    invoke-virtual {v0, v2, v1}, Landroid/tw/john/TWUtil;->addHandler(Ljava/lang/String;Landroid/os/Handler;)V

    .line 5
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->xi:Z

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->z(Z)V

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ta(Ljava/lang/String;)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 8
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/s;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v0

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 9
    new-instance v0, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    invoke-direct {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;-><init>(Landroid/content/Context;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setOnCompletionListener(Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;)V

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setOnErrorListener(Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;)V

    .line 12
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    if-eqz v0, :cond_1

    .line 13
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v1, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 14
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->seekTo(I)V

    :cond_1
    return-void
.end method

.method private play(I)Z
    .locals 3

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    const/4 v1, -0x1

    if-le v0, v1, :cond_0

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge v0, v2, :cond_0

    .line 2
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    sget v1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    aget-object v0, v0, v1

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    if-eqz v0, :cond_0

    .line 4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v1, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 5
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/U;->seekTo(I)V

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Va()V

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v0, 0xff09

    invoke-virtual {p1, v0}, Landroid/os/Handler;->removeMessages(I)V

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const-wide/16 v1, 0x1f4

    invoke-virtual {p0, v0, v1, v2}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    const/4 p0, 0x1

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method


# virtual methods
.method public Ab()V
    .locals 2

    const/4 v0, 0x4

    .line 35
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Pa(I)Z

    .line 36
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 37
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 38
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v1, :cond_0

    .line 39
    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_0

    :cond_1
    return-void
.end method

.method public Bb()V
    .locals 2

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v1, :cond_0

    .line 4
    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_0

    :cond_1
    return-void
.end method

.method public Cb()V
    .locals 3

    const/4 v0, 0x1

    .line 1
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Pa(I)Z

    move-result p0

    const/4 v1, 0x0

    if-eqz p0, :cond_2

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_1

    .line 3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    add-int/2addr v2, v0

    iput v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v2, p0, :cond_0

    .line 4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 5
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 6
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 7
    :cond_2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_4

    .line 8
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v0, p0, :cond_3

    .line 9
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 10
    :cond_3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 11
    :cond_4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :goto_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_5
    :goto_1
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_6

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 13
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v1, :cond_5

    .line 14
    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_1

    :cond_6
    return-void
.end method

.method public Db()V
    .locals 2

    const/4 v0, 0x2

    .line 1
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Pa(I)Z

    move-result p0

    const/4 v0, 0x0

    if-eqz p0, :cond_2

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_1

    .line 3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v1, p0, :cond_0

    .line 4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 5
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 6
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 7
    :cond_2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_4

    .line 8
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v1, p0, :cond_3

    .line 9
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 10
    :cond_3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 11
    :cond_4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :goto_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_5
    :goto_1
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_6

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 13
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v1, :cond_5

    .line 14
    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_1

    :cond_6
    return-void
.end method

.method public Ea(Ljava/lang/String;)V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/T;

    invoke-direct {v1, p0, p1}, Lcom/eckom/xtlibrary/b/f/d/T;-><init>(Lcom/eckom/xtlibrary/b/f/d/U;Ljava/lang/String;)V

    const-wide/16 p0, 0x5dc

    invoke-virtual {v0, v1, p0, p1}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    return-void
.end method

.method public Eb()V
    .locals 2

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_0
    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v1, :cond_0

    .line 4
    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_0

    :cond_1
    return-void
.end method

.method public Fb()V
    .locals 1

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Th:Z

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ua()V

    return-void
.end method

.method public Gb()V
    .locals 5

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_6

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    const/4 v1, 0x1

    if-eqz v0, :cond_0

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Ljava/lang/String;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/g/a;

    const/4 v3, 0x0

    .line 5
    invoke-interface {v2, v3}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_0

    .line 6
    :cond_0
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Nb()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Xb()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v2, v3}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 7
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 8
    invoke-interface {v2, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_1

    .line 9
    :cond_1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v2, v1, :cond_3

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v3, 0x2

    if-ne v2, v3, :cond_3

    .line 10
    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/U;->fileName:Ljava/lang/String;

    invoke-direct {v2, v4, v3, v1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;IILcom/eckom/xtlibrary/b/f/b/g;)V

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 11
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Vh:Ljava/lang/String;

    invoke-virtual {v0, v1, v2, v3}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 12
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 13
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_2
    :goto_2
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_3

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 15
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v2, :cond_2

    .line 16
    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_2

    .line 17
    :cond_3
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/s;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v0

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v1, 0x4

    if-ne v0, v1, :cond_4

    .line 19
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ab()V

    .line 20
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/l;->Fk:Ljava/lang/String;

    const-string v3, "MUSIC_DATA"

    invoke-static {v0, v3, v2}, Lcom/eckom/xtlibrary/b/f/f/l;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)I

    move-result v0

    if-ne v0, v1, :cond_5

    .line 21
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 22
    :cond_5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff08

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 23
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_6
    return-void
.end method

.method public Hb()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, 0x3a98

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public Ib()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, -0x2710

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public Kb()Landroid/graphics/Bitmap;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->Fb:Landroid/graphics/Bitmap;

    return-object p0
.end method

.method public Nb()Ljava/lang/String;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    return-object p0
.end method

.method public Pb()V
    .locals 2

    const/high16 v0, 0x3f800000    # 1.0f

    .line 1
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Th:Z

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->release(Z)V

    const/4 v0, 0x0

    .line 5
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    .line 6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v0}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 7
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const-string v1, "MusicModel"

    invoke-virtual {p0, v1}, Landroid/tw/john/TWUtil;->removeHandler(Ljava/lang/String;)V

    .line 8
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/f/s;->close()V

    .line 9
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    return-void
.end method

.method public Tb()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff11

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public Ua()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getCurrentPosition()I

    move-result v1

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->pause()V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Xe()V

    :cond_0
    return-void
.end method

.method public Ub()V
    .locals 10

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_2

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 2
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v2, :cond_1

    .line 3
    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 4
    :cond_1
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->mDuration:I

    invoke-interface {v1, v3, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    .line 5
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v4, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v2, v3, v4}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    .line 6
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ue()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->fd()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Nb()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Kb()Landroid/graphics/Bitmap;

    move-result-object v6

    sget-object v7, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v8, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    sget v2, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    sget-object v9, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v9, v9, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    add-int/2addr v9, v2

    move-object v2, v1

    invoke-interface/range {v2 .. v9}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 7
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    .line 8
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->hd()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    .line 9
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;)V

    .line 10
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    if-eqz v2, :cond_0

    .line 11
    invoke-virtual {v2}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getAudioSessionId()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->B(I)V

    goto :goto_0

    :cond_2
    return-void
.end method

.method public Va()V
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-eqz v0, :cond_0

    const/4 v1, 0x1

    .line 2
    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->isPlaying()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->start()V

    const/4 v0, 0x0

    .line 5
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Th:Z

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 8
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ab(Ljava/lang/String;)V

    .line 9
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Xe()V

    .line 10
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "playMusic:playerVolume:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(F)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "MusicModel"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->qi:F

    invoke-virtual {v0, p0, p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    :cond_1
    return-void
.end method

.method public Wb()V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/S;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/S;-><init>(Lcom/eckom/xtlibrary/b/f/d/U;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    .line 2
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method public Xb()Ljava/lang/String;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    return-object p0
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/g/a;)V
    .locals 1

    .line 13
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 14
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 15
    :cond_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-eqz p1, :cond_1

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    const/4 v0, 0x3

    if-ne p1, v0, :cond_1

    .line 16
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Sa()V

    .line 17
    :cond_1
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-nez p1, :cond_2

    .line 18
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Pb()V

    :cond_2
    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/g/a;Landroid/content/Context;)V
    .locals 2

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-nez v0, :cond_1

    .line 7
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    .line 8
    sget p2, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v0, 0x13

    const/4 v1, 0x0

    if-gt p2, v0, :cond_0

    const/4 p2, 0x1

    goto :goto_0

    :cond_0
    move p2, v1

    :goto_0
    sput-boolean p2, Lcom/eckom/xtlibrary/b/f/d/U;->Gd:Z

    const-string p2, "persist.sys.media.sdcardscan"

    .line 9
    invoke-static {p2, v1}, Landroid/os/SystemProperties;->getBoolean(Ljava/lang/String;Z)Z

    move-result p2

    iput-boolean p2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->xi:Z

    .line 10
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->onCreate()V

    .line 11
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result p0

    if-nez p0, :cond_2

    .line 12
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    :cond_2
    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/f/b/f;Z)V
    .locals 3

    .line 3
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_1

    if-nez p2, :cond_0

    .line 4
    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Ljava/lang/String;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    goto :goto_0

    .line 5
    :cond_0
    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 6
    :cond_1
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_1
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result p2

    if-eqz p2, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    invoke-interface {p2, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_1

    .line 8
    :cond_2
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/s;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p1

    sput-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 9
    iget-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Ai:Z

    if-eqz p1, :cond_3

    .line 10
    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 11
    :cond_3
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 p2, 0x4

    if-ne p1, p2, :cond_4

    .line 12
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ab()V

    .line 13
    :cond_4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const p2, 0xff08

    invoke-virtual {p1, p2}, Landroid/os/Handler;->removeMessages(I)V

    .line 14
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const-wide/16 v0, 0x1f4

    invoke-virtual {p0, p2, v0, v1}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public getFileName()Ljava/lang/String;
    .locals 2

    .line 1
    sget p0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge p0, v1, :cond_0

    .line 2
    iget-object p0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    aget-object p0, p0, v0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    return-object p0

    :cond_0
    const-string p0, ""

    return-object p0
.end method

.method public ka(I)V
    .locals 3

    const/4 v0, 0x0

    const/4 v1, 0x1

    if-eqz p1, :cond_2

    const/4 v2, 0x2

    if-eq p1, v1, :cond_1

    if-eq p1, v2, :cond_0

    goto :goto_0

    .line 1
    :cond_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    .line 2
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    .line 3
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    goto :goto_0

    .line 4
    :cond_1
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    .line 5
    iput v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    .line 6
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    goto :goto_0

    .line 7
    :cond_2
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    .line 8
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    .line 9
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 10
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_1
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_3

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 11
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->hd()I

    move-result v1

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    goto :goto_1

    :cond_3
    return-void
.end method

.method public la(I)V
    .locals 8

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const-string v3, "MUSIC_DATA"

    const-string v4, "/"

    const/4 v5, 0x4

    const/4 v6, 0x1

    const/4 v7, 0x0

    if-ne v2, v5, :cond_0

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v0, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v1

    invoke-virtual {v0, v7, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v0

    .line 4
    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    .line 5
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 7
    invoke-direct {p0, v7, v7}, Lcom/eckom/xtlibrary/b/f/d/U;->c(IZ)V

    .line 8
    iput-boolean v6, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Ai:Z

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/l;->Fk:Ljava/lang/String;

    invoke-static {p0, v3, p1, v5}, Lcom/eckom/xtlibrary/b/f/f/l;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    goto/16 :goto_0

    .line 10
    :cond_0
    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-eqz v2, :cond_1

    if-nez p1, :cond_1

    .line 11
    iget-object p0, v1, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_0

    .line 12
    :cond_1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-eqz v0, :cond_2

    add-int/lit8 p1, p1, -0x1

    .line 13
    :cond_2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-nez v1, :cond_4

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-eqz v1, :cond_4

    .line 14
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v2, v1, p1

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/U;->fileName:Ljava/lang/String;

    .line 15
    aget-object v1, v1, p1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Vh:Ljava/lang/String;

    .line 16
    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->oa(I)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v0

    if-nez v0, :cond_3

    .line 17
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v2, v2, p1

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iget v3, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    iget v4, v1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    add-int/2addr v4, v6

    invoke-direct {v0, v2, v3, v4, v1}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;IILcom/eckom/xtlibrary/b/f/b/g;)V

    .line 18
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object p1, v2, p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    invoke-virtual {v1, p0, v0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 19
    :cond_3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 20
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 21
    :cond_4
    iput-boolean v7, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Ai:Z

    .line 22
    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 23
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v0, v0, p1

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 24
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v0, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v1

    invoke-virtual {v0, v7, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_5

    .line 25
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v1, v6, :cond_5

    .line 26
    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 27
    :cond_5
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {v1, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 28
    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    .line 29
    invoke-direct {p0, v7, v7}, Lcom/eckom/xtlibrary/b/f/d/U;->c(IZ)V

    .line 30
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mContext:Landroid/content/Context;

    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/l;->Fk:Ljava/lang/String;

    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    invoke-static {p0, v3, p1, v0}, Lcom/eckom/xtlibrary/b/f/f/l;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    .line 31
    :goto_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_6
    :goto_1
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result p1

    if-eqz p1, :cond_7

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 32
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v0, :cond_6

    .line 33
    invoke-interface {p1, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_1

    :cond_7
    return-void
.end method

.method public onPause()V
    .locals 2

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->wg:Z

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0x83

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ca(I)V

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_0

    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    if-lez v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff09

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_0
    return-void
.end method

.method public onResume()V
    .locals 4

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->wg:Z

    .line 2
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-eqz v1, :cond_0

    .line 3
    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v1, 0x3

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ca(I)V

    .line 5
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0x510

    const/16 v2, 0xff

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0x203

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v1, 0xff07

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x29a

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public pb()V
    .locals 1

    .line 1
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Qh:Z

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->Qh:Z

    return-void

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ve()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v0, 0xff02

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_1
    return-void
.end method

.method public rb()V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ve()Z

    move-result v0

    if-nez v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mHandler:Landroid/os/Handler;

    const v0, 0xff03

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_0
    return-void
.end method

.method public seekTo(I)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->seekTo(I)V

    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/U;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    return-void
.end method

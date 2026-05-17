.class public Lcom/eckom/xtlibrary/b/f/d/ba;
.super Lcom/eckom/xtlibrary/b/f/d/a;
.source "MusicModel.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/b/f/d/ba$a;
    }
.end annotation

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

.field private static ji:Lcom/eckom/xtlibrary/b/f/d/ba;


# instance fields
.field private Ai:Z

.field private Bi:Lcom/eckom/xtlibrary/b/f/d/ba$a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/eckom/xtlibrary/b/f/d/ba<",
            "TP;>.a;"
        }
    .end annotation
.end field

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

.field public mMediaPlayer:Landroid/media/MediaPlayer;

.field private qi:F

.field r:Lcom/eckom/xtlibrary/b/f/b/g;

.field private wg:Z

.field private wi:I

.field private xi:Z

.field private yi:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    const/4 v0, 0x0

    .line 2
    sput-boolean v0, Lcom/eckom/xtlibrary/b/f/d/ba;->Gd:Z

    const/4 v0, 0x0

    .line 3
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->ji:Lcom/eckom/xtlibrary/b/f/d/ba;

    return-void
.end method

.method private constructor <init>()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/a;-><init>()V

    const/4 v0, 0x7

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->yi:I

    const/4 v0, 0x0

    .line 3
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Qh:Z

    .line 4
    iget v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->yi:I

    new-array v1, v1, [J

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHints:[J

    .line 5
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->xi:Z

    .line 6
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Rh:Z

    .line 7
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Sh:Z

    .line 8
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Cg:Z

    .line 9
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Eh:I

    .line 10
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->wi:I

    const/high16 v1, 0x3f800000    # 1.0f

    .line 11
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    .line 12
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Th:Z

    .line 13
    new-instance v1, Landroid/os/Handler;

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/V;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/V;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const/4 v1, -0x1

    .line 14
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Uh:I

    .line 15
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Ai:Z

    const-string v0, ""

    .line 16
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Vh:Ljava/lang/String;

    return-void
.end method

.method private Ab(Ljava/lang/String;)V
    .locals 7

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v3, 0x2

    invoke-virtual {v0, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 10
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v3, 0x1

    invoke-virtual {v0, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 11
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v3, 0x7

    invoke-virtual {v0, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const/4 v2, 0x0

    .line 12
    :try_start_1
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_1

    .line 13
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getFileName()Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 14
    :cond_1
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    const-string v4, " "

    if-eqz v3, :cond_2

    .line 15
    :try_start_2
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 16
    :cond_2
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_3

    .line 17
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 18
    :cond_3
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    sget-object v4, Ljava/nio/charset/StandardCharsets;->UTF_16LE:Ljava/nio/charset/Charset;

    invoke-virtual {v3, v4}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    .line 19
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    sget-object v4, Ljava/nio/charset/StandardCharsets;->UTF_16LE:Ljava/nio/charset/Charset;

    invoke-virtual {v3, v4}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    .line 20
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    sget-object v4, Ljava/nio/charset/StandardCharsets;->UTF_16LE:Ljava/nio/charset/Charset;

    invoke-virtual {v3, v4}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    .line 21
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    invoke-direct {p0, v2, v3}, Lcom/eckom/xtlibrary/b/f/d/ba;->b(ILjava/lang/String;)V

    .line 22
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/W;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/W;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V

    const-wide/16 v5, 0x64

    invoke-virtual {v3, v4, v5, v6}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 23
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/X;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/X;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V

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
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    sget-object v4, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    invoke-direct {p0, v3, v4, v5, p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 26
    invoke-virtual {v0}, Landroid/media/MediaMetadataRetriever;->getEmbeddedPicture()[B

    move-result-object p1

    if-eqz p1, :cond_5

    .line 27
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    array-length v4, p1

    invoke-static {p1, v2, v4}, Landroid/graphics/BitmapFactory;->decodeByteArray([BII)Landroid/graphics/Bitmap;

    move-result-object p1

    iput-object p1, v3, Lcom/eckom/xtlibrary/b/f/f/s;->Fb:Landroid/graphics/Bitmap;

    goto :goto_1

    .line 28
    :cond_4
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getFileName()Ljava/lang/String;

    move-result-object v2

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 29
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    invoke-virtual {v2, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 30
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

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
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getFileName()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 33
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    invoke-virtual {v0, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 34
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

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
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {v3, v4, v1, v5}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 13
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v3, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 14
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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

    const/4 v1, 0x1

    add-int/2addr v0, v1

    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    const-string v4, "."

    invoke-virtual {v3, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v3

    invoke-virtual {p1, v0, v3}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object p1

    move v0, v2

    move v3, v0

    .line 18
    :goto_0
    sget-object v4, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v5, v4, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge v0, v5, :cond_3

    .line 19
    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v4, v4, v0

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_2

    move v3, v0

    :cond_2
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 20
    :cond_3
    sput v3, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 21
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p1, v3}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 22
    invoke-direct {p0, v2, v2, v1}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(IZZ)V

    .line 23
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p1

    const/high16 v0, 0x3f000000    # 0.5f

    invoke-virtual {p1, v0, v0}, Landroid/media/MediaPlayer;->setVolume(FF)V

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    goto :goto_0

    .line 3
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p1

    const/high16 v0, 0x3f800000    # 1.0f

    invoke-virtual {p1, v0, v0}, Landroid/media/MediaPlayer;->setVolume(FF)V

    .line 4
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    :goto_0
    return-void
.end method

.method private Pa(I)Z
    .locals 1

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Uh:I

    if-ne v0, p1, :cond_0

    const/4 p0, 0x1

    return p0

    .line 2
    :cond_0
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Uh:I

    const/4 p0, 0x0

    return p0
.end method

.method private Re()V
    .locals 4

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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

    add-int/2addr v1, v3

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    :cond_0
    const/4 v0, 0x0

    .line 4
    invoke-direct {p0, v0, v0, v3}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(IZZ)V

    :cond_1
    return-void
.end method

.method private Sa()V
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/f/s;->Sa()V

    return-void
.end method

.method private Se()V
    .locals 4

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    invoke-direct {p0, v0, v3, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(IZZ)V

    :cond_1
    return-void
.end method

.method private Ue()Ljava/lang/String;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    return-object p0
.end method

.method private Ve()Z
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff02

    invoke-virtual {v0, v1}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v0

    if-nez v0, :cond_1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

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
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getDuration()I

    move-result v0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getCurrentPosition()I

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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Nb()Ljava/lang/String;

    move-result-object v1

    .line 5
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_3

    const-string v1, ""

    .line 6
    :cond_3
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const v4, 0x9f00

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

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
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v4, 0x303

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

    move-result v5

    if-eqz v5, :cond_5

    move v2, v6

    :cond_5
    or-int/2addr v0, v2

    invoke-virtual {v3, v4, v7, v0, v1}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v0, 0x9e06

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

.method private Ze()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Vh:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_0

    return-void

    .line 2
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Vh:Ljava/lang/String;

    invoke-virtual {v0, v1, v2, v3}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/ba;I)I
    .locals 0

    .line 3
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Eh:I

    return p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/ba;)Landroid/os/Handler;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    return-object p0
.end method

.method private a(IZZ)V
    .locals 7

    .line 19
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    monitor-enter v0

    .line 20
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    if-eqz v1, :cond_d

    .line 21
    array-length v2, v1

    if-lez v2, :cond_d

    .line 22
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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

    .line 23
    aget v6, v1, p1

    sput v6, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 24
    invoke-direct {p0, v5, p3}, Lcom/eckom/xtlibrary/b/f/d/ba;->d(IZ)Z

    move-result v5

    if-eqz v5, :cond_1

    .line 25
    sget-object v5, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput p1, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    move v5, v4

    goto :goto_1

    :cond_1
    add-int/lit8 p1, p1, -0x1

    move v5, v4

    goto :goto_0

    .line 26
    :cond_2
    :goto_1
    sget-object v6, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v6, v6, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-eqz v6, :cond_5

    if-ne p1, p2, :cond_5

    add-int/lit8 v2, v2, -0x1

    :goto_2
    if-le v2, v3, :cond_4

    .line 27
    aget p1, v1, v2

    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 28
    invoke-direct {p0, v5, p3}, Lcom/eckom/xtlibrary/b/f/d/ba;->d(IZ)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 29
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    goto :goto_3

    :cond_3
    add-int/lit8 v2, v2, -0x1

    move v5, v4

    goto :goto_2

    :cond_4
    :goto_3
    if-ne v2, v3, :cond_5

    .line 30
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Tb()V

    .line 31
    :cond_5
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    if-ne p1, p2, :cond_d

    .line 32
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v4, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    .line 33
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    aget p1, v1, p1

    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 34
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Tb()V

    goto :goto_8

    :cond_6
    if-le v3, v2, :cond_7

    move v3, v2

    :cond_7
    move p2, p1

    move p1, v3

    :goto_4
    if-ge p1, v2, :cond_9

    .line 35
    aget v5, v1, p1

    sput v5, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 36
    invoke-direct {p0, p2, p3}, Lcom/eckom/xtlibrary/b/f/d/ba;->d(IZ)Z

    move-result p2

    if-eqz p2, :cond_8

    .line 37
    sget-object p2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput p1, p2, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    move p2, v4

    goto :goto_5

    :cond_8
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_4

    .line 38
    :cond_9
    :goto_5
    sget-object v5, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-eqz v5, :cond_c

    if-ne p1, v2, :cond_c

    move p1, v4

    :goto_6
    if-ge p1, v3, :cond_b

    .line 39
    aget v5, v1, p1

    sput v5, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 40
    invoke-direct {p0, p2, p3}, Lcom/eckom/xtlibrary/b/f/d/ba;->d(IZ)Z

    move-result p2

    if-eqz p2, :cond_a

    .line 41
    sget-object p2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput p1, p2, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    goto :goto_7

    :cond_a
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_6

    :cond_b
    :goto_7
    if-ne p1, v3, :cond_c

    .line 42
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Tb()V

    .line 43
    :cond_c
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    if-ne p1, v2, :cond_d

    .line 44
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    add-int/lit8 v2, v2, -0x1

    iput v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    .line 45
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    aget p1, v1, p1

    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 46
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Tb()V

    .line 47
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

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/ba;ILjava/lang/String;)V
    .locals 0

    .line 4
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/d/ba;->b(ILjava/lang/String;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/ba;Ljava/lang/String;)V
    .locals 0

    .line 5
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->Bb(Ljava/lang/String;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/ba;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->L(Z)V

    return-void
.end method

.method private a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 2

    .line 48
    new-instance v0, Landroid/content/Intent;

    const-string v1, "com.tw.music.info"

    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v1, "musicTitle"

    .line 49
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicaArtist"

    .line 50
    invoke-virtual {v0, p1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicAlbum"

    .line 51
    invoke-virtual {v0, p1, p3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicPath"

    .line 52
    invoke-virtual {v0, p1, p4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 53
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    return-void
.end method

.method static synthetic access$200()Lcom/eckom/xtlibrary/b/f/f/s;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    return-object v0
.end method

.method static synthetic access$400()Ljava/util/ArrayList;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    return-object v0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/ba;Ljava/lang/String;)I
    .locals 0

    .line 3
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->zb(Ljava/lang/String;)I

    move-result p0

    return p0
.end method

.method private b(ILjava/lang/String;)V
    .locals 12

    const/4 v0, 0x0

    const/16 v1, 0x510

    const/4 v2, 0x4

    const/4 v3, 0x0

    if-nez p2, :cond_0

    .line 16
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    shl-int/2addr p1, v2

    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_5

    .line 17
    :cond_0
    iget v4, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Eh:I

    and-int/lit8 v5, v4, 0x1

    const-string v6, "UTF-16"

    const/4 v7, 0x1

    if-ne v5, v7, :cond_2

    .line 18
    :try_start_0
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 19
    :catch_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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

    .line 20
    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    .line 21
    :catch_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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

    .line 22
    :try_start_2
    invoke-virtual {p2, v10}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    :catch_2
    if-nez v0, :cond_5

    .line 23
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

    .line 24
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Eh:I

    and-int/2addr p0, v11

    if-ne p0, v11, :cond_6

    .line 25
    :try_start_4
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_4

    :catch_4
    move v7, v3

    .line 26
    :cond_6
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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

    .line 27
    :try_start_5
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_5

    :catch_5
    if-nez v0, :cond_9

    .line 28
    :try_start_6
    invoke-virtual {p2, v10}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_6

    :catch_6
    move v7, v8

    :cond_9
    if-nez v0, :cond_a

    .line 29
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Eh:I

    and-int/2addr p0, v11

    if-ne p0, v11, :cond_a

    .line 30
    :try_start_7
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_7

    :catch_7
    move v7, v3

    .line 31
    :cond_a
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/ba;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Rh:Z

    return p0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/ba;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Rh:Z

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/ba;)I
    .locals 0

    .line 2
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Eh:I

    return p0
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/ba;Z)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->mute(Z)V

    return-void
.end method

.method private d(IZ)Z
    .locals 4

    .line 3
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    const/4 v1, -0x1

    if-le v0, v1, :cond_3

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge v0, v2, :cond_3

    .line 4
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    sget v1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    aget-object v0, v0, v1

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 5
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ed:Ljava/util/ArrayList;

    const/4 v1, 0x1

    if-eqz v0, :cond_2

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_2

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ed:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_2

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 7
    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v2, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    if-eqz p2, :cond_1

    .line 8
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->pb()V

    goto :goto_0

    .line 9
    :cond_1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->rb()V

    :goto_0
    return v1

    .line 10
    :cond_2
    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/b/f/d/ba;->zb(Ljava/lang/String;)I

    move-result p2

    if-nez p2, :cond_3

    .line 11
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->seekTo(I)V

    .line 12
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Va()V

    .line 13
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const p2, 0xff09

    invoke-virtual {p1, p2}, Landroid/os/Handler;->removeMessages(I)V

    .line 14
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, p2, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return v1

    :cond_3
    const/4 p0, 0x0

    return p0
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/ba;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->wg:Z

    return p0
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/ba;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Cg:Z

    return p1
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/ba;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->xi:Z

    return p0
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/ba;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Sh:Z

    return p1
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/f/d/ba;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Sa()V

    return-void
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/f/d/ba;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Qh:Z

    return p1
.end method

.method private fd()Ljava/lang/String;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    return-object p0
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/b/f/d/ba;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Xe()V

    return-void
.end method

.method private getCurrentPosition()I
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result p0

    return p0
.end method

.method private getDuration()I
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->getDuration()I

    move-result p0

    return p0
.end method

.method public static getInstant()Lcom/eckom/xtlibrary/b/f/d/ba;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->ji:Lcom/eckom/xtlibrary/b/f/d/ba;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/d/ba;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->ji:Lcom/eckom/xtlibrary/b/f/d/ba;

    .line 3
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->ji:Lcom/eckom/xtlibrary/b/f/d/ba;

    return-object v0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/b/f/d/ba;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Cg:Z

    return p0
.end method

.method private hd()I
    .locals 3

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    const/4 v1, 0x1

    if-nez v0, :cond_0

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-ne p0, v1, :cond_0

    const/4 p0, 0x0

    return p0

    .line 2
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    const/4 v2, 0x2

    if-nez v0, :cond_1

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-ne p0, v2, :cond_1

    return v1

    .line 3
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    if-ne v0, v1, :cond_2

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    if-ne p0, v1, :cond_2

    return v2

    :cond_2
    const/4 p0, -0x1

    return p0
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/b/f/d/ba;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Sh:Z

    return p0
.end method

.method private isPlaying()Z
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result p0

    return p0
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/b/f/d/ba;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ze()V

    return-void
.end method

.method static synthetic k(Lcom/eckom/xtlibrary/b/f/d/ba;)Z
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

    move-result p0

    return p0
.end method

.method static synthetic l(Lcom/eckom/xtlibrary/b/f/d/ba;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Re()V

    return-void
.end method

.method static synthetic m(Lcom/eckom/xtlibrary/b/f/d/ba;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Se()V

    return-void
.end method

.method private mute(Z)V
    .locals 1

    if-eqz p1, :cond_0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p1

    const/4 v0, 0x0

    invoke-virtual {p1, v0, v0}, Landroid/media/MediaPlayer;->setVolume(FF)V

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    goto :goto_0

    .line 3
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p1

    const/high16 v0, 0x3f800000    # 1.0f

    invoke-virtual {p1, v0, v0}, Landroid/media/MediaPlayer;->setVolume(FF)V

    .line 4
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    :goto_0
    return-void
.end method

.method static synthetic n(Lcom/eckom/xtlibrary/b/f/d/ba;)Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    return-object p0
.end method

.method private onCreate()V
    .locals 3

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Th:Z

    .line 2
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-nez v1, :cond_0

    .line 3
    sget-boolean v1, Lcom/eckom/xtlibrary/b/f/d/ba;->Gd:Z

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    invoke-static {v1, v0, v2}, Lcom/eckom/xtlibrary/b/f/f/s;->a(ZZLandroid/content/Context;)Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v0

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const-string v2, "MusicModel"

    invoke-virtual {v0, v2, v1}, Landroid/tw/john/TWUtil;->addHandler(Ljava/lang/String;Landroid/os/Handler;)V

    .line 5
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->xi:Z

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->z(Z)V

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ta(Ljava/lang/String;)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 8
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/s;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v0

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/l;->Fk:Ljava/lang/String;

    const-string v2, "MUSIC_DATA"

    invoke-static {v0, v2, v1}, Lcom/eckom/xtlibrary/b/f/f/l;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)I

    move-result v0

    const/4 v1, 0x4

    if-ne v0, v1, :cond_1

    .line 10
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 11
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget v1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 12
    :cond_1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 13
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->zb(Ljava/lang/String;)I

    move-result v0

    if-nez v0, :cond_2

    .line 14
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->seekTo(I)V

    :cond_2
    return-void
.end method

.method private reset()V
    .locals 2

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    const/4 v0, 0x0

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 3
    new-instance v0, Landroid/media/MediaPlayer;

    invoke-direct {v0}, Landroid/media/MediaPlayer;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/Z;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/Z;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V

    invoke-virtual {v0, v1}, Landroid/media/MediaPlayer;->setOnCompletionListener(Landroid/media/MediaPlayer$OnCompletionListener;)V

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/aa;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/aa;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V

    invoke-virtual {v0, v1}, Landroid/media/MediaPlayer;->setOnErrorListener(Landroid/media/MediaPlayer$OnErrorListener;)V

    return-void
.end method

.method private zb(Ljava/lang/String;)I
    .locals 2

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->stop()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->reset()V

    .line 4
    :try_start_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0, p1}, Landroid/media/MediaPlayer;->setDataSource(Ljava/lang/String;)V

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->prepare()V
    :try_end_0
    .catch Ljava/lang/IllegalArgumentException; {:try_start_0 .. :try_end_0} :catch_3
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_2
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const/4 p0, 0x0

    return p0

    :catch_0
    const/4 p0, -0x4

    return p0

    :catch_1
    const/4 p0, -0x3

    return p0

    :catch_2
    const/4 p0, -0x2

    return p0

    :catch_3
    const/4 p0, -0x1

    return p0
.end method


# virtual methods
.method public Ab()V
    .locals 2

    const/4 v0, 0x4

    .line 35
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Pa(I)Z

    .line 36
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 37
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Pa(I)Z

    move-result p0

    const/4 v1, 0x0

    if-eqz p0, :cond_2

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_1

    .line 3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    add-int/2addr v2, v0

    iput v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v2, p0, :cond_0

    .line 4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 5
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 6
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 7
    :cond_2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_4

    .line 8
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v0, p0, :cond_3

    .line 9
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 10
    :cond_3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 11
    :cond_4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :goto_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Pa(I)Z

    move-result p0

    const/4 v0, 0x0

    if-eqz p0, :cond_2

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_1

    .line 3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v1, p0, :cond_0

    .line 4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 5
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 6
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 7
    :cond_2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lez p0, :cond_4

    .line 8
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    move-result p0

    if-lt v1, p0, :cond_3

    .line 9
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 10
    :cond_3
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 11
    :cond_4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :goto_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/Y;

    invoke-direct {v1, p0, p1}, Lcom/eckom/xtlibrary/b/f/d/Y;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;Ljava/lang/String;)V

    const-wide/16 p0, 0x5dc

    invoke-virtual {v0, v1, p0, p1}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    return-void
.end method

.method public Eb()V
    .locals 2

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Th:Z

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ua()V

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
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

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
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Nb()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Xb()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v2, v3}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 7
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v2, v1, :cond_3

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v3, 0x2

    if-ne v2, v3, :cond_3

    .line 10
    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->fileName:Ljava/lang/String;

    invoke-direct {v2, v4, v3, v1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;IILcom/eckom/xtlibrary/b/f/b/g;)V

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 11
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Vh:Ljava/lang/String;

    invoke-virtual {v0, v1, v2, v3}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 12
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 13
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

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
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v1, 0x4

    if-ne v0, v1, :cond_4

    .line 19
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ab()V

    .line 20
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

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
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff08

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 23
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_6
    return-void
.end method

.method public Hb()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, 0x3a98

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public Ib()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, -0x2710

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public Kb()Landroid/graphics/Bitmap;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->Fb:Landroid/graphics/Bitmap;

    return-object p0
.end method

.method public Mb()Landroid/media/MediaPlayer;
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Landroid/media/MediaPlayer;

    invoke-direct {v0}, Landroid/media/MediaPlayer;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 3
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    return-object p0
.end method

.method public Nb()Ljava/lang/String;
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    return-object p0
.end method

.method public Pb()V
    .locals 3

    const/high16 v0, 0x3f800000    # 1.0f

    .line 1
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Th:Z

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Bi:Lcom/eckom/xtlibrary/b/f/d/ba$a;

    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Landroid/os/AsyncTask;->cancel(Z)Z

    .line 4
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    const/4 v0, 0x0

    .line 6
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 7
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v0}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 8
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const-string v1, "MusicModel"

    invoke-virtual {p0, v1}, Landroid/tw/john/TWUtil;->removeHandler(Ljava/lang/String;)V

    .line 9
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/f/s;->close()V

    .line 10
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    return-void
.end method

.method public Tb()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff11

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public Ua()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v1

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result v1

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->pause()V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Xe()V

    :cond_0
    return-void
.end method

.method public Ub()V
    .locals 10

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v2, :cond_1

    .line 3
    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 4
    :cond_1
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v3, v2, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->mDuration:I

    invoke-interface {v1, v3, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    .line 5
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v4, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v2, v3, v4}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    .line 6
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ue()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->fd()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Nb()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Kb()Landroid/graphics/Bitmap;

    move-result-object v6

    sget-object v7, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v8, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    sget v2, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    sget-object v9, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v9, v9, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    add-int/2addr v9, v2

    move-object v2, v1

    invoke-interface/range {v2 .. v9}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 7
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    .line 8
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->hd()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    .line 9
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Landroid/media/MediaPlayer;)V

    .line 10
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v2

    if-eqz v2, :cond_0

    .line 11
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v2

    invoke-virtual {v2}, Landroid/media/MediaPlayer;->getAudioSessionId()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->B(I)V

    goto :goto_0

    :cond_2
    return-void
.end method

.method public Va()V
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-eqz v0, :cond_0

    const/4 v1, 0x1

    .line 2
    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->isPlaying()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->start()V

    const/4 v0, 0x0

    .line 5
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Th:Z

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 8
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ab(Ljava/lang/String;)V

    .line 9
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Xe()V

    .line 10
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "playMusic:playerVolume:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(F)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "MusicModel"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 11
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->qi:F

    invoke-virtual {v0, p0, p0}, Landroid/media/MediaPlayer;->setVolume(FF)V

    :cond_1
    return-void
.end method

.method public Wb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Bi:Lcom/eckom/xtlibrary/b/f/d/ba$a;

    const/4 v1, 0x0

    if-eqz v0, :cond_0

    const/4 v2, 0x1

    .line 2
    invoke-virtual {v0, v2}, Landroid/os/AsyncTask;->cancel(Z)Z

    .line 3
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Bi:Lcom/eckom/xtlibrary/b/f/d/ba$a;

    .line 4
    :cond_0
    new-instance v0, Lcom/eckom/xtlibrary/b/f/d/ba$a;

    invoke-direct {v0, p0, v1}, Lcom/eckom/xtlibrary/b/f/d/ba$a;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;Lcom/eckom/xtlibrary/b/f/d/V;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Bi:Lcom/eckom/xtlibrary/b/f/d/ba$a;

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Bi:Lcom/eckom/xtlibrary/b/f/d/ba$a;

    const-string v0, "/mnt/sdcard"

    filled-new-array {v0}, [Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p0, v0}, Landroid/os/AsyncTask;->execute([Ljava/lang/Object;)Landroid/os/AsyncTask;

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
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 14
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 15
    :cond_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-eqz p1, :cond_1

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    const/4 v0, 0x3

    if-ne p1, v0, :cond_1

    .line 16
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Sa()V

    .line 17
    :cond_1
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-nez p1, :cond_2

    .line 18
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Pb()V

    :cond_2
    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/g/a;Landroid/content/Context;)V
    .locals 2

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-nez v0, :cond_1

    .line 7
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

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
    sput-boolean p2, Lcom/eckom/xtlibrary/b/f/d/ba;->Gd:Z

    const-string p2, "persist.sys.media.sdcardscan"

    .line 9
    invoke-static {p2, v1}, Landroid/os/SystemProperties;->getBoolean(Ljava/lang/String;Z)Z

    move-result p2

    iput-boolean p2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->xi:Z

    .line 10
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->onCreate()V

    .line 11
    :cond_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result p0

    if-nez p0, :cond_2

    .line 12
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    :cond_2
    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/f/b/f;Z)V
    .locals 3

    .line 4
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_1

    if-nez p2, :cond_0

    .line 5
    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Ljava/lang/String;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    goto :goto_0

    .line 6
    :cond_0
    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    sput-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 7
    :cond_1
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_1
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result p2

    if-eqz p2, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    invoke-interface {p2, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_1

    .line 9
    :cond_2
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/s;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p1

    sput-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 10
    iget-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Ai:Z

    if-eqz p1, :cond_3

    .line 11
    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 12
    :cond_3
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 p2, 0x4

    if-ne p1, p2, :cond_4

    .line 13
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ab()V

    .line 14
    :cond_4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const p2, 0xff08

    invoke-virtual {p1, p2}, Landroid/os/Handler;->removeMessages(I)V

    .line 15
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

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
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    .line 2
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    .line 3
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    goto :goto_0

    .line 4
    :cond_1
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    .line 5
    iput v2, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    .line 6
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    goto :goto_0

    .line 7
    :cond_2
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    .line 8
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    .line 9
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 10
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

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
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->hd()I

    move-result v1

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    goto :goto_1

    :cond_3
    return-void
.end method

.method public la(I)V
    .locals 7

    .line 1
    :try_start_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const-string v1, "MUSIC_DATA"

    const-string v2, "/"

    const/4 v3, 0x4

    const/4 v4, 0x1

    const/4 v5, 0x0

    if-ne v0, v3, :cond_0

    .line 2
    :try_start_1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v6, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v6, v2}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v2

    invoke-virtual {v0, v5, v2}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v0

    .line 4
    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    .line 5
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v2}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 7
    invoke-direct {p0, v5, v5, v4}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(IZZ)V

    .line 8
    iput-boolean v4, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Ai:Z

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/l;->Fk:Ljava/lang/String;

    invoke-static {p0, v1, p1, v3}, Lcom/eckom/xtlibrary/b/f/f/l;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    goto/16 :goto_0

    .line 10
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-eqz v0, :cond_1

    if-nez p1, :cond_1

    .line 11
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_0

    .line 12
    :cond_1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-eqz v0, :cond_2

    add-int/lit8 p1, p1, -0x1

    .line 13
    :cond_2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-nez v0, :cond_3

    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-eqz v0, :cond_3

    .line 14
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v0, v0, p1

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->fileName:Ljava/lang/String;

    .line 15
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v0, v0, p1

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Vh:Ljava/lang/String;

    .line 16
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object p1, v1, p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    add-int/2addr v2, v4

    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {v0, p1, v1, v2, v3}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;IILcom/eckom/xtlibrary/b/f/b/g;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 17
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Vh:Ljava/lang/String;

    invoke-virtual {p1, v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 18
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 19
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->r:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p0, p1, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 20
    :cond_3
    iput-boolean v5, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Ai:Z

    .line 21
    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 22
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v0, v0, p1

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 23
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v3, v2}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v2

    invoke-virtual {v0, v5, v2}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_4

    .line 24
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v2, v4, :cond_4

    .line 25
    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v2, v3}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 26
    :cond_4
    sget-object v2, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {v2, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 27
    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    .line 28
    invoke-direct {p0, v5, v5, v4}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(IZZ)V

    .line 29
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mContext:Landroid/content/Context;

    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/l;->Fk:Ljava/lang/String;

    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    invoke-static {p0, v1, p1, v0}, Lcom/eckom/xtlibrary/b/f/f/l;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    .line 30
    :goto_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :cond_5
    :goto_1
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result p1

    if-eqz p1, :cond_6

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 31
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v0, :cond_5

    .line 32
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-interface {p1, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    goto :goto_1

    :catch_0
    move-exception p0

    .line 33
    invoke-virtual {p0}, Ljava/lang/Exception;->printStackTrace()V

    :cond_6
    return-void
.end method

.method public onPause()V
    .locals 2

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->wg:Z

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0x83

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ca(I)V

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_0

    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    if-lez v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff09

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_0
    return-void
.end method

.method public onResume()V
    .locals 4

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->wg:Z

    .line 2
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    if-eqz v1, :cond_0

    .line 3
    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v1, 0x3

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->ca(I)V

    .line 5
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0x112

    const/16 v2, 0xff

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0x510

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 7
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0x203

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v1, 0xff07

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x29a

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public pb()V
    .locals 1

    .line 1
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Qh:Z

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Qh:Z

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ve()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v0, 0xff02

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_1
    return-void
.end method

.method public rb()V
    .locals 1

    .line 1
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Qh:Z

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->Qh:Z

    return-void

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Ve()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba;->mHandler:Landroid/os/Handler;

    const v0, 0xff03

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_1
    return-void
.end method

.method public seekTo(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->getDuration()I

    move-result v0

    if-lez p1, :cond_0

    if-lez v0, :cond_0

    if-ge p1, v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0, p1}, Landroid/media/MediaPlayer;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/ba;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->w(Z)V

    return-void
.end method

.class public Lcom/eckom/xtlibrary/b/f/d/L;
.super Lcom/eckom/xtlibrary/b/f/d/a;
.source "MusicIjkID3Model.java"


# static fields
.field private static final hi:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/g/a;",
            ">;"
        }
    .end annotation
.end field

.field private static jd:Lcom/eckom/xtlibrary/b/f/f/t;

.field private static ji:Lcom/eckom/xtlibrary/b/f/d/L;


# instance fields
.field private Cg:Z

.field private Eh:I

.field private final Hh:I

.field private final Ih:I

.field private final Jh:I

.field private final Kh:I

.field private final Lh:I

.field private final Mh:I

.field private final Nh:I

.field private final Oh:I

.field private final Ph:I

.field private Qh:Z

.field private Rh:Z

.field private Sh:Z

.field Th:Z

.field private Uh:I

.field private Vh:Ljava/lang/String;

.field public Wh:Z

.field public Xh:Z

.field Yc:Lcom/eckom/xtlibrary/b/f/b/e;

.field public Yh:Z

.field public Zh:Z

.field public _h:Z

.field public di:Z

.field public ei:Z

.field public fi:Z

.field private fileName:Ljava/lang/String;

.field public gi:Z

.field private final isForward:Z

.field private final li:Z

.field private mContext:Landroid/content/Context;

.field private final mHandler:Landroid/os/Handler;

.field public mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

.field private mi:Lcom/eckom/xtlibrary/b/f/f/c;

.field private ni:Lcom/eckom/xtlibrary/b/f/a/c;

.field private qi:F

.field private final ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

.field private final ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

.field private ui:Lcom/eckom/xtlibrary/b/f/c/a;

.field private wg:Z


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    const/4 v0, 0x0

    .line 2
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->ji:Lcom/eckom/xtlibrary/b/f/d/L;

    return-void
.end method

.method private constructor <init>()V
    .locals 4

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/a;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Eh:I

    .line 3
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Hh:I

    const/4 v1, 0x1

    .line 4
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Ih:I

    const/4 v2, 0x2

    .line 5
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Jh:I

    const/4 v3, 0x3

    .line 6
    iput v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Kh:I

    .line 7
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Lh:I

    .line 8
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Mh:I

    const/4 v2, 0x4

    .line 9
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Nh:I

    const/16 v2, 0x8

    .line 10
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Oh:I

    const/16 v2, 0x80

    .line 11
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Ph:I

    .line 12
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Qh:Z

    .line 13
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Rh:Z

    .line 14
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Sh:Z

    .line 15
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Cg:Z

    const-string v2, "persist.media.forward"

    .line 16
    invoke-static {v2, v1}, Landroid/os/SystemProperties;->getInt(Ljava/lang/String;I)I

    move-result v2

    if-ne v2, v1, :cond_0

    goto :goto_0

    :cond_0
    move v1, v0

    :goto_0
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    const-string v1, "persist.sys.media.sdcardscan"

    .line 17
    invoke-static {v1, v0}, Landroid/os/SystemProperties;->getBoolean(Ljava/lang/String;Z)Z

    move-result v1

    iput-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->li:Z

    const/high16 v1, 0x3f800000    # 1.0f

    .line 18
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    .line 19
    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/B;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/B;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

    .line 20
    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/C;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/C;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

    .line 21
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Th:Z

    .line 22
    new-instance v1, Landroid/os/Handler;

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/F;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/F;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const/4 v1, -0x1

    .line 23
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Uh:I

    const-string v1, ""

    .line 24
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Vh:Ljava/lang/String;

    .line 25
    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/z;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/z;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ui:Lcom/eckom/xtlibrary/b/f/c/a;

    .line 26
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Wh:Z

    .line 27
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Xh:Z

    .line 28
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yh:Z

    .line 29
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Zh:Z

    .line 30
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->_h:Z

    .line 31
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->di:Z

    .line 32
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ei:Z

    .line 33
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->fi:Z

    .line 34
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->gi:Z

    return-void
.end method

.method private Ab(Ljava/lang/String;)V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const-string v1, ""

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 3
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 4
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fb:Landroid/graphics/Bitmap;

    if-eqz v2, :cond_0

    const/4 v2, 0x0

    .line 5
    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fb:Landroid/graphics/Bitmap;

    .line 6
    :cond_0
    new-instance v0, Landroid/media/MediaMetadataRetriever;

    invoke-direct {v0}, Landroid/media/MediaMetadataRetriever;-><init>()V

    const v2, 0x104000e

    .line 7
    :try_start_0
    invoke-virtual {v0, p1}, Landroid/media/MediaMetadataRetriever;->setDataSource(Ljava/lang/String;)V

    const/16 v3, 0xc

    .line 8
    invoke-virtual {v0, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3

    if-eqz v3, :cond_7

    .line 9
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v4, 0x2

    invoke-virtual {v0, v4}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 10
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v4, 0x1

    invoke-virtual {v0, v4}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 11
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v4, 0x7

    invoke-virtual {v0, v4}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const/4 v3, 0x0

    .line 12
    :try_start_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_1

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    invoke-virtual {v4, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_2

    .line 13
    :cond_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    new-instance v5, Ljava/io/File;

    invoke-direct {v5, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v5

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 14
    :cond_2
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    const-string v5, " "

    if-nez v4, :cond_3

    :try_start_2
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    invoke-virtual {v4, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_4

    .line 15
    :cond_3
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 16
    :cond_4
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_5

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    invoke-virtual {v4, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_6

    .line 17
    :cond_5
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 18
    :cond_6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    invoke-direct {p0, v3, v1}, Lcom/eckom/xtlibrary/b/f/d/L;->b(ILjava/lang/String;)V

    .line 19
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/G;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/G;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    const-wide/16 v5, 0x64

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 20
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/H;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/H;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    const-wide/16 v5, 0xc8

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_0

    :catch_0
    move-exception v1

    :try_start_3
    const-string v4, "MusicIjkID3Model"

    .line 21
    invoke-static {v1}, Landroid/util/Log;->getStackTraceString(Ljava/lang/Throwable;)Ljava/lang/String;

    move-result-object v1

    invoke-static {v4, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 22
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    invoke-direct {p0, v1, v4, v5, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 23
    invoke-virtual {v0}, Landroid/media/MediaMetadataRetriever;->getEmbeddedPicture()[B

    move-result-object v1

    if-eqz v1, :cond_8

    .line 24
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    array-length v5, v1

    invoke-static {v1, v3, v5}, Landroid/graphics/BitmapFactory;->decodeByteArray([BII)Landroid/graphics/Bitmap;

    move-result-object v1

    iput-object v1, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Fb:Landroid/graphics/Bitmap;

    goto :goto_1

    .line 25
    :cond_7
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    new-instance v3, Ljava/io/File;

    invoke-direct {v3, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v3

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 26
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    invoke-virtual {v3, v2}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 27
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    invoke-virtual {v3, v2}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 28
    :cond_8
    :goto_1
    invoke-virtual {v0}, Landroid/media/MediaMetadataRetriever;->release()V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    goto :goto_2

    .line 29
    :catch_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object p1

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 30
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    invoke-virtual {v0, v2}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 31
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v2}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object p0

    iput-object p0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    :goto_2
    return-void
.end method

.method private Bb(Ljava/lang/String;)V
    .locals 9

    .line 4
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 5
    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    :cond_0
    const-string v0, "/storage/emulated/0/"

    .line 6
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const-string v0, "/storage/emulated/0"

    const-string v1, "mnt/sdcard"

    .line 7
    invoke-virtual {p1, v0, v1}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;

    .line 8
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v1, "/"

    .line 9
    invoke-virtual {p1, v1}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v1

    const/4 v2, 0x0

    invoke-virtual {p1, v2, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    .line 10
    new-instance v4, Lcom/eckom/xtlibrary/b/f/b/g;

    const-string v0, "Playlist"

    invoke-direct {v4, v0, v2, v2, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 11
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    iget-boolean v7, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    new-instance v8, Lcom/eckom/xtlibrary/b/f/d/K;

    invoke-direct {v8, p0, p1}, Lcom/eckom/xtlibrary/b/f/d/K;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;Ljava/lang/String;)V

    invoke-static/range {v3 .. v8}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Ljava/util/ArrayList;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    return-void
.end method

.method private Cb(Ljava/lang/String;)V
    .locals 2

    .line 24
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/w;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/w;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    const-string p0, "/data/tw/.like"

    invoke-static {p1, p0, v0, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    return-void
.end method

.method private L(Z)V
    .locals 1

    if-eqz p1, :cond_0

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/high16 v0, 0x3f000000    # 0.5f

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    goto :goto_0

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/high16 v0, 0x3f800000    # 1.0f

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 4
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    :goto_0
    return-void
.end method

.method private Pa(I)Z
    .locals 1

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Uh:I

    if-ne v0, p1, :cond_0

    const/4 p0, 0x1

    return p0

    .line 2
    :cond_0
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Uh:I

    const/4 p0, 0x0

    return p0
.end method

.method private Qa(I)V
    .locals 5

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-ne p1, v2, :cond_c

    .line 2
    iget-object p1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    const/4 v1, 0x3

    const/4 v3, 0x2

    const/4 v4, 0x1

    if-eq v2, v4, :cond_5

    if-eq v2, v3, :cond_1

    if-eq v2, v1, :cond_0

    goto/16 :goto_0

    .line 3
    :cond_0
    iget-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Pj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 5
    iget-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Sj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_0

    .line 6
    :cond_1
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->zj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0}, Ljava/util/LinkedHashMap;->size()I

    move-result v0

    if-lez v0, :cond_2

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->zj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 8
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0}, Ljava/util/LinkedHashMap;->size()I

    move-result v0

    if-lez v0, :cond_3

    .line 9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 10
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_4

    .line 11
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    iget v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v0, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :cond_4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 13
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 15
    :cond_5
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Lj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0}, Ljava/util/LinkedHashMap;->size()I

    move-result v0

    if-lez v0, :cond_6

    .line 16
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Lj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 17
    :cond_6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Kj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0}, Ljava/util/LinkedHashMap;->size()I

    move-result v0

    if-lez v0, :cond_7

    .line 18
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Kj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 19
    :cond_7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_8

    .line 20
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    iget v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v0, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 21
    :cond_8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 22
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 23
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 24
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, p1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    .line 25
    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-nez p1, :cond_c

    if-eq v0, v4, :cond_b

    if-eq v0, v3, :cond_a

    if-eq v0, v1, :cond_9

    goto :goto_1

    .line 26
    :cond_9
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Qb()V

    goto :goto_1

    .line 27
    :cond_a
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Sb()V

    goto :goto_1

    .line 28
    :cond_b
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Rb()V

    :cond_c
    :goto_1
    return-void
.end method

.method private Re()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    if-eqz v1, :cond_1

    array-length v1, v1

    if-lez v1, :cond_1

    .line 2
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    const/4 v2, 0x2

    if-eq v1, v2, :cond_0

    .line 3
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    add-int/lit8 v1, v1, 0x1

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    :cond_0
    const/4 v0, 0x0

    .line 4
    invoke-direct {p0, v0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Se()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    if-eqz v1, :cond_1

    array-length v1, v1

    if-lez v1, :cond_1

    .line 2
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    const/4 v2, 0x2

    const/4 v3, 0x1

    if-eq v1, v2, :cond_0

    .line 3
    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    sub-int/2addr v1, v3

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    :cond_0
    const/4 v0, 0x0

    .line 4
    invoke-direct {p0, v0, v3}, Lcom/eckom/xtlibrary/b/f/d/L;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Ue()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    return-object p0
.end method

.method static synthetic Vb()Lcom/eckom/xtlibrary/b/f/f/t;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    return-object v0
.end method

.method private Ve()Z
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff02

    invoke-virtual {v0, v1}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v0

    if-nez v0, :cond_1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

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

.method private We()V
    .locals 8

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "initializePlayListRecord: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "MusicIjkID3Model"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ye()V

    .line 3
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v4, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    iget-boolean v6, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    new-instance v7, Lcom/eckom/xtlibrary/b/f/d/A;

    invoke-direct {v7, p0}, Lcom/eckom/xtlibrary/b/f/d/A;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    invoke-static/range {v2 .. v7}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Ljava/util/ArrayList;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    return-void
.end method

.method private Xe()V
    .locals 8

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->getDuration()I

    move-result v0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->getCurrentPosition()I

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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Nb()Ljava/lang/String;

    move-result-object v1

    .line 5
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_3

    const-string v1, ""

    .line 6
    :cond_3
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const v4, 0x9f00

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

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
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/16 v4, 0x303

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

    move-result v5

    if-eqz v5, :cond_5

    move v2, v6

    :cond_5
    or-int/2addr v0, v2

    invoke-virtual {v3, v4, v7, v0, v1}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v0, 0x9e06

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

.method private Ye()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz v0, :cond_0

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->canRead()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    if-eqz v0, :cond_0

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->seekTo(I)V

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0x9e06

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 7
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x7d0

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_0
    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/L;I)I
    .locals 0

    .line 3
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Eh:I

    return p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;
    .locals 0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    return-object p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/L;ILjava/lang/String;)V
    .locals 0

    .line 6
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/d/L;->b(ILjava/lang/String;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/L;IZ)V
    .locals 0

    .line 7
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/d/L;->c(IZ)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/L;Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/L;Ljava/lang/String;)V
    .locals 0

    .line 5
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->Ab(Ljava/lang/String;)V

    return-void
.end method

.method private a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 2

    .line 18
    new-instance v0, Landroid/content/Intent;

    const-string v1, "com.tw.music.info"

    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v1, "musicTitle"

    .line 19
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicaArtist"

    .line 20
    invoke-virtual {v0, p1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicAlbum"

    .line 21
    invoke-virtual {v0, p1, p3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p1, "musicPath"

    .line 22
    invoke-virtual {v0, p1, p4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 23
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z
    .locals 0

    .line 4
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Cg:Z

    return p1
.end method

.method static synthetic access$1000()Ljava/util/ArrayList;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    return-object v0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    return-object p0
.end method

.method private b(ILjava/lang/String;)V
    .locals 11

    const/4 v0, 0x0

    const/16 v1, 0x510

    const/4 v2, 0x4

    const/4 v3, 0x0

    if-nez p2, :cond_0

    .line 19
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    shl-int/2addr p1, v2

    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_6

    .line 20
    :cond_0
    iget v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Eh:I

    and-int/lit8 v5, v4, 0x1

    const/4 v6, 0x1

    if-ne v5, v6, :cond_2

    .line 21
    :try_start_0
    sget-object p0, Ljava/nio/charset/StandardCharsets;->UTF_16:Ljava/nio/charset/Charset;

    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    move-result-object v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 22
    :catch_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    shl-int/2addr p1, v2

    or-int/2addr p1, v3

    if-nez v0, :cond_1

    goto :goto_0

    :cond_1
    array-length v3, v0

    :goto_0
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_6

    :cond_2
    and-int/lit8 v5, v4, 0x2

    const/4 v7, 0x2

    if-ne v5, v7, :cond_4

    :try_start_1
    const-string p0, "Unicode"

    .line 23
    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    .line 24
    :catch_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    shl-int/2addr p1, v2

    or-int/2addr p1, v6

    if-nez v0, :cond_3

    goto :goto_1

    :cond_3
    array-length v3, v0

    :goto_1
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto :goto_6

    :cond_4
    and-int/lit8 v5, v4, 0x4

    const/4 v6, 0x3

    const-string v8, "GB2312"

    const-string v9, "GBK"

    const/16 v10, 0x80

    if-ne v5, v2, :cond_8

    .line 25
    :try_start_2
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    :catch_2
    if-nez v0, :cond_5

    .line 26
    :try_start_3
    invoke-virtual {p2, v8}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_3

    goto :goto_2

    :cond_5
    move v6, v7

    :catch_3
    :goto_2
    if-nez v0, :cond_6

    .line 27
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Eh:I

    and-int/2addr p0, v10

    if-ne p0, v10, :cond_6

    .line 28
    :try_start_4
    sget-object p0, Ljava/nio/charset/StandardCharsets;->UTF_16:Ljava/nio/charset/Charset;

    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    move-result-object v0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_4

    :catch_4
    move v6, v3

    .line 29
    :cond_6
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    shl-int/2addr p1, v2

    or-int/2addr p1, v6

    if-nez v0, :cond_7

    goto :goto_3

    :cond_7
    array-length v3, v0

    :goto_3
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto :goto_6

    :cond_8
    const/16 v5, 0x8

    and-int/2addr v4, v5

    if-ne v4, v5, :cond_c

    .line 30
    :try_start_5
    invoke-virtual {p2, v8}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_5

    :catch_5
    if-nez v0, :cond_9

    .line 31
    :try_start_6
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_6

    goto :goto_4

    :catch_6
    move-exception v4

    .line 32
    invoke-virtual {v4}, Ljava/lang/Exception;->printStackTrace()V

    :goto_4
    move v6, v7

    :cond_9
    if-nez v0, :cond_a

    .line 33
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Eh:I

    and-int/2addr p0, v10

    if-ne p0, v10, :cond_a

    .line 34
    :try_start_7
    sget-object p0, Ljava/nio/charset/StandardCharsets;->UTF_16:Ljava/nio/charset/Charset;

    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    move-result-object v0
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_7

    :catch_7
    move v6, v3

    .line 35
    :cond_a
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    shl-int/2addr p1, v2

    or-int/2addr p1, v6

    if-nez v0, :cond_b

    goto :goto_5

    :cond_b
    array-length v3, v0

    :goto_5
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    :cond_c
    :goto_6
    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/L;I)V
    .locals 0

    .line 3
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->Qa(I)V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/L;Ljava/lang/String;)V
    .locals 0

    .line 4
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->Bb(Ljava/lang/String;)V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Sh:Z

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/L;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Eh:I

    return p0
.end method

.method private c(IZ)V
    .locals 8

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    monitor-enter v0

    .line 4
    :try_start_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    if-eqz v1, :cond_d

    .line 5
    array-length v2, v1

    if-lez v2, :cond_d

    .line 6
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

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
    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v7, v1, p1

    iput v7, v6, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 8
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/L;->play(I)Z

    move-result v5

    if-eqz v5, :cond_1

    .line 9
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput p1, v5, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    move v5, v4

    goto :goto_1

    :cond_1
    add-int/lit8 p1, p1, -0x1

    move v5, v4

    goto :goto_0

    .line 10
    :cond_2
    :goto_1
    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v6, v6, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-eqz v6, :cond_5

    if-ne p1, p2, :cond_5

    add-int/lit8 v2, v2, -0x1

    :goto_2
    if-le v2, v3, :cond_4

    .line 11
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v6, v1, v2

    iput v6, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 12
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/L;->play(I)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 13
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    goto :goto_3

    :cond_3
    add-int/lit8 v2, v2, -0x1

    move v5, v4

    goto :goto_2

    :cond_4
    :goto_3
    if-ne v2, v3, :cond_5

    .line 14
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Tb()V

    .line 15
    :cond_5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    if-ne p1, p2, :cond_d

    .line 16
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v4, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    .line 17
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    aget p2, v1, p2

    iput p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 18
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Tb()V

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
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v6, v1, p1

    iput v6, v5, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 20
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/b/f/d/L;->play(I)Z

    move-result p2

    if-eqz p2, :cond_8

    .line 21
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    move p2, v4

    goto :goto_5

    :cond_8
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_4

    .line 22
    :cond_9
    :goto_5
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-eqz v5, :cond_c

    if-ne p1, v2, :cond_c

    move p1, v4

    :goto_6
    if-ge p1, v3, :cond_b

    .line 23
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v6, v1, p1

    iput v6, v5, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 24
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/b/f/d/L;->play(I)Z

    move-result p2

    if-eqz p2, :cond_a

    .line 25
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    goto :goto_7

    :cond_a
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_6

    :cond_b
    :goto_7
    if-ne p1, v3, :cond_c

    .line 26
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Tb()V

    .line 27
    :cond_c
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    if-ne p1, v2, :cond_d

    .line 28
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    add-int/lit8 v2, v2, -0x1

    iput v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    .line 29
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    aget p2, v1, p2

    iput p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 30
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Tb()V

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

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Rh:Z

    return p1
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/L;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->L(Z)V

    return-void
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/L;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->wg:Z

    return p0
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/L;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Xe()V

    return-void
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/L;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->mute(Z)V

    return-void
.end method

.method private f(Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 9

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_3

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    if-eqz p1, :cond_2

    .line 3
    iget-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_1

    array-length v3, v2

    if-lez v3, :cond_1

    .line 4
    array-length v3, v2

    const/4 v4, 0x0

    :goto_1
    if-ge v4, v3, :cond_1

    aget-object v5, v2, v4

    if-eqz v5, :cond_0

    .line 5
    iget-object v6, v5, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    if-eqz v6, :cond_0

    .line 6
    iget-object v7, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v8, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v7, v6, v8}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v6

    iput-boolean v6, v5, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    :cond_0
    add-int/lit8 v4, v4, 0x1

    goto :goto_1

    .line 7
    :cond_1
    invoke-interface {v1, p1}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 8
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "onCurrentCList:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v1}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    goto :goto_0

    :cond_2
    const-string v1, "record == null"

    .line 9
    invoke-static {v1}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    goto :goto_0

    :cond_3
    return-void
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/f/d/L;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Cg:Z

    return p0
.end method

.method private fd()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    return-object p0
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/b/f/d/L;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Sh:Z

    return p0
.end method

.method private getCurrentPosition()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getCurrentPosition()I

    move-result p0

    return p0
.end method

.method private getDuration()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getDuration()I

    move-result p0

    return p0
.end method

.method public static getInstant()Lcom/eckom/xtlibrary/b/f/d/L;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->ji:Lcom/eckom/xtlibrary/b/f/d/L;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/d/L;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->ji:Lcom/eckom/xtlibrary/b/f/d/L;

    .line 3
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->ji:Lcom/eckom/xtlibrary/b/f/d/L;

    return-object v0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    return-object p0
.end method

.method private hd()I
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    const/4 v2, 0x1

    if-nez v1, :cond_0

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-ne v0, v2, :cond_0

    const/4 p0, 0x0

    return p0

    .line 2
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    const/4 v3, 0x2

    if-nez v1, :cond_1

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-ne v0, v3, :cond_1

    return v2

    .line 3
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    if-ne v0, v2, :cond_2

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-ne p0, v2, :cond_2

    return v3

    :cond_2
    const/4 p0, -0x1

    return p0
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/b/f/d/L;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Rh:Z

    return p0
.end method

.method private isPlaying()Z
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->isPlaying()Z

    move-result p0

    return p0
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/c/a;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ui:Lcom/eckom/xtlibrary/b/f/c/a;

    return-object p0
.end method

.method static synthetic k(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/a/c;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    return-object p0
.end method

.method static synthetic l(Lcom/eckom/xtlibrary/b/f/d/L;)Z
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

    move-result p0

    return p0
.end method

.method private m(II)I
    .locals 4

    .line 2
    :goto_0
    invoke-static {}, Ljava/lang/Math;->random()D

    move-result-wide v0

    int-to-double v2, p2

    mul-double/2addr v0, v2

    double-to-int v0, v0

    if-nez v0, :cond_0

    if-nez p1, :cond_0

    goto :goto_0

    :cond_0
    move p1, v0

    :goto_1
    if-ge p1, p2, :cond_2

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    aget v1, v1, p1

    if-nez v1, :cond_1

    goto :goto_2

    :cond_1
    add-int/lit8 p1, p1, 0x1

    goto :goto_1

    :cond_2
    :goto_2
    if-ne p1, p2, :cond_4

    const/4 p1, 0x1

    :goto_3
    if-ge p1, v0, :cond_4

    .line 4
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    aget p2, p2, p1

    if-nez p2, :cond_3

    goto :goto_4

    :cond_3
    add-int/lit8 p1, p1, 0x1

    goto :goto_3

    :cond_4
    :goto_4
    return p1
.end method

.method static synthetic m(Lcom/eckom/xtlibrary/b/f/d/L;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Re()V

    return-void
.end method

.method private mute(Z)V
    .locals 1

    if-eqz p1, :cond_0

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/4 v0, 0x0

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    goto :goto_0

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/high16 v0, 0x3f800000    # 1.0f

    invoke-virtual {p1, v0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    .line 4
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    :goto_0
    return-void
.end method

.method static synthetic n(Lcom/eckom/xtlibrary/b/f/d/L;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Se()V

    return-void
.end method

.method static synthetic o(Lcom/eckom/xtlibrary/b/f/d/L;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    return p0
.end method

.method private onCreate()V
    .locals 3

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Th:Z

    const/4 v0, 0x0

    .line 2
    invoke-static {v0}, Ltv/danmaku/ijk/media/player/IjkMediaPlayer;->loadLibrariesOnce(Ltv/danmaku/ijk/media/player/IjkLibLoader;)V

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-nez v0, :cond_0

    .line 4
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/t;->open()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v0

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    .line 5
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v0, :cond_1

    .line 6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const-string v2, "MusicIjkID3Model"

    invoke-virtual {v0, v2, v1}, Landroid/tw/john/TWUtil;->addHandler(Ljava/lang/String;Landroid/os/Handler;)V

    .line 7
    :cond_1
    new-instance v0, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    invoke-direct {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;-><init>(Landroid/content/Context;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setOnCompletionListener(Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;)V

    .line 9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setOnErrorListener(Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;)V

    .line 10
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/b/e;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    .line 11
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/c;->getInstance()Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/e;)V

    .line 13
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->We()V

    .line 14
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ob()V

    return-void
.end method

.method private play(I)Z
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    const/4 v2, -0x1

    if-le v1, v2, :cond_0

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v3, v2, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge v1, v3, :cond_0

    .line 2
    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v3, v2, v1

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v3, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    .line 3
    aget-object v1, v2, v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 4
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz v0, :cond_0

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->canRead()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 6
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->seekTo(I)V

    .line 7
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v0, 0xff09

    invoke-virtual {p1, v0}, Landroid/os/Handler;->removeMessages(I)V

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

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

    .line 32
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->Pa(I)Z

    .line 33
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 34
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Bb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-nez v2, :cond_0

    return-void

    .line 2
    :cond_0
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Cb()V
    .locals 4

    const/4 v0, 0x1

    .line 1
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->Pa(I)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    add-int/2addr v2, v0

    iput v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 3
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_2

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt v1, v0, :cond_1

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v1, 0x0

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 6
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 7
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/e;->uc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 8
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 9
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "/storage/"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/f/a/c;->Na(Ljava/lang/String;)V

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Lj:Ljava/util/LinkedHashMap;

    invoke-virtual {v1}, Ljava/util/LinkedHashMap;->size()I

    move-result v1

    if-lez v1, :cond_3

    .line 11
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Lj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, v0}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :cond_3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Kj:Ljava/util/LinkedHashMap;

    invoke-virtual {v1}, Ljava/util/LinkedHashMap;->size()I

    move-result v1

    if-lez v1, :cond_4

    .line 13
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Kj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, v0}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    :cond_4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_6

    .line 15
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :cond_5
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_6

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 16
    iget-object v3, v2, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-static {v3, v0}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_5

    .line 17
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    :cond_6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 19
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 20
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 21
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 22
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 23
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Db()V
    .locals 4

    const/4 v0, 0x2

    .line 1
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->Pa(I)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    add-int/lit8 v1, v1, 0x1

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 3
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_2

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt v1, v0, :cond_1

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v1, 0x0

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 6
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 7
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/e;->vc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 8
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 9
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "/storage/"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/f/a/c;->Na(Ljava/lang/String;)V

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->zj:Ljava/util/LinkedHashMap;

    invoke-virtual {v1}, Ljava/util/LinkedHashMap;->size()I

    move-result v1

    if-lez v1, :cond_3

    .line 11
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->zj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, v0}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :cond_3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yj:Ljava/util/LinkedHashMap;

    invoke-virtual {v1}, Ljava/util/LinkedHashMap;->size()I

    move-result v1

    if-lez v1, :cond_4

    .line 13
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, v0}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    :cond_4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_6

    .line 15
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :cond_5
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_6

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 16
    iget-object v3, v2, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-static {v3, v0}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_5

    .line 17
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    :cond_6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 19
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 20
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 21
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 22
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 23
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Ea(Ljava/lang/String;)V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/J;

    invoke-direct {v1, p0, p1}, Lcom/eckom/xtlibrary/b/f/d/J;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;Ljava/lang/String;)V

    const-wide/16 p0, 0x5dc

    invoke-virtual {v0, v1, p0, p1}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    return-void
.end method

.method public Eb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    const-string v1, "/mnt/sdcard"

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/a/c;->Na(Ljava/lang/String;)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Pj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Sj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 5
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 7
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Fb()V
    .locals 1

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Th:Z

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ua()V

    return-void
.end method

.method public Ga(Ljava/lang/String;)V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    invoke-static {v0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p0, p1, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Gb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_3

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v2, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Ljava/lang/String;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    const/4 v2, 0x0

    .line 5
    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_0

    .line 6
    :cond_0
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Nb()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Lb()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    .line 7
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    const/4 v2, 0x1

    .line 8
    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_1

    .line 9
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    const-string v1, "/data/tw/.like"

    invoke-virtual {v0, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_3

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v2, 0x4

    if-ne v1, v2, :cond_2

    .line 13
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_3
    return-void
.end method

.method public Hb()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, 0x3a98

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public Ib()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, -0x2710

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public Jb()I
    .locals 3
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "SdCardPath"
        }
    .end annotation

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v1, "usb"

    invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    const/4 v1, 0x0

    if-eqz v0, :cond_0

    return v1

    .line 2
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v2, "extsd"

    invoke-virtual {v0, v2}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const/4 p0, 0x1

    return p0

    .line 3
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v0, "/mnt/sdcard/./iNand"

    invoke-virtual {p0, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p0

    if-eqz p0, :cond_2

    const/4 p0, 0x2

    return p0

    :cond_2
    return v1
.end method

.method public Kb()Landroid/graphics/Bitmap;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Fb:Landroid/graphics/Bitmap;

    return-object p0
.end method

.method public Lb()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    return-object p0
.end method

.method public Nb()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    return-object p0
.end method

.method public Ob()V
    .locals 6
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "SdCardPath"
        }
    .end annotation

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/f/a/c;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/a/c;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    .line 2
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->li:Z

    if-eqz v0, :cond_0

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Wb()V

    goto :goto_0

    :cond_0
    const-string v0, "/mnt/sdcard/iNand"

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ga(Ljava/lang/String;)V

    .line 5
    :goto_0
    new-instance v0, Ljava/io/File;

    const-string v1, "/storage"

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/u;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/u;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    invoke-virtual {v0, v2}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    const/4 v2, 0x0

    if-eqz v0, :cond_1

    .line 6
    array-length v3, v0

    move v4, v2

    :goto_1
    if-ge v4, v3, :cond_1

    aget-object v5, v0, v4

    .line 7
    invoke-virtual {v5}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/L;->pa(Ljava/lang/String;)V

    add-int/lit8 v4, v4, 0x1

    goto :goto_1

    .line 8
    :cond_1
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/v;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/v;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    invoke-virtual {v0, v1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    if-eqz v0, :cond_2

    .line 9
    array-length v1, v0

    :goto_2
    if-ge v2, v1, :cond_2

    aget-object v3, v0, v2

    .line 10
    invoke-virtual {v3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {p0, v3}, Lcom/eckom/xtlibrary/b/f/d/L;->qa(Ljava/lang/String;)V

    add-int/lit8 v2, v2, 0x1

    goto :goto_2

    :cond_2
    const-string v0, "/data/tw/.like"

    .line 11
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->Cb(Ljava/lang/String;)V

    return-void
.end method

.method public Pb()V
    .locals 3

    const/high16 v0, 0x3f800000    # 1.0f

    .line 1
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Th:Z

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->release(Z)V

    const/4 v0, 0x0

    .line 5
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    .line 6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    invoke-virtual {v1, v0}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 7
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const-string v2, "MusicIjkID3Model"

    invoke-virtual {v1, v2}, Landroid/tw/john/TWUtil;->removeHandler(Ljava/lang/String;)V

    .line 8
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/f/t;->close()V

    .line 9
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    .line 10
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/a/c;->qc()V

    return-void
.end method

.method public Qb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_0

    array-length v2, v2

    if-lez v2, :cond_0

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_0
    return-void
.end method

.method public Ra()Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 6

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    new-array v0, v0, [Lcom/eckom/xtlibrary/b/f/b/f;

    const/4 v1, 0x0

    .line 2
    :goto_0
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v2

    if-ge v1, v2, :cond_0

    .line 3
    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v4, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    const/4 v5, 0x1

    invoke-direct {v2, v3, v4, v5}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;Z)V

    aput-object v2, v0, v1

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 4
    :cond_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    invoke-virtual {v2, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    .line 6
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    iput v0, v2, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    .line 7
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    return-object p0
.end method

.method public Rb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_0

    array-length v2, v2

    if-lez v2, :cond_0

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_0
    return-void
.end method

.method public Sb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_0

    array-length v2, v2

    if-lez v2, :cond_0

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_0
    return-void
.end method

.method public Tb()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff11

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public Ua()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getCurrentPosition()I

    move-result v1

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->pause()V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Xe()V

    :cond_0
    return-void
.end method

.method public Ub()V
    .locals 10

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_0

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 2
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v2, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mDuration:I

    invoke-interface {v1, v3, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    .line 3
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v2, v4, v3}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    .line 4
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ue()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->fd()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Nb()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Kb()Landroid/graphics/Bitmap;

    move-result-object v6

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v2, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v8, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget v9, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    move-object v2, v1

    invoke-interface/range {v2 .. v9}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 5
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    .line 6
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->hd()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    .line 7
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v2}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getAudioSessionId()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->B(I)V

    goto :goto_0

    .line 8
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Va()V
    .locals 10

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v0, :cond_0

    const/4 v1, 0x1

    .line 2
    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->isPlaying()Z

    move-result v0

    if-nez v0, :cond_2

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->start()V

    const/4 v0, 0x0

    .line 5
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Th:Z

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff0a

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 10
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    move-object v2, v1

    check-cast v2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 11
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ue()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->fd()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Nb()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Kb()Landroid/graphics/Bitmap;

    move-result-object v6

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget v9, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-interface/range {v2 .. v9}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    goto :goto_0

    .line 12
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 13
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "playMusic:playerVolume:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(F)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "MusicIjkID3Model"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->qi:F

    invoke-virtual {v0, p0, p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setVolume(FF)V

    :cond_2
    return-void
.end method

.method public Wb()V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/I;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/I;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    .line 2
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/g/a;)V
    .locals 3

    .line 13
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 14
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz p1, :cond_0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    const/4 v2, 0x3

    if-ne v1, v2, :cond_0

    .line 15
    invoke-static {v0, p1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/e;Landroid/tw/john/TWUtil;)V

    .line 16
    :cond_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-nez p1, :cond_1

    .line 17
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Pb()V

    :cond_1
    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/g/a;Landroid/content/Context;)V
    .locals 1

    .line 8
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-nez v0, :cond_0

    .line 9
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    .line 10
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->onCreate()V

    .line 11
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result p0

    if-nez p0, :cond_1

    .line 12
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    :cond_1
    return-void
.end method

.method public a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 5

    .line 24
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "scanMediaID3 \u5f00\u59cb\u6574\u7406\uff1a"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "   ,\u53ef\u8bfb"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->canRead()Z

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    .line 25
    new-instance v0, Ljava/text/SimpleDateFormat;

    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v1

    const-string v2, "yyyy-MM-dd HH:mm:ss"

    invoke-direct {v0, v2, v1}, Ljava/text/SimpleDateFormat;-><init>(Ljava/lang/String;Ljava/util/Locale;)V

    .line 26
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "scanMediaID3:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v2, " currentTime="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    new-instance v2, Ljava/util/Date;

    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v3

    invoke-direct {v2, v3, v4}, Ljava/util/Date;-><init>(J)V

    invoke-virtual {v0, v2}, Ljava/text/SimpleDateFormat;->format(Ljava/util/Date;)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "MusicIjkID3Model"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const-string v0, "/mnt/sdcard"

    .line 27
    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 28
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/c;->wk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 29
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/c;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 30
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/c;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    goto/16 :goto_0

    :cond_0
    const-string v0, "/storage/usb"

    .line 31
    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v1

    const/16 v2, 0x9

    if-eqz v1, :cond_3

    .line 32
    invoke-virtual {p1, v2}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v0

    .line 33
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/c;->zk:Ljava/util/HashMap;

    invoke-virtual {v1, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-eqz v1, :cond_1

    .line 34
    invoke-virtual {v1}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 35
    :cond_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/c;->Bj:Ljava/util/HashMap;

    invoke-virtual {v1, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-eqz v1, :cond_2

    .line 36
    invoke-virtual {v1}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 37
    :cond_2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/c;->Cj:Ljava/util/HashMap;

    invoke-virtual {v1, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-eqz v0, :cond_6

    .line 38
    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    goto :goto_0

    .line 39
    :cond_3
    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_6

    .line 40
    invoke-virtual {p1, v2}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v0

    .line 41
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/c;->Ak:Ljava/util/HashMap;

    invoke-virtual {v1, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-eqz v1, :cond_4

    .line 42
    invoke-virtual {v1}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 43
    :cond_4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/c;->Nj:Ljava/util/HashMap;

    invoke-virtual {v1, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-eqz v1, :cond_5

    .line 44
    invoke-virtual {v1}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 45
    :cond_5
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mi:Lcom/eckom/xtlibrary/b/f/f/c;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/c;->Oj:Ljava/util/HashMap;

    invoke-virtual {v1, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-eqz v0, :cond_6

    .line 46
    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    :cond_6
    :goto_0
    if-eqz p2, :cond_8

    .line 47
    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-nez p2, :cond_7

    return-void

    .line 48
    :cond_7
    new-instance v0, Lcom/eckom/xtlibrary/b/f/d/y;

    invoke-direct {v0, p0, p2, p1}, Lcom/eckom/xtlibrary/b/f/d/y;-><init>(Lcom/eckom/xtlibrary/b/f/d/L;[Lcom/eckom/xtlibrary/b/f/b/f;Ljava/lang/String;)V

    .line 49
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    :cond_8
    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/f/b/f;Z)V
    .locals 3

    .line 5
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_1

    if-eqz p2, :cond_0

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    if-nez v0, :cond_0

    .line 7
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    goto :goto_0

    :cond_0
    if-nez p2, :cond_1

    .line 8
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {p2, v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result p2

    if-eqz p2, :cond_1

    .line 9
    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Ljava/lang/String;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    .line 10
    :cond_1
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_1
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result p2

    if-eqz p2, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v2, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    invoke-interface {p2, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_1

    .line 12
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p2

    iput-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 13
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 p2, 0x4

    if-eq p1, p2, :cond_3

    if-nez p1, :cond_5

    .line 14
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    const-string v0, "/data/tw/.like"

    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 15
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 16
    :cond_4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-ne v0, p2, :cond_5

    .line 17
    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ab()V

    :cond_5
    return-void
.end method

.method public ea(I)V
    .locals 5

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v1, 0x0

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    const/4 v1, 0x0

    .line 2
    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    .line 3
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v2, :cond_4

    .line 4
    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-lez v2, :cond_4

    .line 5
    new-array v3, v2, [I

    iput-object v3, v0, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    if-lt p1, v2, :cond_0

    move p1, v1

    .line 6
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    aput p1, v0, v1

    const/4 v0, 0x1

    if-le v2, v0, :cond_4

    add-int/lit8 v0, p1, 0x1

    :goto_0
    if-ge v0, v2, :cond_2

    sub-int v3, v0, p1

    .line 7
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    if-eqz v4, :cond_1

    .line 8
    invoke-direct {p0, p1, v2}, Lcom/eckom/xtlibrary/b/f/d/L;->m(II)I

    move-result v3

    .line 9
    :cond_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    aput v0, v4, v3

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_2
    :goto_1
    if-ge v1, p1, :cond_4

    add-int v0, v1, v2

    sub-int/2addr v0, p1

    .line 10
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    if-eqz v3, :cond_3

    .line 11
    invoke-direct {p0, p1, v2}, Lcom/eckom/xtlibrary/b/f/d/L;->m(II)I

    move-result v0

    .line 12
    :cond_3
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    aput v1, v3, v0

    add-int/lit8 v1, v1, 0x1

    goto :goto_1

    :cond_4
    return-void
.end method

.method public getFileName()Ljava/lang/String;
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, p0, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge v0, v1, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

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
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    .line 2
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    .line 3
    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    goto :goto_0

    .line 4
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    .line 5
    iput v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    .line 6
    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    goto :goto_0

    .line 7
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    .line 8
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    .line 9
    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    .line 10
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/L;->hi:Ljava/util/ArrayList;

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
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->hd()I

    move-result v1

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    goto :goto_1

    :cond_3
    return-void
.end method

.method public la(I)V
    .locals 8

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    const/4 v3, 0x4

    const/4 v4, 0x1

    if-ne v2, v4, :cond_0

    if-nez p1, :cond_0

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v2, v4, :cond_0

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-eq v2, v3, :cond_0

    .line 3
    iget-object p1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto/16 :goto_4

    .line 5
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v1, v4, :cond_1

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v0, v4, :cond_1

    add-int/lit8 p1, p1, -0x1

    .line 6
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-nez v2, :cond_f

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-eqz v2, :cond_f

    iget v5, v1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v5, v4, :cond_f

    if-eq v2, v3, :cond_f

    .line 7
    iget-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v6, v3, p1

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iput-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/L;->fileName:Ljava/lang/String;

    .line 8
    aget-object v3, v3, p1

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Vh:Ljava/lang/String;

    const/4 v3, 0x0

    const/4 v6, 0x2

    const/4 v7, 0x3

    if-ne v2, v7, :cond_5

    if-eqz v5, :cond_4

    if-eq v5, v6, :cond_3

    if-eq v5, v7, :cond_2

    goto/16 :goto_2

    .line 9
    :cond_2
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Uj:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    move-object v3, v0

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_2

    .line 10
    :cond_3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Vj:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    move-object v3, v0

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_2

    .line 11
    :cond_4
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tj:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    move-object v3, v0

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_2

    :cond_5
    if-ne v2, v6, :cond_9

    if-eqz v5, :cond_8

    if-eq v5, v6, :cond_7

    if-eq v5, v7, :cond_6

    move-object v0, v3

    goto :goto_0

    .line 12
    :cond_6
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Bj:Ljava/util/LinkedHashMap;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/ArrayList;

    goto :goto_0

    .line 13
    :cond_7
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Cj:Ljava/util/LinkedHashMap;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/ArrayList;

    goto :goto_0

    .line 14
    :cond_8
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Aj:Ljava/util/LinkedHashMap;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/ArrayList;

    :goto_0
    if-eqz v0, :cond_d

    .line 15
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, p1, :cond_d

    .line 16
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    move-object v3, v0

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    :cond_9
    if-ne v2, v4, :cond_d

    if-eqz v5, :cond_c

    if-eq v5, v6, :cond_b

    if-eq v5, v7, :cond_a

    move-object v0, v3

    goto :goto_1

    .line 17
    :cond_a
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Nj:Ljava/util/LinkedHashMap;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/ArrayList;

    goto :goto_1

    .line 18
    :cond_b
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Oj:Ljava/util/LinkedHashMap;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/ArrayList;

    goto :goto_1

    .line 19
    :cond_c
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Mj:Ljava/util/LinkedHashMap;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/ArrayList;

    :goto_1
    if-eqz v0, :cond_d

    .line 20
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, p1, :cond_d

    .line 21
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    move-object v3, v0

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_d
    :goto_2
    if-eqz v3, :cond_e

    .line 22
    iget-object v0, v3, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v0, :cond_e

    .line 23
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, v3, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 24
    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/b/g;->d(Lcom/eckom/xtlibrary/b/f/b/g;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 25
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto :goto_4

    .line 26
    :cond_e
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "setListItemPosition: "

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p1, ",record == null"

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "MusicIjkID3Model"

    invoke-static {p1, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_4

    .line 27
    :cond_f
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 28
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 29
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v2, v2, p1

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    .line 30
    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v2, 0x0

    if-ne v1, v3, :cond_10

    const-string v1, "/data/tw/.like"

    .line 31
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    goto :goto_3

    .line 32
    :cond_10
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_11

    .line 33
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v3, "/"

    invoke-virtual {v1, v3}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v3

    invoke-virtual {v1, v2, v3}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    .line 34
    :cond_11
    :goto_3
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    .line 35
    invoke-direct {p0, v2, v2}, Lcom/eckom/xtlibrary/b/f/d/L;->c(IZ)V

    :goto_4
    return-void
.end method

.method public onPause()V
    .locals 2

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->wg:Z

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/16 v1, 0x83

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/t;->ca(I)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    if-lez v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff09

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_0
    return-void
.end method

.method public onResume()V
    .locals 5

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->wg:Z

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mContext:Landroid/content/Context;

    sget-object v2, Lcom/eckom/xtlibrary/b/j/o;->pm:Ljava/lang/String;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Jb()I

    move-result v3

    const-string v4, "MUSIC_DATA"

    invoke-static {v1, v4, v2, v3}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v1, :cond_0

    .line 4
    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    .line 5
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/4 v1, 0x3

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/t;->ca(I)V

    .line 6
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v0, :cond_1

    const/16 v1, 0x510

    const/16 v2, 0xff

    .line 7
    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 8
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/16 v1, 0x203

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 9
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v1, 0xff07

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 10
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x96

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public pa(Ljava/lang/String;)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-void

    .line 3
    :cond_1
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-direct {v0, p1, v1, v2, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 4
    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    invoke-static {v0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    .line 5
    invoke-virtual {p0, p1, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 6
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p1, :cond_2

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 8
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    return-void
.end method

.method public pb()V
    .locals 1

    .line 1
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Qh:Z

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Qh:Z

    return-void

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ve()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v0, 0xff02

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_1
    return-void
.end method

.method public qa(Ljava/lang/String;)V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-void

    .line 3
    :cond_1
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v1, 0x2

    const/4 v2, 0x0

    invoke-direct {v0, p1, v1, v2, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 4
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v1

    if-nez v1, :cond_2

    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v1

    const/16 v3, 0x9

    if-le v1, v3, :cond_2

    .line 5
    invoke-virtual {p1, v3}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 6
    :cond_2
    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->isForward:Z

    invoke-static {v0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    .line 7
    invoke-virtual {p0, p1, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 8
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p1, :cond_3

    .line 9
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 10
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_4

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_4
    return-void
.end method

.method public ra(Ljava/lang/String;)V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/a/c;->La(Ljava/lang/String;)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    const/4 v1, 0x0

    move-object v2, v1

    .line 3
    :cond_0
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    const/4 v4, 0x1

    if-eqz v3, :cond_2

    .line 4
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 5
    iget-object v5, v3, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_0

    .line 6
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 7
    iget v5, v2, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v5, v4, :cond_1

    iget v5, v2, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v5, v4, :cond_1

    .line 8
    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 9
    :cond_1
    invoke-virtual {v3}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 10
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    goto :goto_0

    :cond_2
    const-string v0, ""

    .line 11
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-nez v3, :cond_4

    .line 12
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Lj:Ljava/util/LinkedHashMap;

    invoke-virtual {v3, v0}, Ljava/util/LinkedHashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 13
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Kj:Ljava/util/LinkedHashMap;

    invoke-virtual {v3, v0}, Ljava/util/LinkedHashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 14
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v3}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_3
    :goto_1
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_4

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 15
    iget-object v6, v5, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-static {v6, v0}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v6

    if-eqz v6, :cond_3

    .line 16
    invoke-virtual {v5}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 17
    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v6, v5}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    goto :goto_1

    .line 18
    :cond_4
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v5, v3, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    move-result v3

    if-lt v5, v3, :cond_5

    .line 19
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v3, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    move-result v5

    sub-int/2addr v5, v4

    iput v5, v3, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 20
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    if-gez v4, :cond_5

    const/4 v4, 0x0

    .line 21
    iput v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    :cond_5
    if-eqz v2, :cond_8

    .line 22
    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_8

    .line 23
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_6

    .line 24
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    iget v3, p1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 25
    :cond_6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/b/e;->vc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v2

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 26
    :goto_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result p1

    if-nez p1, :cond_8

    .line 27
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Lj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, v0}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 28
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Kj:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, v0}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 29
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_7

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    if-lez v0, :cond_7

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-ge v0, p1, :cond_7

    .line 30
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_3

    .line 31
    :cond_7
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_8
    :goto_3
    return-void
.end method

.method public rb()V
    .locals 1

    .line 1
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Qh:Z

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Qh:Z

    return-void

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Ve()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mHandler:Landroid/os/Handler;

    const v0, 0xff03

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_1
    return-void
.end method

.method public sa(Ljava/lang/String;)V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->ni:Lcom/eckom/xtlibrary/b/f/a/c;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/a/c;->Ma(Ljava/lang/String;)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 3
    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    const/4 v2, 0x0

    const/4 v3, 0x1

    if-eqz v1, :cond_2

    .line 4
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 5
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_0

    .line 6
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 7
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 8
    iget v6, v5, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v6, v3, :cond_1

    iget v6, v5, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v6, v3, :cond_1

    .line 9
    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 10
    :cond_1
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 11
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    goto :goto_0

    :cond_2
    const-string v4, ""

    move-object v5, v2

    .line 12
    :goto_0
    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_4

    .line 13
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->zj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0, v4}, Ljava/util/LinkedHashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0, v4}, Ljava/util/LinkedHashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 15
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_3
    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_4

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 16
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-static {v6, v4}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v6

    if-eqz v6, :cond_3

    .line 17
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 18
    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v6, v1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    goto :goto_1

    .line 19
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt v1, v0, :cond_5

    .line 20
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    sub-int/2addr v1, v3

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 21
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    if-gez v1, :cond_5

    const/4 v1, 0x0

    .line 22
    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    :cond_5
    if-eqz v5, :cond_8

    .line 23
    iget-object v0, v5, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_8

    .line 24
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_6

    .line 25
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    iget v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 26
    :cond_6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/b/e;->vc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 27
    :goto_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result p1

    if-nez p1, :cond_8

    .line 28
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->zj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0, v4}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 29
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yj:Ljava/util/LinkedHashMap;

    invoke-virtual {v0, v4}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 30
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_7

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    if-lez v0, :cond_7

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-ge v0, p1, :cond_7

    .line 31
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    iget v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_3

    .line 32
    :cond_7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 33
    :cond_8
    :goto_3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Db()V

    return-void
.end method

.method public seekTo(I)V
    .locals 0

    if-lez p1, :cond_0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {p0, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/L;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    return-void
.end method

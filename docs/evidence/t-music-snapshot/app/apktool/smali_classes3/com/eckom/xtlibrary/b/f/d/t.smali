.class public Lcom/eckom/xtlibrary/b/f/d/t;
.super Lcom/eckom/xtlibrary/b/f/d/a;
.source "MusicID3Model.java"


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

.field private static ji:Lcom/eckom/xtlibrary/b/f/d/t;


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

.field private mContext:Landroid/content/Context;

.field private final mHandler:Landroid/os/Handler;

.field public mMediaPlayer:Landroid/media/MediaPlayer;

.field private wg:Z


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    const/4 v0, 0x0

    .line 2
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->ji:Lcom/eckom/xtlibrary/b/f/d/t;

    return-void
.end method

.method private constructor <init>()V
    .locals 4

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/a;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Eh:I

    .line 3
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Hh:I

    const/4 v1, 0x1

    .line 4
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Ih:I

    const/4 v2, 0x2

    .line 5
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Jh:I

    const/4 v3, 0x3

    .line 6
    iput v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Kh:I

    .line 7
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Lh:I

    .line 8
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Mh:I

    const/4 v2, 0x4

    .line 9
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Nh:I

    const/16 v2, 0x8

    .line 10
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Oh:I

    const/16 v2, 0x80

    .line 11
    iput v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Ph:I

    .line 12
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Qh:Z

    .line 13
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Rh:Z

    .line 14
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Sh:Z

    .line 15
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Cg:Z

    const-string v2, "persist.media.forward"

    .line 16
    invoke-static {v2, v1}, Landroid/os/SystemProperties;->getInt(Ljava/lang/String;I)I

    move-result v2

    if-ne v2, v1, :cond_0

    goto :goto_0

    :cond_0
    move v1, v0

    :goto_0
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    .line 17
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Th:Z

    .line 18
    new-instance v1, Landroid/os/Handler;

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/n;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/n;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const/4 v1, -0x1

    .line 19
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Uh:I

    const-string v1, ""

    .line 20
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Vh:Ljava/lang/String;

    .line 21
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Wh:Z

    .line 22
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Xh:Z

    .line 23
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yh:Z

    .line 24
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Zh:Z

    .line 25
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->_h:Z

    .line 26
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->di:Z

    .line 27
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->ei:Z

    .line 28
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->fi:Z

    .line 29
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->gi:Z

    return-void
.end method

.method private Ab(Ljava/lang/String;)V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v4, 0x2

    invoke-virtual {v0, v4}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 10
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v4, 0x1

    invoke-virtual {v0, v4}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 11
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    const/4 v4, 0x7

    invoke-virtual {v0, v4}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v4

    iput-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const/4 v3, 0x0

    .line 12
    :try_start_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_1

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    invoke-virtual {v4, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_2

    .line 13
    :cond_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    new-instance v5, Ljava/io/File;

    invoke-direct {v5, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v5

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 14
    :cond_2
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    const-string v5, " "

    if-nez v4, :cond_3

    :try_start_2
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    invoke-virtual {v4, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_4

    .line 15
    :cond_3
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 16
    :cond_4
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_5

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    invoke-virtual {v4, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_6

    .line 17
    :cond_5
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 18
    :cond_6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    invoke-direct {p0, v3, v1}, Lcom/eckom/xtlibrary/b/f/d/t;->b(ILjava/lang/String;)V

    .line 19
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/o;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/o;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    const-wide/16 v5, 0x64

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 20
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    new-instance v4, Lcom/eckom/xtlibrary/b/f/d/p;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/d/p;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    const-wide/16 v5, 0xc8

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_0

    :catch_0
    move-exception v1

    :try_start_3
    const-string v4, "MusicID3Model"

    .line 21
    invoke-static {v1}, Landroid/util/Log;->getStackTraceString(Ljava/lang/Throwable;)Ljava/lang/String;

    move-result-object v1

    invoke-static {v4, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 22
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    invoke-direct {p0, v1, v4, v5, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 23
    invoke-virtual {v0}, Landroid/media/MediaMetadataRetriever;->getEmbeddedPicture()[B

    move-result-object v1

    if-eqz v1, :cond_8

    .line 24
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    array-length v5, v1

    invoke-static {v1, v3, v5}, Landroid/graphics/BitmapFactory;->decodeByteArray([BII)Landroid/graphics/Bitmap;

    move-result-object v1

    iput-object v1, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Fb:Landroid/graphics/Bitmap;

    goto :goto_1

    .line 25
    :cond_7
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    new-instance v3, Ljava/io/File;

    invoke-direct {v3, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v3

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 26
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    invoke-virtual {v3, v2}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v3

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 27
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

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
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object p1

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 30
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    invoke-virtual {v0, v2}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 31
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v2}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object p0

    iput-object p0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    :goto_2
    return-void
.end method

.method private Bb(Ljava/lang/String;)V
    .locals 9

    .line 3
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 4
    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    :cond_0
    const-string v0, "/storage/emulated/0/"

    .line 5
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const-string v0, "/storage/emulated/0"

    const-string v1, "mnt/sdcard"

    .line 6
    invoke-virtual {p1, v0, v1}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;

    .line 7
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v1, "/"

    .line 8
    invoke-virtual {p1, v1}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v1

    const/4 v2, 0x0

    invoke-virtual {p1, v2, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    .line 9
    new-instance v4, Lcom/eckom/xtlibrary/b/f/b/g;

    const-string v0, "Playlist"

    invoke-direct {v4, v0, v2, v2, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;III)V

    .line 10
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    iget-boolean v7, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    new-instance v8, Lcom/eckom/xtlibrary/b/f/d/d;

    invoke-direct {v8, p0, p1}, Lcom/eckom/xtlibrary/b/f/d/d;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)V

    invoke-static/range {v3 .. v8}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Ljava/util/ArrayList;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    return-void
.end method

.method private L(Z)V
    .locals 0

    if-eqz p1, :cond_0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    const/high16 p1, 0x3f000000    # 0.5f

    invoke-virtual {p0, p1, p1}, Landroid/media/MediaPlayer;->setVolume(FF)V

    goto :goto_0

    .line 2
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    const/high16 p1, 0x3f800000    # 1.0f

    invoke-virtual {p0, p1, p1}, Landroid/media/MediaPlayer;->setVolume(FF)V

    :goto_0
    return-void
.end method

.method private Pa(I)Z
    .locals 1

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Uh:I

    if-ne v0, p1, :cond_0

    const/4 p0, 0x1

    return p0

    .line 2
    :cond_0
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Uh:I

    const/4 p0, 0x0

    return p0
.end method

.method private Re()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    invoke-direct {p0, v0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Se()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    invoke-direct {p0, v0, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Te()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->ea(I)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->ta(Ljava/lang/String;)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz v0, :cond_0

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->canRead()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->zb(Ljava/lang/String;)I

    move-result v0

    if-nez v0, :cond_0

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->seekTo(I)V

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Va()V

    .line 7
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 8
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez v1, :cond_3

    .line 9
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    const/4 v1, 0x0

    if-lez v0, :cond_1

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 11
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/e;->uc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v2

    iput-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez v2, :cond_3

    .line 13
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_2

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_1

    .line 15
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 16
    :goto_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez v1, :cond_3

    .line 17
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez v1, :cond_3

    .line 19
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 20
    :cond_3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0x9e06

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 21
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x7d0

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method private Ue()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    return-object p0
.end method

.method private Ve()Z
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff02

    invoke-virtual {v0, v1}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v0

    if-nez v0, :cond_1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

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
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->a(Ljava/lang/Object;)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/h;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/h;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    const-string p0, "/data/tw/.like"

    invoke-static {v0, p0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/h;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    return-void
.end method

.method private Xe()V
    .locals 8

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->getDuration()I

    move-result v0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->getCurrentPosition()I

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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Nb()Ljava/lang/String;

    move-result-object v1

    .line 5
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_3

    const-string v1, ""

    .line 6
    :cond_3
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const v4, 0x9f00

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

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
    sget-object v3, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/16 v4, 0x303

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

    move-result v5

    if-eqz v5, :cond_5

    move v2, v6

    :cond_5
    or-int/2addr v0, v2

    invoke-virtual {v3, v4, v7, v0, v1}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v0, 0x9e06

    const-wide/16 v1, 0x64

    invoke-virtual {p0, v0, v1, v2}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/t;I)I
    .locals 0

    .line 5
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Eh:I

    return p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)I
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->zb(Ljava/lang/String;)I

    move-result p0

    return p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/t;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Te()V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/t;ILjava/lang/String;)V
    .locals 0

    .line 6
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/d/t;->b(ILjava/lang/String;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/t;IZ)V
    .locals 0

    .line 7
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/d/t;->c(IZ)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/t;Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 0

    .line 4
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/f/d/t;Z)V
    .locals 0

    .line 3
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->L(Z)V

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
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    return-void
.end method

.method static synthetic access$1000()Lcom/eckom/xtlibrary/b/f/f/t;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    return-object v0
.end method

.method static synthetic access$300()Ljava/util/ArrayList;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    return-object v0
.end method

.method private b(ILjava/lang/String;)V
    .locals 11

    const/4 v0, 0x0

    const/16 v1, 0x510

    const/4 v2, 0x4

    const/4 v3, 0x0

    if-nez p2, :cond_0

    .line 18
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    shl-int/2addr p1, v2

    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_6

    .line 19
    :cond_0
    iget v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Eh:I

    and-int/lit8 v5, v4, 0x1

    const/4 v6, 0x1

    if-ne v5, v6, :cond_2

    .line 20
    :try_start_0
    sget-object p0, Ljava/nio/charset/StandardCharsets;->UTF_16:Ljava/nio/charset/Charset;

    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    move-result-object v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 21
    :catch_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

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

    .line 22
    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    .line 23
    :catch_1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

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

    .line 24
    :try_start_2
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    :catch_2
    if-nez v0, :cond_5

    .line 25
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

    .line 26
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Eh:I

    and-int/2addr p0, v10

    if-ne p0, v10, :cond_6

    .line 27
    :try_start_4
    sget-object p0, Ljava/nio/charset/StandardCharsets;->UTF_16:Ljava/nio/charset/Charset;

    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    move-result-object v0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_4

    :catch_4
    move v6, v3

    .line 28
    :cond_6
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

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

    .line 29
    :try_start_5
    invoke-virtual {p2, v8}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_5

    :catch_5
    if-nez v0, :cond_9

    .line 30
    :try_start_6
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_6

    goto :goto_4

    :catch_6
    move-exception v4

    .line 31
    invoke-virtual {v4}, Ljava/lang/Exception;->printStackTrace()V

    :goto_4
    move v6, v7

    :cond_9
    if-nez v0, :cond_a

    .line 32
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Eh:I

    and-int/2addr p0, v10

    if-ne p0, v10, :cond_a

    .line 33
    :try_start_7
    sget-object p0, Ljava/nio/charset/StandardCharsets;->UTF_16:Ljava/nio/charset/Charset;

    invoke-virtual {p2, p0}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    move-result-object v0
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_7

    :catch_7
    move v6, v3

    .line 34
    :cond_a
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

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

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)V
    .locals 0

    .line 3
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Bb(Ljava/lang/String;)V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/t;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->mute(Z)V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/f/d/t;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    return p0
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/t;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Eh:I

    return p0
.end method

.method private c(IZ)V
    .locals 8

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    monitor-enter v0

    .line 4
    :try_start_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    if-eqz v1, :cond_d

    .line 5
    array-length v2, v1

    if-lez v2, :cond_d

    .line 6
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v7, v1, p1

    iput v7, v6, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 8
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/t;->play(I)Z

    move-result v5

    if-eqz v5, :cond_1

    .line 9
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v6, v6, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-eqz v6, :cond_5

    if-ne p1, p2, :cond_5

    add-int/lit8 v2, v2, -0x1

    :goto_2
    if-le v2, v3, :cond_4

    .line 11
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v6, v1, v2

    iput v6, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 12
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/t;->play(I)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 13
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Tb()V

    .line 15
    :cond_5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    if-ne p1, p2, :cond_d

    .line 16
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v4, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    .line 17
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    aget p2, v1, p2

    iput p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 18
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Tb()V

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
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v6, v1, p1

    iput v6, v5, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 20
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/b/f/d/t;->play(I)Z

    move-result p2

    if-eqz p2, :cond_8

    .line 21
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v5, v5, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-eqz v5, :cond_c

    if-ne p1, v2, :cond_c

    move p1, v4

    :goto_6
    if-ge p1, v3, :cond_b

    .line 23
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    aget v6, v1, p1

    iput v6, v5, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 24
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/b/f/d/t;->play(I)Z

    move-result p2

    if-eqz p2, :cond_a

    .line 25
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Tb()V

    .line 27
    :cond_c
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    if-ne p1, v2, :cond_d

    .line 28
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    add-int/lit8 v2, v2, -0x1

    iput v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    .line 29
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ld:I

    aget p2, v1, p2

    iput p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 30
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Tb()V

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

.method static synthetic c(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Cg:Z

    return p1
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/t;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->wg:Z

    return p0
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Sh:Z

    return p1
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/t;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Xe()V

    return-void
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Qh:Z

    return p1
.end method

.method private f(Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 9

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

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

    .line 4
    iget-object v2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_1

    array-length v3, v2

    if-lez v3, :cond_1

    .line 5
    array-length v3, v2

    const/4 v4, 0x0

    :goto_1
    if-ge v4, v3, :cond_1

    aget-object v5, v2, v4

    .line 6
    iget-object v6, v5, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    if-eqz v6, :cond_0

    .line 7
    iget-object v7, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v8, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v7, v6, v8}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v6

    iput-boolean v6, v5, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    :cond_0
    add-int/lit8 v4, v4, 0x1

    goto :goto_1

    .line 8
    :cond_1
    invoke-interface {v1, p1}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 9
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

    .line 10
    invoke-static {v1}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    goto :goto_0

    :cond_3
    return-void
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/f/d/t;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Cg:Z

    return p0
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Rh:Z

    return p1
.end method

.method private fd()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    return-object p0
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/b/f/d/t;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Sh:Z

    return p0
.end method

.method private getCurrentPosition()I
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result p0

    return p0
.end method

.method private getDuration()I
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->getDuration()I

    move-result p0

    return p0
.end method

.method public static getInstant()Lcom/eckom/xtlibrary/b/f/d/t;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->ji:Lcom/eckom/xtlibrary/b/f/d/t;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/d/t;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->ji:Lcom/eckom/xtlibrary/b/f/d/t;

    .line 3
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->ji:Lcom/eckom/xtlibrary/b/f/d/t;

    return-object v0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    return-object p0
.end method

.method private hd()I
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    const/4 v2, 0x1

    if-nez v1, :cond_0

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-ne v0, v2, :cond_0

    const/4 p0, 0x0

    return p0

    .line 2
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    const/4 v3, 0x2

    if-nez v1, :cond_1

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-ne v0, v3, :cond_1

    return v2

    .line 3
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    if-ne v0, v2, :cond_2

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    if-ne p0, v2, :cond_2

    return v3

    :cond_2
    const/4 p0, -0x1

    return p0
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/b/f/d/t;)Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Vh:Ljava/lang/String;

    return-object p0
.end method

.method private isPlaying()Z
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result p0

    return p0
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    return-object p0
.end method

.method static synthetic k(Lcom/eckom/xtlibrary/b/f/d/t;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Rh:Z

    return p0
.end method

.method static synthetic l(Lcom/eckom/xtlibrary/b/f/d/t;)Z
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

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
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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

.method static synthetic m(Lcom/eckom/xtlibrary/b/f/d/t;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Re()V

    return-void
.end method

.method private mute(Z)V
    .locals 0

    if-eqz p1, :cond_0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    const/4 p1, 0x0

    invoke-virtual {p0, p1, p1}, Landroid/media/MediaPlayer;->setVolume(FF)V

    goto :goto_0

    .line 2
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    const/high16 p1, 0x3f800000    # 1.0f

    invoke-virtual {p0, p1, p1}, Landroid/media/MediaPlayer;->setVolume(FF)V

    :goto_0
    return-void
.end method

.method static synthetic n(Lcom/eckom/xtlibrary/b/f/d/t;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Se()V

    return-void
.end method

.method private onCreate()V
    .locals 3

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Th:Z

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-nez v0, :cond_0

    .line 3
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/f/t;->open()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v0

    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    .line 4
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v0, :cond_1

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const-string v2, "MusicID3Model"

    invoke-virtual {v0, v2, v1}, Landroid/tw/john/TWUtil;->addHandler(Ljava/lang/String;Landroid/os/Handler;)V

    .line 6
    :cond_1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 7
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/b/e;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/e;)V

    .line 9
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->We()V

    .line 10
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ob()V

    return-void
.end method

.method private play(I)Z
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    const/4 v2, -0x1

    if-le v1, v2, :cond_0

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v3, v2, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge v1, v3, :cond_0

    .line 2
    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, v2, v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz v0, :cond_0

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->canRead()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->zb(Ljava/lang/String;)I

    move-result v0

    if-nez v0, :cond_0

    .line 5
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->seekTo(I)V

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Va()V

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v0, 0xff09

    invoke-virtual {p1, v0}, Landroid/os/Handler;->removeMessages(I)V

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const-wide/16 v1, 0x1f4

    invoke-virtual {p0, v0, v1, v2}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    const/4 p0, 0x1

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method private reset()V
    .locals 2

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    const/4 v0, 0x0

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 3
    new-instance v0, Landroid/media/MediaPlayer;

    invoke-direct {v0}, Landroid/media/MediaPlayer;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/i;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/i;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    invoke-virtual {v0, v1}, Landroid/media/MediaPlayer;->setOnCompletionListener(Landroid/media/MediaPlayer$OnCompletionListener;)V

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/j;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/j;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    invoke-virtual {v0, v1}, Landroid/media/MediaPlayer;->setOnErrorListener(Landroid/media/MediaPlayer$OnErrorListener;)V

    return-void
.end method

.method private zb(Ljava/lang/String;)I
    .locals 2

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->stop()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->reset()V

    .line 4
    :try_start_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0, p1}, Landroid/media/MediaPlayer;->setDataSource(Ljava/lang/String;)V

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

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

    .line 32
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Pa(I)Z

    .line 33
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 34
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Bb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Cb()V
    .locals 4

    const/4 v0, 0x1

    .line 1
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Pa(I)Z

    move-result v1

    const/4 v2, 0x0

    if-eqz v1, :cond_4

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_1

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    add-int/2addr v3, v0

    iput v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v0, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt v3, v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 5
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 6
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/e;->uc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 7
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_2

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-ge v1, v0, :cond_2

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 9
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_3

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-ge v1, v0, :cond_3

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 11
    :cond_3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_9

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-ge v1, v0, :cond_9

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_2

    .line 13
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_6

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt v1, v0, :cond_5

    .line 15
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 16
    :cond_5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_1

    .line 17
    :cond_6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/e;->uc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    :goto_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_7

    .line 19
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 20
    :cond_7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_8

    .line 21
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 22
    :cond_8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_9

    .line 23
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 24
    :cond_9
    :goto_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 25
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 26
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 27
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 28
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 29
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Db()V
    .locals 3

    const/4 v0, 0x2

    .line 1
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Pa(I)Z

    move-result v0

    const/4 v1, 0x0

    if-eqz v0, :cond_4

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_1

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    add-int/lit8 v2, v2, 0x1

    iput v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt v2, v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 5
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 6
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/e;->vc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 7
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_2

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-ge v1, v0, :cond_2

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 9
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_3

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-ge v1, v0, :cond_3

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 11
    :cond_3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_9

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-ge v1, v0, :cond_9

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_2

    .line 13
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_6

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt v2, v0, :cond_5

    .line 15
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 16
    :cond_5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_1

    .line 17
    :cond_6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/b/e;->vc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    :goto_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_7

    .line 19
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 20
    :cond_7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_8

    .line 21
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 22
    :cond_8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lez v0, :cond_9

    .line 23
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 24
    :cond_9
    :goto_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 25
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 26
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 27
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 28
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 29
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Ea(Ljava/lang/String;)V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/s;

    invoke-direct {v1, p0, p1}, Lcom/eckom/xtlibrary/b/f/d/s;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)V

    const-wide/16 p0, 0x5dc

    invoke-virtual {v0, v1, p0, p1}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    return-void
.end method

.method public Eb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Pj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Sj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Wj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 5
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Fa(Ljava/lang/String;)V
    .locals 3

    .line 1
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

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/g;

    invoke-direct {v2, p0, p1}, Lcom/eckom/xtlibrary/b/f/d/g;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)V

    invoke-static {p1, v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/e;ZLcom/eckom/xtlibrary/b/f/f/h$f;)V

    return-void
.end method

.method public Fb()V
    .locals 1

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Th:Z

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ua()V

    return-void
.end method

.method public Gb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_3

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v2, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Ljava/lang/String;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

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

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Nb()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Lb()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    .line 7
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

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
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    const-string v1, "/data/tw/.like"

    invoke-virtual {v0, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_3

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v2, 0x4

    if-ne v1, v2, :cond_2

    .line 13
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_3
    return-void
.end method

.method public Hb()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, 0x3a98

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public Ib()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, -0x2710

    if-lez v0, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->seekTo(I)V

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
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v1, "usb"

    invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    const/4 v1, 0x0

    if-eqz v0, :cond_0

    return v1

    .line 2
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v2, "extsd"

    invoke-virtual {v0, v2}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const/4 p0, 0x1

    return p0

    .line 3
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Fb:Landroid/graphics/Bitmap;

    return-object p0
.end method

.method public Lb()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    return-object p0
.end method

.method public Mb()Landroid/media/MediaPlayer;
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Landroid/media/MediaPlayer;

    invoke-direct {v0}, Landroid/media/MediaPlayer;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 3
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    return-object p0
.end method

.method public Nb()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    return-object p0
.end method

.method public Ob()V
    .locals 7
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "SdCardPath"
        }
    .end annotation

    .line 1
    new-instance v0, Ljava/io/File;

    const-string v1, "/storage"

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/e;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/e;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    invoke-virtual {v0, v2}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    const/4 v2, 0x0

    if-eqz v0, :cond_0

    .line 2
    array-length v3, v0

    move v4, v2

    :goto_0
    if-ge v4, v3, :cond_0

    aget-object v5, v0, v4

    .line 3
    invoke-virtual {v5}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v6

    invoke-virtual {p0, v6}, Lcom/eckom/xtlibrary/b/f/d/t;->pa(Ljava/lang/String;)V

    .line 4
    invoke-virtual {v5}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0, v5}, Lcom/eckom/xtlibrary/b/f/d/t;->Fa(Ljava/lang/String;)V

    add-int/lit8 v4, v4, 0x1

    goto :goto_0

    .line 5
    :cond_0
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/f;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/f;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    invoke-virtual {v0, v1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    if-eqz v0, :cond_1

    .line 6
    array-length v1, v0

    :goto_1
    if-ge v2, v1, :cond_1

    aget-object v3, v0, v2

    .line 7
    invoke-virtual {v3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0, v4}, Lcom/eckom/xtlibrary/b/f/d/t;->qa(Ljava/lang/String;)V

    .line 8
    invoke-virtual {v3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {p0, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->Fa(Ljava/lang/String;)V

    add-int/lit8 v2, v2, 0x1

    goto :goto_1

    :cond_1
    const-string v0, "/mnt/sdcard/iNand"

    .line 9
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Fa(Ljava/lang/String;)V

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    invoke-static {v1, v0, p0}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    return-void
.end method

.method public Pb()V
    .locals 2

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Th:Z

    .line 2
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    const/4 v0, 0x0

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v0}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 6
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const-string v1, "MusicID3Model"

    invoke-virtual {p0, v1}, Landroid/tw/john/TWUtil;->removeHandler(Ljava/lang/String;)V

    .line 7
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/f/t;->close()V

    .line 8
    sput-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    return-void
.end method

.method public Qb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Yj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_0

    array-length v2, v2

    if-lez v2, :cond_0

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_0
    return-void
.end method

.method public Ra()Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 6

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    new-array v0, v0, [Lcom/eckom/xtlibrary/b/f/b/f;

    const/4 v1, 0x0

    .line 2
    :goto_0
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v2

    if-ge v1, v2, :cond_0

    .line 3
    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    invoke-virtual {v2, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    .line 6
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    iput v0, v2, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    .line 7
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    return-object p0
.end method

.method public Rb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_0

    array-length v2, v2

    if-lez v2, :cond_0

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_0
    return-void
.end method

.method public Sb()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Zj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v2, :cond_0

    array-length v2, v2

    if-lez v2, :cond_0

    .line 2
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_0
    return-void
.end method

.method public Tb()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff11

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public Ua()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v1

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result v1

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->pause()V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Xe()V

    :cond_0
    return-void
.end method

.method public Ub()V
    .locals 10

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

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
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v2, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mDuration:I

    invoke-interface {v1, v3, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    .line 3
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v2, v4, v3}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    .line 4
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ue()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->fd()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Nb()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Kb()Landroid/graphics/Bitmap;

    move-result-object v6

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v2, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v8, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget v9, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    move-object v2, v1

    invoke-interface/range {v2 .. v9}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 5
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    .line 6
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->hd()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    .line 7
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v2}, Landroid/media/MediaPlayer;->getAudioSessionId()I

    move-result v2

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->B(I)V

    goto :goto_0

    .line 8
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method public Va()V
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v0, :cond_0

    const/4 v1, 0x1

    .line 2
    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->isPlaying()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->start()V

    const/4 v0, 0x0

    .line 5
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Th:Z

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ab(Ljava/lang/String;)V

    .line 9
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Xe()V

    :cond_1
    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/g/a;)V
    .locals 3

    .line 13
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 14
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz p1, :cond_0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    const/4 v2, 0x3

    if-ne v1, v2, :cond_0

    .line 15
    invoke-static {v0, p1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/e;Landroid/tw/john/TWUtil;)V

    .line 16
    :cond_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-nez p1, :cond_1

    .line 17
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Pb()V

    :cond_1
    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/g/a;Landroid/content/Context;)V
    .locals 1

    .line 8
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-nez v0, :cond_0

    .line 9
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    .line 10
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->onCreate()V

    .line 11
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result p0

    if-nez p0, :cond_1

    .line 12
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    :cond_1
    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/f/b/f;Z)V
    .locals 3

    .line 4
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_1

    if-eqz p2, :cond_0

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    if-nez v0, :cond_0

    .line 6
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    goto :goto_0

    :cond_0
    if-nez p2, :cond_1

    .line 7
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {p2, v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result p2

    if-eqz p2, :cond_1

    .line 8
    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Ljava/lang/String;Ljava/util/ArrayList;)Ljava/util/ArrayList;

    .line 9
    :cond_1
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_1
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result p2

    if-eqz p2, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v2, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    invoke-interface {p2, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->h(Z)V

    goto :goto_1

    .line 11
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ra()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p2

    iput-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 p2, 0x4

    if-eq p1, p2, :cond_3

    if-nez p1, :cond_5

    .line 13
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    const-string v0, "/data/tw/.like"

    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 15
    :cond_4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-ne v0, p2, :cond_5

    .line 16
    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 17
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ab()V

    :cond_5
    return-void
.end method

.method public ea(I)V
    .locals 5

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->kd:[I

    aput p1, v0, v1

    const/4 v0, 0x1

    if-le v2, v0, :cond_4

    add-int/lit8 v0, p1, 0x1

    :goto_0
    if-ge v0, v2, :cond_2

    sub-int v3, v0, p1

    .line 7
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    if-eqz v4, :cond_1

    .line 8
    invoke-direct {p0, p1, v2}, Lcom/eckom/xtlibrary/b/f/d/t;->m(II)I

    move-result v3

    .line 9
    :cond_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    if-eqz v3, :cond_3

    .line 11
    invoke-direct {p0, p1, v2}, Lcom/eckom/xtlibrary/b/f/d/t;->m(II)I

    move-result v0

    .line 12
    :cond_3
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    .line 2
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    .line 3
    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->ea(I)V

    goto :goto_0

    .line 4
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    .line 5
    iput v2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    .line 6
    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->ea(I)V

    goto :goto_0

    .line 7
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    .line 8
    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    .line 9
    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->ea(I)V

    .line 10
    :goto_0
    sget-object p1, Lcom/eckom/xtlibrary/b/f/d/t;->hi:Ljava/util/ArrayList;

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
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->hd()I

    move-result v1

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->D(I)V

    goto :goto_1

    :cond_3
    return-void
.end method

.method public la(I)V
    .locals 12

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    const/4 v3, 0x4

    const/4 v4, 0x1

    if-ne v2, v4, :cond_0

    if-nez p1, :cond_0

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v2, v4, :cond_0

    iget v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-eq v2, v3, :cond_0

    .line 2
    iget-object p1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto/16 :goto_2

    .line 4
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v0, v4, :cond_1

    add-int/lit8 p1, p1, -0x1

    .line 5
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    const/4 v2, 0x2

    const/4 v5, 0x3

    if-nez v1, :cond_5

    iget v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-eqz v1, :cond_5

    iget v6, v0, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v6, v4, :cond_5

    if-eq v1, v3, :cond_5

    .line 6
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v3, v1, p1

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iput-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/t;->fileName:Ljava/lang/String;

    .line 7
    aget-object v1, v1, p1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Vh:Ljava/lang/String;

    .line 8
    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->oa(I)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p1

    if-nez p1, :cond_4

    .line 9
    new-instance p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v7, p0, Lcom/eckom/xtlibrary/b/f/d/t;->fileName:Ljava/lang/String;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v11, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v8, v11, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    iget v9, v11, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    iget v0, v11, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    add-int/lit8 v10, v0, 0x1

    move-object v6, p1

    invoke-direct/range {v6 .. v11}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;IIILcom/eckom/xtlibrary/b/f/b/g;)V

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v1, v2, :cond_3

    if-ne v1, v5, :cond_2

    goto :goto_0

    .line 11
    :cond_2
    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    iget-object v8, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Vh:Ljava/lang/String;

    iget-object v9, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    iget-boolean v10, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    new-instance v11, Lcom/eckom/xtlibrary/b/f/d/r;

    invoke-direct {v11, p0}, Lcom/eckom/xtlibrary/b/f/d/r;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    move-object v7, p1

    invoke-static/range {v6 .. v11}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Ljava/util/ArrayList;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    goto/16 :goto_2

    .line 12
    :cond_3
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Vh:Ljava/lang/String;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/q;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/f/d/q;-><init>(Lcom/eckom/xtlibrary/b/f/d/t;)V

    invoke-static {p1, v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/h;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    goto/16 :goto_2

    .line 13
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 15
    iget-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto/16 :goto_2

    .line 16
    :cond_5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 17
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v6, v1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    const/4 v7, 0x0

    if-ne v6, v4, :cond_6

    .line 18
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v2, "/.all"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    .line 19
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Xj:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 20
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, v1, p1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    goto :goto_1

    .line 21
    :cond_6
    iget v4, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-ne v4, v3, :cond_7

    const-string v1, "/data/tw/.like"

    .line 22
    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    .line 23
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 24
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, v1, p1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    goto :goto_1

    :cond_7
    if-nez v6, :cond_8

    .line 25
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 26
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, v1, p1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    .line 27
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v2, "/"

    invoke-virtual {v1, v2}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v2

    invoke-virtual {v1, v7, v2}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    goto :goto_1

    :cond_8
    if-ne v6, v5, :cond_9

    .line 28
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 29
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, v1, p1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    goto :goto_1

    :cond_9
    if-ne v6, v2, :cond_a

    .line 30
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 31
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, v1, p1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    .line 32
    :cond_a
    :goto_1
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->ea(I)V

    .line 33
    invoke-direct {p0, v7, v7}, Lcom/eckom/xtlibrary/b/f/d/t;->c(IZ)V

    :goto_2
    return-void
.end method

.method public onPause()V
    .locals 2

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->wg:Z

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/16 v1, 0x83

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/t;->ca(I)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    if-lez v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff09

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_0
    return-void
.end method

.method public onResume()V
    .locals 5

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->wg:Z

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mContext:Landroid/content/Context;

    sget-object v2, Lcom/eckom/xtlibrary/b/j/o;->pm:Ljava/lang/String;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Jb()I

    move-result v3

    const-string v4, "MUSIC_DATA"

    invoke-static {v1, v4, v2, v3}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v1, :cond_0

    .line 4
    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    .line 5
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/4 v1, 0x3

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/t;->ca(I)V

    .line 6
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    if-eqz v0, :cond_1

    const/16 v1, 0x510

    const/16 v2, 0xff

    .line 7
    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 8
    sget-object v0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/16 v1, 0x203

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 9
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v1, 0xff07

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 10
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x96

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return-void
.end method

.method public pa(Ljava/lang/String;)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    invoke-static {v0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    .line 5
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p1, :cond_2

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 7
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Qh:Z

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Qh:Z

    return-void

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ve()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v0, 0xff02

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_1
    return-void
.end method

.method public qa(Ljava/lang/String;)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->isForward:Z

    invoke-static {v0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    .line 5
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p1, :cond_2

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 7
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    return-void
.end method

.method public ra(Ljava/lang/String;)V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 2
    :cond_0
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    const/4 v2, 0x0

    const/4 v3, 0x1

    if-eqz v1, :cond_4

    .line 3
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_0

    .line 5
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    iget v5, v4, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v5, v3, :cond_1

    iget v5, v4, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v5, v3, :cond_1

    .line 7
    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 8
    :cond_1
    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    .line 9
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 10
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 11
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lt v5, v1, :cond_2

    .line 12
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    move-result v5

    sub-int/2addr v5, v3

    iput v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 13
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    if-gez v3, :cond_2

    .line 14
    iput v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 15
    :cond_2
    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 16
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_3

    .line 17
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    iget v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 18
    :cond_3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/e;->uc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v2

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Dj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 19
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 20
    :cond_5
    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    const-string v4, "/"

    if-eqz v1, :cond_7

    .line 21
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 22
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v4

    invoke-virtual {v5, v4}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v4

    .line 23
    invoke-virtual {p1, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v4

    if-eqz v4, :cond_5

    .line 24
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 25
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 26
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, v3, :cond_6

    .line 27
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    iget v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 28
    :cond_6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ij:Ljava/util/ArrayList;

    invoke-virtual {v4, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 29
    :goto_2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-static {v1}, Lcom/eckom/xtlibrary/a/b;->a(Ljava/lang/Object;)V

    goto :goto_1

    .line 30
    :cond_7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 31
    :cond_8
    :goto_3
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_a

    .line 32
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 33
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v6

    invoke-virtual {v5, v6}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v5

    .line 34
    invoke-virtual {p1, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v5

    if-eqz v5, :cond_8

    .line 35
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 36
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 37
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, v3, :cond_9

    .line 38
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    iget v6, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_4

    .line 39
    :cond_9
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {v5, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 40
    :goto_4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-static {v1}, Lcom/eckom/xtlibrary/a/b;->a(Ljava/lang/Object;)V

    goto :goto_3

    .line 41
    :cond_a
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 42
    :cond_b
    :goto_5
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_d

    .line 43
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 44
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v6

    invoke-virtual {v5, v6}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v5

    .line 45
    invoke-virtual {p1, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v5

    if-eqz v5, :cond_b

    .line 46
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 47
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 48
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, v3, :cond_c

    .line 49
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    iget v6, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_6

    .line 50
    :cond_c
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-virtual {v5, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 51
    :goto_6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ej:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-static {v1}, Lcom/eckom/xtlibrary/a/b;->a(Ljava/lang/Object;)V

    goto :goto_5

    :cond_d
    return-void
.end method

.method public rb()V
    .locals 1

    .line 1
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Qh:Z

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Qh:Z

    return-void

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ve()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mHandler:Landroid/os/Handler;

    const v0, 0xff03

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_1
    return-void
.end method

.method public sa(Ljava/lang/String;)V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 2
    :cond_0
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    const/4 v2, 0x0

    const/4 v3, 0x1

    if-eqz v1, :cond_4

    .line 3
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_0

    .line 5
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    iget v5, v4, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-ne v5, v3, :cond_1

    iget v5, v4, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    if-eq v5, v3, :cond_1

    .line 7
    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 8
    :cond_1
    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    .line 9
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 10
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 11
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lt v5, v1, :cond_2

    .line 12
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    move-result v5

    sub-int/2addr v5, v3

    iput v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 13
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    if-gez v3, :cond_2

    .line 14
    iput v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 15
    :cond_2
    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 16
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_3

    .line 17
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    iget v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 18
    :cond_3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/e;->vc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v2

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->rj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 19
    :cond_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 20
    :cond_5
    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    const-string v4, "/"

    if-eqz v1, :cond_7

    .line 21
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 22
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v4

    invoke-virtual {v5, v4}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v4

    .line 23
    invoke-virtual {p1, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v4

    if-eqz v4, :cond_5

    .line 24
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 25
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 26
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, v3, :cond_6

    .line 27
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    iget v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_1

    .line 28
    :cond_6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_5

    .line 29
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->xj:Ljava/util/ArrayList;

    invoke-virtual {v4, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v4, v1, Lcom/eckom/xtlibrary/b/f/b/e;->tj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_1

    .line 30
    :cond_7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 31
    :cond_8
    :goto_2
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_a

    .line 32
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 33
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v6

    invoke-virtual {v5, v6}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v5

    .line 34
    invoke-virtual {p1, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v5

    if-eqz v5, :cond_8

    .line 35
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 36
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 37
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, v3, :cond_9

    .line 38
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    iget v6, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 39
    :cond_9
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_8

    .line 40
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v5, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 41
    :cond_a
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .line 42
    :cond_b
    :goto_3
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_d

    .line 43
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 44
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v6

    invoke-virtual {v5, v6}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v5

    .line 45
    invoke-virtual {p1, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v5

    if-eqz v5, :cond_b

    .line 46
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 47
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    .line 48
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-le v1, v3, :cond_c

    .line 49
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    iget v6, v1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_3

    .line 50
    :cond_c
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    if-lez v1, :cond_b

    .line 51
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-virtual {v5, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->sj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_3

    :cond_d
    return-void
.end method

.method public seekTo(I)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->getDuration()I

    move-result v0

    if-lez p1, :cond_0

    if-lez v0, :cond_0

    if-ge p1, v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-virtual {p0, p1}, Landroid/media/MediaPlayer;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public ta(Ljava/lang/String;)V
    .locals 2
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "SdCardPath"
        }
    .end annotation

    if-nez p1, :cond_0

    return-void

    :cond_0
    const-string v0, "/mnt/sdcard/iNand"

    .line 1
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto/16 :goto_2

    :cond_1
    const-string v0, "/storage/usb"

    .line 3
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    const/4 v1, 0x0

    if-nez v0, :cond_7

    const-string v0, "/mnt/usbhost"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_2

    goto :goto_1

    :cond_2
    const-string v0, "/storage/extsd"

    .line 4
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_4

    const-string v0, "/mnt/extsd"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_3

    goto :goto_0

    .line 5
    :cond_3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 6
    :cond_4
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_6

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lt v0, p1, :cond_5

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    .line 9
    :cond_5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->vd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 10
    :cond_6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/b/e;->uc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 11
    :cond_7
    :goto_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_9

    .line 12
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lt v0, p1, :cond_8

    .line 13
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    .line 14
    :cond_8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->wd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 15
    :cond_9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/b/e;->vc()Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    :goto_2
    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 1
    sget-object p0, Lcom/eckom/xtlibrary/b/f/d/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/t;->w(Z)V

    return-void
.end method

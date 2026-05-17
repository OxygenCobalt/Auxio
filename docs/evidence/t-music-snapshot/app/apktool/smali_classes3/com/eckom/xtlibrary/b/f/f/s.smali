.class public Lcom/eckom/xtlibrary/b/f/f/s;
.super Landroid/tw/john/TWUtil;
.source "TWMusic.java"


# static fields
.field public static Ad:I = 0x0

.field public static Bd:Ljava/lang/String; = null

.field public static Cd:Ljava/lang/String; = null

.field public static Dd:Lcom/eckom/xtlibrary/b/f/b/g; = null

.field public static Ed:Ljava/util/ArrayList; = null
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;"
        }
    .end annotation
.end field

.field public static Fd:Lcom/eckom/xtlibrary/b/f/b/g; = null

.field public static Gd:Z = false

.field public static Hd:Z = false

.field public static Id:I = 0x0

.field public static Jd:Z = false

.field public static Kd:[Ljava/lang/String; = null

.field private static TAG:Ljava/lang/String; = "TWMusic"

.field public static Tc:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;"
        }
    .end annotation
.end field

.field public static isForward:Z

.field private static jd:Lcom/eckom/xtlibrary/b/f/f/s;

.field private static mCount:I


# instance fields
.field public Fb:Landroid/graphics/Bitmap;

.field public hc:I

.field public ic:I

.field public kd:[I

.field public ld:I

.field public mDuration:I

.field private mService:I

.field public mSource:I

.field public md:I

.field public nd:Ljava/lang/String;

.field public od:Ljava/lang/String;

.field public pd:Ljava/lang/String;

.field public qd:Lcom/eckom/xtlibrary/b/f/b/g;

.field public rd:Lcom/eckom/xtlibrary/b/f/b/g;

.field public td:Lcom/eckom/xtlibrary/b/f/b/g;

.field public ud:Lcom/eckom/xtlibrary/b/f/b/g;

.field public vd:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/g;",
            ">;"
        }
    .end annotation
.end field

.field public wd:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/g;",
            ">;"
        }
    .end annotation
.end field

.field public xd:I

.field public yd:I

.field public zd:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method static constructor <clinit>()V
    .locals 3

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/f/s;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/4 v0, 0x0

    .line 2
    sput v0, Lcom/eckom/xtlibrary/b/f/f/s;->mCount:I

    .line 3
    sput v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    const-string v1, ""

    .line 4
    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 5
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    .line 6
    sput-boolean v0, Lcom/eckom/xtlibrary/b/f/f/s;->Gd:Z

    const/4 v1, 0x1

    .line 7
    sput-boolean v1, Lcom/eckom/xtlibrary/b/f/f/s;->Hd:Z

    .line 8
    sput-boolean v0, Lcom/eckom/xtlibrary/b/f/f/s;->Jd:Z

    const-string v2, "persist.media.forward"

    .line 9
    invoke-static {v2, v1}, Landroid/os/SystemProperties;->getInt(Ljava/lang/String;I)I

    move-result v2

    if-ne v2, v1, :cond_0

    move v0, v1

    :cond_0
    sput-boolean v0, Lcom/eckom/xtlibrary/b/f/f/s;->isForward:Z

    return-void
.end method

.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Landroid/tw/john/TWUtil;-><init>()V

    const/4 v0, 0x1

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    const-string v0, ""

    .line 3
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 5
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 6
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    .line 7
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    const/4 v0, 0x0

    .line 8
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 9
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 10
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->mService:I

    .line 11
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->zd:Ljava/util/List;

    return-void
.end method

.method public static Ra()Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 6

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    new-array v0, v0, [Lcom/eckom/xtlibrary/b/f/b/f;

    const/4 v1, 0x0

    .line 2
    :goto_0
    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v2

    if-ge v1, v2, :cond_0

    .line 3
    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/f;

    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    sget-object v4, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

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
    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, v1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    .line 5
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    iput v0, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    return-object v0
.end method

.method public static a(ZZLandroid/content/Context;)Lcom/eckom/xtlibrary/b/f/f/s;
    .locals 3

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->mCount:I

    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/eckom/xtlibrary/b/f/f/s;->mCount:I

    if-nez v0, :cond_4

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const/16 v1, 0xc

    new-array v1, v1, [S

    fill-array-data v1, :array_0

    invoke-virtual {v0, v1}, Landroid/tw/john/TWUtil;->open([S)I

    move-result v0

    const/4 v1, 0x0

    const/4 v2, 0x1

    if-eqz v0, :cond_0

    .line 3
    sget p0, Lcom/eckom/xtlibrary/b/f/f/s;->mCount:I

    sub-int/2addr p0, v2

    sput p0, Lcom/eckom/xtlibrary/b/f/f/s;->mCount:I

    return-object v1

    :cond_0
    if-eqz p1, :cond_1

    .line 4
    invoke-static {v1}, Ltv/danmaku/ijk/media/player/IjkMediaPlayer;->loadLibrariesOnce(Ltv/danmaku/ijk/media/player/IjkLibLoader;)V

    .line 5
    :cond_1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-virtual {v0}, Landroid/tw/john/TWUtil;->start()V

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    invoke-direct {v0, p2}, Lcom/eckom/xtlibrary/b/f/f/s;->e(Landroid/content/Context;)V

    .line 7
    sput-boolean p0, Lcom/eckom/xtlibrary/b/f/f/s;->Gd:Z

    .line 8
    sput-boolean p1, Lcom/eckom/xtlibrary/b/f/f/s;->Hd:Z

    .line 9
    invoke-virtual {p2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object p0

    sget p1, Lcom/eckom/xtlibrary/R$array;->music_supported_formats:I

    invoke-virtual {p0, p1}, Landroid/content/res/Resources;->getStringArray(I)[Ljava/lang/String;

    move-result-object p0

    sput-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Kd:[Ljava/lang/String;

    .line 10
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Kd:[Ljava/lang/String;

    invoke-static {p1}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->zd:Ljava/util/List;

    .line 11
    new-instance p0, Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 p1, 0x0

    const-string v0, "Playlist"

    invoke-direct {p0, v0, p1, p1}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    sput-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 12
    new-instance p0, Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v0, 0x4

    const-string v1, "LIKE"

    invoke-direct {p0, v1, v0, p1}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    sput-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 13
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {p0, p2, v0, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 14
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    sget p2, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {p0, p2}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 15
    new-instance p0, Ljava/util/ArrayList;

    invoke-direct {p0}, Ljava/util/ArrayList;-><init>()V

    sput-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Ed:Ljava/util/ArrayList;

    .line 16
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    const p2, 0xfff1

    invoke-virtual {p0, p2}, Landroid/tw/john/TWUtil;->write(I)I

    move-result p0

    sput p0, Lcom/eckom/xtlibrary/b/f/f/s;->Id:I

    .line 17
    new-instance p0, Ljava/io/File;

    const-string p2, "/system/bin/z-sender"

    invoke-direct {p0, p2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0}, Ljava/io/File;->exists()Z

    move-result p0

    if-nez p0, :cond_2

    sget p0, Lcom/eckom/xtlibrary/b/f/f/s;->Id:I

    const/16 p2, 0x23

    if-ne p0, p2, :cond_3

    :cond_2
    move p1, v2

    :cond_3
    sput-boolean p1, Lcom/eckom/xtlibrary/b/f/f/s;->Jd:Z

    .line 18
    :cond_4
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    return-object p0

    nop

    :array_0
    .array-data 2
        0x112s
        0x201s
        0x202s
        0x203s
        0x20cs
        0x301s
        0x302s
        0x304s
        0x510s
        -0x61fds
        -0x61e1s
        -0x60e4s
    .end array-data
.end method

.method static synthetic access$000()Lcom/eckom/xtlibrary/b/f/f/s;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->jd:Lcom/eckom/xtlibrary/b/f/f/s;

    return-object v0
.end method

.method static synthetic access$100()Ljava/lang/String;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/s;->TAG:Ljava/lang/String;

    return-object v0
.end method

.method private e(Landroid/content/Context;)V
    .locals 4

    const/4 p1, 0x0

    .line 1
    :try_start_0
    new-instance v0, Ljava/io/BufferedReader;

    new-instance v1, Ljava/io/FileReader;

    const-string v2, "/data/tw/music"

    invoke-direct {v1, v2}, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V

    invoke-direct {v0, v1}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 2
    :try_start_1
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object p1

    sput-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    .line 3
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object p1

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    sput p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    .line 4
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object p1

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    iput p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    .line 5
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object p1

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    iput p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    .line 6
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object p1

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    iput p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 7
    :goto_0
    :try_start_2
    invoke-virtual {v0}, Ljava/io/BufferedReader;->close()V

    goto :goto_2

    :catchall_0
    move-exception p1

    goto :goto_1

    :catchall_1
    move-exception v0

    move-object v3, v0

    move-object v0, p1

    move-object p1, v3

    :goto_1
    if-eqz v0, :cond_0

    invoke-virtual {v0}, Ljava/io/BufferedReader;->close()V

    .line 8
    :cond_0
    throw p1
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    :catch_0
    move-object v0, p1

    :catch_1
    if-eqz v0, :cond_1

    goto :goto_0

    .line 9
    :catch_2
    :cond_1
    :goto_2
    iget p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    const/4 v0, 0x1

    if-ge p1, v0, :cond_2

    .line 10
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ic:I

    .line 11
    :cond_2
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-static {p0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result p0

    if-nez p0, :cond_3

    .line 12
    sget-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    const-string p1, "/"

    invoke-virtual {p0, p1}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result p0

    if-lez p0, :cond_3

    .line 13
    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    const/4 v0, 0x0

    invoke-virtual {p1, v0, p0}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object p0

    sput-object p0, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    :cond_3
    return-void
.end method

.method private m(II)I
    .locals 4

    .line 1
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

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

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

    .line 3
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

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


# virtual methods
.method public Sa()V
    .locals 1

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/f/f/r;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/f/f/r;-><init>(Lcom/eckom/xtlibrary/b/f/f/s;)V

    .line 2
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method public a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 9

    if-eqz p2, :cond_1

    if-eqz p3, :cond_1

    .line 19
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, p3}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v1, Lcom/eckom/xtlibrary/b/f/f/m;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/f/m;-><init>(Lcom/eckom/xtlibrary/b/f/f/s;)V

    invoke-virtual {v0, v1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object p0

    const/4 v0, 0x0

    .line 20
    iput v0, p2, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    .line 21
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    if-eqz p0, :cond_0

    .line 22
    array-length v2, p0

    invoke-virtual {p2, v2}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 23
    array-length v2, p0

    move v3, v0

    :goto_0
    if-ge v3, v2, :cond_0

    aget-object v4, p0, v3

    .line 24
    invoke-virtual {v4}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v5

    .line 25
    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v6, p3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v7, "/"

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    .line 26
    sget-object v7, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {p1, v6, v7}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v6

    .line 27
    new-instance v7, Lcom/eckom/xtlibrary/b/f/b/f;

    const-string v8, "."

    invoke-virtual {v5, v8}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v8

    invoke-virtual {v5, v0, v8}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v4}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-direct {v7, v5, v4, v6}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;Z)V

    .line 28
    invoke-interface {v1, v7}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    add-int/lit8 v3, v3, 0x1

    goto :goto_0

    .line 29
    :cond_0
    sget-boolean p0, Lcom/eckom/xtlibrary/b/f/f/s;->isForward:Z

    invoke-static {p2, v1, p0}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/List;Z)V

    :cond_1
    return-void
.end method

.method public b(IIIII)V
    .locals 1

    shl-int/lit8 p3, p3, 0x10

    const v0, 0xffff

    and-int/2addr p2, v0

    or-int/2addr p2, p3

    shl-int/lit8 p1, p1, 0x1f

    and-int/lit8 p3, p5, 0x7f

    shl-int/lit8 p3, p3, 0x18

    or-int/2addr p1, p3

    const p3, 0xffffff

    and-int/2addr p3, p4

    or-int/2addr p1, p3

    const/16 p3, 0x502

    .line 1
    invoke-virtual {p0, p3, p2, p1}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 6

    const-string p0, ""

    const-string v0, "/"

    if-eqz p1, :cond_a

    if-eqz p2, :cond_a

    const/4 v1, 0x0

    .line 2
    :try_start_0
    sget-boolean v2, Lcom/eckom/xtlibrary/b/f/f/s;->Gd:Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    const-string v3, "/DCIM"

    const-string v4, "/data/tw/"

    if-eqz v2, :cond_2

    :try_start_1
    const-string v2, "/mnt/extsd"

    .line 3
    invoke-virtual {p2, v2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 4
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/4 v3, 0x5

    invoke-virtual {p2, v3}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    goto :goto_1

    :cond_0
    const-string v2, "/mnt/usbhost/Storage"

    .line 5
    invoke-virtual {p2, v2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_1

    .line 6
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/16 v3, 0xd

    invoke-virtual {p2, v3}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    goto :goto_1

    .line 7
    :cond_1
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    goto :goto_1

    :cond_2
    const-string v2, "/storage/usb"

    .line 8
    invoke-virtual {p2, v2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-nez v2, :cond_4

    const-string v2, "/storage/extsd"

    invoke-virtual {p2, v2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_3

    goto :goto_0

    .line 9
    :cond_3
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    goto :goto_1

    .line 10
    :cond_4
    :goto_0
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/16 v3, 0x9

    invoke-virtual {p2, v3}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    .line 11
    :goto_1
    new-instance v3, Ljava/io/BufferedReader;

    new-instance v4, Ljava/io/FileReader;

    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v2, "/.music"

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v4, v2}, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V

    invoke-direct {v3, v4}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    .line 12
    :try_start_2
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    .line 13
    :cond_5
    :goto_2
    invoke-virtual {v3}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v2

    if-eqz v2, :cond_7

    .line 14
    new-instance v4, Ljava/io/File;

    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v5, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v4, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 15
    invoke-virtual {v4}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v2

    sget-object v5, Ljava/util/Locale;->ENGLISH:Ljava/util/Locale;

    invoke-virtual {v2, v5}, Ljava/lang/String;->toUpperCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object v2

    .line 16
    invoke-virtual {v4}, Ljava/io/File;->canRead()Z

    move-result v5

    if-eqz v5, :cond_5

    invoke-virtual {v4}, Ljava/io/File;->isDirectory()Z

    move-result v5

    if-eqz v5, :cond_5

    const-string v5, "ECC"

    .line 17
    invoke-virtual {v2, v5}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-nez v2, :cond_5

    .line 18
    invoke-virtual {v4}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v2

    .line 19
    invoke-virtual {v4}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    const-string v5, "."

    .line 20
    invoke-virtual {v2, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_6

    const/4 v2, 0x0

    .line 21
    invoke-virtual {v4, v0}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v5

    invoke-virtual {v4, v2, v5}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v2

    .line 22
    invoke-virtual {v2, v0}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v5

    add-int/lit8 v5, v5, 0x1

    invoke-virtual {v2, v5}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v2

    .line 23
    new-instance v5, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-direct {v5, v2, v4}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v1, v5}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_2

    .line 24
    :cond_6
    new-instance v5, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-direct {v5, v2, v4}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v1, v5}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_2

    .line 25
    :cond_7
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result p2

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 26
    sget-boolean p2, Lcom/eckom/xtlibrary/b/f/f/s;->isForward:Z

    invoke-static {p1, v1, p2}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/List;Z)V

    .line 27
    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p2

    :goto_3
    invoke-interface {p2}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_8

    invoke-interface {p2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 28
    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    goto :goto_3

    .line 29
    :cond_8
    invoke-virtual {v1}, Ljava/util/ArrayList;->clear()V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    .line 30
    :try_start_3
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_2

    goto :goto_6

    :catchall_0
    move-exception p1

    move-object v1, v3

    goto :goto_5

    :catch_0
    move-exception p1

    move-object v1, v3

    goto :goto_4

    :catchall_1
    move-exception p1

    goto :goto_5

    :catch_1
    move-exception p1

    .line 31
    :goto_4
    :try_start_4
    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->TAG:Ljava/lang/String;

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {p2, p1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_1

    if-eqz v1, :cond_a

    .line 32
    :try_start_5
    invoke-virtual {v1}, Ljava/io/BufferedReader;->close()V

    goto :goto_6

    :goto_5
    if-eqz v1, :cond_9

    invoke-virtual {v1}, Ljava/io/BufferedReader;->close()V

    .line 33
    :cond_9
    throw p1
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_2

    :catch_2
    move-exception p1

    .line 34
    sget-object p2, Lcom/eckom/xtlibrary/b/f/f/s;->TAG:Ljava/lang/String;

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {p2, p0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :cond_a
    :goto_6
    return-void
.end method

.method public ca(I)V
    .locals 1

    .line 1
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->mService:I

    const v0, 0x9e00

    .line 2
    invoke-virtual {p0, v0, p1}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method public close()V
    .locals 1

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->mCount:I

    if-lez v0, :cond_0

    add-int/lit8 v0, v0, -0x1

    .line 2
    sput v0, Lcom/eckom/xtlibrary/b/f/f/s;->mCount:I

    if-nez v0, :cond_0

    .line 3
    invoke-virtual {p0}, Landroid/tw/john/TWUtil;->stop()V

    .line 4
    invoke-super {p0}, Landroid/tw/john/TWUtil;->close()V

    :cond_0
    return-void
.end method

.method public da(I)V
    .locals 2

    const v0, 0x9e11

    const/16 v1, 0xc0

    .line 1
    invoke-virtual {p0, v0, v1, p1}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public ea(I)V
    .locals 5

    const/4 v0, 0x0

    .line 1
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ld:I

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v1, :cond_4

    .line 4
    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-lez v1, :cond_4

    .line 5
    new-array v2, v1, [I

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    if-lt p1, v1, :cond_0

    move p1, v0

    .line 6
    :cond_0
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    aput p1, v2, v0

    const/4 v2, 0x1

    if-le v1, v2, :cond_4

    add-int/lit8 v2, p1, 0x1

    :goto_0
    if-ge v2, v1, :cond_2

    sub-int v3, v2, p1

    .line 7
    iget v4, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    if-eqz v4, :cond_1

    .line 8
    invoke-direct {p0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->m(II)I

    move-result v3

    .line 9
    :cond_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    aput v2, v4, v3

    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    :cond_2
    :goto_1
    if-ge v0, p1, :cond_4

    add-int v2, v0, v1

    sub-int/2addr v2, p1

    .line 10
    iget v3, p0, Lcom/eckom/xtlibrary/b/f/f/s;->hc:I

    if-eqz v3, :cond_3

    .line 11
    invoke-direct {p0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/s;->m(II)I

    move-result v2

    .line 12
    :cond_3
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/f/s;->kd:[I

    aput v0, v3, v2

    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    :cond_4
    return-void
.end method

.method public getService()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->mService:I

    return p0
.end method

.method public pa(Ljava/lang/String;)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

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

    invoke-direct {v0, p1, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    .line 4
    invoke-virtual {p0, v0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 5
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p1, :cond_2

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 7
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz p1, :cond_3

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    const-string v0, "SD"

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_3
    return-void
.end method

.method public qa(Ljava/lang/String;)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

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

    invoke-direct {v0, p1, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    .line 4
    invoke-virtual {p0, v0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 5
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p1, :cond_2

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 7
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz p1, :cond_3

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    const-string v0, "USB"

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_3
    return-void
.end method

.method public ra(Ljava/lang/String;)V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_4

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    const/4 v3, 0x1

    if-ne v2, v3, :cond_1

    .line 5
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    :cond_1
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    .line 7
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 8
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 9
    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v2

    if-lt v1, v2, :cond_2

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    sub-int/2addr v1, v3

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 11
    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    if-gez v1, :cond_2

    const/4 v1, 0x0

    .line 12
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 13
    :cond_2
    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_3

    .line 15
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 16
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_4
    :goto_0
    return-void
.end method

.method public sa(Ljava/lang/String;)V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_4

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget v2, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    const/4 v3, 0x1

    if-ne v2, v3, :cond_1

    .line 5
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->nk:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    :cond_1
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    .line 7
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 8
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 9
    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v2

    if-lt v1, v2, :cond_2

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    sub-int/2addr v1, v3

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 11
    iget v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    if-gez v1, :cond_2

    const/4 v1, 0x0

    .line 12
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 13
    :cond_2
    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_3

    .line 15
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 16
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_4
    :goto_0
    return-void
.end method

.method public ta(Ljava/lang/String;)V
    .locals 2

    if-nez p1, :cond_0

    return-void

    :cond_0
    const-string v0, "/mnt/sdcard/iNand"

    .line 1
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 p1, 0x3

    iput p1, p0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    goto/16 :goto_2

    :cond_1
    const-string v0, "/storage/usb"

    .line 4
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

    .line 5
    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_4

    const-string v0, "/mnt/extsd"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_3

    goto :goto_0

    .line 6
    :cond_3
    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 7
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iput v1, p0, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    goto :goto_2

    .line 8
    :cond_4
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_6

    .line 9
    iget p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt p1, v0, :cond_5

    .line 10
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    .line 11
    :cond_5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->xd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 12
    :cond_6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 13
    :cond_7
    :goto_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_9

    .line 14
    iget p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    if-lt p1, v0, :cond_8

    .line 15
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    .line 16
    :cond_8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->yd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_2

    .line 17
    :cond_9
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    :goto_2
    return-void
.end method

.method public w(Z)V
    .locals 0

    if-eqz p1, :cond_0

    const/4 p1, 0x3

    goto :goto_0

    :cond_0
    const/16 p1, 0x83

    .line 1
    :goto_0
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->da(I)V

    return-void
.end method

.method public z(Z)V
    .locals 8

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v1, 0x1

    const/4 v2, 0x0

    const-string v3, "SD"

    invoke-direct {v0, v3, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    sget-boolean v0, Lcom/eckom/xtlibrary/b/f/f/s;->Gd:Z

    const/4 v1, 0x2

    const-string v3, "USB"

    if-eqz v0, :cond_1

    .line 3
    new-instance v0, Ljava/io/File;

    const-string v4, "/mnt"

    invoke-direct {v0, v4}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v4, Lcom/eckom/xtlibrary/b/f/f/n;

    invoke-direct {v4, p0}, Lcom/eckom/xtlibrary/b/f/f/n;-><init>(Lcom/eckom/xtlibrary/b/f/f/s;)V

    invoke-virtual {v0, v4}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    array-length v4, v0

    move v5, v2

    :goto_0
    if-ge v5, v4, :cond_0

    aget-object v6, v0, v5

    .line 5
    invoke-virtual {v6}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v6

    invoke-virtual {p0, v6}, Lcom/eckom/xtlibrary/b/f/f/s;->pa(Ljava/lang/String;)V

    add-int/lit8 v5, v5, 0x1

    goto :goto_0

    .line 6
    :cond_0
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {v0, v3, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 7
    new-instance v0, Ljava/io/File;

    const-string v1, "/mnt/usbhost"

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v1, Lcom/eckom/xtlibrary/b/f/f/o;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/f/o;-><init>(Lcom/eckom/xtlibrary/b/f/f/s;)V

    invoke-virtual {v0, v1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    if-eqz v0, :cond_3

    .line 8
    array-length v1, v0

    move v3, v2

    :goto_1
    if-ge v3, v1, :cond_3

    aget-object v4, v0, v3

    .line 9
    invoke-virtual {v4}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->qa(Ljava/lang/String;)V

    add-int/lit8 v3, v3, 0x1

    goto :goto_1

    .line 10
    :cond_1
    new-instance v0, Ljava/io/File;

    const-string v4, "/storage"

    invoke-direct {v0, v4}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v5, Lcom/eckom/xtlibrary/b/f/f/p;

    invoke-direct {v5, p0}, Lcom/eckom/xtlibrary/b/f/f/p;-><init>(Lcom/eckom/xtlibrary/b/f/f/s;)V

    invoke-virtual {v0, v5}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    if-eqz v0, :cond_2

    .line 11
    array-length v5, v0

    move v6, v2

    :goto_2
    if-ge v6, v5, :cond_2

    aget-object v7, v0, v6

    .line 12
    invoke-virtual {v7}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v7

    invoke-virtual {p0, v7}, Lcom/eckom/xtlibrary/b/f/f/s;->pa(Ljava/lang/String;)V

    add-int/lit8 v6, v6, 0x1

    goto :goto_2

    .line 13
    :cond_2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-direct {v0, v3, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 14
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, v4}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v1, Lcom/eckom/xtlibrary/b/f/f/q;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/f/q;-><init>(Lcom/eckom/xtlibrary/b/f/f/s;)V

    invoke-virtual {v0, v1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    if-eqz v0, :cond_3

    .line 15
    array-length v1, v0

    move v3, v2

    :goto_3
    if-ge v3, v1, :cond_3

    aget-object v4, v0, v3

    .line 16
    invoke-virtual {v4}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->qa(Ljava/lang/String;)V

    add-int/lit8 v3, v3, 0x1

    goto :goto_3

    .line 17
    :cond_3
    new-instance v0, Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v1, 0x3

    const-string v3, "iNand"

    invoke-direct {v0, v3, v1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;-><init>(Ljava/lang/String;II)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 18
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz p1, :cond_4

    const-string p1, "/mnt/sdcard"

    goto :goto_4

    :cond_4
    const-string p1, "/mnt/sdcard/iNand"

    :goto_4
    invoke-virtual {p0, v0, p1}, Lcom/eckom/xtlibrary/b/f/f/s;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 19
    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 20
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez p1, :cond_7

    .line 21
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_5

    .line 22
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_5

    .line 23
    :cond_5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->qd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 24
    :goto_5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez p1, :cond_7

    .line 25
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_6

    .line 26
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_6

    .line 27
    :cond_6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->rd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 28
    :goto_6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez p1, :cond_7

    .line 29
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 30
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-nez p1, :cond_7

    .line 31
    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_7
    return-void
.end method

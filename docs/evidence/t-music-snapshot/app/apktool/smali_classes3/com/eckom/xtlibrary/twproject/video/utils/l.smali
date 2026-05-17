.class public Lcom/eckom/xtlibrary/twproject/video/utils/l;
.super Landroid/tw/john/TWUtil;
.source "TWVideo.java"


# static fields
.field public static Ad:I = 0x0

.field public static Bd:Ljava/lang/String; = null
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation
.end field

.field public static Cd:Ljava/lang/String; = null
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation
.end field

.field public static Hd:Z = false

.field public static Qd:Z = false

.field public static Rd:I = 0x0

.field public static Sd:I = 0x0

.field private static TAG:Ljava/lang/String; = "TWVideo"

.field public static hc:I

.field public static ic:I

.field private static jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

.field public static ld:I

.field private static mCount:I

.field public static md:I


# instance fields
.field public Dd:Lcom/eckom/xtlibrary/b/k/a/b;

.field public Ld:Z

.field public Md:I

.field public Nd:I

.field public Od:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field public Pd:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/k/a/a;",
            ">;"
        }
    .end annotation
.end field

.field public kd:[I

.field public mMediaPlayer:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

.field public mSource:I

.field public qd:Lcom/eckom/xtlibrary/b/k/a/b;

.field public rd:Lcom/eckom/xtlibrary/b/k/a/b;

.field public td:Lcom/eckom/xtlibrary/b/k/a/b;

.field public ud:Lcom/eckom/xtlibrary/b/k/a/b;

.field public vd:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/k/a/b;",
            ">;"
        }
    .end annotation
.end field

.field public wd:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/k/a/b;",
            ">;"
        }
    .end annotation
.end field

.field public xd:I

.field public yd:I


# direct methods
.method static constructor <clinit>()V
    .locals 3

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/l;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    const/4 v0, 0x0

    .line 2
    sput v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mCount:I

    .line 3
    sput-boolean v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Qd:Z

    const/4 v1, 0x1

    .line 4
    sput-boolean v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Hd:Z

    const/4 v2, -0x1

    .line 5
    sput v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Rd:I

    .line 6
    sput v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Sd:I

    .line 7
    sput v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    .line 8
    sput v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    .line 9
    sput v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    return-void
.end method

.method public constructor <init>()V
    .locals 2

    .line 1
    invoke-direct {p0}, Landroid/tw/john/TWUtil;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    .line 3
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    const/4 v1, 0x0

    .line 4
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mMediaPlayer:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    .line 5
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Od:Ljava/util/List;

    .line 6
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    .line 7
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    .line 8
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Pd:Ljava/util/ArrayList;

    .line 9
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->xd:I

    .line 10
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->yd:I

    return-void
.end method

.method public static Sa()V
    .locals 7

    const-string v0, "/data/tw/video"

    const/4 v1, 0x0

    .line 1
    :try_start_0
    new-instance v2, Ljava/io/BufferedWriter;

    new-instance v3, Ljava/io/FileWriter;

    invoke-direct {v3, v0}, Ljava/io/FileWriter;-><init>(Ljava/lang/String;)V

    invoke-direct {v2, v3}, Ljava/io/BufferedWriter;-><init>(Ljava/io/Writer;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 2
    :try_start_1
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Bd:Ljava/lang/String;

    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    const/16 v1, 0xa

    .line 3
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 4
    sget v3, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 5
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 6
    sget v3, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 7
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 8
    sget v3, Lcom/eckom/xtlibrary/twproject/video/utils/l;->hc:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 9
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 10
    sget v3, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 11
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 12
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->flush()V

    .line 13
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    const v3, 0x9f1a

    const/4 v4, 0x1

    const/4 v5, 0x0

    const-string v6, "sync"

    invoke-virtual {v1, v3, v4, v5, v6}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 14
    :try_start_2
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->close()V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    goto :goto_1

    :catchall_0
    move-exception v0

    goto :goto_2

    :catch_0
    move-object v1, v2

    goto :goto_0

    :catchall_1
    move-exception v0

    move-object v2, v1

    goto :goto_2

    .line 15
    :catch_1
    :goto_0
    :try_start_3
    new-instance v2, Ljava/io/File;

    invoke-direct {v2, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2}, Ljava/io/File;->delete()Z
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    if-eqz v1, :cond_0

    .line 16
    :try_start_4
    invoke-virtual {v1}, Ljava/io/BufferedWriter;->close()V

    :cond_0
    :goto_1
    const/16 v1, 0x1b6

    const/4 v2, -0x1

    .line 17
    invoke-static {v0, v1, v2, v2}, Landroid/os/FileUtils;->setPermissions(Ljava/lang/String;III)I

    goto :goto_3

    :goto_2
    if-eqz v2, :cond_1

    .line 18
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->close()V

    .line 19
    :cond_1
    throw v0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_2

    :catch_2
    :goto_3
    return-void
.end method


# virtual methods
.method public Ta()Z
    .locals 4
    .annotation build Landroid/annotation/TargetApi;
        value = 0x18
    .end annotation

    const/4 p0, 0x0

    .line 1
    :try_start_0
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x18

    if-lt v0, v1, :cond_1

    .line 2
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    invoke-interface {v0}, Landroid/view/IWindowManager;->getDockedStackSide()I

    move-result v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    if-lez v0, :cond_0

    const/4 p0, 0x1

    :cond_0
    return p0

    :catch_0
    move-exception v0

    .line 3
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->TAG:Ljava/lang/String;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "getMultiWindowMode:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v1, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_1
    return p0
.end method

.method public a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V
    .locals 8

    if-eqz p2, :cond_1

    if-eqz p3, :cond_1

    .line 31
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, p3}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/utils/k;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/k;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/l;)V

    invoke-virtual {v0, v1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object v0

    const/4 v1, 0x0

    .line 32
    iput v1, p2, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    if-eqz v0, :cond_1

    .line 33
    array-length v2, v0

    invoke-virtual {p2, v2}, Lcom/eckom/xtlibrary/b/k/a/b;->setLength(I)V

    .line 34
    array-length v2, v0

    move v3, v1

    :goto_0
    if-ge v3, v2, :cond_1

    aget-object v4, v0, v3

    .line 35
    invoke-virtual {v4}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v5

    .line 36
    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v6, p3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v7, "/"

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    if-eqz p1, :cond_0

    .line 37
    iget-object v7, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Pd:Ljava/util/ArrayList;

    invoke-static {p1, v6, v7}, Lcom/eckom/xtlibrary/twproject/video/utils/b;->b(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v6

    goto :goto_1

    :cond_0
    move v6, v1

    :goto_1
    const-string v7, "."

    .line 38
    invoke-virtual {v5, v7}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v7

    invoke-virtual {v5, v1, v7}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v4}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p2, v5, v4, v6}, Lcom/eckom/xtlibrary/b/k/a/b;->a(Ljava/lang/String;Ljava/lang/String;Z)V

    add-int/lit8 v3, v3, 0x1

    goto :goto_0

    :cond_1
    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V
    .locals 5

    const-string p0, "/"

    if-eqz p1, :cond_a

    if-eqz p2, :cond_a

    const/4 v0, 0x0

    .line 1
    :try_start_0
    sget-boolean v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Qd:Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    const-string v2, "/DCIM"

    const-string v3, "/data/tw/"

    if-eqz v1, :cond_2

    :try_start_1
    const-string v1, "/mnt/extsd"

    .line 2
    invoke-virtual {p2, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 3
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/4 v2, 0x5

    invoke-virtual {p2, v2}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    goto :goto_1

    :cond_0
    const-string v1, "/mnt/usbhost/Storage"

    .line 4
    invoke-virtual {p2, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v1

    if-eqz v1, :cond_1

    .line 5
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/16 v2, 0xd

    invoke-virtual {p2, v2}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    goto :goto_1

    .line 6
    :cond_1
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    goto :goto_1

    :cond_2
    const-string v1, "/storage/usb"

    .line 7
    invoke-virtual {p2, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v1

    if-nez v1, :cond_4

    const-string v1, "/storage/extsd"

    invoke-virtual {p2, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v1

    if-eqz v1, :cond_3

    goto :goto_0

    .line 8
    :cond_3
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    goto :goto_1

    .line 9
    :cond_4
    :goto_0
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const/16 v2, 0x9

    invoke-virtual {p2, v2}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    .line 10
    :goto_1
    new-instance v2, Ljava/io/BufferedReader;

    new-instance v3, Ljava/io/FileReader;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "/.video"

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v3, v1}, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V

    invoke-direct {v2, v3}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    .line 11
    :try_start_2
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    .line 12
    :cond_5
    :goto_2
    invoke-virtual {v2}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v1

    if-eqz v1, :cond_7

    .line 13
    new-instance v3, Ljava/io/File;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v3, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 14
    invoke-virtual {v3}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v1

    sget-object v4, Ljava/util/Locale;->ENGLISH:Ljava/util/Locale;

    invoke-virtual {v1, v4}, Ljava/lang/String;->toUpperCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object v1

    .line 15
    invoke-virtual {v3}, Ljava/io/File;->canRead()Z

    move-result v4

    if-eqz v4, :cond_5

    invoke-virtual {v3}, Ljava/io/File;->isDirectory()Z

    move-result v4

    if-eqz v4, :cond_5

    const-string v4, "ECC"

    .line 16
    invoke-virtual {v1, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v1

    if-nez v1, :cond_5

    .line 17
    invoke-virtual {v3}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v1

    .line 18
    invoke-virtual {v3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v3

    const-string v4, "."

    .line 19
    invoke-virtual {v1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_6

    const/4 v1, 0x0

    .line 20
    invoke-virtual {v3, p0}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v4

    invoke-virtual {v3, v1, v4}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v1

    .line 21
    invoke-virtual {v1, p0}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v4

    add-int/lit8 v4, v4, 0x1

    invoke-virtual {v1, v4}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v1

    .line 22
    new-instance v4, Lcom/eckom/xtlibrary/b/k/a/a;

    invoke-direct {v4, v1, v3}, Lcom/eckom/xtlibrary/b/k/a/a;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v0, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_2

    .line 23
    :cond_6
    new-instance v4, Lcom/eckom/xtlibrary/b/k/a/a;

    invoke-direct {v4, v1, v3}, Lcom/eckom/xtlibrary/b/k/a/a;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v0, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_2

    .line 24
    :cond_7
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result p0

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/k/a/b;->setLength(I)V

    .line 25
    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_3
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result p2

    if-eqz p2, :cond_8

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Lcom/eckom/xtlibrary/b/k/a/a;

    .line 26
    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/k/a/b;->a(Lcom/eckom/xtlibrary/b/k/a/a;)V

    goto :goto_3

    .line 27
    :cond_8
    invoke-virtual {v0}, Ljava/util/ArrayList;->clear()V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    .line 28
    :try_start_3
    invoke-virtual {v2}, Ljava/io/BufferedReader;->close()V

    goto :goto_6

    :catchall_0
    move-exception p0

    move-object v0, v2

    goto :goto_4

    :catch_0
    move-object v0, v2

    goto :goto_5

    :catchall_1
    move-exception p0

    :goto_4
    if-eqz v0, :cond_9

    invoke-virtual {v0}, Ljava/io/BufferedReader;->close()V

    .line 29
    :cond_9
    throw p0

    :catch_1
    :goto_5
    if-eqz v0, :cond_a

    .line 30
    invoke-virtual {v0}, Ljava/io/BufferedReader;->close()V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_2

    :catch_2
    :cond_a
    :goto_6
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

.method public close()V
    .locals 1

    .line 1
    sget v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mCount:I

    if-lez v0, :cond_0

    add-int/lit8 v0, v0, -0x1

    .line 2
    sput v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mCount:I

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

.method public pa(Ljava/lang/String;)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/k/a/b;

    .line 2
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-void

    .line 3
    :cond_1
    new-instance v0, Lcom/eckom/xtlibrary/b/k/a/b;

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-direct {v0, p1, v1, v2}, Lcom/eckom/xtlibrary/b/k/a/b;-><init>(Ljava/lang/String;II)V

    .line 4
    invoke-virtual {p0, v0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    if-eqz p1, :cond_2

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    const-string v0, "SD"

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_2

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/k/a/b;

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    :cond_2
    return-void
.end method

.method public qa(Ljava/lang/String;)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/k/a/b;

    .line 2
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-void

    .line 3
    :cond_1
    new-instance v0, Lcom/eckom/xtlibrary/b/k/a/b;

    const/4 v1, 0x2

    const/4 v2, 0x0

    invoke-direct {v0, p1, v1, v2}, Lcom/eckom/xtlibrary/b/k/a/b;-><init>(Ljava/lang/String;II)V

    .line 4
    invoke-virtual {p0, v0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    if-eqz p1, :cond_2

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    const-string v0, "USB"

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_2

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/k/a/b;

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    :cond_2
    return-void
.end method

.method public ra(Ljava/lang/String;)V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_4

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/k/a/b;

    .line 2
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    .line 4
    iget v2, v0, Lcom/eckom/xtlibrary/b/k/a/b;->ik:I

    const/4 v3, 0x1

    if-ne v2, v3, :cond_1

    .line 5
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/k/a/b;->nk:Lcom/eckom/xtlibrary/b/k/a/b;

    .line 6
    :cond_1
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    .line 7
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    .line 8
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 9
    iget v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->xd:I

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v2

    if-lt v1, v2, :cond_2

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    sub-int/2addr v1, v3

    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->xd:I

    .line 11
    iget v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->xd:I

    if-gez v1, :cond_2

    const/4 v1, 0x0

    .line 12
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->xd:I

    .line 13
    :cond_2
    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_3

    .line 15
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->vd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->xd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/k/a/b;

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    goto :goto_0

    .line 16
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->qd:Lcom/eckom/xtlibrary/b/k/a/b;

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    :cond_4
    :goto_0
    return-void
.end method

.method public sa(Ljava/lang/String;)V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_4

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/k/a/b;

    .line 2
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    .line 4
    iget v2, v0, Lcom/eckom/xtlibrary/b/k/a/b;->ik:I

    const/4 v3, 0x1

    if-ne v2, v3, :cond_1

    .line 5
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/k/a/b;->nk:Lcom/eckom/xtlibrary/b/k/a/b;

    .line 6
    :cond_1
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    .line 7
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    .line 8
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 9
    iget v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->yd:I

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v2

    if-lt v1, v2, :cond_2

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v1

    sub-int/2addr v1, v3

    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->yd:I

    .line 11
    iget v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->yd:I

    if-gez v1, :cond_2

    const/4 v1, 0x0

    .line 12
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->yd:I

    .line 13
    :cond_2
    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_3

    .line 15
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->wd:Ljava/util/ArrayList;

    iget v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->yd:I

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/k/a/b;

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    goto :goto_0

    .line 16
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->rd:Lcom/eckom/xtlibrary/b/k/a/b;

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    :cond_4
    :goto_0
    return-void
.end method

.method public w(Z)V
    .locals 0

    if-eqz p1, :cond_0

    const/16 p1, 0x9

    goto :goto_0

    :cond_0
    const/16 p1, 0x89

    .line 1
    :goto_0
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->da(I)V

    return-void
.end method

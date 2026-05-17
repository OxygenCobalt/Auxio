.class public Lcom/eckom/xtlibrary/b/f/a/a;
.super Ljava/lang/Thread;
.source "MusicMediaParseTask.java"


# instance fields
.field private Dn:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field private En:Ljava/util/concurrent/CopyOnWriteArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;"
        }
    .end annotation
.end field

.field public Zc:Lcom/eckom/xtlibrary/b/f/c/a;

.field private mContext:Landroid/content/Context;

.field private mKey:Ljava/lang/String;

.field private path:Ljava/lang/String;

.field private xk:Ljava/util/concurrent/CopyOnWriteArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;"
        }
    .end annotation
.end field

.field private yk:Ljava/util/concurrent/CopyOnWriteArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lcom/eckom/xtlibrary/b/f/c/a;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Ljava/lang/String;",
            "Ljava/lang/String;",
            "Ljava/util/List<",
            "Ljava/lang/String;",
            ">;",
            "Lcom/eckom/xtlibrary/b/f/c/a;",
            ")V"
        }
    .end annotation

    .line 1
    invoke-direct {p0}, Ljava/lang/Thread;-><init>()V

    .line 2
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->Dn:Ljava/util/List;

    .line 3
    new-instance v0, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->En:Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 4
    new-instance v0, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 5
    new-instance v0, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 6
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/a/a;->path:Ljava/lang/String;

    .line 7
    iput-object p3, p0, Lcom/eckom/xtlibrary/b/f/a/a;->mKey:Ljava/lang/String;

    .line 8
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/a/a;->Dn:Ljava/util/List;

    invoke-interface {p2, p4}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 9
    iput-object p5, p0, Lcom/eckom/xtlibrary/b/f/a/a;->Zc:Lcom/eckom/xtlibrary/b/f/c/a;

    .line 10
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/a/a;->mContext:Landroid/content/Context;

    return-void
.end method

.method private b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 7

    .line 1
    new-instance v6, Lcom/eckom/xtlibrary/b/f/b/f;

    const/4 v5, 0x0

    move-object v0, v6

    move-object v1, p1

    move-object v2, p4

    move-object v3, p2

    move-object v4, p3

    invoke-direct/range {v0 .. v5}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;)V

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/a/a;->En:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {p1, v6}, Ljava/util/concurrent/CopyOnWriteArrayList;->add(Ljava/lang/Object;)Z

    .line 3
    new-instance p1, Lcom/eckom/xtlibrary/b/f/b/d;

    invoke-direct {p1, p3, v6}, Lcom/eckom/xtlibrary/b/f/b/d;-><init>(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/f;)V

    .line 4
    iget-object p4, p0, Lcom/eckom/xtlibrary/b/f/a/a;->mKey:Ljava/lang/String;

    invoke-virtual {p1, p4}, Lcom/eckom/xtlibrary/b/f/b/d;->setKey(Ljava/lang/String;)V

    .line 5
    iget-object p4, p0, Lcom/eckom/xtlibrary/b/f/a/a;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-static {p4, p3}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/f/b/d;

    move-result-object p3

    if-eqz p3, :cond_0

    .line 6
    invoke-virtual {p3}, Lcom/eckom/xtlibrary/b/f/b/d;->tc()Ljava/util/ArrayList;

    move-result-object p1

    invoke-virtual {p1, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_0

    .line 7
    :cond_0
    iget-object p3, p0, Lcom/eckom/xtlibrary/b/f/a/a;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {p3, p1}, Ljava/util/concurrent/CopyOnWriteArrayList;->add(Ljava/lang/Object;)Z

    .line 8
    :goto_0
    new-instance p1, Lcom/eckom/xtlibrary/b/f/b/d;

    invoke-direct {p1, p2, v6}, Lcom/eckom/xtlibrary/b/f/b/d;-><init>(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/f;)V

    .line 9
    iget-object p3, p0, Lcom/eckom/xtlibrary/b/f/a/a;->mKey:Ljava/lang/String;

    invoke-virtual {p1, p3}, Lcom/eckom/xtlibrary/b/f/b/d;->setKey(Ljava/lang/String;)V

    .line 10
    iget-object p3, p0, Lcom/eckom/xtlibrary/b/f/a/a;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-static {p3, p2}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/f/b/d;

    move-result-object p2

    if-eqz p2, :cond_1

    .line 11
    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/f/b/d;->tc()Ljava/util/ArrayList;

    move-result-object p0

    invoke-virtual {p0, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_1

    .line 12
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {p0, p1}, Ljava/util/concurrent/CopyOnWriteArrayList;->add(Ljava/lang/Object;)Z

    :goto_1
    return-void
.end method

.method private c(Ljava/io/File;)V
    .locals 9

    const-string v0, "MusicMediaParseTask"

    const-string v1, "unKnown"

    if-eqz p1, :cond_4

    .line 1
    invoke-virtual {p1}, Ljava/io/File;->exists()Z

    move-result v2

    if-eqz v2, :cond_4

    invoke-virtual {p1}, Ljava/io/File;->canRead()Z

    move-result v2

    if-eqz v2, :cond_4

    .line 2
    new-instance v2, Landroid/media/MediaMetadataRetriever;

    invoke-direct {v2}, Landroid/media/MediaMetadataRetriever;-><init>()V

    .line 3
    :try_start_0
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "getMusicinfo:00000 "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/io/File;->getPath()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v0, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 4
    invoke-virtual {p1}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Landroid/media/MediaMetadataRetriever;->setDataSource(Ljava/lang/String;)V

    const/4 v3, 0x2

    .line 5
    invoke-virtual {v2, v3}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v3
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_4
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    const/4 v4, 0x1

    .line 6
    :try_start_1
    invoke-virtual {v2, v4}, Landroid/media/MediaMetadataRetriever;->extractMetadata(I)Ljava/lang/String;

    move-result-object v4
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_3
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 7
    :try_start_2
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "getMusicinfo:11111 "

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/io/File;->getPath()Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v0, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 8
    invoke-virtual {p1}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v0

    const/4 v5, 0x0

    invoke-virtual {p1}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v6

    const-string v7, "."

    invoke-virtual {v6, v7}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v6

    invoke-virtual {v0, v5, v6}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v0
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    const-string v5, ""

    if-eqz v4, :cond_0

    .line 9
    :try_start_3
    invoke-virtual {v4, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v6

    if-eqz v6, :cond_1

    goto :goto_0

    :catch_0
    move-exception v1

    move-object v8, v1

    move-object v1, v0

    move-object v0, v8

    goto :goto_2

    :cond_0
    :goto_0
    move-object v4, v1

    :cond_1
    if-eqz v3, :cond_3

    .line 10
    invoke-virtual {v3, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_0
    .catchall {:try_start_3 .. :try_end_3} :catchall_0

    if-eqz v5, :cond_2

    goto :goto_1

    :cond_2
    move-object v1, v3

    .line 11
    :cond_3
    :goto_1
    :try_start_4
    invoke-virtual {p1}, Ljava/io/File;->getPath()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0, v0, v4, v1, v3}, Lcom/eckom/xtlibrary/b/f/a/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_1
    .catchall {:try_start_4 .. :try_end_4} :catchall_0

    goto :goto_3

    :catch_1
    move-exception v3

    move-object v8, v1

    move-object v1, v0

    move-object v0, v3

    move-object v3, v8

    goto :goto_2

    :catch_2
    move-exception v0

    goto :goto_2

    :catch_3
    move-exception v0

    move-object v4, v1

    goto :goto_2

    :catchall_0
    move-exception p0

    goto :goto_4

    :catch_4
    move-exception v0

    move-object v3, v1

    move-object v4, v3

    .line 12
    :goto_2
    :try_start_5
    invoke-virtual {p1}, Ljava/io/File;->getPath()Ljava/lang/String;

    move-result-object p1

    invoke-direct {p0, v1, v4, v3, p1}, Lcom/eckom/xtlibrary/b/f/a/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 13
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_0

    .line 14
    :goto_3
    invoke-virtual {v2}, Landroid/media/MediaMetadataRetriever;->release()V

    goto :goto_5

    :goto_4
    invoke-virtual {v2}, Landroid/media/MediaMetadataRetriever;->release()V

    throw p0

    :cond_4
    :goto_5
    return-void
.end method


# virtual methods
.method public jb(Ljava/lang/String;)V
    .locals 5

    .line 1
    new-instance p1, Ljava/text/SimpleDateFormat;

    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v0

    const-string v1, "yyyy-MM-dd HH:mm:ss"

    invoke-direct {p1, v1, v0}, Ljava/text/SimpleDateFormat;-><init>(Ljava/lang/String;Ljava/util/Locale;)V

    .line 2
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "scanMedia1:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Thread;->getName()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, " currentTime="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    new-instance v2, Ljava/util/Date;

    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v3

    invoke-direct {v2, v3, v4}, Ljava/util/Date;-><init>(J)V

    invoke-virtual {p1, v2}, Ljava/text/SimpleDateFormat;->format(Ljava/util/Date;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v2, "MusicMediaParseTask"

    invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->Dn:Ljava/util/List;

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 4
    :goto_0
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/a/a;->Dn:Ljava/util/List;

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v3

    if-ge v0, v3, :cond_0

    .line 5
    new-instance v3, Ljava/io/File;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/a/a;->Dn:Ljava/util/List;

    invoke-interface {v4, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/lang/String;

    invoke-direct {v3, v4}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-direct {p0, v3}, Lcom/eckom/xtlibrary/b/f/a/a;->c(Ljava/io/File;)V

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 6
    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "scanMedia2:"

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Thread;->getName()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    new-instance p0, Ljava/util/Date;

    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v3

    invoke-direct {p0, v3, v4}, Ljava/util/Date;-><init>(J)V

    invoke-virtual {p1, p0}, Ljava/text/SimpleDateFormat;->format(Ljava/util/Date;)Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v2, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

.method public run()V
    .locals 7

    .line 1
    invoke-super {p0}, Ljava/lang/Thread;->run()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->En:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;->clear()V

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/a;->path:Ljava/lang/String;

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/a/a;->jb(Ljava/lang/String;)V

    .line 6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/a/a;->Zc:Lcom/eckom/xtlibrary/b/f/c/a;

    if-eqz v1, :cond_0

    .line 7
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/a/a;->path:Ljava/lang/String;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/a/a;->mKey:Ljava/lang/String;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/f/a/a;->En:Ljava/util/concurrent/CopyOnWriteArrayList;

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/f/a/a;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    iget-object v6, p0, Lcom/eckom/xtlibrary/b/f/a/a;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-interface/range {v1 .. v6}, Lcom/eckom/xtlibrary/b/f/c/a;->a(Ljava/lang/String;Ljava/lang/String;Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    :cond_0
    return-void
.end method

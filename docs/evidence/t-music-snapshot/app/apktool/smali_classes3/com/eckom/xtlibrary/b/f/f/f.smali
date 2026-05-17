.class Lcom/eckom/xtlibrary/b/f/f/f;
.super Ljava/lang/Thread;
.source "MusicUtils.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/e;Landroid/tw/john/TWUtil;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic Gn:Landroid/tw/john/TWUtil;

.field final synthetic hm:Lcom/eckom/xtlibrary/b/f/b/e;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/b/e;Landroid/tw/john/TWUtil;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/f;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/f/f;->Gn:Landroid/tw/john/TWUtil;

    invoke-direct {p0}, Ljava/lang/Thread;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 6

    const-string v0, "/data/tw/music"

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
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/f;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    const/16 v1, 0xa

    .line 3
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 4
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/f/f;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 5
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 6
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/f/f;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 7
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 8
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/f/f;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 9
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 10
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/f/f;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->hc:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 11
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 12
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/f/f;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->ic:I

    invoke-static {v3}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 13
    invoke-virtual {v2, v1}, Ljava/io/BufferedWriter;->write(I)V

    .line 14
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->flush()V

    .line 15
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/f;->Gn:Landroid/tw/john/TWUtil;

    const v1, 0x9f1a

    const/4 v3, 0x1

    const/4 v4, 0x0

    const-string v5, "sync"

    invoke-virtual {p0, v1, v3, v4, v5}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 16
    :try_start_2
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->close()V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    goto :goto_1

    :catchall_0
    move-exception p0

    goto :goto_2

    :catch_0
    move-object v1, v2

    goto :goto_0

    :catchall_1
    move-exception p0

    move-object v2, v1

    goto :goto_2

    .line 17
    :catch_1
    :goto_0
    :try_start_3
    new-instance p0, Ljava/io/File;

    invoke-direct {p0, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0}, Ljava/io/File;->delete()Z
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    if-eqz v1, :cond_0

    .line 18
    :try_start_4
    invoke-virtual {v1}, Ljava/io/BufferedWriter;->close()V

    :cond_0
    :goto_1
    const/16 p0, 0x1b6

    const/4 v1, -0x1

    .line 19
    invoke-static {v0, p0, v1, v1}, Landroid/os/FileUtils;->setPermissions(Ljava/lang/String;III)I

    goto :goto_3

    :goto_2
    if-eqz v2, :cond_1

    .line 20
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->close()V

    .line 21
    :cond_1
    throw p0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_2

    :catch_2
    move-exception p0

    .line 22
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/h;->TAG:Ljava/lang/String;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, ""

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :goto_3
    return-void
.end method

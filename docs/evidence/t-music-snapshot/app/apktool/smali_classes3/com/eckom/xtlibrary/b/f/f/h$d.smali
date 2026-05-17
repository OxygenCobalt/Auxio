.class public Lcom/eckom/xtlibrary/b/f/f/h$d;
.super Landroid/os/AsyncTask;
.source "MusicUtils.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/f/h;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "d"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/os/AsyncTask<",
        "Ljava/lang/Void;",
        "Ljava/lang/Void;",
        "Lcom/eckom/xtlibrary/b/f/b/g;",
        ">;"
    }
.end annotation


# instance fields
.field private Uc:Z

.field private Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

.field private Wc:Ljava/lang/String;

.field private mRecord:Lcom/eckom/xtlibrary/b/f/b/g;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V
    .locals 0

    .line 1
    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Wc:Ljava/lang/String;

    .line 4
    iput-boolean p3, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Uc:Z

    return-void
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/f/f/h$a;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    return-void
.end method

.method protected b(Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 2

    .line 1
    invoke-super {p0, p1}, Landroid/os/AsyncTask;->onPostExecute(Ljava/lang/Object;)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    if-eqz v0, :cond_0

    .line 3
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "\u5b8c\u6210\u8bfb\u53d6\uff1a"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Wc:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->d(Ljava/lang/Object;)V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Wc:Ljava/lang/String;

    invoke-interface {v0, p1, p0}, Lcom/eckom/xtlibrary/b/f/f/h$a;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method protected varargs doInBackground([Ljava/lang/Void;)Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 8

    const-string p1, "."

    const-string v0, ""

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    if-eqz v1, :cond_0

    .line 3
    invoke-interface {v1}, Lcom/eckom/xtlibrary/b/f/f/h$a;->S()V

    .line 4
    :cond_0
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    .line 5
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v2, :cond_5

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Wc:Ljava/lang/String;

    if-eqz v2, :cond_5

    const/4 v3, 0x0

    .line 6
    :try_start_0
    new-instance v4, Ljava/io/BufferedReader;

    new-instance v5, Ljava/io/FileReader;

    invoke-direct {v5, v2}, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V

    invoke-direct {v4, v5}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 7
    :cond_1
    :goto_0
    :try_start_1
    invoke-virtual {v4}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v2

    if-eqz v2, :cond_2

    .line 8
    new-instance v3, Ljava/io/File;

    invoke-direct {v3, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 9
    invoke-virtual {v3}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v5

    const/4 v6, 0x0

    .line 10
    invoke-virtual {v5, p1}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v7

    invoke-virtual {v5, v6, v7}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v6

    .line 11
    invoke-virtual {v3}, Ljava/io/File;->isFile()Z

    move-result v3

    if-eqz v3, :cond_1

    invoke-virtual {v5, p1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v3

    if-nez v3, :cond_1

    sget-object v3, Ljava/util/Locale;->ENGLISH:Ljava/util/Locale;

    invoke-virtual {v5, v3}, Ljava/lang/String;->toUpperCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/f/h;->isAudio(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_1

    .line 12
    new-instance v3, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-direct {v3, v6, v2}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v1, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_0

    .line 13
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v2

    invoke-virtual {p1, v2}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-boolean v2, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->Uc:Z

    invoke-static {p1, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/List;Z)V

    .line 15
    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_1
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_3

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 16
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v3, v2}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    goto :goto_1

    .line 17
    :cond_3
    invoke-virtual {v1}, Ljava/util/ArrayList;->clear()V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 18
    :try_start_2
    invoke-virtual {v4}, Ljava/io/BufferedReader;->close()V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    goto :goto_4

    :catchall_0
    move-exception p1

    goto :goto_3

    :catch_0
    move-exception p1

    move-object v3, v4

    goto :goto_2

    :catchall_1
    move-exception p1

    move-object v4, v3

    goto :goto_3

    :catch_1
    move-exception p1

    .line 19
    :goto_2
    :try_start_3
    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/h;->TAG:Ljava/lang/String;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v1, p1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    if-eqz v3, :cond_5

    .line 20
    :try_start_4
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V

    goto :goto_4

    :goto_3
    if-eqz v4, :cond_4

    invoke-virtual {v4}, Ljava/io/BufferedReader;->close()V

    .line 21
    :cond_4
    throw p1
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_2

    :catch_2
    move-exception p1

    .line 22
    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/h;->TAG:Ljava/lang/String;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v1, p1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 23
    :cond_5
    :goto_4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$d;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    return-object p0
.end method

.method protected bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    .line 1
    check-cast p1, [Ljava/lang/Void;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$d;->doInBackground([Ljava/lang/Void;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p0

    return-object p0
.end method

.method protected bridge synthetic onPostExecute(Ljava/lang/Object;)V
    .locals 0

    .line 1
    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$d;->b(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

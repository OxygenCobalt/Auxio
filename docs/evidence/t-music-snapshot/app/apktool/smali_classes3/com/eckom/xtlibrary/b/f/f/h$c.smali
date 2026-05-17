.class public Lcom/eckom/xtlibrary/b/f/f/h$c;
.super Landroid/os/AsyncTask;
.source "MusicUtils.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/f/h;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "c"
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

.field private Wc:Ljava/lang/String;

.field private Xc:Lcom/eckom/xtlibrary/b/f/f/h$a;

.field private mRecord:Lcom/eckom/xtlibrary/b/f/b/g;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V
    .locals 0

    .line 1
    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Wc:Ljava/lang/String;

    .line 4
    iput-boolean p3, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Uc:Z

    return-void
.end method


# virtual methods
.method protected b(Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 2

    .line 2
    invoke-super {p0, p1}, Landroid/os/AsyncTask;->onPostExecute(Ljava/lang/Object;)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Xc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    if-eqz v0, :cond_0

    .line 4
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "\u5b8c\u6210\u8bfb\u53d6\uff1a"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Wc:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->d(Ljava/lang/Object;)V

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Xc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Wc:Ljava/lang/String;

    invoke-interface {v0, p1, p0}, Lcom/eckom/xtlibrary/b/f/f/h$a;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/f/f/h$a;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Xc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    return-void
.end method

.method protected varargs doInBackground([Ljava/lang/Void;)Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 3

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Xc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    if-eqz p1, :cond_0

    .line 3
    invoke-interface {p1}, Lcom/eckom/xtlibrary/b/f/f/h$a;->S()V

    .line 4
    :cond_0
    new-instance p1, Ljava/util/ArrayList;

    invoke-direct {p1}, Ljava/util/ArrayList;-><init>()V

    .line 5
    new-instance v0, Ljava/io/File;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Wc:Ljava/lang/String;

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance v1, Lcom/eckom/xtlibrary/b/f/f/j;

    invoke-direct {v1, p0, p1}, Lcom/eckom/xtlibrary/b/f/f/j;-><init>(Lcom/eckom/xtlibrary/b/f/f/h$c;Ljava/util/ArrayList;)V

    invoke-virtual {v0, v1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result v1

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->Uc:Z

    invoke-static {v0, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/List;Z)V

    .line 8
    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 9
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v2, v1}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    goto :goto_0

    .line 10
    :cond_1
    invoke-virtual {p1}, Ljava/util/ArrayList;->clear()V

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    return-object p0
.end method

.method protected bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    .line 1
    check-cast p1, [Ljava/lang/Void;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$c;->doInBackground([Ljava/lang/Void;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p0

    return-object p0
.end method

.method protected bridge synthetic onPostExecute(Ljava/lang/Object;)V
    .locals 0

    .line 1
    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$c;->b(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

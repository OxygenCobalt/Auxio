.class public Lcom/eckom/xtlibrary/b/f/f/h$b;
.super Landroid/os/AsyncTask;
.source "MusicUtils.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/f/h;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "b"
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
.field private Tc:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;"
        }
    .end annotation
.end field

.field private Uc:Z

.field Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

.field private mContext:Landroid/content/Context;

.field private mPath:Ljava/lang/String;

.field private mRecord:Lcom/eckom/xtlibrary/b/f/b/g;


# direct methods
.method public constructor <init>(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Ljava/util/ArrayList;Z)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Lcom/eckom/xtlibrary/b/f/b/g;",
            "Ljava/lang/String;",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;Z)V"
        }
    .end annotation

    .line 1
    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mContext:Landroid/content/Context;

    .line 3
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iput-object p3, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mPath:Ljava/lang/String;

    .line 5
    iput-object p4, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->Tc:Ljava/util/ArrayList;

    .line 6
    iput-boolean p5, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->Uc:Z

    return-void
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/f/f/h$a;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    return-void
.end method

.method protected b(Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 2

    .line 1
    invoke-super {p0, p1}, Landroid/os/AsyncTask;->onPostExecute(Ljava/lang/Object;)V

    .line 2
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "\u5b8c\u6210\u626b\u63cf\uff1a"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mPath:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/eckom/xtlibrary/a/b;->d(Ljava/lang/Object;)V

    if-eqz p1, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    if-eqz v0, :cond_0

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mPath:Ljava/lang/String;

    invoke-interface {v0, p1, p0}, Lcom/eckom/xtlibrary/b/f/f/h$a;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method protected varargs doInBackground([Ljava/lang/Void;)Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 9

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->Vc:Lcom/eckom/xtlibrary/b/f/f/h$a;

    if-eqz p1, :cond_0

    .line 3
    invoke-interface {p1}, Lcom/eckom/xtlibrary/b/f/f/h$a;->S()V

    .line 4
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz p1, :cond_2

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mPath:Ljava/lang/String;

    if-eqz p1, :cond_2

    .line 5
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    new-instance p1, Lcom/eckom/xtlibrary/b/f/f/i;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/b/f/f/i;-><init>(Lcom/eckom/xtlibrary/b/f/f/h$b;)V

    invoke-virtual {v0, p1}, Ljava/io/File;->listFiles(Ljava/io/FileFilter;)[Ljava/io/File;

    move-result-object p1

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v1, 0x0

    iput v1, v0, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    .line 7
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    if-eqz p1, :cond_1

    .line 8
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    array-length v3, p1

    invoke-virtual {v2, v3}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 9
    array-length v2, p1

    move v3, v1

    :goto_0
    if-ge v3, v2, :cond_1

    aget-object v4, p1, v3

    .line 10
    invoke-virtual {v4}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v5

    .line 11
    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v7, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mPath:Ljava/lang/String;

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v7, "/"

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    .line 12
    iget-object v7, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mContext:Landroid/content/Context;

    iget-object v8, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->Tc:Ljava/util/ArrayList;

    invoke-static {v7, v6, v8}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v6

    .line 13
    new-instance v7, Lcom/eckom/xtlibrary/b/f/b/f;

    const-string v8, "."

    invoke-virtual {v5, v8}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v8

    invoke-virtual {v5, v1, v8}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v4}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-direct {v7, v5, v4, v6}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;Z)V

    .line 14
    invoke-interface {v0, v7}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    add-int/lit8 v3, v3, 0x1

    goto :goto_0

    .line 15
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->Uc:Z

    invoke-static {p1, v0, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/List;Z)V

    .line 16
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$b;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    return-object p0
.end method

.method protected bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    .line 1
    check-cast p1, [Ljava/lang/Void;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$b;->doInBackground([Ljava/lang/Void;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p0

    return-object p0
.end method

.method protected bridge synthetic onPostExecute(Ljava/lang/Object;)V
    .locals 0

    .line 1
    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$b;->b(Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.method protected onPreExecute()V
    .locals 0

    .line 1
    invoke-super {p0}, Landroid/os/AsyncTask;->onPreExecute()V

    return-void
.end method

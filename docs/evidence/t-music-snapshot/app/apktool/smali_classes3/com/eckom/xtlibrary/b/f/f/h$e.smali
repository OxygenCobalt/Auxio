.class public Lcom/eckom/xtlibrary/b/f/f/h$e;
.super Landroid/os/AsyncTask;
.source "MusicUtils.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/f/h;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "e"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/os/AsyncTask<",
        "Ljava/lang/String;",
        "Ljava/lang/Void;",
        "Ljava/lang/String;",
        ">;"
    }
.end annotation


# instance fields
.field Yc:Lcom/eckom/xtlibrary/b/f/b/e;

.field Zc:Lcom/eckom/xtlibrary/b/f/f/h$f;

.field isForward:Z

.field path:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/e;Z)V
    .locals 0

    .line 1
    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    .line 2
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    .line 3
    iput-boolean p3, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->isForward:Z

    .line 4
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->path:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method protected varargs a([Ljava/lang/String;)Ljava/lang/String;
    .locals 3

    .line 2
    new-instance p1, Lcom/eckom/xtlibrary/b/j/m;

    invoke-direct {p1}, Lcom/eckom/xtlibrary/b/j/m;-><init>()V

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->path:Ljava/lang/String;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-boolean v2, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->isForward:Z

    invoke-static {v2}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->Zc:Lcom/eckom/xtlibrary/b/f/f/h$f;

    invoke-virtual {p1, v0, v1, v2, p0}, Lcom/eckom/xtlibrary/b/j/m;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/e;Ljava/lang/Boolean;Lcom/eckom/xtlibrary/b/f/f/h$f;)Ljava/lang/String;

    return-object v0
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/f/h$f;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->Zc:Lcom/eckom/xtlibrary/b/f/f/h$f;

    return-void
.end method

.method protected bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    .line 1
    check-cast p1, [Ljava/lang/String;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$e;->a([Ljava/lang/String;)Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method protected oa(Ljava/lang/String;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Landroid/os/AsyncTask;->onPostExecute(Ljava/lang/Object;)V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/f/h$e;->Zc:Lcom/eckom/xtlibrary/b/f/f/h$f;

    if-eqz p0, :cond_0

    .line 3
    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$f;->ia(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method protected bridge synthetic onPostExecute(Ljava/lang/Object;)V
    .locals 0

    .line 1
    check-cast p1, Ljava/lang/String;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/h$e;->oa(Ljava/lang/String;)V

    return-void
.end method

.method protected onPreExecute()V
    .locals 0

    .line 1
    invoke-super {p0}, Landroid/os/AsyncTask;->onPreExecute()V

    return-void
.end method

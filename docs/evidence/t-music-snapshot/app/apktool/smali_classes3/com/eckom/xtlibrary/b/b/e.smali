.class public Lcom/eckom/xtlibrary/b/b/e;
.super Lc/b/a/a/a/f$a;
.source "VideoCallBackImp.java"


# instance fields
.field private ed:Lcom/eckom/xtlibrary/b/k/b/a;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/g/a;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Lc/b/a/a/a/f$a;-><init>()V

    .line 2
    instance-of v0, p1, Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz v0, :cond_0

    .line 3
    check-cast p1, Lcom/eckom/xtlibrary/b/k/b/a;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/b/e;->ed:Lcom/eckom/xtlibrary/b/k/b/a;

    :cond_0
    return-void
.end method


# virtual methods
.method public J()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/e;->ed:Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/k/b/a;->Hk:Lcom/eckom/xtlibrary/b/c/d;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/d;->J()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/k/b/a;->ic()V

    :cond_1
    :goto_0
    return-void
.end method

.method public P()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/e;->ed:Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/k/b/a;->Hk:Lcom/eckom/xtlibrary/b/c/d;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/d;->P()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/k/b/a;->P()V

    :cond_1
    :goto_0
    return-void
.end method

.method public a(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/e;->ed:Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz p0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/k/b/a;->Hk:Lcom/eckom/xtlibrary/b/c/d;

    if-eqz p0, :cond_0

    .line 3
    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/c/d;->a(Landroid/os/Bundle;)V

    :cond_0
    return-void
.end method

.method public ha()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/e;->ed:Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/k/b/a;->Hk:Lcom/eckom/xtlibrary/b/c/d;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/d;->ha()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/k/b/a;->jc()V

    :cond_1
    :goto_0
    return-void
.end method

.method public ma()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/e;->ed:Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/k/b/a;->Hk:Lcom/eckom/xtlibrary/b/c/d;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/d;->ma()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/k/b/a;->ma()V

    :cond_1
    :goto_0
    return-void
.end method

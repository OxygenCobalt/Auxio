.class public Lcom/eckom/xtlibrary/b/b/c;
.super Lc/b/a/a/a/b$a;
.source "MusicCallBackImp.java"


# instance fields
.field private ed:Lcom/eckom/xtlibrary/b/f/e/a;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/g/a;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Lc/b/a/a/a/b$a;-><init>()V

    .line 2
    instance-of v0, p1, Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz v0, :cond_0

    .line 3
    check-cast p1, Lcom/eckom/xtlibrary/b/f/e/a;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/b/c;->ed:Lcom/eckom/xtlibrary/b/f/e/a;

    :cond_0
    return-void
.end method


# virtual methods
.method public a(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/c;->ed:Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz p0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/b;

    if-eqz p0, :cond_0

    .line 3
    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/c/b;->a(Landroid/os/Bundle;)V

    :cond_0
    return-void
.end method

.method public ba()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/c;->ed:Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/b;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/b;->ba()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->ba()V

    :cond_1
    :goto_0
    return-void
.end method

.method public ea()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/c;->ed:Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/b;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/b;->ea()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->rb()V

    :cond_1
    :goto_0
    return-void
.end method

.method public fa()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/c;->ed:Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/b;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/b;->fa()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->fa()V

    :cond_1
    :goto_0
    return-void
.end method

.method public na()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/c;->ed:Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/b;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/b;->na()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->pb()V

    :cond_1
    :goto_0
    return-void
.end method

.method public z(I)V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/c;->ed:Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/b;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0, p1}, Lcom/eckom/xtlibrary/b/c/b;->z(I)V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/e/a;->pa(I)V

    :cond_1
    :goto_0
    return-void
.end method

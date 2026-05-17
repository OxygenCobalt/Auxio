.class public Lcom/eckom/xtlibrary/b/d/b/a;
.super Lcom/eckom/xtlibrary/b/g/a;
.source "LauncherPresenter.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/d/a/b;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/b/g/a<",
        "Lcom/eckom/xtlibrary/b/d/c/a;",
        "Lcom/eckom/xtlibrary/b/d/a/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/d/a/b;"
    }
.end annotation


# instance fields
.field private mContext:Landroid/content/Context;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/g/a;-><init>(Landroid/content/Context;)V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/d/b/a;->mContext:Landroid/content/Context;

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/d/b/a;->onCreate()V

    return-void
.end method


# virtual methods
.method public Ra(Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/d/a/a;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/d/a/a;->Da(Ljava/lang/String;)V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/d/a/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/d/a/a;->zb()V

    return-void
.end method

.method public Ta(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/b/d/a/a;->getInstance()Lcom/eckom/xtlibrary/b/d/a/a;

    move-result-object v0

    invoke-virtual {v0, p1, p0}, Lcom/eckom/xtlibrary/b/d/a/a;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/d/a/b;)V

    return-void
.end method

.method public Ua(Ljava/lang/String;)V
    .locals 0

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/b/d/a/a;->getInstance()Lcom/eckom/xtlibrary/b/d/a/a;

    move-result-object p0

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/d/a/a;->Da(Ljava/lang/String;)V

    return-void
.end method

.method public b(Landroid/os/Bundle;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/d/c/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/d/c/a;->b(Landroid/os/Bundle;)V

    :cond_0
    return-void
.end method

.method public getModel()Lcom/eckom/xtlibrary/b/d/a/a;
    .locals 0

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/d/a/a;->getInstance()Lcom/eckom/xtlibrary/b/d/a/a;

    move-result-object p0

    return-object p0
.end method

.method public bridge synthetic getModel()Lcom/eckom/xtlibrary/b/e/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/d/b/a;->getModel()Lcom/eckom/xtlibrary/b/d/a/a;

    move-result-object p0

    return-object p0
.end method

.method public onCreate()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/d/a/a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/d/b/a;->mContext:Landroid/content/Context;

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/d/a/a;->a(Landroid/content/Context;)V

    return-void
.end method

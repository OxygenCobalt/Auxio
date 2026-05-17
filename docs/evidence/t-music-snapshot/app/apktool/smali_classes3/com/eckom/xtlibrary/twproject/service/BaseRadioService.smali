.class public abstract Lcom/eckom/xtlibrary/twproject/service/BaseRadioService;
.super Lcom/eckom/xtlibrary/twproject/service/XTService;
.source "BaseRadioService.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/h/e/a;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/service/XTService<",
        "Lcom/eckom/xtlibrary/b/h/c/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/h/e/a;"
    }
.end annotation


# instance fields
.field private Va:Ljava/lang/String;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;-><init>()V

    const-string v0, ""

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseRadioService;->Va:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method public A(I)V
    .locals 0

    return-void
.end method

.method public abstract Da()Ljava/lang/String;
.end method

.method public E(I)V
    .locals 0

    return-void
.end method

.method public I(I)V
    .locals 0

    return-void
.end method

.method public K(I)V
    .locals 0

    return-void
.end method

.method public S(I)V
    .locals 0

    return-void
.end method

.method public a(IIIII)V
    .locals 0

    return-void
.end method

.method public a(IIIIII)V
    .locals 0

    return-void
.end method

.method public a([Lcom/eckom/xtlibrary/b/h/a/a;)V
    .locals 0

    return-void
.end method

.method public b(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    return-void
.end method

.method public ba(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public e(Z)V
    .locals 0

    return-void
.end method

.method public f(Z)V
    .locals 0

    return-void
.end method

.method public ha(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public i(Z)V
    .locals 0

    return-void
.end method

.method public k(Z)V
    .locals 0

    return-void
.end method

.method public m(Z)V
    .locals 0

    return-void
.end method

.method public n(I)V
    .locals 0

    return-void
.end method

.method public onCreate()V
    .locals 1

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->onCreate()V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/BaseRadioService;->Da()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseRadioService;->Va:Ljava/lang/String;

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/h/c/a;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/h/c/a;->C(Z)V

    return-void
.end method

.method public onDestroy()V
    .locals 0

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->onDestroy()V

    return-void
.end method

.method public p(Z)V
    .locals 0

    return-void
.end method

.method public q(Z)V
    .locals 0

    return-void
.end method

.method public r(I)V
    .locals 0

    return-void
.end method

.method public r(Z)V
    .locals 0

    return-void
.end method

.method public s(I)V
    .locals 0

    return-void
.end method

.method public s(Z)V
    .locals 0

    return-void
.end method

.method public t(Z)V
    .locals 0

    return-void
.end method

.method public u(Z)V
    .locals 0

    return-void
.end method

.method public x(I)V
    .locals 0

    return-void
.end method

.method public y(I)V
    .locals 0

    return-void
.end method

.method public bridge synthetic za()Lcom/eckom/xtlibrary/b/g/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/BaseRadioService;->za()Lcom/eckom/xtlibrary/b/h/c/a;

    move-result-object p0

    return-object p0
.end method

.method public za()Lcom/eckom/xtlibrary/b/h/c/a;
    .locals 1

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/h/c/a;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/h/c/a;-><init>(Landroid/content/Context;)V

    return-object v0
.end method

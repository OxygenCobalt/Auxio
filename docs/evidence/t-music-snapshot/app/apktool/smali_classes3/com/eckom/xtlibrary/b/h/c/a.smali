.class public Lcom/eckom/xtlibrary/b/h/c/a;
.super Lcom/eckom/xtlibrary/b/g/a;
.source "RadioPresenter.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/h/b/f;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/b/g/a<",
        "Lcom/eckom/xtlibrary/b/h/e/a;",
        "Lcom/eckom/xtlibrary/b/h/b/e;",
        ">;",
        "Lcom/eckom/xtlibrary/b/h/b/f;"
    }
.end annotation


# instance fields
.field public Hk:Lcom/eckom/xtlibrary/b/c/c;

.field private mContext:Landroid/content/Context;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/g/a;-><init>(Landroid/content/Context;)V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/h/c/a;->mContext:Landroid/content/Context;

    return-void
.end method

.method private onCreate()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->mContext:Landroid/content/Context;

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/h/b/e;->a(Landroid/content/Context;)V

    return-void
.end method


# virtual methods
.method public A(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->A(I)V

    :cond_0
    return-void
.end method

.method public B(Z)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->B(Z)V

    :cond_0
    return-void
.end method

.method public C(Z)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->C(Z)V

    :cond_0
    return-void
.end method

.method public E(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->E(I)V

    :cond_0
    return-void
.end method

.method public I(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->I(I)V

    :cond_0
    return-void
.end method

.method public K(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->K(I)V

    :cond_0
    return-void
.end method

.method public Ra(Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->Ha(Ljava/lang/String;)V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->zb()V

    return-void
.end method

.method public S(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->S(I)V

    :cond_0
    return-void
.end method

.method public Ta(Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->onResume()V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/b/e;->getInstance()Lcom/eckom/xtlibrary/b/h/b/e;

    move-result-object v0

    invoke-virtual {v0, p1, p0}, Lcom/eckom/xtlibrary/b/h/b/e;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/h/b/f;)V

    return-void
.end method

.method public Va(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->onCreate()V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/b/e;->getInstance()Lcom/eckom/xtlibrary/b/h/b/e;

    move-result-object v0

    invoke-virtual {v0, p1, p0}, Lcom/eckom/xtlibrary/b/h/b/e;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/h/b/f;)V

    return-void
.end method

.method public Zb()Z
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->Zb()Z

    move-result p0

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method public a(IIIII)V
    .locals 6

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    move-object v0, p0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/e/a;

    move v1, p1

    move v2, p2

    move v3, p3

    move v4, p4

    move v5, p5

    invoke-interface/range {v0 .. v5}, Lcom/eckom/xtlibrary/b/h/e/a;->a(IIIII)V

    :cond_0
    return-void
.end method

.method public a(IIIIII)V
    .locals 7

    .line 7
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 8
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    move-object v0, p0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/e/a;

    move v1, p1

    move v2, p2

    move v3, p3

    move v4, p4

    move v5, p5

    move v6, p6

    invoke-interface/range {v0 .. v6}, Lcom/eckom/xtlibrary/b/h/e/a;->a(IIIIII)V

    :cond_0
    return-void
.end method

.method public a(Landroid/graphics/drawable/Drawable;)V
    .locals 1

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->b(Landroid/graphics/drawable/Drawable;)V

    :cond_0
    return-void
.end method

.method public a([Lcom/eckom/xtlibrary/b/h/a/a;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->a([Lcom/eckom/xtlibrary/b/h/a/a;)V

    :cond_0
    return-void
.end method

.method public ac()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->ac()V

    :cond_0
    return-void
.end method

.method public ba(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->ba(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public cc()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->cc()V

    :cond_0
    return-void
.end method

.method public dc()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->dc()V

    :cond_0
    return-void
.end method

.method public e(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->e(Z)V

    :cond_0
    return-void
.end method

.method public ec()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->ec()V

    :cond_0
    return-void
.end method

.method public f(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->f(Z)V

    :cond_0
    return-void
.end method

.method public fc()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->fc()V

    :cond_0
    return-void
.end method

.method public bridge synthetic getModel()Lcom/eckom/xtlibrary/b/e/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->getModel()Lcom/eckom/xtlibrary/b/h/b/e;

    move-result-object p0

    return-object p0
.end method

.method public getModel()Lcom/eckom/xtlibrary/b/h/b/e;
    .locals 0

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/b/e;->getInstance()Lcom/eckom/xtlibrary/b/h/b/e;

    move-result-object p0

    return-object p0
.end method

.method public ha(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->ha(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public hc()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->hc()V

    :cond_0
    return-void
.end method

.method public i(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->i(Z)V

    :cond_0
    return-void
.end method

.method public k(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->k(Z)V

    :cond_0
    return-void
.end method

.method public m(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->m(Z)V

    :cond_0
    return-void
.end method

.method public ma(I)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->ma(I)V

    :cond_0
    return-void
.end method

.method public n(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->n(I)V

    :cond_0
    return-void
.end method

.method public na(I)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->na(I)V

    :cond_0
    return-void
.end method

.method public next()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->next()V

    :cond_0
    return-void
.end method

.method public p(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->p(Z)V

    :cond_0
    return-void
.end method

.method public q(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->q(Z)V

    :cond_0
    return-void
.end method

.method public r(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->r(I)V

    :cond_0
    return-void
.end method

.method public r(Z)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->r(Z)V

    :cond_0
    return-void
.end method

.method public s(I)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->s(I)V

    :cond_0
    return-void
.end method

.method public s(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->s(Z)V

    :cond_0
    return-void
.end method

.method public t(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->t(Z)V

    :cond_0
    return-void
.end method

.method public u(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->u(Z)V

    :cond_0
    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->w(Z)V

    :cond_0
    return-void
.end method

.method public x(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->x(I)V

    :cond_0
    return-void
.end method

.method public y(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/h/e/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/h/e/a;->y(I)V

    :cond_0
    return-void
.end method

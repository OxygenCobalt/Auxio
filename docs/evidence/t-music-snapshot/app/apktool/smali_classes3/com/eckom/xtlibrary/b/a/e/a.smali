.class public Lcom/eckom/xtlibrary/b/a/e/a;
.super Lcom/eckom/xtlibrary/b/g/a;
.source "BTPresenter.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/a/d/g;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/b/g/a<",
        "Lcom/eckom/xtlibrary/b/a/g/a;",
        "Lcom/eckom/xtlibrary/b/a/d/h;",
        ">;",
        "Lcom/eckom/xtlibrary/b/a/d/g;"
    }
.end annotation


# instance fields
.field public Hk:Lcom/eckom/xtlibrary/b/c/a;

.field private mContext:Landroid/content/Context;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/g/a;-><init>(Landroid/content/Context;)V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/e/a;->mContext:Landroid/content/Context;

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->onCreate()V

    return-void
.end method

.method private onCreate()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/a/d/h;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->mContext:Landroid/content/Context;

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/a/d/h;->a(Landroid/content/Context;)V

    return-void
.end method


# virtual methods
.method public Aa(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/h;->Aa(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public B(Z)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/h;->B(Z)V

    :cond_0
    return-void
.end method

.method public C(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->C(I)V

    :cond_0
    return-void
.end method

.method public Ca(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/h;->Ca(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public H(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->H(I)V

    :cond_0
    return-void
.end method

.method public I()V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0}, Lcom/eckom/xtlibrary/b/a/g/a;->I()V

    :cond_0
    return-void
.end method

.method public J(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->J(I)V

    :cond_0
    return-void
.end method

.method public M(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->M(I)V

    :cond_0
    return-void
.end method

.method public N()V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0}, Lcom/eckom/xtlibrary/b/a/g/a;->N()V

    :cond_0
    return-void
.end method

.method public N(I)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->aa(I)V

    :cond_0
    return-void
.end method

.method public P(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->P(I)V

    :cond_0
    return-void
.end method

.method public Q(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->Q(I)V

    :cond_0
    return-void
.end method

.method public Ra(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/h;->Ba(Ljava/lang/String;)V

    return-void
.end method

.method public Sa(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/h;->Ba(Ljava/lang/String;)V

    return-void
.end method

.method public T()V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0}, Lcom/eckom/xtlibrary/b/a/g/a;->T()V

    :cond_0
    return-void
.end method

.method public Ta(Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {v0, p1, p0}, Lcom/eckom/xtlibrary/b/a/d/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/a/d/g;)V

    return-void
.end method

.method public W()V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0}, Lcom/eckom/xtlibrary/b/a/g/a;->W()V

    :cond_0
    return-void
.end method

.method public X()V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0}, Lcom/eckom/xtlibrary/b/a/g/a;->X()V

    :cond_0
    return-void
.end method

.method public a(ILjava/lang/String;)V
    .locals 1

    .line 7
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 8
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->a(ILjava/lang/String;)V

    :cond_0
    return-void
.end method

.method public a(ILjava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/a/g/a;->a(ILjava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public a(IZ)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->a(IZ)V

    :cond_0
    return-void
.end method

.method public a(Ljava/util/ArrayList;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;",
            ">;)V"
        }
    .end annotation

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->a(Ljava/util/ArrayList;)V

    :cond_0
    return-void
.end method

.method public answer()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->answer()V

    :cond_0
    return-void
.end method

.method public b(ILjava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/a/g/a;->b(ILjava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public b(IZ)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->b(IZ)V

    :cond_0
    return-void
.end method

.method public b(Ljava/util/ArrayList;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;",
            ">;)V"
        }
    .end annotation

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->b(Ljava/util/ArrayList;)V

    :cond_0
    return-void
.end method

.method public ba()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->ba()V

    :cond_0
    return-void
.end method

.method public da(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->da(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public e(II)V
    .locals 1

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->e(II)V

    :cond_0
    return-void
.end method

.method public e(ILjava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/a/g/a;->e(ILjava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/a/g/a;->f(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public f(II)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->f(II)V

    :cond_0
    return-void
.end method

.method public f(ILjava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/a/g/a;->f(ILjava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public f(Z)V
    .locals 1

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->f(Z)V

    :cond_0
    return-void
.end method

.method public ga(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->ga(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public getCallState()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->getCallState()I

    move-result p0

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method public getModel()Lcom/eckom/xtlibrary/b/a/d/h;
    .locals 1

    const-string p0, "persist.tw.bt.module"

    const/4 v0, 0x2

    .line 2
    invoke-static {p0, v0}, Landroid/os/SystemProperties;->getInt(Ljava/lang/String;I)I

    move-result p0

    if-nez p0, :cond_0

    .line 3
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/d/j;->getInstance()Lcom/eckom/xtlibrary/b/a/d/j;

    move-result-object p0

    return-object p0

    .line 4
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/d/f;->getInstance()Lcom/eckom/xtlibrary/b/a/d/f;

    move-result-object p0

    return-object p0
.end method

.method public bridge synthetic getModel()Lcom/eckom/xtlibrary/b/e/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->getModel()Lcom/eckom/xtlibrary/b/a/d/h;

    move-result-object p0

    return-object p0
.end method

.method public i(II)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->i(II)V

    :cond_0
    return-void
.end method

.method public j(I)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->j(I)V

    :cond_0
    return-void
.end method

.method public j(Z)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->o(Z)V

    :cond_0
    return-void
.end method

.method public ja(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->ca(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public ka(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->ka(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public m(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->m(Ljava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public mb()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->mb()I

    move-result p0

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method public nb()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->nb()V

    :cond_0
    return-void
.end method

.method public o(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->o(Ljava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public ob()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->ob()V

    :cond_0
    return-void
.end method

.method public p(I)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->p(I)V

    :cond_0
    return-void
.end method

.method public p(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->p(Ljava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public pb()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->pb()V

    :cond_0
    return-void
.end method

.method public q(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->q(Ljava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public q(Z)V
    .locals 1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->q(Z)V

    :cond_0
    return-void
.end method

.method public qb()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->qb()V

    :cond_0
    return-void
.end method

.method public r(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->n(Ljava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public rb()V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/h;->rb()V

    :cond_0
    return-void
.end method

.method public s(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/g/a;->s(Ljava/lang/String;Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public setDeviceName(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 2
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/h;->setDeviceName(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public w(I)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->get()Lcom/eckom/xtlibrary/b/l/a;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/a/g/a;

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/g/a;->w(I)V

    :cond_0
    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    if-eqz p0, :cond_0

    .line 4
    check-cast p0, Lcom/eckom/xtlibrary/b/a/d/h;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/h;->w(Z)V

    :cond_0
    return-void
.end method

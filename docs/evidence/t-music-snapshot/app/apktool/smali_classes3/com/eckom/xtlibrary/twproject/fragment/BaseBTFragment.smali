.class public abstract Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;
.super Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;
.source "BaseBTFragment.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/a/g/a;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/fragment/XTFragment<",
        "Lcom/eckom/xtlibrary/b/a/e/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/a/g/a;"
    }
.end annotation


# instance fields
.field protected la:Lcom/eckom/xtlibrary/b/a/b/a;

.field protected mContext:Landroid/content/Context;

.field public ma:Ljava/lang/String;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;-><init>()V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/a;->getInstance()Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    const-string v0, ""

    .line 3
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->ma:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method public C(I)V
    .locals 1

    .line 1
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "onDeviceHFP: "

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "BaseBTFragment"

    invoke-static {p1, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

.method public H(I)V
    .locals 0

    return-void
.end method

.method public I()V
    .locals 0

    return-void
.end method

.method public J(I)V
    .locals 0

    return-void
.end method

.method public M(I)V
    .locals 0

    return-void
.end method

.method public N()V
    .locals 0

    return-void
.end method

.method public P(I)V
    .locals 0

    return-void
.end method

.method public Q(I)V
    .locals 0

    return-void
.end method

.method public T()V
    .locals 0

    return-void
.end method

.method public W()V
    .locals 0

    return-void
.end method

.method public X()V
    .locals 0

    return-void
.end method

.method public a(ILjava/lang/String;)V
    .locals 0

    return-void
.end method

.method public a(ILjava/lang/String;Ljava/lang/String;)V
    .locals 0

    .line 1
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "onPairInfo: "

    invoke-virtual {p0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string p1, " pairName: pairMac:"

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "BaseBTFragment"

    invoke-static {p1, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

.method public a(IZ)V
    .locals 0

    return-void
.end method

.method public a(Ljava/util/ArrayList;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;",
            ">;)V"
        }
    .end annotation

    return-void
.end method

.method public aa(I)V
    .locals 0

    return-void
.end method

.method public b(ILjava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public b(IZ)V
    .locals 0

    return-void
.end method

.method public b(Ljava/util/ArrayList;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;",
            ">;)V"
        }
    .end annotation

    return-void
.end method

.method public ca(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public da(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public e(II)V
    .locals 0

    return-void
.end method

.method public e(ILjava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public f(II)V
    .locals 0

    return-void
.end method

.method public f(ILjava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public f(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public f(Z)V
    .locals 0

    return-void
.end method

.method public ga(Ljava/lang/String;)V
    .locals 1

    .line 1
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "onBtNameInfo: "

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "BaseBTFragment"

    invoke-static {p1, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

.method public i(II)V
    .locals 0

    return-void
.end method

.method public j(I)V
    .locals 0

    return-void
.end method

.method public ka(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public m(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public n(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public o(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public o(Z)V
    .locals 0

    return-void
.end method

.method public onAttach(Landroid/content/Context;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->mContext:Landroid/content/Context;

    .line 2
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->onAttach(Landroid/content/Context;)V

    return-void
.end method

.method public onDestroy()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-eqz v0, :cond_0

    .line 2
    check-cast v0, Lcom/eckom/xtlibrary/b/a/e/a;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->ma:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/a/e/a;->Sa(Ljava/lang/String;)V

    .line 3
    :cond_0
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->onDestroy()V

    return-void
.end method

.method public onResume()V
    .locals 0

    .line 1
    invoke-super {p0}, Landroid/app/Fragment;->onResume()V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->sa()V

    return-void
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-super {p0, p1, p2}, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->ta()Ljava/lang/String;

    move-result-object p2

    iput-object p2, p0, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->ma:Ljava/lang/String;

    .line 3
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->a(Landroid/view/View;)V

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/fragment/XTFragment;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p1, Lcom/eckom/xtlibrary/b/a/e/a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->ma:Ljava/lang/String;

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/a/e/a;->Ta(Ljava/lang/String;)V

    return-void
.end method

.method public p(I)V
    .locals 1

    .line 1
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "onDeviceHFPInfo: "

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "BaseBTFragment"

    invoke-static {p1, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

.method public p(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public q(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public q(Z)V
    .locals 0

    return-void
.end method

.method public ra()Lcom/eckom/xtlibrary/b/a/e/a;
    .locals 1

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/a/e/a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->mContext:Landroid/content/Context;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/a/e/a;-><init>(Landroid/content/Context;)V

    return-object v0
.end method

.method public bridge synthetic ra()Lcom/eckom/xtlibrary/b/g/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/fragment/BaseBTFragment;->ra()Lcom/eckom/xtlibrary/b/a/e/a;

    move-result-object p0

    return-object p0
.end method

.method public s(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public abstract sa()V
.end method

.method public abstract ta()Ljava/lang/String;
.end method

.method public w(I)V
    .locals 0

    return-void
.end method

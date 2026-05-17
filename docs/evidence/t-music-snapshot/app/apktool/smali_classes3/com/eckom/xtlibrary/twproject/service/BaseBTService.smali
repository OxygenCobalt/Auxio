.class public abstract Lcom/eckom/xtlibrary/twproject/service/BaseBTService;
.super Lcom/eckom/xtlibrary/twproject/service/XTService;
.source "BaseBTService.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/a/g/a;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/service/XTService<",
        "Lcom/eckom/xtlibrary/b/a/e/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/a/g/a;"
    }
.end annotation


# instance fields
.field protected Sa:Lcom/eckom/xtlibrary/b/a/f/a;

.field private Ta:Ljava/lang/String;

.field private Ua:Ljava/lang/String;

.field private Va:Ljava/lang/String;

.field protected la:Lcom/eckom/xtlibrary/b/a/b/a;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;-><init>()V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/a;->getInstance()Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    const-string v0, ""

    .line 3
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ta:Ljava/lang/String;

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ua:Ljava/lang/String;

    .line 5
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Va:Ljava/lang/String;

    return-void
.end method

.method private ub(Ljava/lang/String;)V
    .locals 3

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ba()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ta:Ljava/lang/String;

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ta:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_2

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ta:Ljava/lang/String;

    invoke-static {v0, p1}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_0

    goto :goto_0

    :cond_0
    const-string p1, "needSetDefaultName"

    const-string v0, "BaseBTService"

    .line 3
    invoke-static {p0, v0, p1}, Lcom/eckom/xtlibrary/b/j/o;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v1

    if-nez v1, :cond_1

    return-void

    .line 4
    :cond_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v1, Lcom/eckom/xtlibrary/b/a/e/a;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ta:Ljava/lang/String;

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/a/e/a;->setDeviceName(Ljava/lang/String;)V

    const/4 v1, 0x0

    .line 5
    invoke-static {p0, v0, p1, v1}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Z)V

    :cond_2
    :goto_0
    return-void
.end method

.method private vb(Ljava/lang/String;)V
    .locals 3

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ca()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ua:Ljava/lang/String;

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ua:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_2

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ua:Ljava/lang/String;

    invoke-static {v0, p1}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_0

    goto :goto_0

    :cond_0
    const-string p1, "needSetDefaultPin"

    const-string v0, "BaseBTService"

    .line 3
    invoke-static {p0, v0, p1}, Lcom/eckom/xtlibrary/b/j/o;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v1

    if-nez v1, :cond_1

    return-void

    .line 4
    :cond_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v1, Lcom/eckom/xtlibrary/b/a/e/a;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Ua:Ljava/lang/String;

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/a/e/a;->Ca(Ljava/lang/String;)V

    const/4 v1, 0x0

    .line 5
    invoke-static {p0, v0, p1, v1}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Z)V

    :cond_2
    :goto_0
    return-void
.end method


# virtual methods
.method public abstract Ba()Ljava/lang/String;
.end method

.method public C(I)V
    .locals 0

    return-void
.end method

.method public abstract Ca()Ljava/lang/String;
.end method

.method public abstract Da()Ljava/lang/String;
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
    .locals 2

    const/16 v0, 0xb

    const/4 v1, 0x0

    if-eq p1, v0, :cond_2

    const/16 v0, 0xc

    if-eq p1, v0, :cond_0

    const/16 v0, 0x8

    packed-switch p1, :pswitch_data_0

    goto :goto_0

    .line 1
    :pswitch_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget v1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    if-ne v1, v0, :cond_3

    iget-boolean p1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->Gg:Z

    if-eqz p1, :cond_3

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->ob()V

    goto :goto_0

    .line 3
    :pswitch_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget v1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    if-ne v1, v0, :cond_3

    iget-boolean p1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->Gg:Z

    if-nez p1, :cond_3

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->ob()V

    goto :goto_0

    .line 5
    :pswitch_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->rb()V

    goto :goto_0

    .line 6
    :pswitch_3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->pb()V

    goto :goto_0

    .line 7
    :pswitch_4
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->ob()V

    goto :goto_0

    .line 8
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Sa:Lcom/eckom/xtlibrary/b/a/f/a;

    if-nez p0, :cond_1

    goto :goto_0

    :cond_1
    const-string p1, "unmute"

    .line 9
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/f/a;->za(Ljava/lang/String;)V

    throw v1

    .line 10
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Sa:Lcom/eckom/xtlibrary/b/a/f/a;

    if-nez p0, :cond_4

    :cond_3
    :goto_0
    :pswitch_5
    return-void

    :cond_4
    const-string p1, "mute"

    .line 11
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/f/a;->za(Ljava/lang/String;)V

    throw v1

    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_4
        :pswitch_5
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
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
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->ub(Ljava/lang/String;)V

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

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->vb(Ljava/lang/String;)V

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

.method public onCreate()V
    .locals 1

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->onCreate()V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Da()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Va:Ljava/lang/String;

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/a/e/a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->Va:Ljava/lang/String;

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/a/e/a;->Ta(Ljava/lang/String;)V

    return-void
.end method

.method public onDestroy()V
    .locals 0

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->onDestroy()V

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

    const-string p1, "BaseBTService"

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

.method public s(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public w(I)V
    .locals 0

    return-void
.end method

.method public za()Lcom/eckom/xtlibrary/b/a/e/a;
    .locals 1

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/a/e/a;-><init>(Landroid/content/Context;)V

    return-object v0
.end method

.method public bridge synthetic za()Lcom/eckom/xtlibrary/b/g/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/BaseBTService;->za()Lcom/eckom/xtlibrary/b/a/e/a;

    move-result-object p0

    return-object p0
.end method

.class public abstract Lcom/eckom/xtlibrary/twproject/activity/BaseBTActivity;
.super Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.source "BaseBTActivity.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/a/g/a;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/activity/XTActivity<",
        "Lcom/eckom/xtlibrary/b/a/e/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/a/g/a;"
    }
.end annotation


# instance fields
.field private Va:Ljava/lang/String;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;-><init>()V

    const-string v0, ""

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseBTActivity;->Va:Ljava/lang/String;

    return-void
.end method

.method private oe()V
    .locals 2
    .annotation build Landroid/annotation/TargetApi;
        value = 0x18
    .end annotation

    .line 1
    :try_start_0
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x1e

    if-lt v0, v1, :cond_0

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/a;->getInstance()Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    invoke-virtual {p0}, Landroid/app/Activity;->isInMultiWindowMode()Z

    move-result p0

    iput-boolean p0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Ng:Z

    goto :goto_1

    .line 3
    :cond_0
    sget p0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v0, 0x18

    if-lt p0, v0, :cond_2

    .line 4
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/a;->getInstance()Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    invoke-interface {v0}, Landroid/view/IWindowManager;->getDockedStackSide()I

    move-result v0

    if-lez v0, :cond_1

    const/4 v0, 0x1

    goto :goto_0

    :cond_1
    const/4 v0, 0x0

    :goto_0
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Ng:Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    :catch_0
    move-exception p0

    .line 5
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "getMultiWindowMode:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string v0, "BaseBTActivity"

    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_2
    :goto_1
    return-void
.end method


# virtual methods
.method public C(I)V
    .locals 0

    return-void
.end method

.method public abstract Da()Ljava/lang/String;
.end method

.method public H(I)V
    .locals 0

    return-void
.end method

.method public Ha()Ljava/lang/String;
    .locals 0

    const/4 p0, 0x0

    return-object p0
.end method

.method public I()V
    .locals 0

    return-void
.end method

.method public Ia()Ljava/lang/String;
    .locals 0

    const-string p0, "com.tw.bt.theme"

    return-object p0
.end method

.method public J(I)V
    .locals 0

    return-void
.end method

.method public Ja()Ljava/lang/String;
    .locals 0

    const-string p0, "/data/tw/theme/default/Sub/BluetoothTheme.apk"

    return-object p0
.end method

.method public Ka()Lcom/eckom/xtlibrary/b/i/m;
    .locals 0

    const/4 p0, 0x0

    return-object p0
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

.method public c(Lcom/eckom/xtlibrary/b/i/m;)V
    .locals 0

    return-void
.end method

.method public ca(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public d(Lcom/eckom/xtlibrary/b/i/m;)V
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

.method public onBackPressed()V
    .locals 2

    .line 1
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onBackPressed()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/a/e/a;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/a/e/a;->w(Z)V

    const-string v0, "persist.tw.bt.module"

    const/4 v1, 0x2

    .line 3
    invoke-static {v0, v1}, Landroid/os/SystemProperties;->getInt(Ljava/lang/String;I)I

    move-result v0

    if-nez v0, :cond_0

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->ba()V

    :cond_0
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseBTActivity;->oe()V

    .line 2
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onCreate(Landroid/os/Bundle;)V

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseBTActivity;->Da()Ljava/lang/String;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseBTActivity;->Va:Ljava/lang/String;

    return-void
.end method

.method protected onDestroy()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-eqz v0, :cond_0

    .line 2
    check-cast v0, Lcom/eckom/xtlibrary/b/a/e/a;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseBTActivity;->Va:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/a/e/a;->Ra(Ljava/lang/String;)V

    .line 3
    :cond_0
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onDestroy()V

    return-void
.end method

.method protected onPause()V
    .locals 1

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onPause()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/a/e/a;->B(Z)V

    return-void
.end method

.method protected onResume()V
    .locals 1

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onResume()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/a/e/a;

    const/4 v0, 0x1

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/a/e/a;->B(Z)V

    return-void
.end method

.method public p(I)V
    .locals 0

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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseBTActivity;->za()Lcom/eckom/xtlibrary/b/a/e/a;

    move-result-object p0

    return-object p0
.end method

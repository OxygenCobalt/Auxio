.class public abstract Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;
.super Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.source "BaseRadioActivity.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/h/e/a;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/activity/XTActivity<",
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
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;-><init>()V

    const-string v0, ""

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;->Va:Ljava/lang/String;

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

.method public Ha()Ljava/lang/String;
    .locals 0

    const/4 p0, 0x0

    return-object p0
.end method

.method public I(I)V
    .locals 0

    return-void
.end method

.method public Ia()Ljava/lang/String;
    .locals 0

    const-string p0, "com.tw.radio.theme"

    return-object p0
.end method

.method public Ja()Ljava/lang/String;
    .locals 0

    const-string p0, "/data/tw/theme/default/Sub/RadioTheme.apk"

    return-object p0
.end method

.method public K(I)V
    .locals 0

    return-void
.end method

.method public Ka()Lcom/eckom/xtlibrary/b/i/m;
    .locals 0

    const/4 p0, 0x0

    return-object p0
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

.method public c(Lcom/eckom/xtlibrary/b/i/m;)V
    .locals 0

    return-void
.end method

.method public d(Lcom/eckom/xtlibrary/b/i/m;)V
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

.method public onBackPressed()V
    .locals 1

    .line 1
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onBackPressed()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/h/c/a;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/h/c/a;->w(Z)V

    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onCreate(Landroid/os/Bundle;)V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;->Da()Ljava/lang/String;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;->Va:Ljava/lang/String;

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p1, Lcom/eckom/xtlibrary/b/h/c/a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;->Va:Ljava/lang/String;

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/h/c/a;->Va(Ljava/lang/String;)V

    return-void
.end method

.method protected onDestroy()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-eqz v0, :cond_0

    .line 2
    check-cast v0, Lcom/eckom/xtlibrary/b/h/c/a;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;->Va:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/h/c/a;->Ra(Ljava/lang/String;)V

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

    check-cast p0, Lcom/eckom/xtlibrary/b/h/c/a;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/h/c/a;->B(Z)V

    return-void
.end method

.method protected onResume()V
    .locals 2

    .line 1
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x1e

    if-eq v0, v1, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/h/c/a;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/h/c/a;->B(Z)V

    .line 3
    :cond_0
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onResume()V

    return-void
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 2

    .line 1
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    move-result v0

    const/4 v1, 0x1

    if-ne v0, v1, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/h/c/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/h/c/a;->Zb()Z

    move-result v0

    if-nez v0, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/h/c/a;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/h/c/a;->w(Z)V

    .line 4
    :cond_0
    invoke-super {p0, p1}, Landroid/app/Activity;->onTouchEvent(Landroid/view/MotionEvent;)Z

    move-result p0

    return p0
.end method

.method public onWindowFocusChanged(Z)V
    .locals 2

    .line 1
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onWindowFocusChanged(Z)V

    .line 2
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x1e

    if-ne v0, v1, :cond_0

    if-eqz p1, :cond_0

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p1, Lcom/eckom/xtlibrary/b/h/c/a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;->Va:Ljava/lang/String;

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/h/c/a;->Ta(Ljava/lang/String;)V

    :cond_0
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
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseRadioActivity;->za()Lcom/eckom/xtlibrary/b/h/c/a;

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

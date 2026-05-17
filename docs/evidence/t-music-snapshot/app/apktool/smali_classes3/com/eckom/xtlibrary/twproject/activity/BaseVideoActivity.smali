.class public abstract Lcom/eckom/xtlibrary/twproject/activity/BaseVideoActivity;
.super Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.source "BaseVideoActivity.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/k/c/c;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/activity/XTActivity<",
        "Lcom/eckom/xtlibrary/b/k/b/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/k/c/c;"
    }
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;-><init>()V

    return-void
.end method


# virtual methods
.method public D(I)V
    .locals 0

    return-void
.end method

.method public Ha()Ljava/lang/String;
    .locals 0

    const/4 p0, 0x0

    return-object p0
.end method

.method public Ia()Ljava/lang/String;
    .locals 0

    const-string p0, "com.tw.video.theme"

    return-object p0
.end method

.method public Ja()Ljava/lang/String;
    .locals 0

    const-string p0, "/data/tw/theme/default/Sub/VideoTheme.apk"

    return-object p0
.end method

.method public Ka()Lcom/eckom/xtlibrary/b/i/m;
    .locals 0

    const/4 p0, 0x0

    return-object p0
.end method

.method public L()V
    .locals 0

    return-void
.end method

.method public Y()V
    .locals 0

    return-void
.end method

.method public Y(I)V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/k/a/b;)V
    .locals 0

    return-void
.end method

.method public a(Ljava/lang/Boolean;)V
    .locals 0

    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/k/a/b;)V
    .locals 0

    return-void
.end method

.method public c(Lcom/eckom/xtlibrary/b/i/m;)V
    .locals 0

    return-void
.end method

.method public d(II)V
    .locals 0

    return-void
.end method

.method public d(Lcom/eckom/xtlibrary/b/i/m;)V
    .locals 0

    return-void
.end method

.method public d(Z)V
    .locals 0

    return-void
.end method

.method public f(Z)V
    .locals 0

    return-void
.end method

.method public fa(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public h(Z)V
    .locals 0

    return-void
.end method

.method public l(I)V
    .locals 0

    return-void
.end method

.method public l(Z)V
    .locals 0

    return-void
.end method

.method public n(Z)V
    .locals 0

    return-void
.end method

.method public onBackPressed()V
    .locals 1

    .line 1
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onBackPressed()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/k/b/a;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/k/b/a;->w(Z)V

    return-void
.end method

.method public onMediaView(Landroid/view/View;)V
    .locals 0

    return-void
.end method

.method public q(I)V
    .locals 0

    return-void
.end method

.method public q(Z)V
    .locals 0

    return-void
.end method

.method public u(Ljava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public v(I)V
    .locals 0

    return-void
.end method

.method public v(Z)V
    .locals 0

    return-void
.end method

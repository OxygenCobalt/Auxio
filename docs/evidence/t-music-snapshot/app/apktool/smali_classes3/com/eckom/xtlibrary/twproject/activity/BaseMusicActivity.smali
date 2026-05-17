.class public abstract Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;
.super Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.source "BaseMusicActivity.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/g/b;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/activity/XTActivity<",
        "Lcom/eckom/xtlibrary/b/f/e/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/f/g/b;"
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
.method public B(I)V
    .locals 0

    return-void
.end method

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

    const-string p0, "com.tw.music.theme"

    return-object p0
.end method

.method public Ja()Ljava/lang/String;
    .locals 0

    const-string p0, "/data/tw/theme/default/Sub/MusicTheme.apk"

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

.method public a(Landroid/media/MediaPlayer;)V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 0

    return-void
.end method

.method public a(Ljava/lang/Boolean;)V
    .locals 0

    return-void
.end method

.method public a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V
    .locals 0

    return-void
.end method

.method public a(Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;)V
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

.method public f(Z)V
    .locals 0

    return-void
.end method

.method public h(Z)V
    .locals 0

    return-void
.end method

.method public onBackPressed()V
    .locals 1

    .line 1
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onBackPressed()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/f/e/a;->w(Z)V

    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onCreate(Landroid/os/Bundle;)V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->xc()V

    return-void
.end method

.method protected onDestroy()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/e/a;->yc()V

    .line 2
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onDestroy()V

    return-void
.end method

.method protected onPause()V
    .locals 0

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onPause()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->onPause()V

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

    check-cast v0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/e/a;->onResume()V

    .line 3
    :cond_0
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onResume()V

    return-void
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
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->onResume()V

    :cond_0
    return-void
.end method

.method public q(Z)V
    .locals 0

    return-void
.end method

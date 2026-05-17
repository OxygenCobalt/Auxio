.class public abstract Lcom/eckom/xtlibrary/twproject/activity/BaseLauncherActivity;
.super Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.source "BaseLauncherActivity.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/d/c/a;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/activity/XTActivity<",
        "Lcom/eckom/xtlibrary/b/d/b/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/d/c/a;"
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
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseLauncherActivity;->Va:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method public abstract Da()Ljava/lang/String;
.end method

.method public Ha()Ljava/lang/String;
    .locals 0

    const/4 p0, 0x0

    return-object p0
.end method

.method public Ia()Ljava/lang/String;
    .locals 0

    const-string p0, "com.tw.launcher.theme"

    return-object p0
.end method

.method public Ja()Ljava/lang/String;
    .locals 0

    const-string p0, "/data/tw/theme/default/Launcher/LauncherTheme.apk"

    return-object p0
.end method

.method public Ka()Lcom/eckom/xtlibrary/b/i/m;
    .locals 0

    const/4 p0, 0x0

    return-object p0
.end method

.method public b(Landroid/os/Bundle;)V
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

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onCreate(Landroid/os/Bundle;)V

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-eqz p1, :cond_0

    .line 3
    check-cast p1, Lcom/eckom/xtlibrary/b/d/b/a;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/d/b/a;->onCreate()V

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseLauncherActivity;->Da()Ljava/lang/String;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseLauncherActivity;->Va:Ljava/lang/String;

    return-void
.end method

.method protected onDestroy()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-eqz v0, :cond_0

    .line 2
    check-cast v0, Lcom/eckom/xtlibrary/b/d/b/a;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/BaseLauncherActivity;->Va:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/d/b/a;->Ra(Ljava/lang/String;)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/d/b/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/g/a;->onDestroy()V

    .line 4
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

    if-eqz p0, :cond_0

    .line 3
    check-cast p0, Lcom/eckom/xtlibrary/b/d/b/a;

    const-string v0, "BaseLauncherActivity"

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/d/b/a;->Ua(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method protected onResume()V
    .locals 1

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->onResume()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-eqz p0, :cond_0

    .line 3
    check-cast p0, Lcom/eckom/xtlibrary/b/d/b/a;

    const-string v0, "BaseLauncherActivity"

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/d/b/a;->Ta(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method public za()Lcom/eckom/xtlibrary/b/d/b/a;
    .locals 1

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/d/b/a;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/d/b/a;-><init>(Landroid/content/Context;)V

    return-object v0
.end method

.method public bridge synthetic za()Lcom/eckom/xtlibrary/b/g/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseLauncherActivity;->za()Lcom/eckom/xtlibrary/b/d/b/a;

    move-result-object p0

    return-object p0
.end method

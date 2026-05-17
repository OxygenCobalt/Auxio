.class public abstract Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;
.super Lcom/eckom/xtlibrary/twproject/service/XTService;
.source "BaseMusicService.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/g/b;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/eckom/xtlibrary/twproject/service/XTService<",
        "Lcom/eckom/xtlibrary/b/f/e/a;",
        ">;",
        "Lcom/eckom/xtlibrary/b/f/g/b;"
    }
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;-><init>()V

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

.method public d(II)V
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

.method public onCreate()V
    .locals 0

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->onCreate()V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->xc()V

    return-void
.end method

.method public onDestroy()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/e/a;->yc()V

    .line 2
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->onDestroy()V

    return-void
.end method

.method public q(Z)V
    .locals 0

    return-void
.end method

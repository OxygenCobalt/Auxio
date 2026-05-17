.class public abstract Lcom/eckom/xtlibrary/twproject/service/XTService;
.super Landroid/app/Service;
.source "XTService.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Landroid/app/Service;"
    }
.end annotation


# instance fields
.field private mHandler:Landroid/os/Handler;

.field public mPresenter:Lcom/eckom/xtlibrary/b/g/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "TP;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Landroid/app/Service;-><init>()V

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/twproject/service/a;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/twproject/service/a;-><init>(Lcom/eckom/xtlibrary/twproject/service/XTService;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mHandler:Landroid/os/Handler;

    return-void
.end method


# virtual methods
.method protected Aa()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-nez v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->za()Lcom/eckom/xtlibrary/b/g/a;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    .line 3
    instance-of v0, p0, Lcom/eckom/xtlibrary/b/l/a;

    if-eqz v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/l/a;

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/g/a;->a(Lcom/eckom/xtlibrary/b/l/a;)V

    :cond_0
    return-void
.end method

.method public onBind(Landroid/content/Intent;)Landroid/os/IBinder;
    .locals 0

    const/4 p0, 0x0

    return-object p0
.end method

.method public onCreate()V
    .locals 0

    .line 1
    invoke-super {p0}, Landroid/app/Service;->onCreate()V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/service/XTService;->Aa()V

    return-void
.end method

.method public onDestroy()V
    .locals 1

    .line 1
    invoke-super {p0}, Landroid/app/Service;->onDestroy()V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/b;->db()V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/g/a;->delete()V

    const/4 v0, 0x0

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    return-void
.end method

.method public onStartCommand(Landroid/content/Intent;II)I
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x9c4

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 3
    invoke-super {p0, p1, p2, p3}, Landroid/app/Service;->onStartCommand(Landroid/content/Intent;II)I

    move-result p0

    return p0
.end method

.method public abstract za()Lcom/eckom/xtlibrary/b/g/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TP;"
        }
    .end annotation
.end method

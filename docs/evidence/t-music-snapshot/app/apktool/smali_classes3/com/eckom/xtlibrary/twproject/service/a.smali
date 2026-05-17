.class Lcom/eckom/xtlibrary/twproject/service/a;
.super Landroid/os/Handler;
.source "XTService.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/service/XTService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field private mCount:I

.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/service/XTService;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/service/XTService;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/service/a;->this$0:Lcom/eckom/xtlibrary/twproject/service/XTService;

    invoke-direct {p0}, Landroid/os/Handler;-><init>()V

    const/4 p1, 0x0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/service/a;->mCount:I

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .locals 1

    .line 1
    invoke-super {p0, p1}, Landroid/os/Handler;->handleMessage(Landroid/os/Message;)V

    .line 2
    iget p1, p1, Landroid/os/Message;->what:I

    const v0, 0xff01

    if-eq p1, v0, :cond_0

    goto :goto_0

    .line 3
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/service/a;->this$0:Lcom/eckom/xtlibrary/twproject/service/XTService;

    invoke-virtual {v0}, Landroid/app/Service;->getApplicationContext()Landroid/content/Context;

    move-result-object v0

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/b;->init(Landroid/content/Context;)V

    .line 4
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/a;->this$0:Lcom/eckom/xtlibrary/twproject/service/XTService;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/b;->a(Lcom/eckom/xtlibrary/b/g/a;)V

    :goto_0
    return-void
.end method

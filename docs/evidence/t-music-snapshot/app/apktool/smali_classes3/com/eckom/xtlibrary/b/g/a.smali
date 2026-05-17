.class public abstract Lcom/eckom/xtlibrary/b/g/a;
.super Ljava/lang/Object;
.source "BasePresenter.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<V::",
        "Lcom/eckom/xtlibrary/b/l/a;",
        "M:",
        "Lcom/eckom/xtlibrary/b/e/a;",
        ">",
        "Ljava/lang/Object;"
    }
.end annotation


# instance fields
.field private Gk:Ljava/lang/ref/WeakReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/ref/WeakReference<",
            "TV;>;"
        }
    .end annotation
.end field

.field public mContext:Landroid/content/Context;

.field public mHandler:Landroid/os/Handler;

.field public mModel:Lcom/eckom/xtlibrary/b/e/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "TM;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 2
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mHandler:Landroid/os/Handler;

    .line 3
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/g/a;->mContext:Landroid/content/Context;

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->getModel()Lcom/eckom/xtlibrary/b/e/a;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    .line 5
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mContext:Landroid/content/Context;

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/b;->init(Landroid/content/Context;)V

    .line 6
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/b;->a(Lcom/eckom/xtlibrary/b/g/a;)V

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;ZZ)V
    .locals 1

    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 8
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->mHandler:Landroid/os/Handler;

    .line 9
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/g/a;->mContext:Landroid/content/Context;

    .line 10
    invoke-virtual {p0, p2, p3}, Lcom/eckom/xtlibrary/b/g/a;->b(ZZ)Lcom/eckom/xtlibrary/b/e/a;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    .line 11
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/g/a;->mContext:Landroid/content/Context;

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/b;->init(Landroid/content/Context;)V

    .line 12
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/b;->a(Lcom/eckom/xtlibrary/b/g/a;)V

    return-void
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/l/a;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TV;)V"
        }
    .end annotation

    .line 1
    new-instance v0, Ljava/lang/ref/WeakReference;

    invoke-direct {v0, p1}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->Gk:Ljava/lang/ref/WeakReference;

    return-void
.end method

.method public b(ZZ)Lcom/eckom/xtlibrary/b/e/a;
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ZZ)TM;"
        }
    .end annotation

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->mModel:Lcom/eckom/xtlibrary/b/e/a;

    return-object p0
.end method

.method public delete()V
    .locals 1

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/b;->db()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->Gk:Ljava/lang/ref/WeakReference;

    if-eqz v0, :cond_0

    .line 3
    invoke-virtual {v0}, Ljava/lang/ref/WeakReference;->clear()V

    const/4 v0, 0x0

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/g/a;->Gk:Ljava/lang/ref/WeakReference;

    :cond_0
    return-void
.end method

.method public get()Lcom/eckom/xtlibrary/b/l/a;
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TV;"
        }
    .end annotation

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/g/a;->Gk:Ljava/lang/ref/WeakReference;

    if-nez p0, :cond_0

    const/4 p0, 0x0

    goto :goto_0

    :cond_0
    invoke-virtual {p0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/l/a;

    :goto_0
    return-object p0
.end method

.method public abstract getModel()Lcom/eckom/xtlibrary/b/e/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TM;"
        }
    .end annotation
.end method

.method public onDestroy()V
    .locals 0

    return-void
.end method

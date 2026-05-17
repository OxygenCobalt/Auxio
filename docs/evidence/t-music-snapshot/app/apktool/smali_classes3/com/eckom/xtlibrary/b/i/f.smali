.class public Lcom/eckom/xtlibrary/b/i/f;
.super Ljava/lang/Object;
.source "RunUtil.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/b/i/f$a;
    }
.end annotation


# static fields
.field private static sHandler:Landroid/os/Handler;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static a(Ljava/lang/Runnable;Z)V
    .locals 3

    .line 1
    invoke-static {}, Landroid/os/Looper;->myLooper()Landroid/os/Looper;

    move-result-object v0

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v1

    if-ne v0, v1, :cond_0

    .line 2
    invoke-interface {p0}, Ljava/lang/Runnable;->run()V

    return-void

    :cond_0
    const/4 v0, 0x0

    const/4 v1, 0x1

    if-eqz p1, :cond_1

    .line 3
    new-instance v0, Ljava/util/concurrent/CountDownLatch;

    invoke-direct {v0, v1}, Ljava/util/concurrent/CountDownLatch;-><init>(I)V

    .line 4
    :cond_1
    new-instance v2, Landroid/util/Pair;

    invoke-direct {v2, p0, v0}, Landroid/util/Pair;-><init>(Ljava/lang/Object;Ljava/lang/Object;)V

    .line 5
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/f;->getHandler()Landroid/os/Handler;

    move-result-object p0

    invoke-virtual {p0, v1, v2}, Landroid/os/Handler;->obtainMessage(ILjava/lang/Object;)Landroid/os/Message;

    move-result-object p0

    invoke-virtual {p0}, Landroid/os/Message;->sendToTarget()V

    if-eqz p1, :cond_2

    .line 6
    :try_start_0
    invoke-virtual {v0}, Ljava/util/concurrent/CountDownLatch;->await()V
    :try_end_0
    .catch Ljava/lang/InterruptedException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 7
    invoke-virtual {p0}, Ljava/lang/InterruptedException;->printStackTrace()V

    :cond_2
    :goto_0
    return-void
.end method

.method private static getHandler()Landroid/os/Handler;
    .locals 2

    .line 1
    const-class v0, Lcom/eckom/xtlibrary/b/i/f;

    monitor-enter v0

    .line 2
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/i/f;->sHandler:Landroid/os/Handler;

    if-nez v1, :cond_0

    .line 3
    new-instance v1, Lcom/eckom/xtlibrary/b/i/f$a;

    invoke-direct {v1}, Lcom/eckom/xtlibrary/b/i/f$a;-><init>()V

    sput-object v1, Lcom/eckom/xtlibrary/b/i/f;->sHandler:Landroid/os/Handler;

    .line 4
    :cond_0
    sget-object v1, Lcom/eckom/xtlibrary/b/i/f;->sHandler:Landroid/os/Handler;

    monitor-exit v0

    return-object v1

    :catchall_0
    move-exception v1

    .line 5
    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1
.end method

.method public static runOnUiThread(Ljava/lang/Runnable;)V
    .locals 1

    const/4 v0, 0x0

    .line 1
    invoke-static {p0, v0}, Lcom/eckom/xtlibrary/b/i/f;->a(Ljava/lang/Runnable;Z)V

    return-void
.end method

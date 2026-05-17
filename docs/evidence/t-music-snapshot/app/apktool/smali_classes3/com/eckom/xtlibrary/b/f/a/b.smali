.class Lcom/eckom/xtlibrary/b/f/a/b;
.super Landroid/os/Handler;
.source "ThreadPoolManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/a/c;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/a/c;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/a/c;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/a/b;->this$0:Lcom/eckom/xtlibrary/b/f/a/c;

    invoke-direct {p0}, Landroid/os/Handler;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .locals 4

    .line 1
    invoke-super {p0, p1}, Landroid/os/Handler;->handleMessage(Landroid/os/Message;)V

    .line 2
    iget v0, p1, Landroid/os/Message;->what:I

    const v1, 0xff01

    if-eq v0, v1, :cond_0

    goto :goto_0

    .line 3
    :cond_0
    iget-object p1, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast p1, Ljava/lang/String;

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/a/b;->this$0:Lcom/eckom/xtlibrary/b/f/a/c;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/a/c;->Ja(Ljava/lang/String;)Ljava/util/concurrent/ThreadPoolExecutor;

    move-result-object v0

    if-eqz v0, :cond_2

    .line 5
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "handleMessage: POOL_GET_TASK_COUNT "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v3, ","

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/util/concurrent/ThreadPoolExecutor;->getQueue()Ljava/util/concurrent/BlockingQueue;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/concurrent/BlockingQueue;->size()I

    move-result v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    const-string v3, "ThreadPoolManager"

    invoke-static {v3, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 6
    invoke-virtual {v0}, Ljava/util/concurrent/ThreadPoolExecutor;->getQueue()Ljava/util/concurrent/BlockingQueue;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/concurrent/BlockingQueue;->size()I

    move-result v0

    if-nez v0, :cond_1

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/a/b;->this$0:Lcom/eckom/xtlibrary/b/f/a/c;

    const-string v0, "/mnt/sdcard"

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/b/f/a/c;->a(Lcom/eckom/xtlibrary/b/f/a/c;Ljava/lang/String;)V

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/a/b;->this$0:Lcom/eckom/xtlibrary/b/f/a/c;

    const-string v0, "/storage/usb"

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/b/f/a/c;->b(Lcom/eckom/xtlibrary/b/f/a/c;Ljava/lang/String;)V

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/a/b;->this$0:Lcom/eckom/xtlibrary/b/f/a/c;

    const-string p1, "/storage/extsd"

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/f/a/c;->b(Lcom/eckom/xtlibrary/b/f/a/c;Ljava/lang/String;)V

    goto :goto_0

    .line 10
    :cond_1
    invoke-static {}, Landroid/os/Message;->obtain()Landroid/os/Message;

    move-result-object v0

    .line 11
    iput v1, v0, Landroid/os/Message;->what:I

    .line 12
    iput-object p1, v0, Landroid/os/Message;->obj:Ljava/lang/Object;

    .line 13
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/a/b;->this$0:Lcom/eckom/xtlibrary/b/f/a/c;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/a/c;->a(Lcom/eckom/xtlibrary/b/f/a/c;)Landroid/os/Handler;

    move-result-object p0

    const-wide/16 v1, 0x3e8

    invoke-virtual {p0, v0, v1, v2}, Landroid/os/Handler;->sendMessageDelayed(Landroid/os/Message;J)Z

    :cond_2
    :goto_0
    return-void
.end method

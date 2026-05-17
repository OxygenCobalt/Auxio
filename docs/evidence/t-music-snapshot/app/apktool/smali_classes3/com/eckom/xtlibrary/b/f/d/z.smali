.class Lcom/eckom/xtlibrary/b/f/d/z;
.super Ljava/lang/Object;
.source "MusicIjkID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/c/a;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/d/L;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/L;

.field private uk:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/L;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public a(Ljava/lang/String;Ljava/lang/String;Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            "Ljava/lang/String;",
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;",
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;",
            "Ljava/util/concurrent/CopyOnWriteArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/d;",
            ">;)V"
        }
    .end annotation

    .line 1
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/z;->uk:Ljava/lang/String;

    const-string v0, "/mnt/sdcard"

    .line 2
    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    const-wide/16 v1, 0x1f4

    if-eqz v0, :cond_0

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->wk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {p1, p3}, Ljava/util/concurrent/CopyOnWriteArrayList;->addAll(Ljava/util/Collection;)Z

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-static {p1, p4}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-static {p1, p5}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object p1

    const p3, 0xff04

    invoke-virtual {p1, p3}, Landroid/os/Handler;->removeMessages(I)V

    .line 7
    invoke-static {}, Landroid/os/Message;->obtain()Landroid/os/Message;

    move-result-object p1

    .line 8
    iput p3, p1, Landroid/os/Message;->what:I

    const/4 p3, 0x3

    .line 9
    iput p3, p1, Landroid/os/Message;->arg1:I

    .line 10
    iput-object p2, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object p0

    invoke-virtual {p0, p1, v1, v2}, Landroid/os/Handler;->sendMessageDelayed(Landroid/os/Message;J)Z

    goto/16 :goto_0

    :cond_0
    const-string v0, "/storage/usb"

    .line 12
    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_5

    .line 13
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->zk:Ljava/util/HashMap;

    invoke-virtual {p1, p2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-nez p1, :cond_1

    .line 14
    new-instance p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {p1}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    .line 15
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/c;->zk:Ljava/util/HashMap;

    invoke-virtual {v0, p2, p1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 16
    :cond_1
    invoke-virtual {p1, p3}, Ljava/util/concurrent/CopyOnWriteArrayList;->addAll(Ljava/util/Collection;)Z

    .line 17
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->Bj:Ljava/util/HashMap;

    invoke-virtual {p1, p2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-nez p1, :cond_2

    .line 18
    new-instance p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {p1}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    .line 19
    iget-object p3, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p3}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p3

    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/f/c;->Bj:Ljava/util/HashMap;

    invoke-virtual {p3, p2, p1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 20
    :cond_2
    invoke-static {p1, p4}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 21
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->Cj:Ljava/util/HashMap;

    invoke-virtual {p1, p2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-nez p1, :cond_3

    .line 22
    new-instance p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {p1}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    .line 23
    iget-object p3, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p3}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p3

    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/f/c;->Cj:Ljava/util/HashMap;

    invoke-virtual {p3, p2, p1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 24
    :cond_3
    invoke-static {p1, p5}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 25
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->uk:Ljava/lang/String;

    invoke-static {p1, p2}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result p1

    const p3, 0xff08

    if-eqz p1, :cond_4

    .line 26
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object p1

    invoke-virtual {p1, p3}, Landroid/os/Handler;->removeMessages(I)V

    .line 27
    :cond_4
    invoke-static {}, Landroid/os/Message;->obtain()Landroid/os/Message;

    move-result-object p1

    .line 28
    iput p3, p1, Landroid/os/Message;->what:I

    const/4 p3, 0x2

    .line 29
    iput p3, p1, Landroid/os/Message;->arg1:I

    .line 30
    iput-object p2, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    .line 31
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object p0

    invoke-virtual {p0, p1, v1, v2}, Landroid/os/Handler;->sendMessageDelayed(Landroid/os/Message;J)Z

    goto/16 :goto_0

    :cond_5
    const-string v0, "/storage/extsd"

    .line 32
    invoke-virtual {p1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result p1

    if-eqz p1, :cond_a

    .line 33
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->Ak:Ljava/util/HashMap;

    invoke-virtual {p1, p2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-nez p1, :cond_6

    .line 34
    new-instance p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {p1}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    .line 35
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/c;->Ak:Ljava/util/HashMap;

    invoke-virtual {v0, p2, p1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 36
    :cond_6
    invoke-virtual {p1, p3}, Ljava/util/concurrent/CopyOnWriteArrayList;->addAll(Ljava/util/Collection;)Z

    .line 37
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->Nj:Ljava/util/HashMap;

    invoke-virtual {p1, p2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-nez p1, :cond_7

    .line 38
    new-instance p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {p1}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    .line 39
    iget-object p3, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p3}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p3

    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/f/c;->Nj:Ljava/util/HashMap;

    invoke-virtual {p3, p2, p1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 40
    :cond_7
    invoke-static {p1, p4}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 41
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/f/c;->Oj:Ljava/util/HashMap;

    invoke-virtual {p1, p2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    if-nez p1, :cond_8

    .line 42
    new-instance p1, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {p1}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    .line 43
    iget-object p3, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p3}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object p3

    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/f/c;->Oj:Ljava/util/HashMap;

    invoke-virtual {p3, p2, p1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 44
    :cond_8
    invoke-static {p1, p5}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/concurrent/CopyOnWriteArrayList;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 45
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->uk:Ljava/lang/String;

    invoke-static {p1, p2}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result p1

    const p3, 0xff06

    if-eqz p1, :cond_9

    .line 46
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object p1

    invoke-virtual {p1, p3}, Landroid/os/Handler;->removeMessages(I)V

    .line 47
    :cond_9
    invoke-static {}, Landroid/os/Message;->obtain()Landroid/os/Message;

    move-result-object p1

    .line 48
    iput p3, p1, Landroid/os/Message;->what:I

    const/4 p3, 0x1

    .line 49
    iput p3, p1, Landroid/os/Message;->arg1:I

    .line 50
    iput-object p2, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    .line 51
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/z;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object p0

    invoke-virtual {p0, p1, v1, v2}, Landroid/os/Handler;->sendMessageDelayed(Landroid/os/Message;J)Z

    :cond_a
    :goto_0
    return-void
.end method

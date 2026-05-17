.class Lcom/eckom/xtlibrary/b/f/d/F;
.super Ljava/lang/Object;
.source "MusicIjkID3Model.java"

# interfaces
.implements Landroid/os/Handler$Callback;


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


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/L;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 21
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "SdCardPath"
        }
    .end annotation

    move-object/from16 v1, p0

    move-object/from16 v2, p1

    const-string v3, "/data/tw/.like"

    const-string v4, ""

    const-string v5, "MusicIjkID3Model"

    const/4 v6, 0x0

    .line 1
    :try_start_0
    iget v7, v2, Landroid/os/Message;->what:I

    const/16 v8, 0x112

    const/4 v9, 0x1

    if-eq v7, v8, :cond_24

    const/16 v8, 0x510

    const/16 v10, 0xff

    if-eq v7, v8, :cond_23

    const v8, 0x9e03

    const v11, 0xff09

    if-eq v7, v8, :cond_21

    const v8, 0x9e06

    if-eq v7, v8, :cond_20

    const v12, 0x9e1f

    const/4 v13, 0x2

    const/4 v14, 0x3

    if-eq v7, v12, :cond_19

    const/16 v3, 0x202

    if-eq v7, v3, :cond_17

    const/16 v3, 0x203

    if-eq v7, v3, :cond_15

    const/16 v3, 0x301

    if-eq v7, v3, :cond_14

    const/16 v3, 0x302

    if-eq v7, v3, :cond_f

    const v3, 0xff01

    packed-switch v7, :pswitch_data_0

    packed-switch v7, :pswitch_data_1

    goto/16 :goto_c

    .line 2
    :pswitch_0
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v2}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->stopPlayback()V

    .line 3
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v3}, Landroid/os/Handler;->removeMessages(I)V

    .line 4
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v4, v2, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 5
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v4, v2, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 6
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v4, v2, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 7
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v6, v2, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 8
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v6, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mDuration:I

    .line 9
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->e(Lcom/eckom/xtlibrary/b/f/d/L;)V

    goto/16 :goto_c

    .line 10
    :pswitch_1
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->pb()V

    .line 11
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->access$1000()Ljava/util/ArrayList;

    move-result-object v2

    invoke-virtual {v2}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :cond_0
    :goto_0
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_28

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 12
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v4

    if-nez v4, :cond_0

    const-string v8, ""

    const-string v9, ""

    const-string v10, ""

    const/4 v11, 0x0

    .line 13
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v12, v4, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v13, v4, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v14, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    move-object v7, v3

    invoke-interface/range {v7 .. v14}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 14
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v4

    invoke-interface {v3, v4}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    .line 15
    invoke-interface {v3, v6, v6}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    goto :goto_0

    .line 16
    :pswitch_2
    new-instance v2, Lcom/eckom/xtlibrary/b/f/d/E;

    invoke-direct {v2, v1}, Lcom/eckom/xtlibrary/b/f/d/E;-><init>(Lcom/eckom/xtlibrary/b/f/d/F;)V

    .line 17
    invoke-virtual {v2}, Ljava/lang/Thread;->start()V

    goto/16 :goto_c

    .line 18
    :pswitch_3
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 19
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->Vb()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v3

    invoke-static {v2, v3}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/e;Landroid/tw/john/TWUtil;)V

    .line 20
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/f/h;->c(Ljava/util/ArrayList;)V

    goto/16 :goto_c

    .line 21
    :pswitch_4
    iget v3, v2, Landroid/os/Message;->arg1:I

    .line 22
    iget-object v2, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v2, Ljava/lang/String;

    .line 23
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/c;->zk:Ljava/util/HashMap;

    invoke-virtual {v4, v2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 24
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->vj:Ljava/util/ArrayList;

    invoke-static {v7, v2, v13, v9, v9}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Ljava/lang/String;III)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v7

    .line 25
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->size()I

    move-result v8

    invoke-virtual {v7, v8}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 26
    iput-object v2, v7, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 27
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_1
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v8

    if-eqz v8, :cond_1

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 28
    invoke-virtual {v7, v8}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    goto :goto_1

    .line 29
    :cond_1
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/c;->Bj:Ljava/util/HashMap;

    invoke-virtual {v4, v2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 30
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->zj:Ljava/util/LinkedHashMap;

    invoke-static {v7, v2, v13, v14}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/LinkedHashMap;Ljava/lang/String;II)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v7

    .line 31
    iget-object v8, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/b/e;->Bj:Ljava/util/LinkedHashMap;

    invoke-virtual {v8, v2}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/util/ArrayList;

    if-nez v8, :cond_2

    .line 32
    new-instance v8, Ljava/util/ArrayList;

    invoke-direct {v8}, Ljava/util/ArrayList;-><init>()V

    .line 33
    iget-object v9, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/b/e;->Bj:Ljava/util/LinkedHashMap;

    invoke-virtual {v9, v2, v8}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 34
    :cond_2
    invoke-static {v8, v7, v4}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 35
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/c;->Cj:Ljava/util/HashMap;

    invoke-virtual {v4, v2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 36
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->yj:Ljava/util/LinkedHashMap;

    invoke-static {v7, v2, v13, v13}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/LinkedHashMap;Ljava/lang/String;II)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v7

    .line 37
    iget-object v8, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/b/e;->Cj:Ljava/util/LinkedHashMap;

    invoke-virtual {v8, v2}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/util/ArrayList;

    if-nez v8, :cond_3

    .line 38
    new-instance v8, Ljava/util/ArrayList;

    invoke-direct {v8}, Ljava/util/ArrayList;-><init>()V

    .line 39
    iget-object v9, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/b/e;->Cj:Ljava/util/LinkedHashMap;

    invoke-virtual {v9, v2, v8}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 40
    :cond_3
    invoke-static {v8, v7, v4}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 41
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v3}, Lcom/eckom/xtlibrary/b/f/d/L;->b(Lcom/eckom/xtlibrary/b/f/d/L;I)V

    goto/16 :goto_c

    .line 42
    :pswitch_5
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->d(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-eqz v2, :cond_28

    .line 43
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-nez v2, :cond_4

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-ne v2, v14, :cond_4

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-boolean v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Th:Z

    if-nez v2, :cond_4

    .line 44
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz v2, :cond_4

    new-instance v2, Ljava/io/File;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-direct {v2, v4}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2}, Ljava/io/File;->canRead()Z

    move-result v2

    if-eqz v2, :cond_4

    .line 45
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-virtual {v2, v4}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 46
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-virtual {v2, v4}, Lcom/eckom/xtlibrary/b/f/d/L;->seekTo(I)V

    .line 47
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V

    .line 48
    :cond_4
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v8}, Landroid/os/Handler;->removeMessages(I)V

    .line 49
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v2

    const-wide/16 v9, 0x3e8

    invoke-virtual {v2, v8, v9, v10}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 50
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v3}, Landroid/os/Handler;->removeMessages(I)V

    .line 51
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v3}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    goto/16 :goto_c

    .line 52
    :pswitch_6
    iget v3, v2, Landroid/os/Message;->arg1:I

    .line 53
    iget-object v2, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v2, Ljava/lang/String;

    .line 54
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/c;->Ak:Ljava/util/HashMap;

    invoke-virtual {v4, v2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 55
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->Hj:Ljava/util/ArrayList;

    invoke-static {v7, v2, v9, v9, v9}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Ljava/lang/String;III)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v7

    .line 56
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->size()I

    move-result v8

    invoke-virtual {v7, v8}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 57
    iput-object v2, v7, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 58
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_2
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v8

    if-eqz v8, :cond_5

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 59
    invoke-virtual {v7, v8}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    goto :goto_2

    .line 60
    :cond_5
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/c;->Nj:Ljava/util/HashMap;

    invoke-virtual {v4, v2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 61
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->Lj:Ljava/util/LinkedHashMap;

    invoke-static {v7, v2, v9, v14}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/LinkedHashMap;Ljava/lang/String;II)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v7

    .line 62
    iget-object v8, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/b/e;->Nj:Ljava/util/LinkedHashMap;

    invoke-virtual {v8, v2}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/util/ArrayList;

    if-nez v8, :cond_6

    .line 63
    new-instance v8, Ljava/util/ArrayList;

    invoke-direct {v8}, Ljava/util/ArrayList;-><init>()V

    .line 64
    iget-object v10, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v10, v10, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v10, v10, Lcom/eckom/xtlibrary/b/f/b/e;->Nj:Ljava/util/LinkedHashMap;

    invoke-virtual {v10, v2, v8}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 65
    :cond_6
    invoke-static {v8, v7, v4}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 66
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/f/c;->Oj:Ljava/util/HashMap;

    invoke-virtual {v4, v2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/concurrent/CopyOnWriteArrayList;

    .line 67
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->Kj:Ljava/util/LinkedHashMap;

    invoke-static {v7, v2, v9, v13}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/LinkedHashMap;Ljava/lang/String;II)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v7

    .line 68
    iget-object v8, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/f/b/e;->Oj:Ljava/util/LinkedHashMap;

    invoke-virtual {v8, v2}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/util/ArrayList;

    if-nez v8, :cond_7

    .line 69
    new-instance v8, Ljava/util/ArrayList;

    invoke-direct {v8}, Ljava/util/ArrayList;-><init>()V

    .line 70
    iget-object v9, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/b/e;->Oj:Ljava/util/LinkedHashMap;

    invoke-virtual {v9, v2, v8}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 71
    :cond_7
    invoke-static {v8, v7, v4}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 72
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v3}, Lcom/eckom/xtlibrary/b/f/d/L;->b(Lcom/eckom/xtlibrary/b/f/d/L;I)V

    goto/16 :goto_c

    .line 73
    :pswitch_7
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    const-string v2, "/mnt/sdcard"

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/f/d/L;->Ga(Ljava/lang/String;)V

    goto/16 :goto_c

    .line 74
    :pswitch_8
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-ne v2, v14, :cond_28

    .line 75
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->n(Lcom/eckom/xtlibrary/b/f/d/L;)V

    goto/16 :goto_c

    .line 76
    :pswitch_9
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-ne v2, v14, :cond_28

    .line 77
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->m(Lcom/eckom/xtlibrary/b/f/d/L;)V

    goto/16 :goto_c

    .line 78
    :pswitch_a
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-eqz v2, :cond_e

    .line 79
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v2}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getDuration()I

    move-result v2

    .line 80
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v4}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getCurrentPosition()I

    move-result v4

    .line 81
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v4, v7, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 82
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v2, v7, Lcom/eckom/xtlibrary/b/f/b/e;->mDuration:I

    if-gez v2, :cond_8

    move v2, v6

    :cond_8
    if-gez v4, :cond_9

    move v4, v6

    :cond_9
    if-le v4, v2, :cond_a

    return v9

    .line 83
    :cond_a
    div-int/lit16 v7, v4, 0x3e8

    .line 84
    div-int/lit8 v8, v7, 0x3c

    .line 85
    div-int/lit8 v10, v8, 0x3c

    .line 86
    rem-int/lit8 v7, v7, 0x3c

    .line 87
    rem-int/lit8 v8, v8, 0x3c

    .line 88
    rem-int/lit8 v10, v10, 0x18

    .line 89
    iget-object v11, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v11, v11, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v4, v11, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 90
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->access$1000()Ljava/util/ArrayList;

    move-result-object v11

    invoke-virtual {v11}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v11

    :goto_3
    invoke-interface {v11}, Ljava/util/Iterator;->hasNext()Z

    move-result v12

    if-eqz v12, :cond_b

    invoke-interface {v11}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v12

    check-cast v12, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 91
    iget-object v13, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v13, v13, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v13, v13, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-interface {v12, v13, v2}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    goto :goto_3

    :cond_b
    mul-int/lit8 v11, v4, 0x64

    .line 92
    div-int/2addr v11, v2

    .line 93
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->Vb()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v15

    const/16 v16, 0x1

    iget-object v12, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v12, v12, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v12, v12, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    add-int/lit8 v17, v12, 0x1

    iget-object v9, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v9, v9, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    shl-int/lit8 v10, v10, 0x10

    shl-int/lit8 v8, v8, 0x8

    or-int/2addr v8, v10

    or-int v19, v8, v7

    move/from16 v18, v9

    move/from16 v20, v11

    invoke-virtual/range {v15 .. v20}, Lcom/eckom/xtlibrary/b/f/f/t;->b(IIIII)V

    .line 94
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->Vb()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v7

    const v8, 0x9f00

    iget-object v9, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v9}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v9

    if-eqz v9, :cond_c

    const/16 v9, 0x80

    goto :goto_4

    :cond_c
    move v9, v6

    :goto_4
    and-int/lit8 v10, v11, 0x7f

    or-int/2addr v9, v10

    invoke-virtual {v7, v8, v14, v9}, Landroid/tw/john/TWUtil;->write(III)I

    .line 95
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->Vb()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v7

    const/16 v8, 0x303

    iget-object v9, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v9}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v9

    if-eqz v9, :cond_d

    const/16 v9, 0x80

    goto :goto_5

    :cond_d
    move v9, v6

    :goto_5
    or-int/2addr v9, v10

    invoke-virtual {v7, v8, v14, v9}, Landroid/tw/john/TWUtil;->write(III)I

    .line 96
    new-instance v7, Landroid/content/Intent;

    const-string v8, "com.tw.launcher.music_progress_duration"

    invoke-direct {v7, v8}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v8, "msg_music_progress"

    .line 97
    invoke-virtual {v7, v8, v4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    const-string v4, "msg_music_duration"

    .line 98
    invoke-virtual {v7, v4, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    .line 99
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->b(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/content/Context;

    move-result-object v2

    invoke-virtual {v2, v7}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    .line 100
    :cond_e
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v3}, Landroid/os/Handler;->removeMessages(I)V

    .line 101
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v1

    const-wide/16 v7, 0x3e8

    invoke-virtual {v1, v3, v7, v8}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_c

    .line 102
    :cond_f
    iget-object v3, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-eq v3, v14, :cond_10

    goto/16 :goto_c

    .line 103
    :cond_10
    iget v2, v2, Landroid/os/Message;->arg1:I

    .line 104
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "BT_CALL STATE:"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v5, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-eqz v2, :cond_13

    if-eq v2, v9, :cond_11

    if-eq v2, v13, :cond_11

    if-eq v2, v14, :cond_11

    const/4 v3, 0x4

    if-eq v2, v3, :cond_11

    goto/16 :goto_c

    .line 105
    :cond_11
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->f(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-nez v2, :cond_12

    .line 106
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v3, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v3

    invoke-static {v2, v3}, Lcom/eckom/xtlibrary/b/f/d/L;->b(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z

    .line 107
    :cond_12
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2, v9}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z

    .line 108
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Ua()V

    goto/16 :goto_c

    .line 109
    :cond_13
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2, v6}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z

    .line 110
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->g(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-eqz v2, :cond_28

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->i(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-nez v2, :cond_28

    .line 111
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V

    goto/16 :goto_c

    .line 112
    :cond_14
    iget-object v3, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v2, Landroid/os/Message;->arg1:I

    and-int/2addr v2, v10

    iput v2, v3, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    .line 113
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-eq v2, v14, :cond_28

    .line 114
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-eqz v2, :cond_28

    .line 115
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Ua()V

    goto/16 :goto_c

    .line 116
    :cond_15
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->access$1000()Ljava/util/ArrayList;

    move-result-object v1

    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_6
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_28

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 117
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/high16 v7, -0x80000000

    and-int/2addr v4, v7

    if-ne v4, v7, :cond_16

    move v4, v9

    goto :goto_7

    :cond_16
    move v4, v6

    :goto_7
    invoke-interface {v3, v4}, Lcom/eckom/xtlibrary/b/f/g/a;->f(Z)V

    goto :goto_6

    .line 118
    :cond_17
    iget-object v3, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v3

    invoke-virtual {v3, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 119
    iget-object v3, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v3

    invoke-virtual {v3, v11}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 120
    iget v3, v2, Landroid/os/Message;->arg1:I

    if-ne v3, v14, :cond_18

    iget v3, v2, Landroid/os/Message;->arg2:I

    if-ne v3, v9, :cond_18

    .line 121
    iget-object v3, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v3, v9}, Lcom/eckom/xtlibrary/b/f/d/L;->c(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z

    .line 122
    :cond_18
    iget v3, v2, Landroid/os/Message;->arg1:I

    if-ne v3, v14, :cond_28

    iget v2, v2, Landroid/os/Message;->arg2:I

    if-nez v2, :cond_28

    .line 123
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v6}, Lcom/eckom/xtlibrary/b/f/d/L;->c(Lcom/eckom/xtlibrary/b/f/d/L;Z)Z

    goto/16 :goto_c

    .line 124
    :cond_19
    iget v4, v2, Landroid/os/Message;->arg1:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const-string v7, "/storage/"

    if-eq v4, v9, :cond_1e

    if-eq v4, v13, :cond_1c

    if-eq v4, v14, :cond_1a

    goto :goto_8

    :cond_1a
    :try_start_1
    const-string v4, "/mnt/sdcard/iNand"

    .line 125
    iget v7, v2, Landroid/os/Message;->arg2:I

    if-nez v7, :cond_1b

    .line 126
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    goto :goto_8

    .line 127
    :cond_1b
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/f/d/L;->o(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v8

    invoke-static {v7, v4, v8}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    goto :goto_8

    .line 128
    :cond_1c
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v7, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 129
    iget v7, v2, Landroid/os/Message;->arg2:I

    if-nez v7, :cond_1d

    .line 130
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/b/f/d/L;->sa(Ljava/lang/String;)V

    goto :goto_8

    .line 131
    :cond_1d
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/b/f/d/L;->qa(Ljava/lang/String;)V

    goto :goto_8

    .line 132
    :cond_1e
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v7, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 133
    iget v7, v2, Landroid/os/Message;->arg2:I

    if-nez v7, :cond_1f

    .line 134
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/b/f/d/L;->ra(Ljava/lang/String;)V

    goto :goto_8

    .line 135
    :cond_1f
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/b/f/d/L;->pa(Ljava/lang/String;)V

    .line 136
    :goto_8
    iget v2, v2, Landroid/os/Message;->arg2:I

    if-eqz v2, :cond_28

    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-virtual {v2, v3}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_28

    .line 137
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/L;->o(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v4

    new-instance v7, Lcom/eckom/xtlibrary/b/f/d/D;

    invoke-direct {v7, v1}, Lcom/eckom/xtlibrary/b/f/d/D;-><init>(Lcom/eckom/xtlibrary/b/f/d/F;)V

    invoke-static {v2, v3, v4, v7}, Lcom/eckom/xtlibrary/b/f/f/h;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    goto/16 :goto_c

    .line 138
    :cond_20
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Ub()V

    goto/16 :goto_c

    .line 139
    :cond_21
    iget v2, v2, Landroid/os/Message;->arg1:I

    packed-switch v2, :pswitch_data_2

    goto/16 :goto_c

    .line 140
    :pswitch_b
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v6}, Lcom/eckom/xtlibrary/b/f/d/L;->e(Lcom/eckom/xtlibrary/b/f/d/L;Z)V

    goto/16 :goto_c

    .line 141
    :pswitch_c
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v9}, Lcom/eckom/xtlibrary/b/f/d/L;->e(Lcom/eckom/xtlibrary/b/f/d/L;Z)V

    goto/16 :goto_c

    .line 142
    :pswitch_d
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Ib()V

    goto/16 :goto_c

    .line 143
    :pswitch_e
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Hb()V

    goto/16 :goto_c

    .line 144
    :pswitch_f
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v6}, Lcom/eckom/xtlibrary/b/f/d/L;->d(Lcom/eckom/xtlibrary/b/f/d/L;Z)V

    goto/16 :goto_c

    .line 145
    :pswitch_10
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v9}, Lcom/eckom/xtlibrary/b/f/d/L;->d(Lcom/eckom/xtlibrary/b/f/d/L;Z)V

    goto/16 :goto_c

    .line 146
    :pswitch_11
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-eqz v2, :cond_28

    .line 147
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Ua()V

    goto/16 :goto_c

    .line 148
    :pswitch_12
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-nez v2, :cond_28

    .line 149
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V

    goto/16 :goto_c

    .line 150
    :pswitch_13
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->rb()V

    goto/16 :goto_c

    .line 151
    :pswitch_14
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->pb()V

    goto/16 :goto_c

    .line 152
    :pswitch_15
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 153
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;)Landroid/os/Handler;

    move-result-object v1

    const-wide/16 v2, 0x1f4

    invoke-virtual {v1, v11, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_c

    .line 154
    :pswitch_16
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result v2

    if-eqz v2, :cond_22

    .line 155
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Ua()V

    goto/16 :goto_c

    .line 156
    :cond_22
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    goto/16 :goto_c

    .line 157
    :cond_23
    :try_start_2
    iget-object v3, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v3, [B

    .line 158
    iget v2, v2, Landroid/os/Message;->arg1:I

    if-ne v2, v10, :cond_28

    .line 159
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    aget-byte v3, v3, v6

    and-int/2addr v3, v10

    invoke-static {v2, v3}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;I)I

    .line 160
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "music XTL: "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->c(Lcom/eckom/xtlibrary/b/f/d/L;)I

    move-result v1

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v5, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto/16 :goto_c

    :catch_0
    move-exception v0

    move-object v1, v0

    .line 161
    :try_start_3
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "handleMessage: 0x0510:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v5, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_c

    .line 162
    :cond_24
    iget v3, v2, Landroid/os/Message;->arg1:I

    const/high16 v4, 0x10000

    and-int/2addr v3, v4

    if-ne v3, v4, :cond_25

    goto :goto_9

    :cond_25
    move v9, v6

    .line 163
    :goto_9
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->access$1000()Ljava/util/ArrayList;

    move-result-object v3

    invoke-virtual {v3}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :goto_a
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_26

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 164
    invoke-interface {v4, v9}, Lcom/eckom/xtlibrary/b/f/g/a;->q(Z)V

    goto :goto_a

    .line 165
    :cond_26
    :pswitch_17
    iget v3, v2, Landroid/os/Message;->arg1:I

    .line 166
    iget-object v2, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v2, Ljava/lang/String;

    .line 167
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Pj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/f/c;->wk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v7}, Ljava/util/concurrent/CopyOnWriteArrayList;->size()I

    move-result v7

    invoke-virtual {v4, v7}, Lcom/eckom/xtlibrary/b/f/b/g;->setLength(I)V

    .line 168
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Pj:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v4, Lcom/eckom/xtlibrary/b/f/b/g;->mKey:Ljava/lang/String;

    .line 169
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v2

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/f/c;->wk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-virtual {v2}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :goto_b
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_27

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/b/f;

    .line 170
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/e;->Pj:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/b/f/b/g;->a(Lcom/eckom/xtlibrary/b/f/b/f;)V

    goto :goto_b

    .line 171
    :cond_27
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Uj:Ljava/util/ArrayList;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Rj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/f/c;->xk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-static {v2, v4, v7}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 172
    iget-object v2, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Vj:Ljava/util/ArrayList;

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/e;->Sj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/f/d/L;->h(Lcom/eckom/xtlibrary/b/f/d/L;)Lcom/eckom/xtlibrary/b/f/f/c;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/f/c;->yk:Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-static {v2, v4, v7}, Lcom/eckom/xtlibrary/b/j/s;->a(Ljava/util/ArrayList;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/concurrent/CopyOnWriteArrayList;)V

    .line 173
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {v1, v3}, Lcom/eckom/xtlibrary/b/f/d/L;->b(Lcom/eckom/xtlibrary/b/f/d/L;I)V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    goto :goto_c

    :catch_1
    move-exception v0

    move-object v1, v0

    .line 174
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Exception:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v5, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :cond_28
    :goto_c
    return v6

    nop

    :pswitch_data_0
    .packed-switch 0xff01
        :pswitch_a
        :pswitch_9
        :pswitch_8
        :pswitch_17
        :pswitch_7
        :pswitch_6
        :pswitch_5
        :pswitch_4
        :pswitch_3
        :pswitch_2
    .end packed-switch

    :pswitch_data_1
    .packed-switch 0xff10
        :pswitch_1
        :pswitch_0
    .end packed-switch

    :pswitch_data_2
    .packed-switch 0x1
        :pswitch_16
        :pswitch_15
        :pswitch_14
        :pswitch_13
        :pswitch_12
        :pswitch_11
        :pswitch_10
        :pswitch_f
        :pswitch_e
        :pswitch_d
        :pswitch_c
        :pswitch_b
    .end packed-switch
.end method

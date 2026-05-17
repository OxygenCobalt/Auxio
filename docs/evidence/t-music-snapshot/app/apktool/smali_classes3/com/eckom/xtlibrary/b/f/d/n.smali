.class Lcom/eckom/xtlibrary/b/f/d/n;
.super Ljava/lang/Object;
.source "MusicID3Model.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/d/t;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/t;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/t;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

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

    move-object/from16 v0, p0

    move-object/from16 v1, p1

    const-string v2, "/data/tw/.like"

    const-string v3, ""

    const-string v4, "MusicID3Model"

    const/4 v5, 0x0

    .line 1
    :try_start_0
    iget v6, v1, Landroid/os/Message;->what:I

    const-wide/16 v7, 0x3e8

    const/4 v9, 0x2

    const/16 v10, 0xff

    const v11, 0xff01

    const v12, 0xff09

    const/4 v13, 0x3

    const/4 v14, 0x1

    sparse-switch v6, :sswitch_data_0

    goto/16 :goto_a

    .line 2
    :sswitch_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v1

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->stop()V

    .line 3
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 4
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v1

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->reset()V

    .line 5
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->nd:Ljava/lang/String;

    .line 6
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->od:Ljava/lang/String;

    .line 7
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object v3, v1, Lcom/eckom/xtlibrary/b/f/b/e;->pd:Ljava/lang/String;

    .line 8
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 9
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v5, v1, Lcom/eckom/xtlibrary/b/f/b/e;->mDuration:I

    .line 10
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->e(Lcom/eckom/xtlibrary/b/f/d/t;)V

    goto/16 :goto_a

    .line 11
    :sswitch_1
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->pb()V

    .line 12
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$300()Ljava/util/ArrayList;

    move-result-object v1

    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :cond_0
    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1c

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 13
    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v3

    if-nez v3, :cond_0

    const-string v7, ""

    const-string v8, ""

    const-string v9, ""

    const/4 v10, 0x0

    .line 14
    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v11, v3, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v12, v3, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v13, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    move-object v6, v2

    invoke-interface/range {v6 .. v13}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 15
    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v3

    invoke-interface {v2, v3}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    .line 16
    invoke-interface {v2, v5, v5}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    goto :goto_0

    .line 17
    :sswitch_2
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v12}, Landroid/os/Handler;->removeMessages(I)V

    .line 18
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$1000()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v2

    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/e;Landroid/tw/john/TWUtil;)V

    .line 19
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/f/h;->c(Ljava/util/ArrayList;)V

    goto/16 :goto_a

    .line 20
    :sswitch_3
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->d(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-eqz v1, :cond_1c

    .line 21
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-nez v1, :cond_1

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-ne v1, v13, :cond_1

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Th:Z

    if-nez v1, :cond_1

    .line 22
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz v1, :cond_1

    new-instance v1, Ljava/io/File;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-direct {v1, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->canRead()Z

    move-result v1

    if-eqz v1, :cond_1

    .line 23
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)I

    move-result v1

    if-nez v1, :cond_1

    .line 24
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/f/d/t;->seekTo(I)V

    .line 25
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->Va()V

    .line 26
    :cond_1
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v1

    const v2, 0x9e06

    invoke-virtual {v1, v2}, Landroid/os/Handler;->removeMessages(I)V

    .line 27
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v1

    const v2, 0x9e06

    invoke-virtual {v1, v2, v7, v8}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 28
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 29
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v0

    invoke-virtual {v0, v11}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    goto/16 :goto_a

    .line 30
    :sswitch_4
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-ne v1, v13, :cond_1c

    .line 31
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->n(Lcom/eckom/xtlibrary/b/f/d/t;)V

    goto/16 :goto_a

    .line 32
    :sswitch_5
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-ne v1, v13, :cond_1c

    .line 33
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->m(Lcom/eckom/xtlibrary/b/f/d/t;)V

    goto/16 :goto_a

    .line 34
    :sswitch_6
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-eqz v1, :cond_8

    .line 35
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v1

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->getDuration()I

    move-result v1

    .line 36
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v2}, Lcom/eckom/xtlibrary/b/f/d/t;->Mb()Landroid/media/MediaPlayer;

    move-result-object v2

    invoke-virtual {v2}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result v2

    .line 37
    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v2, v3, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 38
    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v1, v3, Lcom/eckom/xtlibrary/b/f/b/e;->mDuration:I

    if-gez v1, :cond_2

    move v1, v5

    :cond_2
    if-gez v2, :cond_3

    move v2, v5

    :cond_3
    if-le v2, v1, :cond_4

    return v14

    .line 39
    :cond_4
    div-int/lit16 v3, v2, 0x3e8

    .line 40
    div-int/lit8 v6, v3, 0x3c

    .line 41
    div-int/lit8 v9, v6, 0x3c

    .line 42
    rem-int/lit8 v3, v3, 0x3c

    .line 43
    rem-int/lit8 v6, v6, 0x3c

    .line 44
    rem-int/lit8 v9, v9, 0x18

    .line 45
    iget-object v10, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v10, v10, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput v2, v10, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    .line 46
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$300()Ljava/util/ArrayList;

    move-result-object v10

    invoke-virtual {v10}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v10

    :goto_1
    invoke-interface {v10}, Ljava/util/Iterator;->hasNext()Z

    move-result v12

    if-eqz v12, :cond_5

    invoke-interface {v10}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v12

    check-cast v12, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 47
    iget-object v15, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v15, v15, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v15, v15, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-interface {v12, v15, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    goto :goto_1

    :cond_5
    mul-int/lit8 v10, v2, 0x64

    .line 48
    div-int/2addr v10, v1

    .line 49
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$1000()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v15

    const/16 v16, 0x1

    iget-object v12, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v12, v12, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v12, v12, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    add-int/lit8 v17, v12, 0x1

    iget-object v12, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v12, v12, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v12, v12, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v12, v12, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    shl-int/lit8 v9, v9, 0x10

    shl-int/lit8 v6, v6, 0x8

    or-int/2addr v6, v9

    or-int v19, v6, v3

    move/from16 v18, v12

    move/from16 v20, v10

    invoke-virtual/range {v15 .. v20}, Lcom/eckom/xtlibrary/b/f/f/t;->b(IIIII)V

    .line 50
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$1000()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v3

    const v6, 0x9f00

    iget-object v9, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v9}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v9

    if-eqz v9, :cond_6

    const/16 v9, 0x80

    goto :goto_2

    :cond_6
    move v9, v5

    :goto_2
    and-int/lit8 v10, v10, 0x7f

    or-int/2addr v9, v10

    invoke-virtual {v3, v6, v13, v9}, Landroid/tw/john/TWUtil;->write(III)I

    .line 51
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$1000()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object v3

    const/16 v6, 0x303

    iget-object v9, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v9}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v9

    if-eqz v9, :cond_7

    const/16 v9, 0x80

    goto :goto_3

    :cond_7
    move v9, v5

    :goto_3
    or-int/2addr v9, v10

    invoke-virtual {v3, v6, v13, v9}, Landroid/tw/john/TWUtil;->write(III)I

    .line 52
    new-instance v3, Landroid/content/Intent;

    const-string v6, "com.tw.launcher.music_progress_duration"

    invoke-direct {v3, v6}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v6, "msg_music_progress"

    .line 53
    invoke-virtual {v3, v6, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    const-string v2, "msg_music_duration"

    .line 54
    invoke-virtual {v3, v2, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    .line 55
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->h(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/content/Context;

    move-result-object v1

    invoke-virtual {v1, v3}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    .line 56
    :cond_8
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 57
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v0

    invoke-virtual {v0, v11, v7, v8}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_a

    :sswitch_7
    const/4 v3, 0x0

    .line 58
    iget v6, v1, Landroid/os/Message;->arg1:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const-string v7, "/storage/"

    if-eq v6, v14, :cond_d

    if-eq v6, v9, :cond_b

    if-eq v6, v13, :cond_9

    goto :goto_4

    :cond_9
    :try_start_1
    const-string v3, "/mnt/sdcard/iNand"

    .line 59
    iget v6, v1, Landroid/os/Message;->arg2:I

    if-nez v6, :cond_a

    .line 60
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v6}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    goto :goto_4

    .line 61
    :cond_a
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/b/e;->Qj:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v7, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v7

    invoke-static {v6, v3, v7}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Z)V

    goto :goto_4

    .line 62
    :cond_b
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v3, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    .line 63
    iget v6, v1, Landroid/os/Message;->arg2:I

    if-nez v6, :cond_c

    .line 64
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v6, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->sa(Ljava/lang/String;)V

    goto :goto_4

    .line 65
    :cond_c
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v6, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->qa(Ljava/lang/String;)V

    .line 66
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v6, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->Fa(Ljava/lang/String;)V

    goto :goto_4

    .line 67
    :cond_d
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v3, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    .line 68
    iget v6, v1, Landroid/os/Message;->arg2:I

    if-nez v6, :cond_e

    .line 69
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v6, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->ra(Ljava/lang/String;)V

    goto :goto_4

    .line 70
    :cond_e
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v6, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->pa(Ljava/lang/String;)V

    .line 71
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v6, v3}, Lcom/eckom/xtlibrary/b/f/d/t;->Fa(Ljava/lang/String;)V

    .line 72
    :goto_4
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-virtual {v6, v2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v6

    if-eqz v6, :cond_f

    .line 73
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v3

    new-instance v6, Lcom/eckom/xtlibrary/b/f/d/k;

    invoke-direct {v6, v0}, Lcom/eckom/xtlibrary/b/f/d/k;-><init>(Lcom/eckom/xtlibrary/b/f/d/n;)V

    invoke-static {v1, v2, v3, v6}, Lcom/eckom/xtlibrary/b/f/f/h;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    goto/16 :goto_5

    :cond_f
    if-eqz v3, :cond_11

    .line 74
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    const-string v6, "/data/tw/"

    invoke-virtual {v2, v6}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_11

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v6, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-static {v6}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v2, v6}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v2

    if-eqz v2, :cond_11

    .line 75
    iget v1, v1, Landroid/os/Message;->arg2:I

    if-nez v1, :cond_10

    .line 76
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 77
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 78
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->Tb()V

    goto :goto_5

    .line 79
    :cond_10
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v3, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v3

    new-instance v6, Lcom/eckom/xtlibrary/b/f/d/l;

    invoke-direct {v6, v0}, Lcom/eckom/xtlibrary/b/f/d/l;-><init>(Lcom/eckom/xtlibrary/b/f/d/n;)V

    invoke-static {v1, v2, v3, v6}, Lcom/eckom/xtlibrary/b/f/f/h;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    goto :goto_5

    :cond_11
    if-eqz v3, :cond_13

    .line 80
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-virtual {v2, v3}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_13

    .line 81
    iget v1, v1, Landroid/os/Message;->arg2:I

    if-nez v1, :cond_12

    .line 82
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 83
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 84
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->Tb()V

    goto :goto_5

    .line 85
    :cond_12
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->h(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/content/Context;

    move-result-object v6

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v9, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v10

    new-instance v11, Lcom/eckom/xtlibrary/b/f/d/m;

    invoke-direct {v11, v0}, Lcom/eckom/xtlibrary/b/f/d/m;-><init>(Lcom/eckom/xtlibrary/b/f/d/n;)V

    invoke-static/range {v6 .. v11}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Ljava/util/ArrayList;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    .line 86
    :cond_13
    :goto_5
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-static {v1, v0}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;Lcom/eckom/xtlibrary/b/f/b/g;)V

    goto/16 :goto_a

    .line 87
    :sswitch_8
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ub()V

    goto/16 :goto_a

    .line 88
    :sswitch_9
    iget v1, v1, Landroid/os/Message;->arg1:I

    packed-switch v1, :pswitch_data_0

    goto/16 :goto_a

    .line 89
    :pswitch_0
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0, v5}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;Z)V

    goto/16 :goto_a

    .line 90
    :pswitch_1
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0, v14}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;Z)V

    goto/16 :goto_a

    .line 91
    :pswitch_2
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ib()V

    goto/16 :goto_a

    .line 92
    :pswitch_3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Hb()V

    goto/16 :goto_a

    .line 93
    :pswitch_4
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0, v5}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;Z)V

    goto/16 :goto_a

    .line 94
    :pswitch_5
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0, v14}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;Z)V

    goto/16 :goto_a

    .line 95
    :pswitch_6
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-eqz v1, :cond_1c

    .line 96
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ua()V

    goto/16 :goto_a

    .line 97
    :pswitch_7
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-nez v1, :cond_1c

    .line 98
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Va()V

    goto/16 :goto_a

    .line 99
    :pswitch_8
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->rb()V

    goto/16 :goto_a

    .line 100
    :pswitch_9
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->pb()V

    goto/16 :goto_a

    .line 101
    :pswitch_a
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v12}, Landroid/os/Handler;->removeMessages(I)V

    .line 102
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v0

    const-wide/16 v1, 0x1f4

    invoke-virtual {v0, v12, v1, v2}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_a

    .line 103
    :pswitch_b
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-eqz v1, :cond_14

    .line 104
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ua()V

    goto/16 :goto_a

    .line 105
    :cond_14
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Va()V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    goto/16 :goto_a

    .line 106
    :sswitch_a
    :try_start_2
    iget-object v2, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v2, [B

    .line 107
    iget v1, v1, Landroid/os/Message;->arg1:I

    if-ne v1, v10, :cond_1c

    .line 108
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    aget-byte v2, v2, v5

    and-int/2addr v2, v10

    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;I)I

    .line 109
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "music XTL: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->c(Lcom/eckom/xtlibrary/b/f/d/t;)I

    move-result v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v4, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto/16 :goto_a

    :catch_0
    move-exception v0

    .line 110
    :try_start_3
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "handleMessage: 0x0510:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v4, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_a

    .line 111
    :sswitch_b
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-eq v2, v13, :cond_15

    goto/16 :goto_a

    .line 112
    :cond_15
    iget v1, v1, Landroid/os/Message;->arg1:I

    .line 113
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "BT_CALL STATE:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v4, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-eqz v1, :cond_18

    if-eq v1, v14, :cond_16

    if-eq v1, v9, :cond_16

    if-eq v1, v13, :cond_16

    const/4 v2, 0x4

    if-eq v1, v2, :cond_16

    goto/16 :goto_a

    .line 114
    :cond_16
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-nez v1, :cond_17

    .line 115
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v2

    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/f/d/t;->d(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z

    .line 116
    :cond_17
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1, v14}, Lcom/eckom/xtlibrary/b/f/d/t;->c(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z

    .line 117
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ua()V

    goto/16 :goto_a

    .line 118
    :cond_18
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1, v5}, Lcom/eckom/xtlibrary/b/f/d/t;->c(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z

    .line 119
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->g(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-eqz v1, :cond_1c

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->k(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-nez v1, :cond_1c

    .line 120
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Va()V

    goto/16 :goto_a

    .line 121
    :sswitch_c
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v1, Landroid/os/Message;->arg1:I

    and-int/2addr v1, v10

    iput v1, v2, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    .line 122
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->mSource:I

    if-eq v1, v13, :cond_1c

    .line 123
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v1

    if-eqz v1, :cond_1c

    .line 124
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/t;->Ua()V

    goto/16 :goto_a

    .line 125
    :sswitch_d
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$300()Ljava/util/ArrayList;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_6
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1c

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 126
    iget v3, v1, Landroid/os/Message;->arg1:I

    const/high16 v6, -0x80000000

    and-int/2addr v3, v6

    if-ne v3, v6, :cond_19

    move v3, v14

    goto :goto_7

    :cond_19
    move v3, v5

    :goto_7
    invoke-interface {v2, v3}, Lcom/eckom/xtlibrary/b/f/g/a;->f(Z)V

    goto :goto_6

    .line 127
    :sswitch_e
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v12}, Landroid/os/Handler;->removeMessages(I)V

    .line 128
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/f/d/t;->j(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/os/Handler;

    move-result-object v2

    invoke-virtual {v2, v12}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 129
    iget v2, v1, Landroid/os/Message;->arg1:I

    if-ne v2, v13, :cond_1a

    iget v2, v1, Landroid/os/Message;->arg2:I

    if-ne v2, v14, :cond_1a

    .line 130
    iget-object v2, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v2, v14}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z

    .line 131
    :cond_1a
    iget v2, v1, Landroid/os/Message;->arg1:I

    if-ne v2, v13, :cond_1c

    iget v1, v1, Landroid/os/Message;->arg2:I

    if-nez v1, :cond_1c

    .line 132
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {v0, v5}, Lcom/eckom/xtlibrary/b/f/d/t;->f(Lcom/eckom/xtlibrary/b/f/d/t;Z)Z

    goto :goto_a

    .line 133
    :sswitch_f
    iget v0, v1, Landroid/os/Message;->arg1:I

    const/high16 v1, 0x10000

    and-int/2addr v0, v1

    if-ne v0, v1, :cond_1b

    goto :goto_8

    :cond_1b
    move v14, v5

    .line 134
    :goto_8
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$300()Ljava/util/ArrayList;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_9
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1c

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 135
    invoke-interface {v1, v14}, Lcom/eckom/xtlibrary/b/f/g/a;->q(Z)V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    goto :goto_9

    :catch_1
    move-exception v0

    .line 136
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "Exception:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v4, v0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :cond_1c
    :goto_a
    :sswitch_10
    return v5

    nop

    :sswitch_data_0
    .sparse-switch
        0x112 -> :sswitch_f
        0x202 -> :sswitch_e
        0x203 -> :sswitch_d
        0x301 -> :sswitch_c
        0x302 -> :sswitch_b
        0x510 -> :sswitch_a
        0x9e03 -> :sswitch_9
        0x9e06 -> :sswitch_8
        0x9e1f -> :sswitch_7
        0xff01 -> :sswitch_6
        0xff02 -> :sswitch_5
        0xff03 -> :sswitch_4
        0xff05 -> :sswitch_10
        0xff07 -> :sswitch_3
        0xff09 -> :sswitch_2
        0xff10 -> :sswitch_1
        0xff11 -> :sswitch_0
    .end sparse-switch

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_b
        :pswitch_a
        :pswitch_9
        :pswitch_8
        :pswitch_7
        :pswitch_6
        :pswitch_5
        :pswitch_4
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

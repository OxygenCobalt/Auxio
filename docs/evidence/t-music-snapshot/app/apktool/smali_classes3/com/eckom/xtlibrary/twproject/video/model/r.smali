.class Lcom/eckom/xtlibrary/twproject/video/model/r;
.super Ljava/lang/Object;
.source "VideoModel.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/z;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/z;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 18
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "SdCardPath"
        }
    .end annotation

    move-object/from16 v0, p0

    move-object/from16 v1, p1

    const-string v2, "VideoModel"

    const/4 v3, 0x0

    .line 1
    :try_start_0
    iget v4, v1, Landroid/os/Message;->what:I

    const/16 v5, 0x8

    const/16 v6, 0x9

    const/4 v7, 0x1

    sparse-switch v4, :sswitch_data_0

    const/16 v8, 0x10

    packed-switch v4, :pswitch_data_0

    packed-switch v4, :pswitch_data_1

    goto/16 :goto_13

    .line 2
    :pswitch_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->j(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    move-result v1

    if-ne v1, v5, :cond_1

    .line 3
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->j(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1, v3}, Landroid/view/View;->setVisibility(I)V

    .line 4
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff08

    invoke-virtual {v1, v4}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 5
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 6
    :cond_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v1

    const-wide/16 v5, 0xfa0

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto :goto_0

    .line 7
    :cond_1
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->j(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1, v5}, Landroid/view/View;->setVisibility(I)V

    .line 8
    :goto_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff06

    invoke-virtual {v1, v4}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v1

    if-eqz v1, :cond_2a

    .line 9
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v0

    invoke-virtual {v0, v4}, Landroid/os/Handler;->removeMessages(I)V

    goto/16 :goto_13

    .line 10
    :pswitch_1
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->td:Lcom/eckom/xtlibrary/b/k/a/b;

    const-string v5, "/mnt/sdcard"

    invoke-virtual {v1, v4, v5}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    .line 11
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    if-eqz v1, :cond_2

    .line 12
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->v(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    .line 13
    :cond_2
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->a(Lcom/eckom/xtlibrary/b/k/a/b;)V

    .line 14
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/k/c/b;->L()V

    goto/16 :goto_13

    .line 15
    :pswitch_2
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->c(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v1

    and-int/lit16 v1, v1, 0x8f

    if-ne v1, v6, :cond_2a

    .line 16
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-virtual {v1, v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->seekTo(I)V

    .line 17
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->ma()V

    .line 18
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/z;->mMediaPlayer:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {v0, v3}, Landroid/view/SurfaceView;->setVisibility(I)V

    goto/16 :goto_13

    .line 19
    :pswitch_3
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    if-ne v1, v6, :cond_2a

    .line 20
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->e(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    goto/16 :goto_13

    .line 21
    :pswitch_4
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    if-ne v1, v6, :cond_2a

    .line 22
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->d(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    goto/16 :goto_13

    .line 23
    :pswitch_5
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v1

    if-eqz v1, :cond_8

    .line 24
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/z;->mMediaPlayer:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->getDuration()I

    move-result v1

    .line 25
    sget-object v4, Lcom/eckom/xtlibrary/twproject/video/model/z;->mMediaPlayer:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->getCurrentPosition()I

    move-result v4

    .line 26
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sput v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    if-gez v1, :cond_3

    move v1, v3

    :cond_3
    if-gez v4, :cond_4

    move v4, v3

    :cond_4
    if-le v4, v1, :cond_5

    return v7

    .line 27
    :cond_5
    div-int/lit16 v9, v4, 0x3e8

    .line 28
    div-int/lit8 v10, v9, 0x3c

    .line 29
    div-int/lit8 v11, v10, 0x3c

    .line 30
    rem-int/lit8 v9, v9, 0x3c

    .line 31
    rem-int/lit8 v10, v10, 0x3c

    .line 32
    rem-int/lit8 v11, v11, 0x18

    .line 33
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sput v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    .line 34
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v12

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v13, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-interface {v12, v13, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->d(II)V

    mul-int/lit8 v4, v4, 0x64

    .line 35
    div-int v1, v4, v1

    .line 36
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v12

    const/4 v13, 0x1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    add-int/lit8 v14, v4, 0x1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    iget v15, v4, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    shl-int/lit8 v4, v11, 0x10

    shl-int/lit8 v5, v10, 0x8

    or-int/2addr v4, v5

    or-int v16, v4, v9

    move/from16 v17, v1

    invoke-virtual/range {v12 .. v17}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->b(IIIII)V

    .line 37
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    const v5, 0x9f00

    iget-object v8, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v8}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v8

    const/16 v9, 0x80

    if-eqz v8, :cond_6

    move v8, v9

    goto :goto_1

    :cond_6
    move v8, v3

    :goto_1
    and-int/lit8 v1, v1, 0x7f

    or-int/2addr v8, v1

    invoke-virtual {v4, v5, v6, v8}, Landroid/tw/john/TWUtil;->write(III)I

    .line 38
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    const/16 v5, 0x303

    iget-object v8, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v8}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v8

    if-eqz v8, :cond_7

    goto :goto_2

    :cond_7
    move v9, v3

    :goto_2
    or-int/2addr v1, v9

    invoke-virtual {v4, v5, v6, v1}, Landroid/tw/john/TWUtil;->write(III)I

    .line 39
    :cond_8
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->i(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v1

    sget v4, Lcom/eckom/xtlibrary/R$id;->img_suspension_pp:I

    invoke-virtual {v1, v4}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/ImageView;

    invoke-virtual {v1}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v4

    if-eqz v4, :cond_9

    goto :goto_3

    :cond_9
    move v7, v3

    :goto_3
    invoke-virtual {v1, v7}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 40
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v4

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->c(Z)V

    .line 41
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff01

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 42
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v0

    const-wide/16 v5, 0x3e8

    invoke-virtual {v0, v4, v5, v6}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_13

    .line 43
    :pswitch_6
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    iget v1, v1, Landroid/os/Message;->arg1:I

    const/high16 v4, -0x80000000

    and-int/2addr v1, v4

    if-ne v1, v4, :cond_a

    goto :goto_4

    :cond_a
    move v7, v3

    :goto_4
    invoke-interface {v0, v7}, Lcom/eckom/xtlibrary/b/k/c/b;->f(Z)V

    goto/16 :goto_13

    .line 44
    :pswitch_7
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->a(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    goto/16 :goto_13

    .line 45
    :pswitch_8
    iget v1, v1, Landroid/os/Message;->arg2:I

    if-ne v1, v8, :cond_2a

    .line 46
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->g(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)Z

    goto/16 :goto_13

    .line 47
    :sswitch_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->h(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/content/Context;

    move-result-object v4

    invoke-virtual {v4}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v4

    const-string v5, "SYSTEM_FLOATVIDEO"

    invoke-static {v4, v5, v3}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v4

    if-ne v4, v7, :cond_b

    move v4, v7

    goto :goto_5

    :cond_b
    move v4, v3

    :goto_5
    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->c(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)Z

    .line 48
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->lc()V

    .line 49
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "0xff11:floatVideo:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->k(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " playing:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/twproject/video/model/z;->Ui:Z

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " inOnclickHome:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->w(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " mReverse:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v4, " showPipView:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->l(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " MultiWindowMode:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ta()Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v2, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 50
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->k(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v1

    if-eqz v1, :cond_c

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/model/z;->Ui:Z

    if-eqz v1, :cond_c

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->w(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v1

    if-eqz v1, :cond_c

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v1

    if-nez v1, :cond_c

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->l(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v1

    if-nez v1, :cond_c

    .line 51
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    goto/16 :goto_13

    .line 52
    :cond_c
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ta()Z

    move-result v1

    if-nez v1, :cond_d

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/model/z;->Ui:Z

    if-eqz v1, :cond_d

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v1

    if-nez v1, :cond_d

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->l(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v1

    if-nez v1, :cond_d

    .line 53
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->F(Z)V

    goto/16 :goto_13

    .line 54
    :cond_d
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->F(Z)V

    .line 55
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    goto/16 :goto_13

    .line 56
    :sswitch_1
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    if-ne v1, v6, :cond_2a

    .line 57
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-virtual {v1, v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->seekTo(I)V

    .line 58
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->ma()V

    goto/16 :goto_13

    .line 59
    :sswitch_2
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->j(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    move-result v1

    if-nez v1, :cond_2a

    .line 60
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->j(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v0

    invoke-virtual {v0, v5}, Landroid/view/View;->setVisibility(I)V

    goto/16 :goto_13

    .line 61
    :sswitch_3
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v4

    iget v5, v1, Landroid/os/Message;->arg1:I

    if-eq v4, v5, :cond_2a

    .line 62
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    iget v1, v1, Landroid/os/Message;->arg1:I

    invoke-static {v4, v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->a(Lcom/eckom/xtlibrary/twproject/video/model/z;I)I

    .line 63
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v4

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->v(I)V

    .line 64
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v1

    if-eqz v1, :cond_e

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->r(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v1

    if-eqz v1, :cond_e

    .line 65
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->e(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)Z

    .line 66
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    goto/16 :goto_13

    .line 67
    :cond_e
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->t(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v1

    if-eqz v1, :cond_2a

    .line 68
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->e(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)Z

    .line 69
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    goto/16 :goto_13

    :sswitch_4
    const/4 v4, 0x0

    .line 70
    iget v5, v1, Landroid/os/Message;->arg1:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const-string v8, "/mnt/"

    const-string v9, "/storage/"

    if-eq v5, v7, :cond_14

    const/4 v7, 0x2

    if-eq v5, v7, :cond_11

    const/4 v7, 0x3

    if-eq v5, v7, :cond_f

    goto/16 :goto_8

    :cond_f
    :try_start_1
    const-string v4, "/mnt/sdcard/iNand"

    .line 71
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_10

    .line 72
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->td:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-virtual {v5}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    goto/16 :goto_8

    .line 73
    :cond_10
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/twproject/video/utils/l;->td:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-virtual {v5, v7, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    goto/16 :goto_8

    .line 74
    :cond_11
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-boolean v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Qd:Z

    if-eqz v4, :cond_12

    .line 75
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    goto :goto_6

    .line 76
    :cond_12
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 77
    :goto_6
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_13

    .line 78
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->sa(Ljava/lang/String;)V

    goto :goto_8

    .line 79
    :cond_13
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->qa(Ljava/lang/String;)V

    goto :goto_8

    .line 80
    :cond_14
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-boolean v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Qd:Z

    if-eqz v4, :cond_15

    .line 81
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    goto :goto_7

    .line 82
    :cond_15
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 83
    :goto_7
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_16

    .line 84
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ra(Ljava/lang/String;)V

    goto :goto_8

    .line 85
    :cond_16
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->pa(Ljava/lang/String;)V

    .line 86
    :goto_8
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-object v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Cd:Ljava/lang/String;

    if-eqz v5, :cond_19

    if-eqz v4, :cond_19

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-object v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Cd:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_19

    .line 87
    iget v1, v1, Landroid/os/Message;->arg2:I

    if-nez v1, :cond_17

    .line 88
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    .line 89
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->f(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    .line 90
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->F(Z)V

    .line 91
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    goto :goto_9

    .line 92
    :cond_17
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->_c()Z

    move-result v1

    if-eqz v1, :cond_18

    .line 93
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->g(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    .line 94
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    sget-object v4, Lcom/eckom/xtlibrary/twproject/video/model/z;->mMediaPlayer:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->onMediaView(Landroid/view/View;)V

    .line 95
    :cond_18
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->h(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/content/Context;

    move-result-object v4

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-object v7, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Cd:Ljava/lang/String;

    invoke-virtual {v1, v4, v5, v7}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    .line 96
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->c(Lcom/eckom/xtlibrary/twproject/video/model/z;)I

    move-result v1

    if-ne v1, v6, :cond_19

    .line 97
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v1

    if-nez v1, :cond_19

    .line 98
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-static {v1, v4, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->a(Lcom/eckom/xtlibrary/twproject/video/model/z;IZ)V

    .line 99
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->ma()V

    .line 100
    :cond_19
    :goto_9
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->a(Lcom/eckom/xtlibrary/b/k/a/b;)V

    .line 101
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->b(Lcom/eckom/xtlibrary/b/k/a/b;)V

    goto/16 :goto_13

    .line 102
    :sswitch_5
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    iget v1, v1, Landroid/os/Message;->arg1:I

    if-eqz v1, :cond_1a

    goto :goto_a

    :cond_1a
    move v7, v3

    :goto_a
    invoke-interface {v0, v7}, Lcom/eckom/xtlibrary/b/k/c/b;->g(Z)V

    goto/16 :goto_13

    .line 103
    :sswitch_6
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v4

    iget v5, v1, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/k/c/b;->u(I)V

    .line 104
    iget v1, v1, Landroid/os/Message;->arg1:I

    packed-switch v1, :pswitch_data_2

    goto/16 :goto_13

    .line 105
    :pswitch_9
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)V

    goto/16 :goto_13

    .line 106
    :pswitch_a
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->b(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)V

    goto/16 :goto_13

    .line 107
    :pswitch_b
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->nc()V

    goto/16 :goto_13

    .line 108
    :pswitch_c
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->mc()V

    goto/16 :goto_13

    .line 109
    :pswitch_d
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->a(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)V

    goto/16 :goto_13

    .line 110
    :pswitch_e
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->a(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)V

    goto/16 :goto_13

    .line 111
    :pswitch_f
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff04

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 112
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v1

    if-eqz v1, :cond_1b

    .line 113
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->P()V

    .line 114
    :cond_1b
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->r(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result v1

    if-eqz v1, :cond_1c

    .line 115
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    .line 116
    :cond_1c
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/model/z;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    if-eqz v1, :cond_2a

    .line 117
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->F(Z)V

    goto/16 :goto_13

    .line 118
    :pswitch_10
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v1

    if-nez v1, :cond_2a

    .line 119
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->ma()V

    goto/16 :goto_13

    .line 120
    :pswitch_11
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->jc()V

    goto/16 :goto_13

    .line 121
    :pswitch_12
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->ic()V

    goto/16 :goto_13

    .line 122
    :pswitch_13
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->a(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    goto/16 :goto_13

    .line 123
    :pswitch_14
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v1

    if-eqz v1, :cond_1d

    .line 124
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->P()V

    goto/16 :goto_13

    .line 125
    :cond_1d
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->ma()V

    goto/16 :goto_13

    .line 126
    :sswitch_7
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->u(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0x9e06

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 127
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->oc()V

    goto/16 :goto_13

    .line 128
    :sswitch_8
    iget v4, v1, Landroid/os/Message;->arg1:I

    if-eq v4, v6, :cond_1f

    .line 129
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->isPlaying()Z

    move-result v4

    if-eqz v4, :cond_1e

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/z;->P()V

    .line 130
    :cond_1e
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v4, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->F(Z)V

    .line 131
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    .line 132
    :cond_1f
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v0

    iget v0, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    iget v4, v1, Landroid/os/Message;->arg1:I

    if-eq v0, v4, :cond_2a

    .line 133
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v0

    iget v1, v1, Landroid/os/Message;->arg1:I

    iput v1, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    .line 134
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->setSource(I)V

    goto/16 :goto_13

    .line 135
    :sswitch_9
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget v1, v1, Landroid/os/Message;->arg1:I

    if-ne v1, v7, :cond_20

    move v1, v7

    goto :goto_b

    :cond_20
    move v1, v3

    :goto_b
    iput-boolean v1, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    .line 136
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v4, :cond_22

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v4, :cond_21

    goto :goto_c

    :cond_21
    move v4, v3

    goto :goto_d

    :cond_22
    :goto_c
    move v4, v7

    :goto_d
    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->l(Z)V

    .line 137
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v1, :cond_24

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v1, :cond_23

    goto :goto_e

    :cond_23
    move v7, v3

    :cond_24
    :goto_e
    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->f(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)V

    goto/16 :goto_13

    .line 138
    :sswitch_a
    iget v0, v1, Landroid/os/Message;->arg1:I

    const/high16 v1, 0x10000

    and-int/2addr v0, v1

    if-ne v0, v1, :cond_25

    goto :goto_f

    :cond_25
    move v7, v3

    .line 139
    :goto_f
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-interface {v0, v7}, Lcom/eckom/xtlibrary/b/k/c/b;->q(Z)V

    goto :goto_13

    .line 140
    :sswitch_b
    iget-object v1, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v1, [B

    .line 141
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    aget-byte v5, v1, v7

    iput v5, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    .line 142
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v4

    aget-byte v1, v1, v7

    invoke-interface {v4, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->Y(I)V

    .line 143
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v4, :cond_27

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v4, :cond_26

    goto :goto_10

    :cond_26
    move v4, v3

    goto :goto_11

    :cond_27
    :goto_10
    move v4, v7

    :goto_11
    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->l(Z)V

    .line 144
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/r;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v1, :cond_29

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v1, :cond_28

    goto :goto_12

    :cond_28
    move v7, v3

    :cond_29
    :goto_12
    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/z;->f(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    goto :goto_13

    :catch_0
    move-exception v0

    .line 145
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, ""

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v2, v0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :cond_2a
    :goto_13
    return v3

    nop

    :sswitch_data_0
    .sparse-switch
        0x10b -> :sswitch_b
        0x112 -> :sswitch_a
        0x205 -> :sswitch_9
        0x301 -> :sswitch_8
        0x9e06 -> :sswitch_7
        0x9e09 -> :sswitch_6
        0x9e1c -> :sswitch_5
        0x9e1f -> :sswitch_4
        0x9f1c -> :sswitch_3
        0xff08 -> :sswitch_2
        0xff0e -> :sswitch_1
        0xff11 -> :sswitch_0
    .end sparse-switch

    :pswitch_data_0
    .packed-switch 0x201
        :pswitch_8
        :pswitch_7
        :pswitch_6
    .end packed-switch

    :pswitch_data_1
    .packed-switch 0xff01
        :pswitch_5
        :pswitch_4
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch

    :pswitch_data_2
    .packed-switch 0x1
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
        :pswitch_a
        :pswitch_9
    .end packed-switch
.end method

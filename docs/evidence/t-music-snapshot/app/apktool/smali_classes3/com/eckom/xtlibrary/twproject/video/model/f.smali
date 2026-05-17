.class Lcom/eckom/xtlibrary/twproject/video/model/f;
.super Ljava/lang/Object;
.source "VideoIjkModel.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/m;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

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

    const-string v2, "VideoIjkModel"

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
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->j(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    move-result v1

    if-ne v1, v5, :cond_1

    .line 3
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->j(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1, v3}, Landroid/view/View;->setVisibility(I)V

    .line 4
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff08

    invoke-virtual {v1, v4}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 5
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 6
    :cond_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v1

    const-wide/16 v5, 0xfa0

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto :goto_0

    .line 7
    :cond_1
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->j(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1, v5}, Landroid/view/View;->setVisibility(I)V

    .line 8
    :goto_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff06

    invoke-virtual {v1, v4}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v1

    if-eqz v1, :cond_29

    .line 9
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v0

    invoke-virtual {v0, v4}, Landroid/os/Handler;->removeMessages(I)V

    goto/16 :goto_13

    .line 10
    :pswitch_1
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->td:Lcom/eckom/xtlibrary/b/k/a/b;

    const-string v5, "/mnt/sdcard"

    invoke-virtual {v1, v4, v5}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    .line 11
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    if-eqz v1, :cond_2

    .line 12
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->v(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    .line 13
    :cond_2
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->a(Lcom/eckom/xtlibrary/b/k/a/b;)V

    .line 14
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/k/c/b;->L()V

    goto/16 :goto_13

    .line 15
    :pswitch_2
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->c(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v1

    and-int/lit16 v1, v1, 0x8f

    if-ne v1, v6, :cond_29

    .line 16
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-virtual {v1, v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->seekTo(I)V

    .line 17
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    .line 18
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0, v3}, Landroid/widget/FrameLayout;->setVisibility(I)V

    goto/16 :goto_13

    .line 19
    :pswitch_3
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    if-ne v1, v6, :cond_29

    .line 20
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->e(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    goto/16 :goto_13

    .line 21
    :pswitch_4
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    if-ne v1, v6, :cond_29

    .line 22
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->d(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    goto/16 :goto_13

    .line 23
    :pswitch_5
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getDuration()I

    move-result v1

    .line 24
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-object v4, v4, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v4}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getCurrentPosition()I

    move-result v4

    .line 25
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sput v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    if-gez v1, :cond_3

    move v1, v3

    :cond_3
    if-gez v4, :cond_4

    move v4, v3

    :cond_4
    if-le v4, v1, :cond_5

    return v7

    .line 26
    :cond_5
    div-int/lit16 v9, v4, 0x3e8

    .line 27
    div-int/lit8 v10, v9, 0x3c

    .line 28
    div-int/lit8 v11, v10, 0x3c

    .line 29
    rem-int/lit8 v9, v9, 0x3c

    .line 30
    rem-int/lit8 v10, v10, 0x3c

    .line 31
    rem-int/lit8 v11, v11, 0x18

    .line 32
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sput v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    .line 33
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v12

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v13, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-interface {v12, v13, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->d(II)V

    mul-int/lit8 v4, v4, 0x64

    .line 34
    div-int v1, v4, v1

    .line 35
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v12

    const/4 v13, 0x1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    add-int/lit8 v14, v4, 0x1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    iget v15, v4, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    shl-int/lit8 v4, v11, 0x10

    shl-int/lit8 v5, v10, 0x8

    or-int/2addr v4, v5

    or-int v16, v4, v9

    move/from16 v17, v1

    invoke-virtual/range {v12 .. v17}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->b(IIIII)V

    .line 36
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    const v5, 0x9f00

    iget-object v8, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v8}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

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

    .line 37
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    const/16 v5, 0x303

    iget-object v8, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v8}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v8

    if-eqz v8, :cond_7

    goto :goto_2

    :cond_7
    move v9, v3

    :goto_2
    or-int/2addr v1, v9

    invoke-virtual {v4, v5, v6, v1}, Landroid/tw/john/TWUtil;->write(III)I

    .line 38
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->i(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v1

    sget v4, Lcom/eckom/xtlibrary/R$id;->img_suspension_pp:I

    invoke-virtual {v1, v4}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/ImageView;

    invoke-virtual {v1}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v4

    if-eqz v4, :cond_8

    goto :goto_3

    :cond_8
    move v7, v3

    :goto_3
    invoke-virtual {v1, v7}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 39
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v4

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->c(Z)V

    .line 40
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff01

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 41
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v0

    const-wide/16 v5, 0x3e8

    invoke-virtual {v0, v4, v5, v6}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_13

    .line 42
    :pswitch_6
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    iget v1, v1, Landroid/os/Message;->arg1:I

    const/high16 v4, -0x80000000

    and-int/2addr v1, v4

    if-ne v1, v4, :cond_9

    goto :goto_4

    :cond_9
    move v7, v3

    :goto_4
    invoke-interface {v0, v7}, Lcom/eckom/xtlibrary/b/k/c/b;->f(Z)V

    goto/16 :goto_13

    .line 43
    :pswitch_7
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    goto/16 :goto_13

    .line 44
    :pswitch_8
    iget v1, v1, Landroid/os/Message;->arg2:I

    if-ne v1, v8, :cond_29

    .line 45
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->g(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z

    goto/16 :goto_13

    .line 46
    :sswitch_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->h(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/content/Context;

    move-result-object v4

    invoke-virtual {v4}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v4

    const-string v5, "SYSTEM_FLOATVIDEO"

    invoke-static {v4, v5, v3}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v4

    if-ne v4, v7, :cond_a

    move v4, v7

    goto :goto_5

    :cond_a
    move v4, v3

    :goto_5
    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->c(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z

    .line 47
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->lc()V

    .line 48
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "0xff11:floatVideo:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->k(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " playing:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ui:Z

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " inOnclickHome:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->w(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " mReverse:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v4, " showPipView:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->l(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, " MultiWindowMode:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ta()Z

    move-result v4

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v2, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 49
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->k(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v1

    if-eqz v1, :cond_b

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ui:Z

    if-eqz v1, :cond_b

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->w(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v1

    if-eqz v1, :cond_b

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v1

    if-nez v1, :cond_b

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->l(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v1

    if-nez v1, :cond_b

    .line 50
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    goto/16 :goto_13

    .line 51
    :cond_b
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ta()Z

    move-result v1

    if-nez v1, :cond_c

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ui:Z

    if-eqz v1, :cond_c

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v1

    if-nez v1, :cond_c

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->l(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v1

    if-nez v1, :cond_c

    .line 52
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->F(Z)V

    goto/16 :goto_13

    .line 53
    :cond_c
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->F(Z)V

    .line 54
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    goto/16 :goto_13

    .line 55
    :sswitch_1
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    if-ne v1, v6, :cond_29

    .line 56
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-virtual {v1, v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->seekTo(I)V

    .line 57
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    goto/16 :goto_13

    .line 58
    :sswitch_2
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->j(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    move-result v1

    if-nez v1, :cond_29

    .line 59
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->j(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v0

    invoke-virtual {v0, v5}, Landroid/view/View;->setVisibility(I)V

    goto/16 :goto_13

    .line 60
    :sswitch_3
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v4

    iget v5, v1, Landroid/os/Message;->arg1:I

    if-eq v4, v5, :cond_29

    .line 61
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget v1, v1, Landroid/os/Message;->arg1:I

    invoke-static {v4, v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;I)I

    .line 62
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v4

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->v(I)V

    .line 63
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v1

    if-eqz v1, :cond_d

    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->r(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v1

    if-eqz v1, :cond_d

    .line 64
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->e(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z

    .line 65
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    goto/16 :goto_13

    .line 66
    :cond_d
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->t(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v1

    if-eqz v1, :cond_29

    .line 67
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->e(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z

    .line 68
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    goto/16 :goto_13

    :sswitch_4
    const/4 v4, 0x0

    .line 69
    iget v5, v1, Landroid/os/Message;->arg1:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const-string v8, "/mnt/"

    const-string v9, "/storage/"

    if-eq v5, v7, :cond_13

    const/4 v7, 0x2

    if-eq v5, v7, :cond_10

    const/4 v7, 0x3

    if-eq v5, v7, :cond_e

    goto/16 :goto_8

    :cond_e
    :try_start_1
    const-string v4, "/mnt/sdcard/iNand"

    .line 70
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_f

    .line 71
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->td:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-virtual {v5}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    goto/16 :goto_8

    .line 72
    :cond_f
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/twproject/video/utils/l;->td:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-virtual {v5, v7, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    goto/16 :goto_8

    .line 73
    :cond_10
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-boolean v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Qd:Z

    if-eqz v4, :cond_11

    .line 74
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    goto :goto_6

    .line 75
    :cond_11
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 76
    :goto_6
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_12

    .line 77
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->sa(Ljava/lang/String;)V

    goto :goto_8

    .line 78
    :cond_12
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->qa(Ljava/lang/String;)V

    goto :goto_8

    .line 79
    :cond_13
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-boolean v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Qd:Z

    if-eqz v4, :cond_14

    .line 80
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    goto :goto_7

    .line 81
    :cond_14
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 82
    :goto_7
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_15

    .line 83
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ra(Ljava/lang/String;)V

    goto :goto_8

    .line 84
    :cond_15
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->pa(Ljava/lang/String;)V

    .line 85
    :goto_8
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-object v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Cd:Ljava/lang/String;

    if-eqz v5, :cond_18

    if-eqz v4, :cond_18

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-object v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Cd:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_18

    .line 86
    iget v1, v1, Landroid/os/Message;->arg2:I

    if-nez v1, :cond_16

    .line 87
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    .line 88
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->f(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    .line 89
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->F(Z)V

    goto :goto_9

    .line 90
    :cond_16
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->_c()Z

    move-result v1

    if-eqz v1, :cond_17

    .line 91
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->g(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    .line 92
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-object v4, v4, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->onMediaView(Landroid/view/View;)V

    .line 93
    :cond_17
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->h(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/content/Context;

    move-result-object v4

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget-object v7, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Cd:Ljava/lang/String;

    invoke-virtual {v1, v4, v5, v7}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    .line 94
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->c(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result v1

    if-ne v1, v6, :cond_18

    .line 95
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v1

    if-nez v1, :cond_18

    .line 96
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    sget v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-static {v1, v4, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;IZ)V

    .line 97
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    .line 98
    :cond_18
    :goto_9
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->a(Lcom/eckom/xtlibrary/b/k/a/b;)V

    .line 99
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->b(Lcom/eckom/xtlibrary/b/k/a/b;)V

    goto/16 :goto_13

    .line 100
    :sswitch_5
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    iget v1, v1, Landroid/os/Message;->arg1:I

    if-eqz v1, :cond_19

    goto :goto_a

    :cond_19
    move v7, v3

    :goto_a
    invoke-interface {v0, v7}, Lcom/eckom/xtlibrary/b/k/c/b;->g(Z)V

    goto/16 :goto_13

    .line 101
    :sswitch_6
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v4

    iget v5, v1, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/k/c/b;->u(I)V

    .line 102
    iget v1, v1, Landroid/os/Message;->arg1:I

    packed-switch v1, :pswitch_data_2

    goto/16 :goto_13

    .line 103
    :pswitch_9
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V

    goto/16 :goto_13

    .line 104
    :pswitch_a
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->b(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V

    goto/16 :goto_13

    .line 105
    :pswitch_b
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->nc()V

    goto/16 :goto_13

    .line 106
    :pswitch_c
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->mc()V

    goto/16 :goto_13

    .line 107
    :pswitch_d
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V

    goto/16 :goto_13

    .line 108
    :pswitch_e
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V

    goto/16 :goto_13

    .line 109
    :pswitch_f
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0xff04

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 110
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v1

    if-eqz v1, :cond_1a

    .line 111
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->P()V

    .line 112
    :cond_1a
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->r(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result v1

    if-eqz v1, :cond_1b

    .line 113
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    .line 114
    :cond_1b
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    if-eqz v1, :cond_29

    .line 115
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->F(Z)V

    goto/16 :goto_13

    .line 116
    :pswitch_10
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v1

    if-nez v1, :cond_29

    .line 117
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    goto/16 :goto_13

    .line 118
    :pswitch_11
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->jc()V

    goto/16 :goto_13

    .line 119
    :pswitch_12
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ic()V

    goto/16 :goto_13

    .line 120
    :pswitch_13
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    goto/16 :goto_13

    .line 121
    :pswitch_14
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v1

    if-eqz v1, :cond_1c

    .line 122
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->P()V

    goto/16 :goto_13

    .line 123
    :cond_1c
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    goto/16 :goto_13

    .line 124
    :sswitch_7
    iget-object v1, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object v1

    const v4, 0x9e06

    invoke-virtual {v1, v4}, Landroid/os/Handler;->removeMessages(I)V

    .line 125
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->oc()V

    goto/16 :goto_13

    .line 126
    :sswitch_8
    iget v4, v1, Landroid/os/Message;->arg1:I

    if-eq v4, v6, :cond_1e

    .line 127
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v4

    if-eqz v4, :cond_1d

    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/twproject/video/model/m;->P()V

    .line 128
    :cond_1d
    iget-object v4, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v4, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->F(Z)V

    .line 129
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {v0, v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    .line 130
    :cond_1e
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v0

    iget v0, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    iget v4, v1, Landroid/os/Message;->arg1:I

    if-eq v0, v4, :cond_29

    .line 131
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v0

    iget v1, v1, Landroid/os/Message;->arg1:I

    iput v1, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    .line 132
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->mSource:I

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->setSource(I)V

    goto/16 :goto_13

    .line 133
    :sswitch_9
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget v1, v1, Landroid/os/Message;->arg1:I

    if-ne v1, v7, :cond_1f

    move v1, v7

    goto :goto_b

    :cond_1f
    move v1, v3

    :goto_b
    iput-boolean v1, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    .line 134
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v4, :cond_21

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v4, :cond_20

    goto :goto_c

    :cond_20
    move v4, v3

    goto :goto_d

    :cond_21
    :goto_c
    move v4, v7

    :goto_d
    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->l(Z)V

    .line 135
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v1, :cond_23

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v1, :cond_22

    goto :goto_e

    :cond_22
    move v7, v3

    :cond_23
    :goto_e
    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->f(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V

    goto/16 :goto_13

    .line 136
    :sswitch_a
    iget v0, v1, Landroid/os/Message;->arg1:I

    const/high16 v1, 0x10000

    and-int/2addr v0, v1

    if-ne v0, v1, :cond_24

    goto :goto_f

    :cond_24
    move v7, v3

    .line 137
    :goto_f
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v0

    invoke-interface {v0, v7}, Lcom/eckom/xtlibrary/b/k/c/b;->q(Z)V

    goto :goto_13

    .line 138
    :sswitch_b
    iget-object v1, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v1, [B

    .line 139
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    aget-byte v5, v1, v7

    iput v5, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    .line 140
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v4

    aget-byte v1, v1, v7

    invoke-interface {v4, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->Y(I)V

    .line 141
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v4, :cond_26

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v4, :cond_25

    goto :goto_10

    :cond_25
    move v4, v3

    goto :goto_11

    :cond_26
    :goto_10
    move v4, v7

    :goto_11
    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/k/c/b;->l(Z)V

    .line 142
    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/model/f;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v1, :cond_28

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v1, :cond_27

    goto :goto_12

    :cond_27
    move v7, v3

    :cond_28
    :goto_12
    invoke-static {v0, v7}, Lcom/eckom/xtlibrary/twproject/video/model/m;->f(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    goto :goto_13

    :catch_0
    move-exception v0

    .line 143
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

    :cond_29
    :goto_13
    return v3

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

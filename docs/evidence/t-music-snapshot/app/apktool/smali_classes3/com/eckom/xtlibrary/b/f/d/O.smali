.class Lcom/eckom/xtlibrary/b/f/d/O;
.super Ljava/lang/Object;
# NOTE: JADX incorrect report references C0596O/MusicIjkModel path; validate this smali directly during behavior audits (docs/reports/jadx-remediation-checklist.md).

.source "MusicIjkModel.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/d/U;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/U;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/U;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 22
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "SdCardPath"
        }
    .end annotation

    move-object/from16 v0, p0

    move-object/from16 v1, p1

    const-string v2, ""

    const-string v3, "MusicModel"

    const/4 v4, 0x0

    .line 1
    :try_start_0
    iget v5, v1, Landroid/os/Message;->what:I

    const-wide/16 v6, 0x3e8

    const v8, 0x9e06

    const/4 v9, 0x2

    const/16 v10, 0xff

    const v11, 0xff01

    const v12, 0xff09

    const/4 v13, 0x3

    const/4 v14, 0x1

    sparse-switch v5, :sswitch_data_0

    goto/16 :goto_f

    .line 2
    :sswitch_0
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->stopPlayback()V

    .line 3
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 4
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/f/s;->nd:Ljava/lang/String;

    .line 5
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/f/s;->od:Ljava/lang/String;

    .line 6
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iput-object v2, v1, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    .line 7
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->g(Lcom/eckom/xtlibrary/b/f/d/U;)V

    goto/16 :goto_f

    .line 8
    :sswitch_1
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->pb()V

    .line 9
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$300()Ljava/util/ArrayList;

    move-result-object v1

    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :cond_0
    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_22

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 10
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v6

    if-nez v6, :cond_0

    const-string v7, ""

    const-string v8, ""

    const-string v9, ""

    const/4 v10, 0x0

    .line 11
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v11, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v12, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget v6, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v13

    iget-object v13, v13, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v13, v13, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    add-int/2addr v13, v6

    move-object v6, v5

    invoke-interface/range {v6 .. v13}, Lcom/eckom/xtlibrary/b/f/g/a;->b(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 12
    iget-object v6, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v6

    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    .line 13
    invoke-interface {v5, v4, v4}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    goto :goto_0

    .line 14
    :sswitch_2
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v12}, Landroid/os/Handler;->removeMessages(I)V

    .line 15
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->f(Lcom/eckom/xtlibrary/b/f/d/U;)V

    goto/16 :goto_f

    .line 16
    :sswitch_3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->m(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/content/Context;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Tc:Ljava/util/ArrayList;

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->b(Landroid/content/Context;Ljava/util/ArrayList;)V

    goto/16 :goto_f

    .line 17
    :sswitch_4
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "RESUME:activityResume:activityResume:"

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/f/d/U;->d(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v5

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 18
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->d(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_22

    .line 19
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->e(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_1

    .line 20
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->Wb()V

    .line 21
    :cond_1
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "RESUME:isPlaying:"

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v5

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v5, " mTW.mSource:"

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v5, " isInitiativePause:"

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-boolean v5, v5, Lcom/eckom/xtlibrary/b/f/d/U;->Th:Z

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 22
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-nez v1, :cond_3

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    if-ne v1, v13, :cond_3

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/b/f/d/U;->Th:Z

    if-eqz v1, :cond_2

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-boolean v1, Lcom/eckom/xtlibrary/b/f/f/s;->Jd:Z

    if-eqz v1, :cond_3

    .line 23
    :cond_2
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    if-eqz v1, :cond_3

    .line 24
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v1, v5}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 25
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    invoke-virtual {v1, v5}, Lcom/eckom/xtlibrary/b/f/d/U;->seekTo(I)V

    const-string v1, "playMusic()--5:"

    .line 26
    invoke-static {v3, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 27
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->Va()V

    .line 28
    :cond_3
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v8}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 29
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 30
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v0

    invoke-virtual {v0, v11, v6, v7}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_f

    .line 31
    :sswitch_5
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    const-string v5, "/mnt/sdcard"

    invoke-virtual {v0, v1, v5}, Lcom/eckom/xtlibrary/b/f/f/s;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 32
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$300()Ljava/util/ArrayList;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :cond_4
    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_22

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 33
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v5, :cond_4

    .line 34
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-interface {v1, v5}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 35
    invoke-interface {v1}, Lcom/eckom/xtlibrary/b/f/g/a;->L()V

    goto :goto_1

    .line 36
    :sswitch_6
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    if-ne v1, v13, :cond_22

    .line 37
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->l(Lcom/eckom/xtlibrary/b/f/d/U;)V

    goto/16 :goto_f

    .line 38
    :sswitch_7
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    if-ne v1, v13, :cond_22

    .line 39
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->k(Lcom/eckom/xtlibrary/b/f/d/U;)V

    goto/16 :goto_f

    .line 40
    :sswitch_8
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_b

    .line 41
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getDuration()I

    move-result v1

    .line 42
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v5}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->getCurrentPosition()I

    move-result v5

    .line 43
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v8

    iput v5, v8, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    .line 44
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v8

    iput v1, v8, Lcom/eckom/xtlibrary/b/f/f/s;->mDuration:I

    if-gez v1, :cond_5

    move v1, v4

    :cond_5
    if-gez v5, :cond_6

    move v5, v4

    :cond_6
    if-le v5, v1, :cond_7

    return v14

    .line 45
    :cond_7
    div-int/lit16 v8, v5, 0x3e8

    .line 46
    div-int/lit8 v9, v8, 0x3c

    .line 47
    div-int/lit8 v10, v9, 0x3c

    .line 48
    rem-int/lit8 v8, v8, 0x3c

    .line 49
    rem-int/lit8 v9, v9, 0x3c

    .line 50
    rem-int/lit8 v10, v10, 0x18

    .line 51
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v12

    iput v5, v12, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    .line 52
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$300()Ljava/util/ArrayList;

    move-result-object v12

    invoke-virtual {v12}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v12

    :goto_2
    invoke-interface {v12}, Ljava/util/Iterator;->hasNext()Z

    move-result v15

    if-eqz v15, :cond_8

    invoke-interface {v12}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v15

    check-cast v15, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 53
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    invoke-interface {v15, v4, v1}, Lcom/eckom/xtlibrary/b/f/g/a;->d(II)V

    const/4 v4, 0x0

    goto :goto_2

    :cond_8
    mul-int/lit8 v4, v5, 0x64

    .line 54
    div-int/2addr v4, v1

    .line 55
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v16

    const/16 v17, 0x1

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget v12, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    add-int/lit8 v18, v12, 0x1

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v12, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v12, v12, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    shl-int/lit8 v10, v10, 0x10

    shl-int/lit8 v9, v9, 0x8

    or-int/2addr v9, v10

    or-int v20, v9, v8

    move/from16 v19, v12

    move/from16 v21, v4

    invoke-virtual/range {v16 .. v21}, Lcom/eckom/xtlibrary/b/f/f/s;->b(IIIII)V

    .line 56
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v8

    const v9, 0x9f00

    iget-object v10, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v10}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v10

    if-eqz v10, :cond_9

    const/16 v10, 0x80

    goto :goto_3

    :cond_9
    const/4 v10, 0x0

    :goto_3
    and-int/lit8 v4, v4, 0x7f

    or-int/2addr v10, v4

    invoke-virtual {v8, v9, v13, v10}, Landroid/tw/john/TWUtil;->write(III)I

    .line 57
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v8

    const/16 v9, 0x303

    iget-object v10, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v10}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v10

    if-eqz v10, :cond_a

    const/16 v10, 0x80

    goto :goto_4

    :cond_a
    const/4 v10, 0x0

    :goto_4
    or-int/2addr v4, v10

    invoke-virtual {v8, v9, v13, v4}, Landroid/tw/john/TWUtil;->write(III)I

    .line 58
    new-instance v4, Landroid/content/Intent;

    const-string v8, "com.tw.launcher.music_progress_duration"

    invoke-direct {v4, v8}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v8, "msg_music_progress"

    .line 59
    invoke-virtual {v4, v8, v5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    const-string v5, "msg_music_duration"

    .line 60
    invoke-virtual {v4, v5, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    .line 61
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->m(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/content/Context;

    move-result-object v1

    invoke-virtual {v1, v4}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    .line 62
    :cond_b
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v11}, Landroid/os/Handler;->removeMessages(I)V

    .line 63
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v0

    invoke-virtual {v0, v11, v6, v7}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_f

    :sswitch_9
    const/4 v4, 0x0

    .line 64
    iget v5, v1, Landroid/os/Message;->arg1:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const-string v6, "/storage/"

    if-eq v5, v14, :cond_11

    if-eq v5, v9, :cond_e

    if-eq v5, v13, :cond_c

    goto/16 :goto_7

    :cond_c
    :try_start_1
    const-string v4, "/mnt/sdcard/iNand"

    .line 65
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_d

    .line 66
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v5}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    goto/16 :goto_7

    .line 67
    :cond_d
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/f/f/s;->td:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v5, v6, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    goto/16 :goto_7

    .line 68
    :cond_e
    sget-boolean v4, Lcom/eckom/xtlibrary/b/f/d/U;->Gd:Z

    if-eqz v4, :cond_f

    .line 69
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "/mnt/usbhost/"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    goto :goto_5

    .line 70
    :cond_f
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 71
    :goto_5
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_10

    .line 72
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->sa(Ljava/lang/String;)V

    goto :goto_7

    .line 73
    :cond_10
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->qa(Ljava/lang/String;)V

    goto :goto_7

    .line 74
    :cond_11
    sget-boolean v4, Lcom/eckom/xtlibrary/b/f/d/U;->Gd:Z

    if-eqz v4, :cond_12

    .line 75
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "/mnt/"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    goto :goto_6

    .line 76
    :cond_12
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 77
    :goto_6
    iget v5, v1, Landroid/os/Message;->arg2:I

    if-nez v5, :cond_13

    .line 78
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->ra(Ljava/lang/String;)V

    goto :goto_7

    .line 79
    :cond_13
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->pa(Ljava/lang/String;)V

    .line 80
    :goto_7
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    if-eqz v5, :cond_16

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {v5, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_16

    .line 81
    iget v1, v1, Landroid/os/Message;->arg2:I

    if-nez v1, :cond_14

    .line 82
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/b/g;->wc()V

    .line 83
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->Tb()V

    goto :goto_8

    .line 84
    :cond_14
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iget-object v4, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/U;->m(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/content/Context;

    move-result-object v4

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v5, Lcom/eckom/xtlibrary/b/f/f/s;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v6, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {v1, v4, v5, v6}, Lcom/eckom/xtlibrary/b/f/f/s;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V

    .line 85
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget v4, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    invoke-virtual {v1, v4}, Lcom/eckom/xtlibrary/b/f/f/s;->ea(I)V

    .line 86
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-nez v1, :cond_16

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/f/s;->getService()I

    move-result v1

    if-ne v1, v13, :cond_16

    .line 87
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    if-eqz v1, :cond_15

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-nez v1, :cond_15

    .line 88
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v4, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {v1, v4}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 89
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/f/f/s;->md:I

    invoke-virtual {v1, v4}, Lcom/eckom/xtlibrary/b/f/d/U;->seekTo(I)V

    .line 90
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->Va()V

    .line 91
    :cond_15
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    const/4 v4, 0x0

    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/b/f/d/U;->d(Lcom/eckom/xtlibrary/b/f/d/U;Z)V

    .line 92
    :cond_16
    :goto_8
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$300()Ljava/util/ArrayList;

    move-result-object v1

    invoke-virtual {v1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_9
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_22

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 93
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    if-eqz v5, :cond_17

    .line 94
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/f/s;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 95
    :cond_17
    iget-object v5, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/f/g/a;->a(Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;)V

    goto :goto_9

    .line 96
    :sswitch_a
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v8}, Landroid/os/Handler;->removeMessages(I)V

    .line 97
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ub()V

    goto/16 :goto_f

    .line 98
    :sswitch_b
    iget v1, v1, Landroid/os/Message;->arg1:I

    packed-switch v1, :pswitch_data_0

    goto/16 :goto_f

    .line 99
    :pswitch_0
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/d/U;->e(Lcom/eckom/xtlibrary/b/f/d/U;Z)V

    goto/16 :goto_f

    .line 100
    :pswitch_1
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0, v14}, Lcom/eckom/xtlibrary/b/f/d/U;->e(Lcom/eckom/xtlibrary/b/f/d/U;Z)V

    goto/16 :goto_f

    .line 101
    :pswitch_2
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ib()V

    goto/16 :goto_f

    .line 102
    :pswitch_3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Hb()V

    goto/16 :goto_f

    .line 103
    :pswitch_4
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/d/U;->d(Lcom/eckom/xtlibrary/b/f/d/U;Z)V

    goto/16 :goto_f

    .line 104
    :pswitch_5
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0, v14}, Lcom/eckom/xtlibrary/b/f/d/U;->d(Lcom/eckom/xtlibrary/b/f/d/U;Z)V

    goto/16 :goto_f

    .line 105
    :pswitch_6
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_22

    .line 106
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ua()V

    goto/16 :goto_f

    .line 107
    :pswitch_7
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-nez v1, :cond_22

    .line 108
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Va()V

    goto/16 :goto_f

    .line 109
    :pswitch_8
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->rb()V

    goto/16 :goto_f

    .line 110
    :pswitch_9
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->pb()V

    goto/16 :goto_f

    .line 111
    :pswitch_a
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v1

    invoke-virtual {v1, v12}, Landroid/os/Handler;->removeMessages(I)V

    .line 112
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v0

    const-wide/16 v4, 0x1f4

    invoke-virtual {v0, v12, v4, v5}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto/16 :goto_f

    .line 113
    :pswitch_b
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_18

    .line 114
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ua()V

    goto/16 :goto_f

    .line 115
    :cond_18
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Va()V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    goto/16 :goto_f

    .line 116
    :sswitch_c
    :try_start_2
    iget-object v4, v1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v4, [B

    .line 117
    iget v1, v1, Landroid/os/Message;->arg1:I

    if-ne v1, v10, :cond_22

    .line 118
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    const/4 v5, 0x0

    aget-byte v4, v4, v5

    and-int/2addr v4, v10

    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;I)I

    .line 119
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "music XTL: "

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->c(Lcom/eckom/xtlibrary/b/f/d/U;)I

    move-result v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto/16 :goto_f

    :catch_0
    move-exception v0

    .line 120
    :try_start_3
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "handleMessage: 0x0510:"

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v3, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_f

    .line 121
    :sswitch_d
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    if-eq v4, v13, :cond_19

    goto/16 :goto_f

    .line 122
    :cond_19
    iget v1, v1, Landroid/os/Message;->arg1:I

    .line 123
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "BT_CALL STATE:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-eqz v1, :cond_1d

    if-eq v1, v14, :cond_1a

    if-eq v1, v9, :cond_1a

    if-eq v1, v13, :cond_1a

    const/4 v4, 0x4

    if-eq v1, v4, :cond_1a

    goto/16 :goto_f

    .line 124
    :cond_1a
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->h(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-nez v1, :cond_1c

    .line 125
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_1b

    .line 126
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1, v14}, Lcom/eckom/xtlibrary/b/f/d/U;->c(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z

    goto :goto_a

    .line 127
    :cond_1b
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    const/4 v4, 0x0

    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/b/f/d/U;->c(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z

    .line 128
    :cond_1c
    :goto_a
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1, v14}, Lcom/eckom/xtlibrary/b/f/d/U;->b(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z

    .line 129
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ua()V

    goto/16 :goto_f

    .line 130
    :cond_1d
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    const/4 v4, 0x0

    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/b/f/d/U;->b(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z

    .line 131
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->i(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_22

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->b(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-nez v1, :cond_22

    .line 132
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Va()V

    goto/16 :goto_f

    .line 133
    :sswitch_e
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v4

    iget v1, v1, Landroid/os/Message;->arg1:I

    and-int/2addr v1, v10

    iput v1, v4, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    .line 134
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    const/16 v4, 0x9

    if-ne v1, v4, :cond_1e

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-boolean v1, Lcom/eckom/xtlibrary/b/f/f/s;->Jd:Z

    if-eqz v1, :cond_1e

    .line 135
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Tb()V

    goto/16 :goto_f

    .line 136
    :cond_1e
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/f/s;->mSource:I

    if-eq v1, v13, :cond_22

    .line 137
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/f/d/U;->j(Lcom/eckom/xtlibrary/b/f/d/U;)Z

    move-result v1

    if-eqz v1, :cond_22

    .line 138
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->Ua()V

    goto/16 :goto_f

    .line 139
    :sswitch_f
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$300()Ljava/util/ArrayList;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_b
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_22

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 140
    iget v5, v1, Landroid/os/Message;->arg1:I

    const/high16 v6, -0x80000000

    and-int/2addr v5, v6

    if-ne v5, v6, :cond_1f

    move v5, v14

    goto :goto_c

    :cond_1f
    const/4 v5, 0x0

    :goto_c
    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/f/g/a;->f(Z)V

    goto :goto_b

    .line 141
    :sswitch_10
    iget-object v4, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v4

    invoke-virtual {v4, v12}, Landroid/os/Handler;->removeMessages(I)V

    .line 142
    iget-object v4, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v4

    invoke-virtual {v4, v12}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 143
    iget v4, v1, Landroid/os/Message;->arg1:I

    if-ne v4, v13, :cond_20

    iget v4, v1, Landroid/os/Message;->arg2:I

    if-ne v4, v14, :cond_20

    .line 144
    iget-object v4, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v4, v14}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z

    .line 145
    :cond_20
    iget v4, v1, Landroid/os/Message;->arg1:I

    if-ne v4, v13, :cond_22

    iget v1, v1, Landroid/os/Message;->arg2:I

    if-nez v1, :cond_22

    .line 146
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/O;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;Z)Z

    goto :goto_f

    .line 147
    :sswitch_11
    iget v0, v1, Landroid/os/Message;->arg1:I

    const/high16 v1, 0x10000

    and-int/2addr v0, v1

    if-ne v0, v1, :cond_21

    goto :goto_d

    :cond_21
    const/4 v14, 0x0

    .line 148
    :goto_d
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$300()Ljava/util/ArrayList;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_e
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_22

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/f/g/a;

    .line 149
    invoke-interface {v1, v14}, Lcom/eckom/xtlibrary/b/f/g/a;->q(Z)V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    goto :goto_e

    :catch_1
    move-exception v0

    .line 150
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v3, v0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :cond_22
    :goto_f
    const/4 v1, 0x0

    return v1

    nop

    :sswitch_data_0
    .sparse-switch
        0x112 -> :sswitch_11
        0x202 -> :sswitch_10
        0x203 -> :sswitch_f
        0x301 -> :sswitch_e
        0x302 -> :sswitch_d
        0x510 -> :sswitch_c
        0x9e03 -> :sswitch_b
        0x9e06 -> :sswitch_a
        0x9e1f -> :sswitch_9
        0xff01 -> :sswitch_8
        0xff02 -> :sswitch_7
        0xff03 -> :sswitch_6
        0xff05 -> :sswitch_5
        0xff07 -> :sswitch_4
        0xff08 -> :sswitch_3
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

.class public Lcom/eckom/xtlibrary/b/a/d/f;
.super Lcom/eckom/xtlibrary/b/a/d/h;
.source "BTModel.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/a/a/b$b;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/b/a/d/f$a;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Lcom/eckom/xtlibrary/b/a/d/h;",
        "Lcom/eckom/xtlibrary/b/a/a/b$b;"
    }
.end annotation


# static fields
.field private static volatile Fh:Lcom/eckom/xtlibrary/b/a/d/f;


# instance fields
.field private Ah:Z

.field private Bh:I

.field private Ch:Z

.field private Dh:Z

.field private Eh:I

.field private la:Lcom/eckom/xtlibrary/b/a/b/a;

.field private mContext:Landroid/content/Context;

.field public mHandler:Landroid/os/Handler;

.field private mMediaPlayer:Landroid/media/MediaPlayer;

.field private mUri:Landroid/net/Uri;

.field private mg:I

.field private wh:Lcom/eckom/xtlibrary/b/a/b/c;

.field private xh:Lcom/eckom/xtlibrary/b/a/a/b;

.field private yh:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Lcom/eckom/xtlibrary/b/a/d/g;",
            ">;"
        }
    .end annotation
.end field

.field private zh:Lcom/eckom/xtlibrary/b/a/h/d;


# direct methods
.method private constructor <init>()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/h;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    .line 3
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mUri:Landroid/net/Uri;

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 5
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/a;->getInstance()Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    .line 6
    new-instance v0, Ljava/util/concurrent/ConcurrentHashMap;

    invoke-direct {v0}, Ljava/util/concurrent/ConcurrentHashMap;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->yh:Ljava/util/Map;

    const/4 v0, 0x0

    .line 7
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ah:Z

    .line 8
    iput v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Bh:I

    .line 9
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ch:Z

    .line 10
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Dh:Z

    .line 11
    new-instance v1, Landroid/os/Handler;

    new-instance v2, Lcom/eckom/xtlibrary/b/a/d/c;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/b/a/d/c;-><init>(Lcom/eckom/xtlibrary/b/a/d/f;)V

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    .line 12
    iput v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Eh:I

    .line 13
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/c;->open()Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    if-eqz v0, :cond_0

    const-string v0, "BTModel"

    const-string v1, "BTModel: Model create "

    .line 15
    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 16
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v1, v0, v2}, Landroid/tw/john/TWUtil;->addHandler(Ljava/lang/String;Landroid/os/Handler;)V

    .line 17
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x10b

    const/16 v1, 0xff

    invoke-virtual {p0, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    :cond_0
    return-void
.end method

.method private Ke()V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/eckom/xtlibrary/b/a/d/e;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/a/d/e;-><init>(Lcom/eckom/xtlibrary/b/a/d/f;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    .line 2
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method private Le()V
    .locals 2

    .line 1
    :try_start_0
    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/eckom/xtlibrary/b/a/d/d;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/a/d/d;-><init>(Lcom/eckom/xtlibrary/b/a/d/f;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    .line 2
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 3
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "deleteContactsFromSystemDatabase: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string v0, "BTModel"

    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :goto_0
    return-void
.end method

.method private Me()V
    .locals 3

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/a/d/f$a;

    const/4 v1, 0x0

    invoke-direct {v0, p0, v1}, Lcom/eckom/xtlibrary/b/a/d/f$a;-><init>(Lcom/eckom/xtlibrary/b/a/d/f;Lcom/eckom/xtlibrary/b/a/d/c;)V

    const/4 p0, 0x1

    new-array p0, p0, [Ljava/lang/Integer;

    const/4 v1, 0x0

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    aput-object v2, p0, v1

    invoke-virtual {v0, p0}, Landroid/os/AsyncTask;->execute([Ljava/lang/Object;)Landroid/os/AsyncTask;

    return-void
.end method

.method private Na(I)Ljava/lang/String;
    .locals 0

    packed-switch p1, :pswitch_data_0

    const-string p0, ""

    goto :goto_0

    :pswitch_0
    const-string p0, "aptX HD"

    goto :goto_0

    :pswitch_1
    const-string p0, "aptll"

    goto :goto_0

    :pswitch_2
    const-string p0, "aptX"

    goto :goto_0

    :pswitch_3
    const-string p0, "FASTSTREAM"

    goto :goto_0

    :pswitch_4
    const-string p0, "AAC"

    goto :goto_0

    :pswitch_5
    const-string p0, "MP3"

    goto :goto_0

    :pswitch_6
    const-string p0, "SBC"

    :goto_0
    return-object p0

    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_6
        :pswitch_5
        :pswitch_4
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method private Ne()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v1, 0x16

    const/16 v2, 0xff

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x7f

    invoke-virtual {p0, v1, v0}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method private Oa(I)V
    .locals 5

    .line 1
    new-instance v0, Landroid/content/Intent;

    const-string v1, "com.zjinnova.zlink"

    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 2
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    .line 3
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    if-nez v2, :cond_0

    return-void

    .line 4
    :cond_0
    invoke-virtual {v2}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v2

    invoke-virtual {v2, v1}, Landroid/content/pm/PackageManager;->getLaunchIntentForPackage(Ljava/lang/String;)Landroid/content/Intent;

    move-result-object v2

    if-eqz v2, :cond_3

    .line 5
    invoke-virtual {v2}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;

    move-result-object v3

    if-nez v3, :cond_1

    goto :goto_0

    .line 6
    :cond_1
    invoke-virtual {v2}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;

    move-result-object v2

    .line 7
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "sendZlinkKeyCode: carPlayCn:"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    const-string v4, " keyCode:"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    const-string v4, "BTModel"

    invoke-static {v4, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 8
    invoke-virtual {v2}, Landroid/content/ComponentName;->getClassName()Ljava/lang/String;

    move-result-object v2

    const-string v3, "com.suding.speedplay.ui.MainActivity"

    invoke-static {v2, v3}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v2

    if-eqz v2, :cond_2

    .line 9
    new-instance v2, Landroid/content/ComponentName;

    const-string v3, "com.texustek.speedplay.broadcast.VendorBroadcastReceiver"

    invoke-direct {v2, v1, v3}, Landroid/content/ComponentName;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    .line 10
    invoke-virtual {v0, v2}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    .line 11
    :cond_2
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "sendZlinkKeyCode: targetCn:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v4, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const-string v1, "command"

    const-string v2, "REQ_SPEC_FUNC_CMD"

    .line 12
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string v1, "specFuncCode"

    .line 13
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    .line 14
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    :cond_3
    :goto_0
    return-void
.end method

.method private Oe()V
    .locals 3

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/a/d/f$a;

    const/4 v1, 0x0

    invoke-direct {v0, p0, v1}, Lcom/eckom/xtlibrary/b/a/d/f$a;-><init>(Lcom/eckom/xtlibrary/b/a/d/f;Lcom/eckom/xtlibrary/b/a/d/c;)V

    const/4 p0, 0x1

    new-array v1, p0, [Ljava/lang/Integer;

    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p0

    const/4 v2, 0x0

    aput-object p0, v1, v2

    invoke-virtual {v0, v1}, Landroid/os/AsyncTask;->execute([Ljava/lang/Object;)Landroid/os/AsyncTask;

    return-void
.end method

.method private Pe()V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    const/4 v1, 0x0

    const-string v2, "lovedata"

    invoke-virtual {v0, v2, v1}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    .line 2
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "love"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    const-string v3, ""

    invoke-interface {v0, v2, v3}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 3
    new-instance v2, Ljava/util/ArrayList;

    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    const-string v3, "#"

    .line 4
    invoke-virtual {v0, v3}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v0

    .line 5
    :goto_0
    array-length v3, v0

    add-int/lit8 v3, v3, -0x2

    if-ge v1, v3, :cond_0

    .line 6
    new-instance v3, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    aget-object v4, v0, v1

    add-int/lit8 v5, v1, 0x1

    aget-object v5, v0, v5

    add-int/lit8 v6, v1, 0x2

    aget-object v6, v0, v6

    invoke-direct {v3, v4, v5, v6}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    const/4 v4, 0x1

    .line 7
    invoke-virtual {v3, v4}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->A(Z)V

    .line 8
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    add-int/lit8 v1, v1, 0x3

    goto :goto_0

    .line 9
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->clear()V

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v0, v2}, Ljava/util/ArrayList;->addAll(Ljava/util/Collection;)Z

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v0, 0xff09

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

.method private Qe()V
    .locals 5

    const-string v0, "BTModel"

    .line 1
    :try_start_0
    iget v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mg:I

    const/4 v2, 0x6

    if-ne v1, v2, :cond_0

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    const-string v2, "V16"

    invoke-virtual {v1, v2}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v1

    if-eqz v1, :cond_0

    return-void

    .line 2
    :cond_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    if-eqz v1, :cond_1

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->pause()V

    .line 4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->stop()V

    const/4 v1, 0x0

    .line 5
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 6
    :cond_1
    new-instance v1, Landroid/media/MediaPlayer;

    invoke-direct {v1}, Landroid/media/MediaPlayer;-><init>()V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 7
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    const/4 v2, 0x1

    invoke-static {v1, v2}, Landroid/media/RingtoneManager;->getActualDefaultRingtoneUri(Landroid/content/Context;I)Landroid/net/Uri;

    move-result-object v1

    .line 8
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mUri:Landroid/net/Uri;

    .line 9
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "initRingPlayer:mUri:"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mUri:Landroid/net/Uri;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v0, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 10
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mUri:Landroid/net/Uri;

    if-eqz v3, :cond_3

    .line 11
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v3}, Landroid/media/MediaPlayer;->reset()V

    .line 12
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    invoke-virtual {v3, v4, v1}, Landroid/media/MediaPlayer;->setDataSource(Landroid/content/Context;Landroid/net/Uri;)V

    .line 13
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    const v3, 0x3ecccccd    # 0.4f

    invoke-virtual {v1, v3, v3}, Landroid/media/MediaPlayer;->setVolume(FF)V

    .line 14
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->_c()Z

    move-result v1

    if-eqz v1, :cond_2

    .line 15
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    const/4 v3, 0x2

    invoke-virtual {v1, v3}, Landroid/media/MediaPlayer;->setAudioStreamType(I)V

    goto :goto_0

    .line 16
    :cond_2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    const/4 v3, 0x0

    invoke-virtual {v1, v3}, Landroid/media/MediaPlayer;->setAudioStreamType(I)V

    .line 17
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->prepare()V

    .line 18
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {p0, v2}, Landroid/media/MediaPlayer;->setLooping(Z)V

    const-string p0, "initRingPlayer:end"

    .line 19
    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    :catch_0
    move-exception p0

    .line 20
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "initRingPlayer:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_3
    :goto_1
    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/a/d/f;I)I
    .locals 0

    .line 3
    iput p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Bh:I

    return p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    return-object p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/a/d/f;ILjava/lang/String;)V
    .locals 0

    .line 2
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/d/f;->b(ILjava/lang/String;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/a/d/f;Z)Z
    .locals 0

    .line 4
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ah:Z

    return p1
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mMediaPlayer:Landroid/media/MediaPlayer;

    return-object p0
.end method

.method private b(ILjava/lang/String;)V
    .locals 12

    const/4 v0, 0x0

    const/16 v1, 0x510

    const/4 v2, 0x4

    const/4 v3, 0x0

    if-nez p2, :cond_0

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    shl-int/2addr p1, v2

    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_5

    .line 5
    :cond_0
    iget v4, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Eh:I

    and-int/lit8 v5, v4, 0x1

    const-string v6, "UTF-8"

    const/4 v7, 0x1

    if-ne v5, v7, :cond_2

    .line 6
    :try_start_0
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 7
    :catch_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    shl-int/2addr p1, v2

    or-int/2addr p1, v3

    if-nez v0, :cond_1

    goto :goto_0

    :cond_1
    array-length v3, v0

    :goto_0
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto/16 :goto_5

    :cond_2
    and-int/lit8 v5, v4, 0x2

    const/4 v8, 0x2

    if-ne v5, v8, :cond_4

    :try_start_1
    const-string v4, "Unicode"

    .line 8
    invoke-virtual {p2, v4}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    .line 9
    :catch_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    shl-int/2addr p1, v2

    or-int/2addr p1, v7

    if-nez v0, :cond_3

    goto :goto_1

    :cond_3
    array-length v3, v0

    :goto_1
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto :goto_5

    :cond_4
    and-int/lit8 v5, v4, 0x4

    const/4 v7, 0x3

    const-string v9, "GB2312"

    const-string v10, "GBK"

    const/16 v11, 0x80

    if-ne v5, v2, :cond_8

    .line 10
    :try_start_2
    invoke-virtual {p2, v10}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    :catch_2
    if-nez v0, :cond_5

    .line 11
    :try_start_3
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_3

    goto :goto_2

    :cond_5
    move v7, v8

    :catch_3
    :goto_2
    if-nez v0, :cond_6

    .line 12
    iget v4, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Eh:I

    and-int/2addr v4, v11

    if-ne v4, v11, :cond_6

    .line 13
    :try_start_4
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_4

    :catch_4
    move v7, v3

    .line 14
    :cond_6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    shl-int/2addr p1, v2

    or-int/2addr p1, v7

    if-nez v0, :cond_7

    goto :goto_3

    :cond_7
    array-length v3, v0

    :goto_3
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    goto :goto_5

    :cond_8
    const/16 v5, 0x8

    and-int/2addr v4, v5

    if-ne v4, v5, :cond_c

    .line 15
    :try_start_5
    invoke-virtual {p2, v9}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_5

    :catch_5
    if-nez v0, :cond_9

    .line 16
    :try_start_6
    invoke-virtual {p2, v10}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_6

    :catch_6
    move v7, v8

    :cond_9
    if-nez v0, :cond_a

    .line 17
    iget v4, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Eh:I

    and-int/2addr v4, v11

    if-ne v4, v11, :cond_a

    .line 18
    :try_start_7
    invoke-virtual {p2, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v0
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_7

    :catch_7
    move v7, v3

    .line 19
    :cond_a
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    shl-int/2addr p1, v2

    or-int/2addr p1, v7

    if-nez v0, :cond_b

    goto :goto_4

    :cond_b
    array-length v3, v0

    :goto_4
    invoke-virtual {p0, v1, p1, v3, v0}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    :cond_c
    :goto_5
    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/a/d/f;I)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/f;->Oa(I)V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/a/d/f;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ch:Z

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/a/d/f;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Eh:I

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->Ke()V

    return-void
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/a/d/f;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Bh:I

    return p0
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/a/d/f;I)Ljava/lang/String;
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/f;->Na(I)Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/a/d/f;)I
    .locals 2

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Bh:I

    add-int/lit8 v1, v0, 0x1

    iput v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Bh:I

    return v0
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->Me()V

    return-void
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->Ne()V

    return-void
.end method

.method public static getInstance()Lcom/eckom/xtlibrary/b/a/d/f;
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/a/d/f;->Fh:Lcom/eckom/xtlibrary/b/a/d/f;

    if-nez v0, :cond_1

    .line 2
    const-class v0, Lcom/eckom/xtlibrary/b/a/d/f;

    monitor-enter v0

    .line 3
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/a/d/f;->Fh:Lcom/eckom/xtlibrary/b/a/d/f;

    if-nez v1, :cond_0

    .line 4
    new-instance v1, Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-direct {v1}, Lcom/eckom/xtlibrary/b/a/d/f;-><init>()V

    sput-object v1, Lcom/eckom/xtlibrary/b/a/d/f;->Fh:Lcom/eckom/xtlibrary/b/a/d/f;

    .line 5
    :cond_0
    monitor-exit v0

    goto :goto_0

    :catchall_0
    move-exception v1

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1

    .line 6
    :cond_1
    :goto_0
    sget-object v0, Lcom/eckom/xtlibrary/b/a/d/f;->Fh:Lcom/eckom/xtlibrary/b/a/d/f;

    return-object v0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->Oe()V

    return-void
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->Pe()V

    return-void
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->Qe()V

    return-void
.end method

.method static synthetic k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    return-object p0
.end method

.method static synthetic l(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/h/d;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->zh:Lcom/eckom/xtlibrary/b/a/h/d;

    return-object p0
.end method

.method static synthetic m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->yh:Ljava/util/Map;

    return-object p0
.end method

.method static synthetic n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->xh:Lcom/eckom/xtlibrary/b/a/a/b;

    return-object p0
.end method

.method static synthetic o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    return-object p0
.end method

.method static synthetic p(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->Le()V

    return-void
.end method

.method static synthetic q(Lcom/eckom/xtlibrary/b/a/d/f;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ch:Z

    return p0
.end method


# virtual methods
.method public Aa(Ljava/lang/String;)V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0xa

    const/4 v1, 0x3

    invoke-virtual {p0, v0, v1, p1}, Lcom/eckom/xtlibrary/b/a/b/c;->write(IILjava/lang/String;)I

    return-void
.end method

.method public B(Z)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iput-boolean p1, v0, Lcom/eckom/xtlibrary/b/a/b/a;->wg:Z

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    if-eqz p1, :cond_0

    const/16 p1, 0x8

    goto :goto_0

    :cond_0
    const/16 p1, 0x88

    :goto_0
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/b/c;->ca(I)V

    return-void
.end method

.method public Ba(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->yh:Ljava/util/Map;

    invoke-interface {p0, p1}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method public Ca(Ljava/lang/String;)V
    .locals 1

    .line 1
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_0

    return-void

    .line 2
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x20

    invoke-virtual {p0, v0, p1}, Lcom/eckom/xtlibrary/b/a/b/c;->write(ILjava/lang/String;)I

    const-string p0, "persist.service.bt.pincode"

    .line 3
    invoke-static {p0, p1}, Landroid/os/SystemProperties;->set(Ljava/lang/String;Ljava/lang/String;)V

    return-void
.end method

.method public a(Landroid/content/Context;)V
    .locals 4

    .line 5
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    .line 6
    iget-boolean p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Dh:Z

    if-nez p1, :cond_0

    .line 7
    new-instance p1, Lcom/eckom/xtlibrary/b/a/a/b;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    invoke-direct {p1, v0, p0}, Lcom/eckom/xtlibrary/b/a/a/b;-><init>(Landroid/content/Context;Lcom/eckom/xtlibrary/b/a/a/b$b;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->xh:Lcom/eckom/xtlibrary/b/a/a/b;

    const/4 p1, 0x1

    .line 8
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Dh:Z

    .line 9
    :cond_0
    new-instance p1, Lcom/eckom/xtlibrary/b/a/h/d;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    invoke-direct {p1, v0, p0}, Lcom/eckom/xtlibrary/b/a/h/d;-><init>(Landroid/content/Context;Lcom/eckom/xtlibrary/b/a/d/h;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->zh:Lcom/eckom/xtlibrary/b/a/h/d;

    .line 10
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object p1

    sget v0, Lcom/eckom/xtlibrary/R$array;->enable_phone_call_record_version:I

    invoke-virtual {p1, v0}, Landroid/content/res/Resources;->getStringArray(I)[Ljava/lang/String;

    move-result-object p1

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    invoke-static {p1}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object p1

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/a/b/a;->kh:Ljava/util/List;

    .line 12
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    const/4 v1, 0x0

    const-string v2, "TABLE_BT"

    const-string v3, "BATTERY_LEVEL"

    invoke-static {v0, v2, v3, v1}, Lcom/eckom/xtlibrary/b/j/o;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)I

    move-result v0

    iput v0, p1, Lcom/eckom/xtlibrary/b/a/b/a;->Ug:I

    const/4 p1, 0x2

    const-string v0, "persist.tw.bt.module"

    .line 13
    invoke-static {v0, p1}, Landroid/os/SystemProperties;->getInt(Ljava/lang/String;I)I

    move-result p1

    iput p1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mg:I

    return-void
.end method

.method public a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/a/d/g;)V
    .locals 1

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->yh:Ljava/util/Map;

    invoke-interface {v0, p1, p2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 15
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 p1, 0x112

    const/16 p2, 0xff

    invoke-virtual {p0, p1, p2}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method public a(ZLjava/lang/String;)V
    .locals 2

    .line 16
    new-instance v0, Landroid/content/Intent;

    invoke-direct {v0}, Landroid/content/Intent;-><init>()V

    if-eqz p1, :cond_0

    const-string p1, "net.easyconn.bt.connected"

    .line 17
    invoke-virtual {v0, p1}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    const/4 p1, 0x1

    new-array p1, p1, [Ljava/lang/String;

    const/4 v1, 0x0

    aput-object p2, p1, v1

    const-string p2, "number"

    .line 18
    invoke-virtual {v0, p2, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[Ljava/lang/String;)Landroid/content/Intent;

    goto :goto_0

    :cond_0
    const-string p1, "net.easyconn.bt.closed"

    .line 19
    invoke-virtual {v0, p1}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    .line 20
    :goto_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mContext:Landroid/content/Context;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    return-void
.end method

.method public answer()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/4 v1, 0x1

    const/16 v2, 0xa

    invoke-virtual {v0, v2, v1}, Landroid/tw/john/TWUtil;->write(II)I

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    invoke-virtual {p0, v2, v1}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method public ba()V
    .locals 0

    return-void
.end method

.method public getCallState()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    return p0
.end method

.method public mb()I
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    return p0
.end method

.method public nb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-boolean v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->qg:Z

    if-nez v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0xa

    const/4 v1, 0x0

    invoke-virtual {p0, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    :cond_0
    return-void
.end method

.method public ob()V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x14

    const/4 v1, 0x0

    invoke-virtual {p0, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method public pb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    if-eqz v0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    const/16 v1, 0x8

    if-eq v0, v1, :cond_0

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->xb()V

    .line 4
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x14

    const/4 v1, 0x2

    invoke-virtual {p0, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    :cond_1
    return-void
.end method

.method public qb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    if-eqz v0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    const/16 v1, 0x8

    if-eq v0, v1, :cond_0

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->xb()V

    .line 4
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x14

    const/4 v1, 0x0

    invoke-virtual {p0, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    :cond_1
    return-void
.end method

.method public rb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    if-eqz v0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    const/16 v1, 0x8

    if-eq v0, v1, :cond_0

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->xb()V

    .line 4
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x14

    const/4 v1, 0x3

    invoke-virtual {p0, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    :cond_1
    return-void
.end method

.method public sb()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0xc

    invoke-virtual {p0, v0}, Landroid/tw/john/TWUtil;->write(I)I

    return-void
.end method

.method public setDeviceName(Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x1e

    invoke-virtual {p0, v0, p1}, Lcom/eckom/xtlibrary/b/a/b/c;->write(ILjava/lang/String;)I

    return-void
.end method

.method public t(I)V
    .locals 0

    return-void
.end method

.method public tb()V
    .locals 2

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ch:Z

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x2c

    const-string v1, "Topway"

    invoke-virtual {p0, v0, v1}, Lcom/eckom/xtlibrary/b/a/b/c;->write(ILjava/lang/String;)I

    return-void
.end method

.method public ub()V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v1, 0xff07

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    const/4 v0, 0x0

    move v2, v0

    .line 2
    :goto_0
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/a/b/a;->kh:Ljava/util/List;

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v3

    const/4 v4, 0x1

    if-ge v2, v3, :cond_1

    .line 3
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v5, v3, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/a/b/a;->kh:Ljava/util/List;

    invoke-interface {v3, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Ljava/lang/CharSequence;

    invoke-virtual {v5, v3}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_0

    move v2, v4

    goto :goto_1

    :cond_0
    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    :cond_1
    move v2, v0

    .line 4
    :goto_1
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "getCallRecord: commonData.mSystemVersion:"

    invoke-virtual {v3, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    invoke-virtual {v3, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    const-string v5, "BTModel"

    invoke-static {v5, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 5
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "getCallRecord: commonData.mVersionName:"

    invoke-virtual {v3, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    invoke-virtual {v3, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v6, " enablePhoneCallRecord:"

    invoke-virtual {v3, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v2}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v6, " supportPhoneCallRecord:"

    invoke-virtual {v3, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-boolean v6, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ah:Z

    invoke-virtual {v3, v6}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v5, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 6
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v3}, Landroid/os/Handler;->obtainMessage()Landroid/os/Message;

    move-result-object v3

    .line 7
    iput v1, v3, Landroid/os/Message;->what:I

    .line 8
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v1

    if-nez v1, :cond_2

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/b/a/d/f;->Ah:Z

    if-eqz v1, :cond_2

    if-eqz v2, :cond_2

    .line 9
    iput v0, v3, Landroid/os/Message;->arg1:I

    .line 10
    iput v0, v3, Landroid/os/Message;->arg2:I

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iput-boolean v4, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Zg:Z

    goto :goto_2

    .line 12
    :cond_2
    iput v4, v3, Landroid/os/Message;->arg1:I

    .line 13
    iput v0, v3, Landroid/os/Message;->arg2:I

    .line 14
    :goto_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v3}, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z

    return-void
.end method

.method public vb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v1, 0xff08

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/b/c;->w(Z)V

    return-void
.end method

.method public wb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v1, 0xff05

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

.method public xb()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Yg:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Yg:Ljava/lang/String;

    const-string v1, "auto"

    invoke-virtual {v0, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_0

    const-string p0, "BTModel"

    const-string v0, "requestSource:Android Auto is connect, do not change source to 0x08"

    .line 2
    invoke-static {p0, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    return-void

    .line 3
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/4 v0, 0x1

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/a/b/c;->w(Z)V

    return-void
.end method

.method public yb()V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v0, 0x40

    const/4 v1, 0x1

    invoke-virtual {p0, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

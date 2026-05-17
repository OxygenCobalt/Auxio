.class public Lcom/tw/music/MusicService;
.super Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;
.source "MusicService.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/tw/music/MusicService$a;
    }
.end annotation


# instance fields
.field public Pa:Lcom/tw/music/b/a;

.field private Qa:Lcom/tw/music/view/MusicWidgetProvider;

.field private Ra:Landroid/content/BroadcastReceiver;
.field private Sa:Lcom/tw/music/media/MediaControlBridge;
.field private Ta:Z

.field private final mBinder:Landroid/os/IBinder;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;-><init>()V

    .line 2
    new-instance v0, Lcom/tw/music/MusicService$a;

    invoke-direct {v0, p0}, Lcom/tw/music/MusicService$a;-><init>(Lcom/tw/music/MusicService;)V

    iput-object v0, p0, Lcom/tw/music/MusicService;->mBinder:Landroid/os/IBinder;

    .line 3
    new-instance v0, Lcom/tw/music/b/a;

    invoke-direct {v0}, Lcom/tw/music/b/a;-><init>()V

    iput-object v0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    const/4 v0, 0x0

    .line 4
    iput-object v0, p0, Lcom/tw/music/MusicService;->Qa:Lcom/tw/music/view/MusicWidgetProvider;

    .line 5
    iput-object v0, p0, Lcom/tw/music/MusicService;->Sa:Lcom/tw/music/media/MediaControlBridge;

    .line 6
    iput-boolean v0, p0, Lcom/tw/music/MusicService;->Ta:Z

    .line 7
    new-instance v0, Lcom/tw/music/k;

    invoke-direct {v0, p0}, Lcom/tw/music/k;-><init>(Lcom/tw/music/MusicService;)V

    iput-object v0, p0, Lcom/tw/music/MusicService;->Ra:Landroid/content/BroadcastReceiver;

    return-void
.end method

.method static synthetic b(Lcom/tw/music/MusicService;)Lcom/tw/music/view/MusicWidgetProvider;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/MusicService;->Qa:Lcom/tw/music/view/MusicWidgetProvider;

    return-object p0
.end method


# virtual methods
.method public B(I)V
    .locals 0

    return-void
.end method

.method public a(Ljava/lang/Boolean;)V
    .locals 10

    .line 3
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;->a(Ljava/lang/Boolean;)V

    .line 4
    iget-object v0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    if-eqz v0, :cond_0

    .line 5
    invoke-virtual {p1}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v1

    invoke-virtual {v0, v1}, Lcom/tw/music/b/a;->H(Z)V

    :cond_0
    iget-object v0, p0, Lcom/tw/music/MusicService;->Sa:Lcom/tw/music/media/MediaControlBridge;

    if-eqz v0, :cond_1

    if-eqz p1, :cond_1

    invoke-virtual {p1}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v1

    iput-boolean v1, p0, Lcom/tw/music/MusicService;->Ta:Z

    const/4 v2, 0x0

    const/4 v3, 0x0

    const-wide/16 v4, 0x0

    const/high16 v6, 0x3f800000    # 1.0f

    const/4 v7, 0x1

    invoke-static/range {v1 .. v7}, Lcom/tw/music/media/PlaybackStateMapper;->map(ZZZJFZ)Landroid/support/v4/media/session/PlaybackStateCompat;

    move-result-object v8

    invoke-virtual {v0, v8}, Lcom/tw/music/media/MediaControlBridge;->publishState(Landroid/support/v4/media/session/PlaybackStateCompat;)V

    invoke-virtual {v0, v1}, Lcom/tw/music/media/MediaControlBridge;->updateNotification(Z)V

    :cond_1
    return-void
.end method

.method public a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V
    .locals 10

    .line 1
    invoke-super/range {p0 .. p7}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;->a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicService;->Qa:Lcom/tw/music/view/MusicWidgetProvider;

    invoke-virtual {v0, p0}, Lcom/tw/music/view/MusicWidgetProvider;->a(Lcom/tw/music/MusicService;)V

    iget-object v0, p0, Lcom/tw/music/MusicService;->Sa:Lcom/tw/music/media/MediaControlBridge;

    if-eqz v0, :cond_0

    move-object v2, p1

    move-object v3, p2

    move-object v4, p3

    move/from16 v1, p7

    int-to-long v5, v1

    move-object v7, p4

    move-object v8, p5

    move-object/from16 v9, p6

    invoke-static/range {v2 .. v9}, Lcom/tw/music/media/MediaMetadataMapper;->map(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLandroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/media/MediaMetadataCompat;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/tw/music/media/MediaControlBridge;->publishMetadata(Landroid/support/v4/media/MediaMetadataCompat;)V

    :cond_0

    return-void
.end method

.method public d(II)V
    .locals 9

    .line 1
    invoke-super {p0, p1, p2}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;->d(II)V

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicService;->Qa:Lcom/tw/music/view/MusicWidgetProvider;

    invoke-virtual {v0, p0}, Lcom/tw/music/view/MusicWidgetProvider;->a(Lcom/tw/music/MusicService;)V

    iget-object v0, p0, Lcom/tw/music/MusicService;->Sa:Lcom/tw/music/media/MediaControlBridge;

    if-eqz v0, :cond_0

    iget-boolean v1, p0, Lcom/tw/music/MusicService;->Ta:Z

    const/4 v2, 0x0

    const/4 v3, 0x0

    int-to-long v4, p1

    const/high16 v6, 0x3f800000    # 1.0f

    const/4 v7, 0x1

    invoke-static/range {v1 .. v7}, Lcom/tw/music/media/PlaybackStateMapper;->map(ZZZJFZ)Landroid/support/v4/media/session/PlaybackStateCompat;

    move-result-object v8

    invoke-virtual {v0, v8}, Lcom/tw/music/media/MediaControlBridge;->publishState(Landroid/support/v4/media/session/PlaybackStateCompat;)V

    :cond_0

    return-void
.end method

.method public onBind(Landroid/content/Intent;)Landroid/os/IBinder;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/MusicService;->mBinder:Landroid/os/IBinder;

    return-object p0
.end method

.method public onCreate()V
    .locals 2

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;->onCreate()V

    .line 2
    new-instance v0, Lcom/tw/music/b/a;

    invoke-direct {v0}, Lcom/tw/music/b/a;-><init>()V

    iput-object v0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    .line 3
    invoke-static {}, Lcom/tw/music/view/MusicWidgetProvider;->getInstance()Lcom/tw/music/view/MusicWidgetProvider;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/MusicService;->Qa:Lcom/tw/music/view/MusicWidgetProvider;

    .line 4
    new-instance v0, Landroid/content/IntentFilter;

    invoke-direct {v0}, Landroid/content/IntentFilter;-><init>()V

    const-string v1, "com.tw.music.action.cmd"

    .line 5
    invoke-virtual {v0, v1}, Landroid/content/IntentFilter;->addAction(Ljava/lang/String;)V

    const-string v1, "com.tw.music.action.prev"

    .line 6
    invoke-virtual {v0, v1}, Landroid/content/IntentFilter;->addAction(Ljava/lang/String;)V

    const-string v1, "com.tw.music.action.next"

    .line 7
    invoke-virtual {v0, v1}, Landroid/content/IntentFilter;->addAction(Ljava/lang/String;)V

    const-string v1, "com.tw.music.action.pp"

    .line 8
    invoke-virtual {v0, v1}, Landroid/content/IntentFilter;->addAction(Ljava/lang/String;)V

    .line 9
    iget-object v1, p0, Lcom/tw/music/MusicService;->Ra:Landroid/content/BroadcastReceiver;

    invoke-virtual {p0, v1, v0}, Landroid/app/Service;->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;

    .line 10
    new-instance v0, Lcom/tw/music/media/MediaControlBridge;

    invoke-direct {v0, p0}, Lcom/tw/music/media/MediaControlBridge;-><init>(Landroid/app/Service;)V

    iput-object v0, p0, Lcom/tw/music/MusicService;->Sa:Lcom/tw/music/media/MediaControlBridge;

    return-void
.end method

.method public onDestroy()V
    .locals 1

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/tw/music/MusicService;->Ra:Landroid/content/BroadcastReceiver;

    invoke-virtual {p0, v0}, Landroid/app/Service;->unregisterReceiver(Landroid/content/BroadcastReceiver;)V

    const/4 v0, 0x0

    .line 2
    iput-object v0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    .line 3
    iget-object v0, p0, Lcom/tw/music/MusicService;->Sa:Lcom/tw/music/media/MediaControlBridge;

    if-eqz v0, :cond_0

    invoke-virtual {v0}, Lcom/tw/music/media/MediaControlBridge;->release()V

    const/4 v0, 0x0

    iput-object v0, p0, Lcom/tw/music/MusicService;->Sa:Lcom/tw/music/media/MediaControlBridge;

    :cond_0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 4
    :catch_0
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicService;->onDestroy()V

    return-void
.end method

.method public onStartCommand(Landroid/content/Intent;II)I
    .locals 0

    if-eqz p1, :cond_6

    .line 1
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p2

    const-string p3, "cmd"

    .line 2
    invoke-virtual {p1, p3}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    const-string p3, "prev"

    .line 3
    invoke-virtual {p3, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p3

    if-nez p3, :cond_5

    const-string p3, "com.tw.music.action.prev"

    invoke-virtual {p3, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p3

    if-eqz p3, :cond_0

    goto :goto_1

    :cond_0
    const-string p3, "next"

    .line 4
    invoke-virtual {p3, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p3

    if-nez p3, :cond_4

    const-string p3, "com.tw.music.action.next"

    invoke-virtual {p3, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p3

    if-eqz p3, :cond_1

    goto :goto_0

    :cond_1
    const-string p3, "pp"

    .line 5
    invoke-virtual {p3, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-nez p1, :cond_2

    const-string p1, "com.tw.music.action.pp"

    invoke-virtual {p1, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_6

    .line 6
    :cond_2
    iget-object p1, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1}, Lcom/tw/music/b/a;->isPlaying()Z

    move-result p1

    if-eqz p1, :cond_3

    .line 7
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->ba()V

    goto :goto_2

    .line 8
    :cond_3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->fa()V

    goto :goto_2

    .line 9
    :cond_4
    :goto_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->pb()V

    goto :goto_2

    .line 10
    :cond_5
    :goto_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->rb()V

    :cond_6
    :goto_2
    const/4 p0, 0x1

    return p0
.end method

.method public za()Lcom/eckom/xtlibrary/b/f/e/a;
    .locals 1

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/f/e/a;-><init>(Landroid/content/Context;)V

    return-object v0
.end method

.method public bridge synthetic za()Lcom/eckom/xtlibrary/b/g/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/tw/music/MusicService;->za()Lcom/eckom/xtlibrary/b/f/e/a;

    move-result-object p0

    return-object p0
.end method

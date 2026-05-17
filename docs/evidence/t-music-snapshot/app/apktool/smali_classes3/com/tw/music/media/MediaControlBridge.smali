.class public final Lcom/tw/music/media/MediaControlBridge;
.super Ljava/lang/Object;
.source "MediaControlBridge.java"


# instance fields
.field private final mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

.field private final notificationController:Lcom/tw/music/media/MediaNotificationController;

.field private final service:Landroid/app/Service;


# direct methods
.method static bridge synthetic -$$Nest$mdispatchTwAction(Lcom/tw/music/media/MediaControlBridge;Ljava/lang/String;Ljava/lang/String;)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lcom/tw/music/media/MediaControlBridge;->dispatchTwAction(Ljava/lang/String;Ljava/lang/String;)V

    return-void
.end method

.method public constructor <init>(Landroid/app/Service;)V
    .registers 4

    .line 20
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 21
    iput-object p1, p0, Lcom/tw/music/media/MediaControlBridge;->service:Landroid/app/Service;

    .line 22
    new-instance v0, Landroid/support/v4/media/session/MediaSessionCompat;

    const-string v1, "com.tw.music.MediaControlBridge"

    invoke-direct {v0, p1, v1}, Landroid/support/v4/media/session/MediaSessionCompat;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    iput-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    .line 25
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    const/4 v1, 0x3

    invoke-virtual {v0, v1}, Landroid/support/v4/media/session/MediaSessionCompat;->setFlags(I)V

    .line 28
    new-instance v0, Lcom/tw/music/media/MediaNotificationController;

    invoke-direct {v0, p1}, Lcom/tw/music/media/MediaNotificationController;-><init>(Landroid/app/Service;)V

    iput-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->notificationController:Lcom/tw/music/media/MediaNotificationController;

    .line 29
    iget-object p1, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    new-instance v0, Lcom/tw/music/media/MediaControlBridge$1;

    invoke-direct {v0, p0}, Lcom/tw/music/media/MediaControlBridge$1;-><init>(Lcom/tw/music/media/MediaControlBridge;)V

    invoke-virtual {p1, v0}, Landroid/support/v4/media/session/MediaSessionCompat;->setCallback(Landroid/support/v4/media/session/MediaSessionCompat$Callback;)V

    .line 56
    return-void
.end method

.method private dispatchTwAction(Ljava/lang/String;Ljava/lang/String;)V
    .registers 4

    .line 86
    new-instance v0, Landroid/content/Intent;

    invoke-direct {v0, p1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 87
    const-string p1, "com.tw.music"

    invoke-virtual {v0, p1}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    .line 88
    const-string p1, "cmd"

    invoke-virtual {v0, p1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 89
    iget-object p1, p0, Lcom/tw/music/media/MediaControlBridge;->service:Landroid/app/Service;

    invoke-virtual {p1, v0}, Landroid/app/Service;->sendBroadcast(Landroid/content/Intent;)V

    .line 90
    return-void
.end method


# virtual methods
.method public publishMetadata(Landroid/support/v4/media/MediaMetadataCompat;)V
    .registers 3

    .line 67
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    invoke-virtual {v0, p1}, Landroid/support/v4/media/session/MediaSessionCompat;->setMetadata(Landroid/support/v4/media/MediaMetadataCompat;)V

    .line 68
    return-void
.end method

.method public publishState(Landroid/support/v4/media/session/PlaybackStateCompat;)V
    .registers 3

    .line 63
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    invoke-virtual {v0, p1}, Landroid/support/v4/media/session/MediaSessionCompat;->setPlaybackState(Landroid/support/v4/media/session/PlaybackStateCompat;)V

    .line 64
    return-void
.end method

.method public release()V
    .registers 3

    .line 75
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->notificationController:Lcom/tw/music/media/MediaNotificationController;

    invoke-virtual {v0}, Lcom/tw/music/media/MediaNotificationController;->cancel()V

    .line 76
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/support/v4/media/session/MediaSessionCompat;->setActive(Z)V

    .line 77
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    invoke-virtual {v0}, Landroid/support/v4/media/session/MediaSessionCompat;->release()V

    .line 78
    return-void
.end method

.method public setActive(Z)V
    .registers 3

    .line 59
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    invoke-virtual {v0, p1}, Landroid/support/v4/media/session/MediaSessionCompat;->setActive(Z)V

    .line 60
    return-void
.end method

.method public updateNotification(Z)V
    .registers 4

    .line 71
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge;->notificationController:Lcom/tw/music/media/MediaNotificationController;

    iget-object v1, p0, Lcom/tw/music/media/MediaControlBridge;->mediaSession:Landroid/support/v4/media/session/MediaSessionCompat;

    invoke-virtual {v1}, Landroid/support/v4/media/session/MediaSessionCompat;->getSessionToken()Landroid/support/v4/media/session/MediaSessionCompat$Token;

    move-result-object v1

    invoke-virtual {v0, v1, p1}, Lcom/tw/music/media/MediaNotificationController;->update(Landroid/support/v4/media/session/MediaSessionCompat$Token;Z)V

    .line 72
    return-void
.end method

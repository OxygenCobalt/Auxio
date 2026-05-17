.class public final Lcom/tw/music/media/MediaNotificationController;
.super Ljava/lang/Object;
.source "MediaNotificationController.java"


# static fields
.field static final CHANNEL_ID:Ljava/lang/String; = "com.tw.music.playback"

.field private static final NOTIFICATION_ID:I = 0x49c


# instance fields
.field private final service:Landroid/app/Service;


# direct methods
.method public constructor <init>(Landroid/app/Service;)V
    .registers 2

    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 31
    iput-object p1, p0, Lcom/tw/music/media/MediaNotificationController;->service:Landroid/app/Service;

    .line 32
    return-void
.end method

.method private commandPendingIntent(Ljava/lang/String;Ljava/lang/String;)Landroid/app/PendingIntent;
    .registers 5

    .line 91
    new-instance v0, Landroid/content/Intent;

    invoke-direct {v0, p1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 92
    const-string v1, "com.tw.music"

    invoke-virtual {v0, v1}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    .line 93
    const-string v1, "cmd"

    invoke-virtual {v0, v1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 95
    nop

    .line 96
    iget-object p2, p0, Lcom/tw/music/media/MediaNotificationController;->service:Landroid/app/Service;

    invoke-virtual {p1}, Ljava/lang/String;->hashCode()I

    move-result p1

    const/high16 v1, 0xc000000

    invoke-static {p2, p1, v0, v1}, Landroid/app/PendingIntent;->getBroadcast(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object p1

    return-object p1
.end method

.method private createNotificationChannel()V
    .registers 6

    .line 70
    nop

    .line 71
    iget-object v0, p0, Lcom/tw/music/media/MediaNotificationController;->service:Landroid/app/Service;

    .line 72
    const-string v1, "notification"

    invoke-virtual {v0, v1}, Landroid/app/Service;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/app/NotificationManager;

    .line 73
    if-eqz v0, :cond_1f

    .line 74
    new-instance v1, Landroid/app/NotificationChannel;

    const-string v2, "Music Playback"

    const/4 v3, 0x2

    const-string v4, "com.tw.music.playback"

    invoke-direct {v1, v4, v2, v3}, Landroid/app/NotificationChannel;-><init>(Ljava/lang/String;Ljava/lang/CharSequence;I)V

    .line 78
    const-string v2, "Media playback controls"

    invoke-virtual {v1, v2}, Landroid/app/NotificationChannel;->setDescription(Ljava/lang/String;)V

    .line 79
    invoke-virtual {v0, v1}, Landroid/app/NotificationManager;->createNotificationChannel(Landroid/app/NotificationChannel;)V

    .line 82
    :cond_1f
    return-void
.end method


# virtual methods
.method public cancel()V
    .registers 3

    .line 60
    iget-object v0, p0, Lcom/tw/music/media/MediaNotificationController;->service:Landroid/app/Service;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Landroid/app/Service;->stopForeground(Z)V

    .line 61
    iget-object v0, p0, Lcom/tw/music/media/MediaNotificationController;->service:Landroid/app/Service;

    const-string v1, "notification"

    invoke-virtual {v0, v1}, Landroid/app/Service;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/app/NotificationManager;

    .line 62
    if-eqz v0, :cond_17

    const/16 v1, 0x49c

    invoke-virtual {v0, v1}, Landroid/app/NotificationManager;->cancel(I)V

    .line 63
    :cond_17
    return-void
.end method

.method public update(Landroid/support/v4/media/session/MediaSessionCompat$Token;Z)V
    .registers 9

    .line 35
    invoke-direct {p0}, Lcom/tw/music/media/MediaNotificationController;->createNotificationChannel()V

    .line 36
    new-instance v0, Landroid/support/v4/app/NotificationCompat$Builder;

    iget-object v1, p0, Lcom/tw/music/media/MediaNotificationController;->service:Landroid/app/Service;

    const-string v2, "com.tw.music.playback"

    invoke-direct {v0, v1, v2}, Landroid/support/v4/app/NotificationCompat$Builder;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    .line 37
    const v1, 0x1080024

    invoke-virtual {v0, v1}, Landroid/support/v4/app/NotificationCompat$Builder;->setSmallIcon(I)Landroid/support/v4/app/NotificationCompat$Builder;

    move-result-object v0

    .line 38
    const-string v2, "Music"

    invoke-virtual {v0, v2}, Landroid/support/v4/app/NotificationCompat$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroid/support/v4/app/NotificationCompat$Builder;

    move-result-object v0

    .line 39
    invoke-virtual {v0, p2}, Landroid/support/v4/app/NotificationCompat$Builder;->setOngoing(Z)Landroid/support/v4/app/NotificationCompat$Builder;

    move-result-object v0

    new-instance v2, Landroid/support/v4/app/NotificationCompat$Action;

    .line 43
    const-string v3, "com.tw.music.action.prev"

    const-string v4, "prev"

    invoke-direct {p0, v3, v4}, Lcom/tw/music/media/MediaNotificationController;->commandPendingIntent(Ljava/lang/String;Ljava/lang/String;)Landroid/app/PendingIntent;

    move-result-object v3

    const v4, 0x1080025

    const-string v5, "Prev"

    invoke-direct {v2, v4, v5, v3}, Landroid/support/v4/app/NotificationCompat$Action;-><init>(ILjava/lang/CharSequence;Landroid/app/PendingIntent;)V

    .line 40
    invoke-virtual {v0, v2}, Landroid/support/v4/app/NotificationCompat$Builder;->addAction(Landroid/support/v4/app/NotificationCompat$Action;)Landroid/support/v4/app/NotificationCompat$Builder;

    move-result-object v0

    new-instance v2, Landroid/support/v4/app/NotificationCompat$Action;

    .line 45
    if-eqz p2, :cond_3a

    const v1, 0x1080023

    .line 46
    :cond_3a
    if-eqz p2, :cond_3f

    const-string p2, "Pause"

    goto :goto_41

    :cond_3f
    const-string p2, "Play"

    .line 47
    :goto_41
    const-string v3, "com.tw.music.action.pp"

    const-string v4, "pp"

    invoke-direct {p0, v3, v4}, Lcom/tw/music/media/MediaNotificationController;->commandPendingIntent(Ljava/lang/String;Ljava/lang/String;)Landroid/app/PendingIntent;

    move-result-object v3

    invoke-direct {v2, v1, p2, v3}, Landroid/support/v4/app/NotificationCompat$Action;-><init>(ILjava/lang/CharSequence;Landroid/app/PendingIntent;)V

    .line 44
    invoke-virtual {v0, v2}, Landroid/support/v4/app/NotificationCompat$Builder;->addAction(Landroid/support/v4/app/NotificationCompat$Action;)Landroid/support/v4/app/NotificationCompat$Builder;

    move-result-object p2

    new-instance v0, Landroid/support/v4/app/NotificationCompat$Action;

    .line 51
    const-string v1, "com.tw.music.action.next"

    const-string v2, "next"

    invoke-direct {p0, v1, v2}, Lcom/tw/music/media/MediaNotificationController;->commandPendingIntent(Ljava/lang/String;Ljava/lang/String;)Landroid/app/PendingIntent;

    move-result-object v1

    const v2, 0x1080022

    const-string v3, "Next"

    invoke-direct {v0, v2, v3, v1}, Landroid/support/v4/app/NotificationCompat$Action;-><init>(ILjava/lang/CharSequence;Landroid/app/PendingIntent;)V

    .line 48
    invoke-virtual {p2, v0}, Landroid/support/v4/app/NotificationCompat$Builder;->addAction(Landroid/support/v4/app/NotificationCompat$Action;)Landroid/support/v4/app/NotificationCompat$Builder;

    move-result-object p2

    new-instance v0, Landroid/support/v4/media/app/NotificationCompat$MediaStyle;

    invoke-direct {v0}, Landroid/support/v4/media/app/NotificationCompat$MediaStyle;-><init>()V

    .line 53
    invoke-virtual {v0, p1}, Landroid/support/v4/media/app/NotificationCompat$MediaStyle;->setMediaSession(Landroid/support/v4/media/session/MediaSessionCompat$Token;)Landroid/support/v4/media/app/NotificationCompat$MediaStyle;

    move-result-object p1

    const/4 v0, 0x1

    const/4 v1, 0x2

    const/4 v2, 0x0

    filled-new-array {v2, v0, v1}, [I

    move-result-object v0

    .line 54
    invoke-virtual {p1, v0}, Landroid/support/v4/media/app/NotificationCompat$MediaStyle;->setShowActionsInCompactView([I)Landroid/support/v4/media/app/NotificationCompat$MediaStyle;

    move-result-object p1

    .line 52
    invoke-virtual {p2, p1}, Landroid/support/v4/app/NotificationCompat$Builder;->setStyle(Landroid/support/v4/app/NotificationCompat$Style;)Landroid/support/v4/app/NotificationCompat$Builder;

    move-result-object p1

    .line 55
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompat$Builder;->build()Landroid/app/Notification;

    move-result-object p1

    .line 56
    iget-object p2, p0, Lcom/tw/music/media/MediaNotificationController;->service:Landroid/app/Service;

    const/16 v0, 0x49c

    invoke-virtual {p2, v0, p1}, Landroid/app/Service;->startForeground(ILandroid/app/Notification;)V

    .line 57
    return-void
.end method

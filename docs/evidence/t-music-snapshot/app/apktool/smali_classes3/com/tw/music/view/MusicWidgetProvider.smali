.class public Lcom/tw/music/view/MusicWidgetProvider;
.super Landroid/appwidget/AppWidgetProvider;
.source "MusicWidgetProvider.java"


# static fields
.field private static sInstance:Lcom/tw/music/view/MusicWidgetProvider;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Landroid/appwidget/AppWidgetProvider;-><init>()V

    return-void
.end method

.method private a(Landroid/content/Context;Landroid/widget/RemoteViews;)V
    .locals 3

    .line 36
    new-instance p0, Landroid/content/ComponentName;

    const-class v0, Lcom/tw/music/MusicService;

    invoke-direct {p0, p1, v0}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 37
    new-instance v0, Landroid/content/Intent;

    const-class v1, Lcom/tw/music/MusicActivity;

    invoke-direct {v0, p1, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    const/4 v1, 0x0

    .line 38
    invoke-static {p1, v1, v0, v1}, Landroid/app/PendingIntent;->getActivity(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object v0

    const v2, 0x7f08001b

    .line 39
    invoke-virtual {p2, v2, v0}, Landroid/widget/RemoteViews;->setOnClickPendingIntent(ILandroid/app/PendingIntent;)V

    .line 40
    new-instance v0, Landroid/content/Intent;

    const-string v2, "com.tw.music.action.prev"

    invoke-direct {v0, v2}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 41
    invoke-virtual {v0, p0}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    .line 42
    invoke-static {p1, v1, v0, v1}, Landroid/app/PendingIntent;->getService(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object v0

    const v2, 0x7f08003b

    .line 43
    invoke-virtual {p2, v2, v0}, Landroid/widget/RemoteViews;->setOnClickPendingIntent(ILandroid/app/PendingIntent;)V

    .line 44
    new-instance v0, Landroid/content/Intent;

    const-string v2, "com.tw.music.action.pp"

    invoke-direct {v0, v2}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 45
    invoke-virtual {v0, p0}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    .line 46
    invoke-static {p1, v1, v0, v1}, Landroid/app/PendingIntent;->getService(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object v0

    const v2, 0x7f08003a

    .line 47
    invoke-virtual {p2, v2, v0}, Landroid/widget/RemoteViews;->setOnClickPendingIntent(ILandroid/app/PendingIntent;)V

    .line 48
    new-instance v0, Landroid/content/Intent;

    const-string v2, "com.tw.music.action.next"

    invoke-direct {v0, v2}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 49
    invoke-virtual {v0, p0}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    .line 50
    invoke-static {p1, v1, v0, v1}, Landroid/app/PendingIntent;->getService(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object p0

    const p1, 0x7f080039

    .line 51
    invoke-virtual {p2, p1, p0}, Landroid/widget/RemoteViews;->setOnClickPendingIntent(ILandroid/app/PendingIntent;)V

    return-void
.end method

.method private a(Landroid/content/Context;[I)V
    .locals 3

    .line 55
    new-instance v0, Landroid/widget/RemoteViews;

    invoke-virtual {p1}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v1

    const v2, 0x7f0a0029

    invoke-direct {v0, v1, v2}, Landroid/widget/RemoteViews;-><init>(Ljava/lang/String;I)V

    .line 56
    invoke-direct {p0, p1, v0}, Lcom/tw/music/view/MusicWidgetProvider;->a(Landroid/content/Context;Landroid/widget/RemoteViews;)V

    .line 57
    invoke-direct {p0, p1, p2, v0}, Lcom/tw/music/view/MusicWidgetProvider;->a(Landroid/content/Context;[ILandroid/widget/RemoteViews;)V

    return-void
.end method

.method private a(Landroid/content/Context;[ILandroid/widget/RemoteViews;)V
    .locals 1

    .line 52
    :try_start_0
    invoke-static {p1}, Landroid/appwidget/AppWidgetManager;->getInstance(Landroid/content/Context;)Landroid/appwidget/AppWidgetManager;

    move-result-object v0

    if-eqz p2, :cond_0

    .line 53
    invoke-virtual {v0, p2, p3}, Landroid/appwidget/AppWidgetManager;->updateAppWidget([ILandroid/widget/RemoteViews;)V

    goto :goto_0

    .line 54
    :cond_0
    new-instance p2, Landroid/content/ComponentName;

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object p0

    invoke-direct {p2, p1, p0}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {v0, p2, p3}, Landroid/appwidget/AppWidgetManager;->updateAppWidget(Landroid/content/ComponentName;Landroid/widget/RemoteViews;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    :goto_0
    return-void
.end method

.method private d(Landroid/content/Context;)Z
    .locals 2

    .line 1
    invoke-static {p1}, Landroid/appwidget/AppWidgetManager;->getInstance(Landroid/content/Context;)Landroid/appwidget/AppWidgetManager;

    move-result-object p0

    .line 2
    new-instance v0, Landroid/content/ComponentName;

    .line 3
    const-class v1, Lcom/tw/music/view/MusicWidgetProvider;

    invoke-direct {v0, p1, v1}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 4
    invoke-virtual {p0, v0}, Landroid/appwidget/AppWidgetManager;->getAppWidgetIds(Landroid/content/ComponentName;)[I

    move-result-object p0

    .line 5
    array-length p0, p0

    if-lez p0, :cond_0

    const/4 p0, 0x1

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method

.method public static declared-synchronized getInstance()Lcom/tw/music/view/MusicWidgetProvider;
    .locals 2

    const-class v0, Lcom/tw/music/view/MusicWidgetProvider;

    monitor-enter v0

    .line 1
    :try_start_0
    sget-object v1, Lcom/tw/music/view/MusicWidgetProvider;->sInstance:Lcom/tw/music/view/MusicWidgetProvider;

    if-nez v1, :cond_0

    .line 2
    new-instance v1, Lcom/tw/music/view/MusicWidgetProvider;

    invoke-direct {v1}, Lcom/tw/music/view/MusicWidgetProvider;-><init>()V

    sput-object v1, Lcom/tw/music/view/MusicWidgetProvider;->sInstance:Lcom/tw/music/view/MusicWidgetProvider;

    .line 3
    :cond_0
    sget-object v1, Lcom/tw/music/view/MusicWidgetProvider;->sInstance:Lcom/tw/music/view/MusicWidgetProvider;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    monitor-exit v0

    return-object v1

    :catchall_0
    move-exception v1

    monitor-exit v0

    throw v1
.end method


# virtual methods
.method public a(Lcom/tw/music/MusicService;)V
    .locals 1

    .line 1
    invoke-direct {p0, p1}, Lcom/tw/music/view/MusicWidgetProvider;->d(Landroid/content/Context;)Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 2
    invoke-virtual {p0, p1, v0}, Lcom/tw/music/view/MusicWidgetProvider;->a(Lcom/tw/music/MusicService;[I)V

    :cond_0
    return-void
.end method

.method public a(Lcom/tw/music/MusicService;[I)V
    .locals 16

    move-object/from16 v0, p0

    move-object/from16 v1, p1

    .line 3
    new-instance v2, Landroid/widget/RemoteViews;

    invoke-virtual/range {p1 .. p1}, Landroid/app/Service;->getPackageName()Ljava/lang/String;

    move-result-object v3

    const v4, 0x7f0a0029

    invoke-direct {v2, v3, v4}, Landroid/widget/RemoteViews;-><init>(Ljava/lang/String;I)V

    .line 4
    iget-object v3, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v3}, Lcom/tw/music/b/a;->Nb()Ljava/lang/String;

    move-result-object v3

    .line 5
    iget-object v4, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v4}, Lcom/tw/music/b/a;->jd()Ljava/lang/String;

    move-result-object v4

    const v5, 0x7f0b004c

    if-nez v3, :cond_0

    .line 6
    iget-object v3, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v3}, Lcom/tw/music/b/a;->Nb()Ljava/lang/String;

    move-result-object v3

    if-nez v3, :cond_0

    .line 7
    invoke-virtual {v1, v5}, Landroid/app/Service;->getString(I)Ljava/lang/String;

    :cond_0
    const v6, 0x7f0800d1

    .line 8
    invoke-virtual {v2, v6, v3}, Landroid/widget/RemoteViews;->setTextViewText(ILjava/lang/CharSequence;)V

    if-nez v4, :cond_1

    .line 9
    invoke-virtual {v1, v5}, Landroid/app/Service;->getString(I)Ljava/lang/String;

    :cond_1
    const v3, 0x7f08001f

    .line 10
    invoke-virtual {v2, v3, v4}, Landroid/widget/RemoteViews;->setTextViewText(ILjava/lang/CharSequence;)V

    .line 11
    iget-object v3, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v3}, Lcom/tw/music/b/a;->getCurrentPosition()I

    move-result v3

    .line 12
    iget-object v4, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v4}, Lcom/tw/music/b/a;->getDuration()I

    move-result v4

    const/4 v5, 0x0

    if-gez v3, :cond_2

    move v3, v5

    :cond_2
    if-gtz v4, :cond_3

    move v4, v5

    .line 13
    :cond_3
    div-int/lit16 v3, v3, 0x3e8

    .line 14
    div-int/lit8 v6, v3, 0x3c

    .line 15
    div-int/lit8 v7, v6, 0x3c

    .line 16
    rem-int/lit8 v8, v3, 0x3c

    .line 17
    rem-int/lit8 v6, v6, 0x3c

    .line 18
    rem-int/lit8 v7, v7, 0x18

    const-string v9, "%d:%02d"

    const/4 v10, 0x3

    const-string v11, "%d:%02d:%02d"

    const v12, 0x7f0800d7

    const/4 v13, 0x2

    const/4 v14, 0x1

    if-nez v7, :cond_4

    .line 19
    sget-object v7, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v15, v13, [Ljava/lang/Object;

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v15, v5

    invoke-static {v8}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v15, v14

    invoke-static {v7, v9, v15}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v2, v12, v6}, Landroid/widget/RemoteViews;->setTextViewText(ILjava/lang/CharSequence;)V

    goto :goto_0

    .line 20
    :cond_4
    sget-object v15, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v12, v10, [Ljava/lang/Object;

    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v12, v5

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v12, v14

    invoke-static {v8}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v12, v13

    invoke-static {v15, v11, v12}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    const v7, 0x7f0800d7

    invoke-virtual {v2, v7, v6}, Landroid/widget/RemoteViews;->setTextViewText(ILjava/lang/CharSequence;)V

    .line 21
    :goto_0
    div-int/lit16 v4, v4, 0x3e8

    .line 22
    div-int/lit8 v6, v4, 0x3c

    .line 23
    div-int/lit8 v7, v6, 0x3c

    .line 24
    rem-int/lit8 v8, v4, 0x3c

    .line 25
    rem-int/lit8 v6, v6, 0x3c

    .line 26
    rem-int/lit8 v7, v7, 0x18

    const v12, 0x7f0800d8

    if-nez v7, :cond_5

    .line 27
    sget-object v7, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v10, v13, [Ljava/lang/Object;

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v10, v5

    invoke-static {v8}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v10, v14

    invoke-static {v7, v9, v10}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v2, v12, v6}, Landroid/widget/RemoteViews;->setTextViewText(ILjava/lang/CharSequence;)V

    goto :goto_1

    .line 28
    :cond_5
    sget-object v9, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v10, v10, [Ljava/lang/Object;

    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v10, v5

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v10, v14

    invoke-static {v8}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v10, v13

    invoke-static {v9, v11, v10}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v2, v12, v6}, Landroid/widget/RemoteViews;->setTextViewText(ILjava/lang/CharSequence;)V

    :goto_1
    const v6, 0x7f0800b4

    .line 29
    invoke-virtual {v2, v6, v4, v3, v5}, Landroid/widget/RemoteViews;->setProgressBar(IIIZ)V

    .line 30
    iget-object v3, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v3}, Lcom/tw/music/b/a;->ed()Landroid/graphics/Bitmap;

    move-result-object v3

    const v4, 0x7f08001b

    if-eqz v3, :cond_7

    .line 31
    invoke-virtual {v3}, Landroid/graphics/Bitmap;->getByteCount()I

    move-result v5

    const v6, 0x382700

    if-le v5, v6, :cond_6

    goto :goto_2

    .line 32
    :cond_6
    invoke-virtual {v2, v4, v3}, Landroid/widget/RemoteViews;->setImageViewBitmap(ILandroid/graphics/Bitmap;)V

    goto :goto_3

    :cond_7
    :goto_2
    const v3, 0x7f070054

    .line 33
    invoke-virtual {v2, v4, v3}, Landroid/widget/RemoteViews;->setImageViewResource(II)V

    .line 34
    :goto_3
    invoke-direct {v0, v1, v2}, Lcom/tw/music/view/MusicWidgetProvider;->a(Landroid/content/Context;Landroid/widget/RemoteViews;)V

    move-object/from16 v3, p2

    .line 35
    invoke-direct {v0, v1, v3, v2}, Lcom/tw/music/view/MusicWidgetProvider;->a(Landroid/content/Context;[ILandroid/widget/RemoteViews;)V

    return-void
.end method

.method public onUpdate(Landroid/content/Context;Landroid/appwidget/AppWidgetManager;[I)V
    .locals 1

    .line 1
    invoke-direct {p0, p1, p3}, Lcom/tw/music/view/MusicWidgetProvider;->a(Landroid/content/Context;[I)V

    .line 2
    new-instance p0, Landroid/content/Intent;

    const-class p2, Lcom/tw/music/MusicService;

    invoke-direct {p0, p1, p2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {p1, p0}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;

    .line 3
    new-instance p0, Landroid/content/Intent;

    const-string p2, "com.tw.music.action.cmd"

    invoke-direct {p0, p2}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string p2, "cmd"

    const-string v0, "update"

    .line 4
    invoke-virtual {p0, p2, v0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string p2, "appWidgetIds"

    .line 5
    invoke-virtual {p0, p2, p3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[I)Landroid/content/Intent;

    const/high16 p2, 0x40000000    # 2.0f

    .line 6
    invoke-virtual {p0, p2}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    .line 7
    invoke-virtual {p1, p0}, Landroid/content/Context;->sendStickyBroadcast(Landroid/content/Intent;)V

    return-void
.end method

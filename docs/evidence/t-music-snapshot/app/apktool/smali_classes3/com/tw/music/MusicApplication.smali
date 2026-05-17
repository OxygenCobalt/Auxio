.class public Lcom/tw/music/MusicApplication;
.super Landroid/app/Application;
.source "MusicApplication.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Landroid/app/Application;-><init>()V

    return-void
.end method


# virtual methods
.method protected attachBaseContext(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Landroid/app/Application;->attachBaseContext(Landroid/content/Context;)V

    return-void
.end method

.method public onCreate()V
    .locals 1

    .line 1
    invoke-super {p0}, Landroid/app/Application;->onCreate()V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v0

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/i/k;->init(Landroid/content/Context;)V

    return-void
.end method

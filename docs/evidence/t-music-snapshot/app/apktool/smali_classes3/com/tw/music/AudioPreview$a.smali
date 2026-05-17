.class Lcom/tw/music/AudioPreview$a;
.super Landroid/media/MediaPlayer;
.source "AudioPreview.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnPreparedListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/AudioPreview;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "a"
.end annotation


# instance fields
.field Rc:Z

.field mActivity:Lcom/tw/music/AudioPreview;


# direct methods
.method private constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Landroid/media/MediaPlayer;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/tw/music/AudioPreview$a;->Rc:Z

    return-void
.end method

.method synthetic constructor <init>(Lcom/tw/music/a;)V
    .locals 0

    .line 3
    invoke-direct {p0}, Lcom/tw/music/AudioPreview$a;-><init>()V

    return-void
.end method


# virtual methods
.method Qa()Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/tw/music/AudioPreview$a;->Rc:Z

    return p0
.end method

.method public a(Landroid/net/Uri;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/tw/music/AudioPreview$a;->mActivity:Lcom/tw/music/AudioPreview;

    invoke-virtual {p0, v0, p1}, Landroid/media/MediaPlayer;->setDataSource(Landroid/content/Context;Landroid/net/Uri;)V

    .line 2
    invoke-virtual {p0}, Landroid/media/MediaPlayer;->prepareAsync()V

    return-void
.end method

.method public l(Lcom/tw/music/AudioPreview;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/AudioPreview$a;->mActivity:Lcom/tw/music/AudioPreview;

    .line 2
    invoke-virtual {p0, p0}, Landroid/media/MediaPlayer;->setOnPreparedListener(Landroid/media/MediaPlayer$OnPreparedListener;)V

    .line 3
    iget-object p1, p0, Lcom/tw/music/AudioPreview$a;->mActivity:Lcom/tw/music/AudioPreview;

    invoke-virtual {p0, p1}, Landroid/media/MediaPlayer;->setOnErrorListener(Landroid/media/MediaPlayer$OnErrorListener;)V

    .line 4
    iget-object p1, p0, Lcom/tw/music/AudioPreview$a;->mActivity:Lcom/tw/music/AudioPreview;

    invoke-virtual {p0, p1}, Landroid/media/MediaPlayer;->setOnCompletionListener(Landroid/media/MediaPlayer$OnCompletionListener;)V

    return-void
.end method

.method public onPrepared(Landroid/media/MediaPlayer;)V
    .locals 1

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/tw/music/AudioPreview$a;->Rc:Z

    .line 2
    iget-object p0, p0, Lcom/tw/music/AudioPreview$a;->mActivity:Lcom/tw/music/AudioPreview;

    invoke-virtual {p0, p1}, Lcom/tw/music/AudioPreview;->onPrepared(Landroid/media/MediaPlayer;)V

    return-void
.end method

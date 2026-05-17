.class public Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;
.super Landroid/view/SurfaceView;
.source "MediaView.java"


# instance fields
.field private TAG:Ljava/lang/String;

.field private Td:I

.field private Ud:Z

.field private Vd:Z

.field private Wd:Z

.field private mBufferingUpdateListener:Landroid/media/MediaPlayer$OnBufferingUpdateListener;

.field private mCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

.field private mCurrentBufferPercentage:I

.field private mCurrentState:I

.field private mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

.field private mInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

.field private mMediaPlayer:Landroid/media/MediaPlayer;

.field private mOnCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

.field private mOnErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

.field private mOnInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

.field private mOnPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

.field private mPath:Ljava/lang/String;

.field mPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

.field mSHCallback:Landroid/view/SurfaceHolder$Callback;

.field private mSeekWhenPrepared:I

.field mSizeChangedListener:Landroid/media/MediaPlayer$OnVideoSizeChangedListener;

.field private mSurfaceHeight:I

.field private mSurfaceHolder:Landroid/view/SurfaceHolder;

.field private mSurfaceWidth:I

.field private mTargetState:I

.field private mVideoHeight:I

.field private mVideoWidth:I


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Landroid/view/SurfaceView;-><init>(Landroid/content/Context;)V

    const-string p1, "MediaView"

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->TAG:Ljava/lang/String;

    const/4 p1, 0x0

    .line 3
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 4
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    const/4 p1, 0x0

    .line 5
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHolder:Landroid/view/SurfaceHolder;

    .line 6
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 7
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/c;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/c;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSizeChangedListener:Landroid/media/MediaPlayer$OnVideoSizeChangedListener;

    .line 8
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/d;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/d;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

    .line 9
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/e;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/e;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

    .line 10
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/f;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/f;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

    .line 11
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/g;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/g;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    .line 12
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/h;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/h;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mBufferingUpdateListener:Landroid/media/MediaPlayer$OnBufferingUpdateListener;

    .line 13
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/i;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/i;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSHCallback:Landroid/view/SurfaceHolder$Callback;

    .line 14
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Ae()V

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 0

    .line 15
    invoke-direct {p0, p1, p2}, Landroid/view/SurfaceView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    const-string p1, "MediaView"

    .line 16
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->TAG:Ljava/lang/String;

    const/4 p1, 0x0

    .line 17
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 18
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    const/4 p1, 0x0

    .line 19
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHolder:Landroid/view/SurfaceHolder;

    .line 20
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 21
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/c;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/c;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSizeChangedListener:Landroid/media/MediaPlayer$OnVideoSizeChangedListener;

    .line 22
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/d;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/d;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

    .line 23
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/e;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/e;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

    .line 24
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/f;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/f;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

    .line 25
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/g;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/g;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    .line 26
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/h;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/h;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mBufferingUpdateListener:Landroid/media/MediaPlayer$OnBufferingUpdateListener;

    .line 27
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/i;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/i;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSHCallback:Landroid/view/SurfaceHolder$Callback;

    .line 28
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Ae()V

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 0

    .line 29
    invoke-direct {p0, p1, p2, p3}, Landroid/view/SurfaceView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    const-string p1, "MediaView"

    .line 30
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->TAG:Ljava/lang/String;

    const/4 p1, 0x0

    .line 31
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 32
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    const/4 p1, 0x0

    .line 33
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHolder:Landroid/view/SurfaceHolder;

    .line 34
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 35
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/c;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/c;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSizeChangedListener:Landroid/media/MediaPlayer$OnVideoSizeChangedListener;

    .line 36
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/d;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/d;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

    .line 37
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/e;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/e;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

    .line 38
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/f;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/f;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

    .line 39
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/g;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/g;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    .line 40
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/h;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/h;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mBufferingUpdateListener:Landroid/media/MediaPlayer$OnBufferingUpdateListener;

    .line 41
    new-instance p1, Lcom/eckom/xtlibrary/twproject/video/utils/i;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/video/utils/i;-><init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSHCallback:Landroid/view/SurfaceHolder$Callback;

    .line 42
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Ae()V

    return-void
.end method

.method private Ae()V
    .locals 3

    const/4 v0, 0x0

    .line 1
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mVideoWidth:I

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mVideoHeight:I

    .line 3
    invoke-virtual {p0}, Landroid/view/SurfaceView;->getHolder()Landroid/view/SurfaceHolder;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSHCallback:Landroid/view/SurfaceHolder$Callback;

    invoke-interface {v1, v2}, Landroid/view/SurfaceHolder;->addCallback(Landroid/view/SurfaceHolder$Callback;)V

    .line 4
    invoke-virtual {p0}, Landroid/view/SurfaceView;->getHolder()Landroid/view/SurfaceHolder;

    move-result-object v1

    const/4 v2, 0x3

    invoke-interface {v1, v2}, Landroid/view/SurfaceHolder;->setType(I)V

    const/4 v1, 0x1

    .line 5
    invoke-virtual {p0, v1}, Landroid/view/SurfaceView;->setFocusable(Z)V

    .line 6
    invoke-virtual {p0, v1}, Landroid/view/SurfaceView;->setFocusableInTouchMode(Z)V

    .line 7
    invoke-virtual {p0}, Landroid/view/SurfaceView;->requestFocus()Z

    .line 8
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 9
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    return-void
.end method

.method private Be()V
    .locals 7

    const-string v0, "Unable to open content: "

    .line 1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPath:Ljava/lang/String;

    if-eqz v1, :cond_2

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHolder:Landroid/view/SurfaceHolder;

    if-eqz v2, :cond_2

    new-instance v2, Ljava/io/File;

    invoke-direct {v2, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2}, Ljava/io/File;->exists()Z

    move-result v1

    if-nez v1, :cond_0

    goto/16 :goto_2

    :cond_0
    const/4 v1, 0x0

    .line 2
    invoke-direct {p0, v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->release(Z)V

    const/4 v2, 0x1

    const/4 v3, -0x1

    .line 3
    :try_start_0
    new-instance v4, Landroid/media/MediaPlayer;

    invoke-direct {v4}, Landroid/media/MediaPlayer;-><init>()V

    iput-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    .line 4
    iget v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Td:I

    if-eqz v4, :cond_1

    .line 5
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Td:I

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setAudioSessionId(I)V

    goto :goto_0

    .line 6
    :cond_1
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->getAudioSessionId()I

    move-result v4

    iput v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Td:I

    .line 7
    :goto_0
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setOnPreparedListener(Landroid/media/MediaPlayer$OnPreparedListener;)V

    .line 8
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSizeChangedListener:Landroid/media/MediaPlayer$OnVideoSizeChangedListener;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setOnVideoSizeChangedListener(Landroid/media/MediaPlayer$OnVideoSizeChangedListener;)V

    .line 9
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setOnCompletionListener(Landroid/media/MediaPlayer$OnCompletionListener;)V

    .line 10
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setOnErrorListener(Landroid/media/MediaPlayer$OnErrorListener;)V

    .line 11
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setOnInfoListener(Landroid/media/MediaPlayer$OnInfoListener;)V

    .line 12
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mBufferingUpdateListener:Landroid/media/MediaPlayer$OnBufferingUpdateListener;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setOnBufferingUpdateListener(Landroid/media/MediaPlayer$OnBufferingUpdateListener;)V

    .line 13
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentBufferPercentage:I

    .line 14
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPath:Ljava/lang/String;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setDataSource(Ljava/lang/String;)V

    .line 15
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHolder:Landroid/view/SurfaceHolder;

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setDisplay(Landroid/view/SurfaceHolder;)V

    .line 16
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    const/4 v5, 0x3

    invoke-virtual {v4, v5}, Landroid/media/MediaPlayer;->setAudioStreamType(I)V

    .line 17
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v4, v2}, Landroid/media/MediaPlayer;->setScreenOnWhilePlaying(Z)V

    .line 18
    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->prepareAsync()V

    .line 19
    iput v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_2
    .catch Ljava/lang/IllegalArgumentException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    :catch_0
    move-exception v4

    .line 20
    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->TAG:Ljava/lang/String;

    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPath:Ljava/lang/String;

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v5, v0, v4}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 21
    iput v3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 22
    iput v3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    .line 23
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-interface {v0, p0, v2, v1}, Landroid/media/MediaPlayer$OnErrorListener;->onError(Landroid/media/MediaPlayer;II)Z

    :goto_1
    return-void

    :catch_1
    move-exception v4

    .line 24
    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->TAG:Ljava/lang/String;

    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPath:Ljava/lang/String;

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v5, v0, v4}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 25
    iput v3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 26
    iput v3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    .line 27
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-interface {v0, p0, v2, v1}, Landroid/media/MediaPlayer$OnErrorListener;->onError(Landroid/media/MediaPlayer;II)Z

    return-void

    :catch_2
    move-exception v4

    .line 28
    iget-object v5, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->TAG:Ljava/lang/String;

    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPath:Ljava/lang/String;

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v5, v0, v4}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 29
    iput v3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 30
    iput v3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    .line 31
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-interface {v0, p0, v2, v1}, Landroid/media/MediaPlayer$OnErrorListener;->onError(Landroid/media/MediaPlayer;II)Z

    :cond_2
    :goto_2
    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mVideoWidth:I

    return p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mVideoWidth:I

    return p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Landroid/view/SurfaceHolder;)Landroid/view/SurfaceHolder;
    .locals 0

    .line 3
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHolder:Landroid/view/SurfaceHolder;

    return-object p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)V
    .locals 0

    .line 4
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->release(Z)V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mVideoHeight:I

    return p0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHeight:I

    return p1
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Ud:Z

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceHeight:I

    return p0
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mVideoHeight:I

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Vd:Z

    return p1
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    return p0
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    return p1
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Wd:Z

    return p1
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentBufferPercentage:I

    return p1
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnCompletionListener;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

    return-object p0
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    return p1
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnInfoListener;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

    return-object p0
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceWidth:I

    return p1
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->TAG:Ljava/lang/String;

    return-object p0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnErrorListener;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    return-object p0
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Be()V

    return-void
.end method

.method private isInPlaybackState()Z
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    const/4 v1, 0x1

    if-eqz v0, :cond_0

    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    const/4 v0, -0x1

    if-eq p0, v0, :cond_0

    if-eqz p0, :cond_0

    if-eq p0, v1, :cond_0

    goto :goto_0

    :cond_0
    const/4 v1, 0x0

    :goto_0
    return v1
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnPreparedListener;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

    return-object p0
.end method

.method static synthetic k(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    return-object p0
.end method

.method static synthetic l(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSeekWhenPrepared:I

    return p0
.end method

.method static synthetic m(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSurfaceWidth:I

    return p0
.end method

.method private release(Z)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {v0}, Landroid/media/MediaPlayer;->reset()V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    const/4 v0, 0x0

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    const/4 v0, 0x0

    .line 5
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    if-eqz p1, :cond_0

    .line 6
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    :cond_0
    return-void
.end method


# virtual methods
.method public getAudioSessionId()I
    .locals 2

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Td:I

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Landroid/media/MediaPlayer;

    invoke-direct {v0}, Landroid/media/MediaPlayer;-><init>()V

    .line 3
    invoke-virtual {v0}, Landroid/media/MediaPlayer;->getAudioSessionId()I

    move-result v1

    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Td:I

    .line 4
    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    .line 5
    :cond_0
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Td:I

    return p0
.end method

.method public getBufferPercentage()I
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    if-eqz v0, :cond_0

    .line 2
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentBufferPercentage:I

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method public getCurrentPosition()I
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isInPlaybackState()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result p0

    return p0

    .line 3
    :cond_0
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSeekWhenPrepared:I

    return p0
.end method

.method public getDuration()I
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isInPlaybackState()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->getDuration()I

    move-result p0

    return p0

    :cond_0
    const/4 p0, -0x1

    return p0
.end method

.method public isPlaying()Z
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isInPlaybackState()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {p0}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result p0

    if-eqz p0, :cond_0

    const/4 p0, 0x1

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method

.method public pause()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isInPlaybackState()Z

    move-result v0

    const/4 v1, 0x4

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->pause()V

    .line 4
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 5
    :cond_0
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    return-void
.end method

.method public seekTo(I)V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isInPlaybackState()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v0, p1}, Landroid/media/MediaPlayer;->seekTo(I)V

    const/4 p1, 0x0

    .line 3
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSeekWhenPrepared:I

    goto :goto_0

    .line 4
    :cond_0
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSeekWhenPrepared:I

    :goto_0
    return-void
.end method

.method public setOnCompletionListener(Landroid/media/MediaPlayer$OnCompletionListener;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnCompletionListener:Landroid/media/MediaPlayer$OnCompletionListener;

    return-void
.end method

.method public setOnErrorListener(Landroid/media/MediaPlayer$OnErrorListener;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnErrorListener:Landroid/media/MediaPlayer$OnErrorListener;

    return-void
.end method

.method public setOnInfoListener(Landroid/media/MediaPlayer$OnInfoListener;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnInfoListener:Landroid/media/MediaPlayer$OnInfoListener;

    return-void
.end method

.method public setOnPreparedListener(Landroid/media/MediaPlayer$OnPreparedListener;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mOnPreparedListener:Landroid/media/MediaPlayer$OnPreparedListener;

    return-void
.end method

.method public setVideoPath(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mPath:Ljava/lang/String;

    const/4 p1, 0x0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mSeekWhenPrepared:I

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->Be()V

    .line 4
    invoke-virtual {p0}, Landroid/view/SurfaceView;->requestLayout()V

    .line 5
    invoke-virtual {p0}, Landroid/view/SurfaceView;->invalidate()V

    return-void
.end method

.method public setVolume(FF)V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isInPlaybackState()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {p0, p1, p2}, Landroid/media/MediaPlayer;->setVolume(FF)V

    :cond_0
    return-void
.end method

.method public start()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isInPlaybackState()Z

    move-result v0

    const/4 v1, 0x3

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->start()V

    .line 3
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 4
    :cond_0
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    return-void
.end method

.method public stopPlayback()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {v0}, Landroid/media/MediaPlayer;->stop()V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    const/4 v0, 0x0

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mMediaPlayer:Landroid/media/MediaPlayer;

    const/4 v0, 0x0

    .line 5
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mCurrentState:I

    .line 6
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->mTargetState:I

    :cond_0
    return-void
.end method

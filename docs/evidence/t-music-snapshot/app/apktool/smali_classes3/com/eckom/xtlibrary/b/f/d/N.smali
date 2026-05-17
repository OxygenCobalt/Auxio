.class Lcom/eckom/xtlibrary/b/f/d/N;
.super Ljava/lang/Object;
.source "MusicIjkModel.java"

# interfaces
.implements Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/N;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onError(Ltv/danmaku/ijk/media/player/IMediaPlayer;II)Z
    .locals 1

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/N;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/U;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v0, p1, p2, p3}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->noError(Ltv/danmaku/ijk/media/player/IMediaPlayer;II)Z

    move-result p1

    if-eqz p1, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/N;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->Va()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    :cond_0
    const/4 p0, 0x1

    return p0
.end method

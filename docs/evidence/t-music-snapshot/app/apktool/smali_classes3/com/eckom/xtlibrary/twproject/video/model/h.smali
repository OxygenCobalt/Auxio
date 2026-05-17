.class Lcom/eckom/xtlibrary/twproject/video/model/h;
.super Ljava/lang/Object;
.source "VideoIjkModel.java"

# interfaces
.implements Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/m;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onError(Ltv/danmaku/ijk/media/player/IMediaPlayer;II)Z
    .locals 4

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object p1

    invoke-interface {p1}, Lcom/eckom/xtlibrary/b/k/c/b;->Y()V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->access$200()Lcom/eckom/xtlibrary/b/k/c/b;

    move-result-object p1

    const/4 p3, 0x0

    invoke-interface {p1, p3}, Lcom/eckom/xtlibrary/b/k/c/b;->c(Z)V

    const/4 p1, 0x1

    if-ne p2, p1, :cond_0

    .line 3
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ic()V

    goto :goto_0

    .line 4
    :cond_0
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {p2}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->stopPlayback()V

    .line 5
    :goto_0
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object p2

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object v0

    array-length v0, v0

    sub-int/2addr v0, p1

    aget-wide v0, p2, v0

    const-wide/16 v2, 0x0

    cmp-long p2, v0, v2

    if-lez p2, :cond_2

    .line 6
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v0

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object p2

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object v2

    array-length v2, v2

    sub-int/2addr v2, p1

    aget-wide v2, p2, v2

    sub-long/2addr v0, v2

    const-wide/16 v2, 0x2bc

    cmp-long p2, v0, v2

    if-gtz p2, :cond_1

    .line 7
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object p2

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object v0

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object v1

    array-length v1, v1

    sub-int/2addr v1, p1

    invoke-static {p2, p1, v0, p3, v1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 8
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object p2

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object v0

    array-length v0, v0

    sub-int/2addr v0, p1

    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v1

    aput-wide v1, p2, v0

    .line 9
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v0

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object p2

    aget-wide p2, p2, p3

    sub-long/2addr v0, p2

    const-wide/16 p2, 0x1388

    cmp-long p2, v0, p2

    if-gtz p2, :cond_3

    .line 10
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->n(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result p3

    new-array p3, p3, [J

    invoke-static {p2, p3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;[J)[J

    .line 11
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2, p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->d(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z

    .line 12
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ic()V

    goto :goto_1

    .line 13
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->n(Lcom/eckom/xtlibrary/twproject/video/model/m;)I

    move-result p2

    new-array p2, p2, [J

    invoke-static {p0, p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->a(Lcom/eckom/xtlibrary/twproject/video/model/m;[J)[J

    goto :goto_1

    .line 14
    :cond_2
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object p2

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J

    move-result-object p0

    array-length p0, p0

    sub-int/2addr p0, p1

    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v0

    aput-wide v0, p2, p0

    :cond_3
    :goto_1
    return p1
.end method

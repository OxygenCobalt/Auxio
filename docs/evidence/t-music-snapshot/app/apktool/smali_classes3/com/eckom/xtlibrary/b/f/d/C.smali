.class Lcom/eckom/xtlibrary/b/f/d/C;
.super Ljava/lang/Object;
.source "MusicIjkID3Model.java"

# interfaces
.implements Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/d/L;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/L;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/L;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onError(Ltv/danmaku/ijk/media/player/IMediaPlayer;II)Z
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/L;->Lb()Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    .line 2
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/L;->Lb()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/e;->Ed:Ljava/util/ArrayList;

    invoke-static {v0, v1, v2}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result v0

    if-nez v0, :cond_0

    const/16 v0, -0x3f2

    if-ne p2, v0, :cond_0

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->Ed:Ljava/util/ArrayList;

    new-instance v2, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/L;->getFileName()Ljava/lang/String;

    move-result-object v0

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {v3}, Lcom/eckom/xtlibrary/b/f/d/L;->Lb()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v0, v3}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 4
    :cond_0
    :try_start_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    invoke-virtual {v0, p1, p2, p3}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->noError(Ltv/danmaku/ijk/media/player/IMediaPlayer;II)Z

    move-result p1

    if-eqz p1, :cond_1

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/C;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    :cond_1
    const/4 p0, 0x1

    return p0
.end method

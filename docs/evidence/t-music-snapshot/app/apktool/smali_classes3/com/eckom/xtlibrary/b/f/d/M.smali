.class Lcom/eckom/xtlibrary/b/f/d/M;
.super Ljava/lang/Object;
.source "MusicIjkModel.java"

# interfaces
.implements Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/M;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onCompletion(Ltv/danmaku/ijk/media/player/IMediaPlayer;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/M;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->pb()V

    return-void
.end method

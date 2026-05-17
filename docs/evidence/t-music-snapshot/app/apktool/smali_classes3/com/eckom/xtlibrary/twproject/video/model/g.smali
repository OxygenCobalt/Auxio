.class Lcom/eckom/xtlibrary/twproject/video/model/g;
.super Ljava/lang/Object;
.source "VideoIjkModel.java"

# interfaces
.implements Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onCompletion(Ltv/danmaku/ijk/media/player/IMediaPlayer;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ic()V

    return-void
.end method

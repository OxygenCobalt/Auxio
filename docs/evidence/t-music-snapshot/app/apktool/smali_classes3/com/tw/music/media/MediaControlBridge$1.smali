.class Lcom/tw/music/media/MediaControlBridge$1;
.super Landroid/support/v4/media/session/MediaSessionCompat$Callback;
.source "MediaControlBridge.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/media/MediaControlBridge;-><init>(Landroid/app/Service;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/media/MediaControlBridge;


# direct methods
.method constructor <init>(Lcom/tw/music/media/MediaControlBridge;)V
    .registers 2
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010
        }
        names = {
            null
        }
    .end annotation

    .line 29
    iput-object p1, p0, Lcom/tw/music/media/MediaControlBridge$1;->this$0:Lcom/tw/music/media/MediaControlBridge;

    invoke-direct {p0}, Landroid/support/v4/media/session/MediaSessionCompat$Callback;-><init>()V

    return-void
.end method


# virtual methods
.method public onPause()V
    .registers 4

    .line 52
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge$1;->this$0:Lcom/tw/music/media/MediaControlBridge;

    const-string v1, "com.tw.music.action.pp"

    const-string v2, "pp"

    invoke-static {v0, v1, v2}, Lcom/tw/music/media/MediaControlBridge;->-$$Nest$mdispatchTwAction(Lcom/tw/music/media/MediaControlBridge;Ljava/lang/String;Ljava/lang/String;)V

    .line 53
    return-void
.end method

.method public onPlay()V
    .registers 4

    .line 46
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge$1;->this$0:Lcom/tw/music/media/MediaControlBridge;

    const-string v1, "com.tw.music.action.pp"

    const-string v2, "pp"

    invoke-static {v0, v1, v2}, Lcom/tw/music/media/MediaControlBridge;->-$$Nest$mdispatchTwAction(Lcom/tw/music/media/MediaControlBridge;Ljava/lang/String;Ljava/lang/String;)V

    .line 47
    return-void
.end method

.method public onSkipToNext()V
    .registers 4

    .line 37
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge$1;->this$0:Lcom/tw/music/media/MediaControlBridge;

    const-string v1, "com.tw.music.action.next"

    const-string v2, "next"

    invoke-static {v0, v1, v2}, Lcom/tw/music/media/MediaControlBridge;->-$$Nest$mdispatchTwAction(Lcom/tw/music/media/MediaControlBridge;Ljava/lang/String;Ljava/lang/String;)V

    .line 38
    return-void
.end method

.method public onSkipToPrevious()V
    .registers 4

    .line 32
    iget-object v0, p0, Lcom/tw/music/media/MediaControlBridge$1;->this$0:Lcom/tw/music/media/MediaControlBridge;

    const-string v1, "com.tw.music.action.prev"

    const-string v2, "prev"

    invoke-static {v0, v1, v2}, Lcom/tw/music/media/MediaControlBridge;->-$$Nest$mdispatchTwAction(Lcom/tw/music/media/MediaControlBridge;Ljava/lang/String;Ljava/lang/String;)V

    .line 33
    return-void
.end method

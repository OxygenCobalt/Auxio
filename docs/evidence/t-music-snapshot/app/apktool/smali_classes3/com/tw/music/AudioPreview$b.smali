.class Lcom/tw/music/AudioPreview$b;
.super Ljava/lang/Object;
.source "AudioPreview.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/AudioPreview;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = "b"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/AudioPreview;


# direct methods
.method constructor <init>(Lcom/tw/music/AudioPreview;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->k(Lcom/tw/music/AudioPreview;)Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->a(Lcom/tw/music/AudioPreview;)I

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object v0

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result v0

    iget-object v1, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v1}, Lcom/tw/music/AudioPreview;->a(Lcom/tw/music/AudioPreview;)I

    move-result v1

    div-int/2addr v0, v1

    .line 3
    iget-object v0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->b(Lcom/tw/music/AudioPreview;)Landroid/widget/SeekBar;

    move-result-object v0

    iget-object v1, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v1}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object v1

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->getCurrentPosition()I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/widget/SeekBar;->setProgress(I)V

    .line 4
    :cond_0
    iget-object v0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->c(Lcom/tw/music/AudioPreview;)Landroid/os/Handler;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 5
    iget-object v0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->c(Lcom/tw/music/AudioPreview;)Landroid/os/Handler;

    move-result-object v0

    new-instance v1, Lcom/tw/music/AudioPreview$b;

    iget-object p0, p0, Lcom/tw/music/AudioPreview$b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-direct {v1, p0}, Lcom/tw/music/AudioPreview$b;-><init>(Lcom/tw/music/AudioPreview;)V

    const-wide/16 v2, 0xc8

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    return-void
.end method

.class Lcom/tw/music/c;
.super Ljava/lang/Object;
.source "AudioPreview.java"

# interfaces
.implements Landroid/widget/SeekBar$OnSeekBarChangeListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/AudioPreview;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/AudioPreview;


# direct methods
.method constructor <init>(Lcom/tw/music/AudioPreview;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c;->this$0:Lcom/tw/music/AudioPreview;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onProgressChanged(Landroid/widget/SeekBar;IZ)V
    .locals 0

    if-nez p3, :cond_0

    return-void

    .line 1
    :cond_0
    iget-object p0, p0, Lcom/tw/music/c;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p0}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object p0

    invoke-virtual {p0, p2}, Landroid/media/MediaPlayer;->seekTo(I)V

    return-void
.end method

.method public onStartTrackingTouch(Landroid/widget/SeekBar;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c;->this$0:Lcom/tw/music/AudioPreview;

    const/4 p1, 0x1

    invoke-static {p0, p1}, Lcom/tw/music/AudioPreview;->b(Lcom/tw/music/AudioPreview;Z)Z

    return-void
.end method

.method public onStopTrackingTouch(Landroid/widget/SeekBar;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c;->this$0:Lcom/tw/music/AudioPreview;

    const/4 p1, 0x0

    invoke-static {p0, p1}, Lcom/tw/music/AudioPreview;->b(Lcom/tw/music/AudioPreview;Z)Z

    return-void
.end method

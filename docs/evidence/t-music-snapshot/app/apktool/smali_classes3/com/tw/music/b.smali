.class Lcom/tw/music/b;
.super Ljava/lang/Object;
.source "AudioPreview.java"

# interfaces
.implements Landroid/media/AudioManager$OnAudioFocusChangeListener;


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
    iput-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onAudioFocusChange(I)V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {v0}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object v0

    if-nez v0, :cond_0

    .line 2
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1}, Lcom/tw/music/AudioPreview;->g(Lcom/tw/music/AudioPreview;)Landroid/media/AudioManager;

    move-result-object p1

    invoke-virtual {p1, p0}, Landroid/media/AudioManager;->abandonAudioFocus(Landroid/media/AudioManager$OnAudioFocusChangeListener;)I

    return-void

    :cond_0
    const/4 v0, -0x3

    const/4 v1, 0x1

    if-eq p1, v0, :cond_3

    const/4 v0, -0x2

    if-eq p1, v0, :cond_3

    const/4 v0, -0x1

    const/4 v2, 0x0

    if-eq p1, v0, :cond_2

    if-eq p1, v1, :cond_1

    goto :goto_0

    .line 3
    :cond_1
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1}, Lcom/tw/music/AudioPreview;->h(Lcom/tw/music/AudioPreview;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 4
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1, v2}, Lcom/tw/music/AudioPreview;->a(Lcom/tw/music/AudioPreview;Z)Z

    .line 5
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1}, Lcom/tw/music/AudioPreview;->i(Lcom/tw/music/AudioPreview;)V

    goto :goto_0

    .line 6
    :cond_2
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1, v2}, Lcom/tw/music/AudioPreview;->a(Lcom/tw/music/AudioPreview;Z)Z

    .line 7
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object p1

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->pause()V

    goto :goto_0

    .line 8
    :cond_3
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object p1

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result p1

    if-eqz p1, :cond_4

    .line 9
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1, v1}, Lcom/tw/music/AudioPreview;->a(Lcom/tw/music/AudioPreview;Z)Z

    .line 10
    iget-object p1, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p1}, Lcom/tw/music/AudioPreview;->f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;

    move-result-object p1

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->pause()V

    .line 11
    :cond_4
    :goto_0
    iget-object p0, p0, Lcom/tw/music/b;->this$0:Lcom/tw/music/AudioPreview;

    invoke-static {p0}, Lcom/tw/music/AudioPreview;->j(Lcom/tw/music/AudioPreview;)V

    return-void
.end method

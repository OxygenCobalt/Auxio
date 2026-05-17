.class Lcom/eckom/xtlibrary/twproject/video/utils/d;
.super Ljava/lang/Object;
.source "MediaView.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnPreparedListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onPrepared(Landroid/media/MediaPlayer;)V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    const/4 v1, 0x2

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->f(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    const/4 v1, 0x1

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)Z

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->c(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)Z

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)Z

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->j(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnPreparedListener;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->j(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnPreparedListener;

    move-result-object v0

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->k(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer;

    move-result-object v1

    invoke-interface {v0, v1}, Landroid/media/MediaPlayer$OnPreparedListener;->onPrepared(Landroid/media/MediaPlayer;)V

    .line 5
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->getVideoWidth()I

    move-result v1

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->getVideoHeight()I

    move-result p1

    invoke-static {v0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->c(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->l(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p1

    if-eqz p1, :cond_1

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->seekTo(I)V

    .line 9
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v0

    const/4 v1, 0x3

    if-eqz v0, :cond_3

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v0

    if-eqz v0, :cond_3

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {v0}, Landroid/view/SurfaceView;->getHolder()Landroid/view/SurfaceHolder;

    move-result-object v0

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v2

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v3}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v3

    invoke-interface {v0, v2, v3}, Landroid/view/SurfaceHolder;->setFixedSize(II)V

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->m(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v0

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v2

    if-ne v0, v2, :cond_4

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->c(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v0

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v2

    if-ne v0, v2, :cond_4

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v0

    if-ne v0, v1, :cond_2

    .line 13
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->start()V

    goto :goto_0

    .line 14
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->isPlaying()Z

    move-result v0

    if-nez v0, :cond_4

    if-nez p1, :cond_4

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    .line 15
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->getCurrentPosition()I

    move-result p0

    goto :goto_0

    .line 16
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p1

    if-ne p1, v1, :cond_4

    .line 17
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/d;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->start()V

    :cond_4
    :goto_0
    return-void
.end method

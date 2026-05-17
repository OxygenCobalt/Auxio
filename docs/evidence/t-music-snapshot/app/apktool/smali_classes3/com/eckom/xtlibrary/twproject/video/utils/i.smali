.class Lcom/eckom/xtlibrary/twproject/video/utils/i;
.super Ljava/lang/Object;
.source "MediaView.java"

# interfaces
.implements Landroid/view/SurfaceHolder$Callback;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public surfaceChanged(Landroid/view/SurfaceHolder;III)V
    .locals 2

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1, p3}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->g(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1, p4}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p1

    const/4 p2, 0x1

    const/4 v0, 0x0

    const/4 v1, 0x3

    if-ne p1, v1, :cond_0

    move p1, p2

    goto :goto_0

    :cond_0
    move p1, v0

    .line 4
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result v1

    if-ne v1, p3, :cond_1

    iget-object p3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p3}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p3

    if-ne p3, p4, :cond_1

    goto :goto_1

    :cond_1
    move p2, v0

    .line 5
    :goto_1
    iget-object p3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p3}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->k(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer;

    move-result-object p3

    if-eqz p3, :cond_3

    if-eqz p1, :cond_3

    if-eqz p2, :cond_3

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->l(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p1

    if-eqz p1, :cond_2

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->l(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p2

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->seekTo(I)V

    .line 8
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->start()V

    :cond_3
    return-void
.end method

.method public surfaceCreated(Landroid/view/SurfaceHolder;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {v0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Landroid/view/SurfaceHolder;)Landroid/view/SurfaceHolder;

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->i(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)V

    return-void
.end method

.method public surfaceDestroyed(Landroid/view/SurfaceHolder;)V
    .locals 1

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    const/4 v0, 0x0

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Landroid/view/SurfaceHolder;)Landroid/view/SurfaceHolder;

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/i;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    const/4 p1, 0x1

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;Z)V

    return-void
.end method

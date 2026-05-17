.class Lcom/eckom/xtlibrary/twproject/video/utils/c;
.super Ljava/lang/Object;
.source "MediaView.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnVideoSizeChangedListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onVideoSizeChanged(Landroid/media/MediaPlayer;II)V
    .locals 0

    .line 1
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->getVideoWidth()I

    move-result p3

    invoke-static {p2, p3}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 2
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->getVideoHeight()I

    move-result p1

    invoke-static {p2, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->c(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p1

    if-eqz p1, :cond_0

    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p1

    if-eqz p1, :cond_0

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p1}, Landroid/view/SurfaceView;->getHolder()Landroid/view/SurfaceHolder;

    move-result-object p1

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->a(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p2

    iget-object p3, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p3}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->b(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)I

    move-result p3

    invoke-interface {p1, p2, p3}, Landroid/view/SurfaceHolder;->setFixedSize(II)V

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/c;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-virtual {p0}, Landroid/view/SurfaceView;->requestLayout()V

    :cond_0
    return-void
.end method

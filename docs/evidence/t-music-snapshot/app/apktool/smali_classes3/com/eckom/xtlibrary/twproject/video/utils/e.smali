.class Lcom/eckom/xtlibrary/twproject/video/utils/e;
.super Ljava/lang/Object;
.source "MediaView.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnCompletionListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onCompletion(Landroid/media/MediaPlayer;)V
    .locals 1

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    const/4 v0, 0x5

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->f(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->e(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnCompletionListener;

    move-result-object p1

    if-eqz p1, :cond_0

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->e(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnCompletionListener;

    move-result-object p1

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->k(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-interface {p1, p0}, Landroid/media/MediaPlayer$OnCompletionListener;->onCompletion(Landroid/media/MediaPlayer;)V

    :cond_0
    return-void
.end method

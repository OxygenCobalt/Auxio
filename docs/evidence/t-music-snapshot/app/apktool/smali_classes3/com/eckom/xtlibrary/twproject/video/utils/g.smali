.class Lcom/eckom/xtlibrary/twproject/video/utils/g;
.super Ljava/lang/Object;
.source "MediaView.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnErrorListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onError(Landroid/media/MediaPlayer;II)Z
    .locals 2

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->g(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Ljava/lang/String;

    move-result-object p1

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "Error: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v1, ","

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {p1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    const/4 v0, -0x1

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->f(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->d(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->h(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnErrorListener;

    move-result-object p1

    const/4 v0, 0x1

    if-eqz p1, :cond_0

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->h(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer$OnErrorListener;

    move-result-object p1

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/g;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->k(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;)Landroid/media/MediaPlayer;

    move-result-object p0

    invoke-interface {p1, p0, p2, p3}, Landroid/media/MediaPlayer$OnErrorListener;->onError(Landroid/media/MediaPlayer;II)Z

    move-result p0

    if-eqz p0, :cond_0

    :cond_0
    return v0
.end method

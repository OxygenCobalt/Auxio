.class Lcom/eckom/xtlibrary/twproject/video/utils/h;
.super Ljava/lang/Object;
.source "MediaView.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnBufferingUpdateListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onBufferingUpdate(Landroid/media/MediaPlayer;I)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/h;->this$0:Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;

    invoke-static {p0, p2}, Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;->e(Lcom/eckom/xtlibrary/twproject/video/utils/MediaView;I)I

    return-void
.end method

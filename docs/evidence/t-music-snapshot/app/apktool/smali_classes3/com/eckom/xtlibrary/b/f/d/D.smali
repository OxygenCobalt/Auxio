.class Lcom/eckom/xtlibrary/b/f/d/D;
.super Ljava/lang/Object;
.source "MusicIjkID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/F;->handleMessage(Landroid/os/Message;)Z
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Lcom/eckom/xtlibrary/b/f/d/F;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/F;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public S()V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result p1

    if-nez p1, :cond_1

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/L;->Vb()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object p1

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/f/t;->getService()I

    move-result p1

    const/4 p2, 0x3

    if-ne p1, p2, :cond_1

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz p1, :cond_0

    new-instance p2, Ljava/io/File;

    invoke-direct {p2, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p2}, Ljava/io/File;->canRead()Z

    move-result p1

    if-eqz p1, :cond_0

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->l(Lcom/eckom/xtlibrary/b/f/d/L;)Z

    move-result p1

    if-nez p1, :cond_0

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/d/L;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-virtual {p2, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayer;->setMPPath(Ljava/lang/String;)V

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/d/L;->seekTo(I)V

    .line 9
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/L;->Va()V

    .line 10
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    const/4 p2, 0x0

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/d/L;->d(Lcom/eckom/xtlibrary/b/f/d/L;Z)V

    .line 11
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/D;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

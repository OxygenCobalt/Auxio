.class Lcom/eckom/xtlibrary/b/f/d/aa;
.super Ljava/lang/Object;
.source "MusicModel.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnErrorListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/ba;->reset()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/ba;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onError(Landroid/media/MediaPlayer;II)Z
    .locals 2

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/ba;->access$400()Ljava/util/ArrayList;

    move-result-object p1

    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_0
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result p3

    if-eqz p3, :cond_0

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p3

    check-cast p3, Lcom/eckom/xtlibrary/b/f/g/a;

    const/4 v0, 0x0

    .line 2
    invoke-interface {p3, v0}, Lcom/eckom/xtlibrary/b/f/g/a;->c(Z)V

    goto :goto_0

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    const/4 p3, 0x1

    invoke-static {p1, p3}, Lcom/eckom/xtlibrary/b/f/d/ba;->f(Lcom/eckom/xtlibrary/b/f/d/ba;Z)Z

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->Xb()Ljava/lang/String;

    move-result-object p1

    if-eqz p1, :cond_1

    const/4 p1, 0x0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    .line 5
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->Xb()Ljava/lang/String;

    move-result-object v0

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/ba;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/s;->Ed:Ljava/util/ArrayList;

    invoke-static {p1, v0, v1}, Lcom/eckom/xtlibrary/b/f/f/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result p1

    if-nez p1, :cond_1

    const/16 p1, -0x3f2

    if-ne p2, p1, :cond_1

    .line 6
    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/ba;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    sget-object p1, Lcom/eckom/xtlibrary/b/f/f/s;->Ed:Ljava/util/ArrayList;

    new-instance p2, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->getFileName()Ljava/lang/String;

    move-result-object v0

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/f/d/ba;->Xb()Ljava/lang/String;

    move-result-object v1

    invoke-direct {p2, v0, v1}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {p1, p2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 7
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(Lcom/eckom/xtlibrary/b/f/d/ba;)Landroid/os/Handler;

    move-result-object p1

    const p2, 0xff10

    invoke-virtual {p1, p2}, Landroid/os/Handler;->removeMessages(I)V

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/aa;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(Lcom/eckom/xtlibrary/b/f/d/ba;)Landroid/os/Handler;

    move-result-object p0

    const-wide/16 v0, 0x3e8

    invoke-virtual {p0, p2, v0, v1}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    return p3
.end method

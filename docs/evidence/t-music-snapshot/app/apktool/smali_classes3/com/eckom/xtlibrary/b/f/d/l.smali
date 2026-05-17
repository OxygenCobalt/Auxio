.class Lcom/eckom/xtlibrary/b/f/d/l;
.super Ljava/lang/Object;
.source "MusicID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/n;->handleMessage(Landroid/os/Message;)Z
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Lcom/eckom/xtlibrary/b/f/d/n;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/n;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

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
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, p2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->ea(I)V

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result p1

    if-nez p1, :cond_1

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/t;->access$1000()Lcom/eckom/xtlibrary/b/f/f/t;

    move-result-object p1

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/f/t;->getService()I

    move-result p1

    const/4 p2, 0x3

    if-ne p1, p2, :cond_1

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    if-eqz p1, :cond_0

    new-instance p2, Ljava/io/File;

    invoke-direct {p2, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p2}, Ljava/io/File;->canRead()Z

    move-result p1

    if-eqz p1, :cond_0

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result p1

    if-nez p1, :cond_0

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)I

    move-result p1

    if-nez p1, :cond_0

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->l(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result p1

    if-nez p1, :cond_0

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->md:I

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/d/t;->seekTo(I)V

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Va()V

    .line 9
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/l;->this$1:Lcom/eckom/xtlibrary/b/f/d/n;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/n;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    const/4 p1, 0x0

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;Z)V

    :cond_1
    return-void
.end method

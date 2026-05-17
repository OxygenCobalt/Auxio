.class Lcom/eckom/xtlibrary/b/f/d/E;
.super Ljava/lang/Thread;
.source "MusicIjkID3Model.java"


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/E;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    invoke-direct {p0}, Ljava/lang/Thread;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    invoke-super {p0}, Ljava/lang/Thread;->run()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/E;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;Ljava/lang/String;)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/E;->this$1:Lcom/eckom/xtlibrary/b/f/d/F;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/F;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/L;->e(Lcom/eckom/xtlibrary/b/f/d/L;)V

    return-void
.end method

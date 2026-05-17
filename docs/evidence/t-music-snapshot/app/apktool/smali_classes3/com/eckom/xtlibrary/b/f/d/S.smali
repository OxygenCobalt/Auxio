.class Lcom/eckom/xtlibrary/b/f/d/S;
.super Ljava/lang/Object;
.source "MusicIjkModel.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/U;->Wb()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/U;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/U;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/S;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/j/c;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/j/c;-><init>()V

    const-string v1, "/mnt/sdcard"

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/b/j/c;->jb(Ljava/lang/String;)V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/S;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object v0

    const v1, 0xff05

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/S;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;)Landroid/os/Handler;

    move-result-object p0

    invoke-virtual {p0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

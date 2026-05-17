.class Lcom/eckom/xtlibrary/b/a/d/a;
.super Ljava/lang/Object;
.source "BTModel.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/a/d/c;->handleMessage(Landroid/os/Message;)Z
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Lcom/eckom/xtlibrary/b/a/d/c;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/a/d/c;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/a;->this$1:Lcom/eckom/xtlibrary/b/a/d/c;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/a;->this$1:Lcom/eckom/xtlibrary/b/a/d/c;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    const/4 v1, 0x1

    invoke-static {p0, v1, v0}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;ILjava/lang/String;)V

    return-void
.end method

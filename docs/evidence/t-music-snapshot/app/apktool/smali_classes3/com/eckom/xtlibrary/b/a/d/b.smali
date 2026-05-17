.class Lcom/eckom/xtlibrary/b/a/d/b;
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/b;->this$1:Lcom/eckom/xtlibrary/b/a/d/c;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/b;->this$1:Lcom/eckom/xtlibrary/b/a/d/c;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v0, 0x2

    const/4 v1, 0x0

    invoke-static {p0, v0, v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;ILjava/lang/String;)V

    return-void
.end method

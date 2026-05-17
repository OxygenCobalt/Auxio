.class Lcom/eckom/xtlibrary/b/f/d/Q;
.super Ljava/lang/Object;
.source "MusicIjkModel.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/U;->Ab(Ljava/lang/String;)V
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/Q;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/Q;->this$0:Lcom/eckom/xtlibrary/b/f/d/U;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/U;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    const/4 v1, 0x2

    invoke-static {p0, v1, v0}, Lcom/eckom/xtlibrary/b/f/d/U;->a(Lcom/eckom/xtlibrary/b/f/d/U;ILjava/lang/String;)V

    return-void
.end method

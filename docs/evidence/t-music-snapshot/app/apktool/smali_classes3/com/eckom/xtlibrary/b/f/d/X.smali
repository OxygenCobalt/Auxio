.class Lcom/eckom/xtlibrary/b/f/d/X;
.super Ljava/lang/Object;
.source "MusicModel.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/ba;->Ab(Ljava/lang/String;)V
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/X;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/X;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-static {}, Lcom/eckom/xtlibrary/b/f/d/ba;->access$200()Lcom/eckom/xtlibrary/b/f/f/s;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/f/s;->pd:Ljava/lang/String;

    const/4 v1, 0x2

    invoke-static {p0, v1, v0}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(Lcom/eckom/xtlibrary/b/f/d/ba;ILjava/lang/String;)V

    return-void
.end method

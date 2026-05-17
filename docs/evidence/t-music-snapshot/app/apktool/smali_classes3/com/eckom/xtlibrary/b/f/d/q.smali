.class Lcom/eckom/xtlibrary/b/f/d/q;
.super Ljava/lang/Object;
.source "MusicID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/t;->la(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/t;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/t;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/q;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public S()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/q;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->i(Lcom/eckom/xtlibrary/b/f/d/t;)Ljava/lang/String;

    move-result-object p0

    iput-object p0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/q;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/q;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

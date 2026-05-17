.class Lcom/eckom/xtlibrary/b/f/d/A;
.super Ljava/lang/Object;
.source "MusicIjkID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/L;->We()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/L;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/L;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/A;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public S()V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 2

    .line 1
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/A;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v0, p2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object v1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-nez v1, :cond_0

    .line 3
    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 4
    iget p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/A;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_0
    return-void
.end method

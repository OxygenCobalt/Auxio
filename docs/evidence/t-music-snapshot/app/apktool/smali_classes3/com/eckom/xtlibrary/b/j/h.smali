.class Lcom/eckom/xtlibrary/b/j/h;
.super Ljava/lang/Object;
.source "MediaScanMedia.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/j/m;->c(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/e;ZLcom/eckom/xtlibrary/b/f/f/h$f;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic hm:Lcom/eckom/xtlibrary/b/f/b/e;

.field final synthetic im:Lcom/eckom/xtlibrary/b/f/f/h$f;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/j/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/j/m;Lcom/eckom/xtlibrary/b/f/b/e;Lcom/eckom/xtlibrary/b/f/f/h$f;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/j/h;->this$0:Lcom/eckom/xtlibrary/b/j/m;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/j/h;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p3, p0, Lcom/eckom/xtlibrary/b/j/h;->im:Lcom/eckom/xtlibrary/b/f/f/h$f;

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
    iget v0, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/j/h;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/j/h;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    const/4 v0, 0x1

    if-le p1, v0, :cond_1

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/j/h;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    iget v1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->yd:I

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 5
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/j/h;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_2

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/j/h;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->wj:Ljava/util/ArrayList;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/e;->uj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 7
    :cond_2
    :goto_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/j/h;->im:Lcom/eckom/xtlibrary/b/f/f/h$f;

    invoke-interface {p0, p2}, Lcom/eckom/xtlibrary/b/f/f/h$f;->ia(Ljava/lang/String;)V

    return-void
.end method

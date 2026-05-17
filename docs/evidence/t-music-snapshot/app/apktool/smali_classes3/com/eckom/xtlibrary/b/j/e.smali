.class Lcom/eckom/xtlibrary/b/j/e;
.super Ljava/lang/Object;
.source "MediaScanMedia.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/j/m;->b(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/e;ZLcom/eckom/xtlibrary/b/f/f/h$f;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic hm:Lcom/eckom/xtlibrary/b/f/b/e;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/j/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/j/m;Lcom/eckom/xtlibrary/b/f/b/e;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/j/e;->this$0:Lcom/eckom/xtlibrary/b/j/m;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/j/e;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public S()V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 0

    .line 1
    iget p2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p2, :cond_0

    .line 2
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/j/e;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {p2, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/j/e;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    const/4 p2, 0x1

    if-le p1, p2, :cond_1

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/j/e;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    iget p2, p0, Lcom/eckom/xtlibrary/b/f/b/e;->xd:I

    invoke-virtual {p1, p2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    goto :goto_0

    .line 5
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/j/e;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    move-result p1

    if-lez p1, :cond_2

    .line 6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/j/e;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Jj:Ljava/util/ArrayList;

    const/4 p2, 0x0

    invoke-virtual {p1, p2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/g;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Gj:Lcom/eckom/xtlibrary/b/f/b/g;

    :cond_2
    :goto_0
    return-void
.end method

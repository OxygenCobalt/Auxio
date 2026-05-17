.class Lcom/eckom/xtlibrary/b/f/d/w;
.super Ljava/lang/Object;
.source "MusicIjkID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/L;->Cb(Ljava/lang/String;)V
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/w;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public S()V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 4

    .line 1
    iget p2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p2, :cond_0

    .line 2
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/w;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    .line 3
    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    array-length v0, p2

    const/4 v1, 0x0

    :goto_0
    if-ge v1, v0, :cond_0

    aget-object v2, p2, v1

    .line 4
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/w;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v3, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 5
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/w;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 p2, 0x4

    if-ne p1, p2, :cond_1

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    :cond_1
    return-void
.end method

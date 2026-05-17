.class Lcom/eckom/xtlibrary/b/f/d/K;
.super Ljava/lang/Object;
.source "MusicIjkID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/L;->Bb(Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic sk:Ljava/lang/String;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/L;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/L;Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/K;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/K;->sk:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public S()V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 6

    .line 1
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/K;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->e(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 2
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/K;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iget-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    const/4 v0, 0x0

    iput v0, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    .line 4
    iput-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/K;->sk:Ljava/lang/String;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v1, "/"

    invoke-virtual {p2, v1}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result p2

    add-int/lit8 p2, p2, 0x1

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/f/d/K;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/e;->_j:Ljava/lang/String;

    const-string v2, "."

    invoke-virtual {v1, v2}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v1

    invoke-virtual {p1, p2, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object p1

    move p2, v0

    move v1, p2

    .line 6
    :goto_0
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/f/d/K;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object v3, v2, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v4, v3, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v5, v4, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    if-ge p2, v5, :cond_1

    .line 7
    iget-object v2, v4, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v2, v2, p2

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    move v1, p2

    :cond_0
    add-int/lit8 p2, p2, 0x1

    goto :goto_0

    .line 8
    :cond_1
    iput v1, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Ad:I

    .line 9
    invoke-virtual {v2, v1}, Lcom/eckom/xtlibrary/b/f/d/L;->ea(I)V

    .line 10
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/K;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    invoke-static {p1, v0, v0}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;IZ)V

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/K;->this$0:Lcom/eckom/xtlibrary/b/f/d/L;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/L;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/L;->a(Lcom/eckom/xtlibrary/b/f/d/L;Lcom/eckom/xtlibrary/b/f/b/g;)V

    return-void
.end method

.class Lcom/eckom/xtlibrary/b/f/d/h;
.super Ljava/lang/Object;
.source "MusicID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/t;->We()V
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

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
    iget p2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mLength:I

    if-lez p2, :cond_0

    .line 2
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

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
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-virtual {v3, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 5
    :cond_0
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 6
    iget-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v0, 0x4

    if-ne p1, v0, :cond_1

    .line 7
    iget-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p1, p2}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Ab()V

    .line 9
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    const-string p2, "/data/tw/.like"

    invoke-virtual {p1, p2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result p1

    if-eqz p1, :cond_2

    .line 10
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->Fd:Lcom/eckom/xtlibrary/b/f/b/g;

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/f/b/g;->c(Lcom/eckom/xtlibrary/b/f/b/g;)V

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->a(Lcom/eckom/xtlibrary/b/f/d/t;)V

    goto :goto_1

    .line 12
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    const-string p2, "/data/tw/"

    invoke-virtual {p1, p2}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 13
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result p1

    new-instance v1, Lcom/eckom/xtlibrary/b/f/d/b;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/f/d/b;-><init>(Lcom/eckom/xtlibrary/b/f/d/h;)V

    invoke-static {v0, p2, p1, v1}, Lcom/eckom/xtlibrary/b/f/f/h;->b(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    goto :goto_1

    .line 14
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->h(Lcom/eckom/xtlibrary/b/f/d/t;)Landroid/content/Context;

    move-result-object v0

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/h;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p2, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v1, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Dd:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, p2, Lcom/eckom/xtlibrary/b/f/b/e;->ck:Ljava/lang/String;

    iget-object v3, p2, Lcom/eckom/xtlibrary/b/f/b/e;->Tc:Ljava/util/ArrayList;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->b(Lcom/eckom/xtlibrary/b/f/d/t;)Z

    move-result v4

    new-instance v5, Lcom/eckom/xtlibrary/b/f/d/c;

    invoke-direct {v5, p0}, Lcom/eckom/xtlibrary/b/f/d/c;-><init>(Lcom/eckom/xtlibrary/b/f/d/h;)V

    invoke-static/range {v0 .. v5}, Lcom/eckom/xtlibrary/b/f/f/h;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;Ljava/util/ArrayList;ZLcom/eckom/xtlibrary/b/f/f/h$a;)V

    :goto_1
    return-void
.end method

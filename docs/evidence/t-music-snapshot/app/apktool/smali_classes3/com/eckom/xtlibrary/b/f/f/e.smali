.class Lcom/eckom/xtlibrary/b/f/f/e;
.super Ljava/lang/Object;
.source "MusicUtils.java"

# interfaces
.implements Ljava/util/Comparator;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/f/h;->a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/util/List;Z)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljava/util/Comparator<",
        "Lcom/eckom/xtlibrary/b/f/b/f;",
        ">;"
    }
.end annotation


# instance fields
.field final synthetic Bk:Z


# direct methods
.method constructor <init>(Z)V
    .locals 0

    .line 1
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/f/f/e;->Bk:Z

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/f/b/f;Lcom/eckom/xtlibrary/b/f/b/f;)I
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/f/e;->Bk:Z

    if-eqz p0, :cond_0

    .line 2
    iget-object p0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iget-object p1, p2, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    invoke-virtual {p0, p1}, Ljava/lang/String;->compareTo(Ljava/lang/String;)I

    move-result p0

    return p0

    .line 3
    :cond_0
    iget-object p0, p2, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    invoke-virtual {p0, p1}, Ljava/lang/String;->compareTo(Ljava/lang/String;)I

    move-result p0

    return p0
.end method

.method public bridge synthetic compare(Ljava/lang/Object;Ljava/lang/Object;)I
    .locals 0

    .line 1
    check-cast p1, Lcom/eckom/xtlibrary/b/f/b/f;

    check-cast p2, Lcom/eckom/xtlibrary/b/f/b/f;

    invoke-virtual {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/f/e;->a(Lcom/eckom/xtlibrary/b/f/b/f;Lcom/eckom/xtlibrary/b/f/b/f;)I

    move-result p0

    return p0
.end method

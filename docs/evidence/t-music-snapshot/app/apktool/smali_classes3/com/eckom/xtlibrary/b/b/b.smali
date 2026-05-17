.class public Lcom/eckom/xtlibrary/b/b/b;
.super Lc/b/a/a/a/e$a;
.source "ITWCommandCallbackImp.java"


# instance fields
.field private ed:Lcom/eckom/xtlibrary/b/d/b/a;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/g/a;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Lc/b/a/a/a/e$a;-><init>()V

    .line 2
    instance-of v0, p1, Lcom/eckom/xtlibrary/b/d/b/a;

    if-eqz v0, :cond_0

    .line 3
    check-cast p1, Lcom/eckom/xtlibrary/b/d/b/a;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/b/b;->ed:Lcom/eckom/xtlibrary/b/d/b/a;

    :cond_0
    return-void
.end method


# virtual methods
.method public O(I)V
    .locals 0

    return-void
.end method

.method public R(I)V
    .locals 0

    return-void
.end method

.method public V(I)V
    .locals 0

    return-void
.end method

.method public X(I)V
    .locals 0

    return-void
.end method

.method public Z(I)V
    .locals 0

    return-void
.end method

.method public b(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/b;->ed:Lcom/eckom/xtlibrary/b/d/b/a;

    if-eqz p0, :cond_0

    .line 2
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/d/b/a;->b(Landroid/os/Bundle;)V

    :cond_0
    return-void
.end method

.method public c(ILjava/lang/String;Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public k(I)V
    .locals 0

    return-void
.end method

.class public Lcom/eckom/xtlibrary/b/b/d;
.super Lc/b/a/a/a/c$a;
.source "RadioCallBackImp.java"


# instance fields
.field private final gd:Lc/b/a/a/a/d;

.field private hd:Lcom/eckom/xtlibrary/b/h/c/a;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/g/a;Lc/b/a/a/a/d;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Lc/b/a/a/a/c$a;-><init>()V

    .line 2
    instance-of v0, p1, Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz v0, :cond_0

    .line 3
    check-cast p1, Lcom/eckom/xtlibrary/b/h/c/a;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    .line 4
    :cond_0
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/b/d;->gd:Lc/b/a/a/a/d;

    return-void
.end method


# virtual methods
.method public K()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/c;->K()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->hc()V

    :cond_1
    :goto_0
    return-void
.end method

.method public R()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/c;->R()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->dc()V

    :cond_1
    :goto_0
    return-void
.end method

.method public U()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/c;->U()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->ec()V

    :cond_1
    :goto_0
    return-void
.end method

.method public a(Landroid/os/Bundle;)V
    .locals 4

    const-string v0, "action"

    .line 1
    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz v1, :cond_6

    .line 3
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v1, :cond_0

    .line 4
    invoke-interface {v1, p1}, Lcom/eckom/xtlibrary/b/c/c;->a(Landroid/os/Bundle;)V

    goto :goto_1

    :cond_0
    const/4 p1, -0x1

    .line 5
    invoke-virtual {v0}, Ljava/lang/String;->hashCode()I

    move-result v1

    const v2, 0x71b5b4e0

    const/4 v3, 0x1

    if-eq v1, v2, :cond_2

    const v2, 0x7c9ee290

    if-eq v1, v2, :cond_1

    goto :goto_0

    :cond_1
    const-string v1, "nextChannel"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_3

    const/4 p1, 0x0

    goto :goto_0

    :cond_2
    const-string v1, "preChannel"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_3

    move p1, v3

    :cond_3
    :goto_0
    if-eqz p1, :cond_5

    if-eq p1, v3, :cond_4

    goto :goto_1

    .line 6
    :cond_4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->ac()V

    goto :goto_1

    .line 7
    :cond_5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->next()V

    :cond_6
    :goto_1
    return-void
.end method

.method public aa(Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0, p1}, Lcom/eckom/xtlibrary/b/c/c;->aa(Ljava/lang/String;)V

    goto :goto_0

    .line 4
    :cond_0
    :try_start_0
    invoke-static {p1}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result p1

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/c/a;->na(I)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 5
    invoke-virtual {p0}, Ljava/lang/Exception;->printStackTrace()V

    :cond_1
    :goto_0
    return-void
.end method

.method public ka()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/c;->ka()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->cc()V

    :cond_1
    :goto_0
    return-void
.end method

.method public o(I)V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0, p1}, Lcom/eckom/xtlibrary/b/c/c;->o(I)V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/c/a;->ma(I)V

    :cond_1
    :goto_0
    return-void
.end method

.method public oa()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/d;->hd:Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/c/a;->Hk:Lcom/eckom/xtlibrary/b/c/c;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/c;->oa()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/c/a;->fc()V

    :cond_1
    :goto_0
    return-void
.end method

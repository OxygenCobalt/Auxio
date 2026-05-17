.class public Lcom/eckom/xtlibrary/b/b/a;
.super Lc/b/a/a/a/a$a;
.source "BTCallBackImp.java"


# instance fields
.field private cd:Lc/b/a/a/a/d;

.field private dd:Lcom/eckom/xtlibrary/b/a/e/a;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/g/a;Lc/b/a/a/a/d;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lc/b/a/a/a/a$a;-><init>()V

    .line 2
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/b/a;->cd:Lc/b/a/a/a/d;

    .line 3
    instance-of p2, p1, Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p2, :cond_0

    .line 4
    check-cast p1, Lcom/eckom/xtlibrary/b/a/e/a;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    :cond_0
    return-void
.end method


# virtual methods
.method public H()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->H()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->answer()V

    :cond_1
    :goto_0
    return-void
.end method

.method public Z()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->Z()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->qb()V

    :cond_1
    :goto_0
    return-void
.end method

.method public a(Landroid/os/Bundle;)V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "extendedInterface:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "action"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "BTCallBackImp"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_0

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz p0, :cond_0

    .line 4
    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/c/a;->a(Landroid/os/Bundle;)V

    :cond_0
    return-void
.end method

.method public aa()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->aa()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->pb()V

    :cond_1
    :goto_0
    return-void
.end method

.method public ca()V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "btGetConnectedStatus**7777**btGetConnectedStatus#:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/a/e/a;->mb()I

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "BTCallBackImp"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz v0, :cond_1

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 4
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->ca()V

    goto :goto_0

    .line 5
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->mb()I

    move-result p0

    invoke-interface {v0, p0}, Lc/b/a/a/a/d;->m(I)V

    :cond_1
    :goto_0
    return-void
.end method

.method public da()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->da()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->rb()V

    :cond_1
    :goto_0
    return-void
.end method

.method public ea(Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0, p1}, Lcom/eckom/xtlibrary/b/c/a;->ea(Ljava/lang/String;)V

    goto :goto_0

    .line 4
    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p1, ""

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/e/a;->Aa(Ljava/lang/String;)V

    :cond_1
    :goto_0
    return-void
.end method

.method public ga()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->ga()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->qb()V

    :cond_1
    :goto_0
    return-void
.end method

.method public ja()V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "btGetPhoneStatus**777**btGetPhoneStatus#:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/a/e/a;->getCallState()I

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "BTCallBackImp"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz v0, :cond_1

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 4
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->ja()V

    goto :goto_0

    .line 5
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->getCallState()I

    move-result p0

    invoke-interface {v0, p0}, Lc/b/a/a/a/d;->W(I)V

    :cond_1
    :goto_0
    return-void
.end method

.method public la()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b/a;->dd:Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz p0, :cond_1

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/e/a;->Hk:Lcom/eckom/xtlibrary/b/c/a;

    if-eqz v0, :cond_0

    .line 3
    invoke-interface {v0}, Lcom/eckom/xtlibrary/b/c/a;->la()V

    goto :goto_0

    .line 4
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/a/e/a;->nb()V

    :cond_1
    :goto_0
    return-void
.end method

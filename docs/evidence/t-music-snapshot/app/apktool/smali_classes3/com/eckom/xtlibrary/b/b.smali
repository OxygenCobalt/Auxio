.class public Lcom/eckom/xtlibrary/b/b;
.super Ljava/lang/Object;
.source "XTManage.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/b/b$a;
    }
.end annotation


# instance fields
.field private Pf:Lcom/eckom/xtlibrary/b/g/a;

.field private Qf:Lcom/eckom/xtlibrary/b/b/d;

.field private Rf:Lcom/eckom/xtlibrary/b/b/a;

.field private Sf:Lcom/eckom/xtlibrary/b/b/b;

.field public Tf:Lcom/eckom/xtlibrary/b/b/c;

.field public Uf:Lcom/eckom/xtlibrary/b/b/e;

.field private final Vf:Ljava/lang/String;

.field private final Wf:Ljava/lang/String;

.field private final Xf:Ljava/lang/String;

.field public cd:Lc/b/a/a/a/d;

.field private mConnection:Landroid/content/ServiceConnection;

.field public mContext:Landroid/content/Context;


# direct methods
.method private constructor <init>()V
    .locals 1

    .line 2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const-string v0, "com.tw.service.xt"

    .line 3
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Vf:Ljava/lang/String;

    const-string v0, "com.tw.service.xt.CommandService"

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Wf:Ljava/lang/String;

    const-string v0, "com.tw.service.xt.CommandService.Bind"

    .line 5
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Xf:Ljava/lang/String;

    .line 6
    new-instance v0, Lcom/eckom/xtlibrary/b/a;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/a;-><init>(Lcom/eckom/xtlibrary/b/b;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/b;->mConnection:Landroid/content/ServiceConnection;

    return-void
.end method

.method synthetic constructor <init>(Lcom/eckom/xtlibrary/b/a;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/b;-><init>()V

    return-void
.end method

.method private Ie()V
    .locals 4

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "=(basePresenter instanceof RadioPresenter)="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/b;->Pf:Lcom/eckom/xtlibrary/b/g/a;

    instance-of v1, v1, Lcom/eckom/xtlibrary/b/h/c/a;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "XTManage"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Pf:Lcom/eckom/xtlibrary/b/g/a;

    instance-of v2, v0, Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz v2, :cond_1

    .line 3
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Qf:Lcom/eckom/xtlibrary/b/b/d;

    if-nez v2, :cond_0

    .line 4
    new-instance v2, Lcom/eckom/xtlibrary/b/b/d;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-direct {v2, v0, v3}, Lcom/eckom/xtlibrary/b/b/d;-><init>(Lcom/eckom/xtlibrary/b/g/a;Lc/b/a/a/a/d;)V

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Qf:Lcom/eckom/xtlibrary/b/b/d;

    :cond_0
    const-string v0, "registerCallBack iRadioCallBack"

    .line 5
    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const-string v0, "=registerRadioCallBack="

    .line 6
    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->Qf:Lcom/eckom/xtlibrary/b/b/d;

    invoke-interface {v0, p0}, Lc/b/a/a/a/d;->b(Lc/b/a/a/a/c;)V

    goto :goto_0

    .line 8
    :cond_1
    instance-of v2, v0, Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz v2, :cond_3

    .line 9
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Rf:Lcom/eckom/xtlibrary/b/b/a;

    if-nez v2, :cond_2

    .line 10
    new-instance v2, Lcom/eckom/xtlibrary/b/b/a;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-direct {v2, v0, v3}, Lcom/eckom/xtlibrary/b/b/a;-><init>(Lcom/eckom/xtlibrary/b/g/a;Lc/b/a/a/a/d;)V

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Rf:Lcom/eckom/xtlibrary/b/b/a;

    :cond_2
    const-string v0, "registerCallBack iBTCallBack"

    .line 11
    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->Rf:Lcom/eckom/xtlibrary/b/b/a;

    invoke-interface {v0, p0}, Lc/b/a/a/a/d;->b(Lc/b/a/a/a/a;)V

    goto :goto_0

    .line 13
    :cond_3
    instance-of v2, v0, Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz v2, :cond_5

    .line 14
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Tf:Lcom/eckom/xtlibrary/b/b/c;

    if-nez v2, :cond_4

    .line 15
    new-instance v2, Lcom/eckom/xtlibrary/b/b/c;

    invoke-direct {v2, v0}, Lcom/eckom/xtlibrary/b/b/c;-><init>(Lcom/eckom/xtlibrary/b/g/a;)V

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Tf:Lcom/eckom/xtlibrary/b/b/c;

    :cond_4
    const-string v0, "registerCallBack iMusicCallBack"

    .line 16
    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 17
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->Tf:Lcom/eckom/xtlibrary/b/b/c;

    invoke-interface {v0, p0}, Lc/b/a/a/a/d;->a(Lc/b/a/a/a/b;)V

    goto :goto_0

    .line 18
    :cond_5
    instance-of v2, v0, Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz v2, :cond_7

    .line 19
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Uf:Lcom/eckom/xtlibrary/b/b/e;

    if-nez v2, :cond_6

    .line 20
    new-instance v2, Lcom/eckom/xtlibrary/b/b/e;

    invoke-direct {v2, v0}, Lcom/eckom/xtlibrary/b/b/e;-><init>(Lcom/eckom/xtlibrary/b/g/a;)V

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/b;->Uf:Lcom/eckom/xtlibrary/b/b/e;

    :cond_6
    const-string v0, "registerCallBack iVideoCallBack"

    .line 21
    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 22
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->Uf:Lcom/eckom/xtlibrary/b/b/e;

    invoke-interface {v0, p0}, Lc/b/a/a/a/d;->b(Lc/b/a/a/a/f;)V

    goto :goto_0

    .line 23
    :cond_7
    instance-of v1, v0, Lcom/eckom/xtlibrary/b/d/b/a;

    if-eqz v1, :cond_9

    .line 24
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/b;->Sf:Lcom/eckom/xtlibrary/b/b/b;

    if-nez v1, :cond_8

    .line 25
    new-instance v1, Lcom/eckom/xtlibrary/b/b/b;

    invoke-direct {v1, v0}, Lcom/eckom/xtlibrary/b/b/b;-><init>(Lcom/eckom/xtlibrary/b/g/a;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/b;->Sf:Lcom/eckom/xtlibrary/b/b/b;

    .line 26
    :cond_8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->Sf:Lcom/eckom/xtlibrary/b/b/b;

    invoke-interface {v0, p0}, Lc/b/a/a/a/d;->b(Lc/b/a/a/a/e;)V

    :cond_9
    :goto_0
    return-void
.end method

.method private Je()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Pf:Lcom/eckom/xtlibrary/b/g/a;

    instance-of v1, v0, Lcom/eckom/xtlibrary/b/h/c/a;

    if-eqz v1, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Qf:Lcom/eckom/xtlibrary/b/b/d;

    if-eqz v0, :cond_4

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {p0, v0}, Lc/b/a/a/a/d;->a(Lc/b/a/a/a/c;)V

    goto :goto_0

    .line 4
    :cond_0
    instance-of v1, v0, Lcom/eckom/xtlibrary/b/a/e/a;

    if-eqz v1, :cond_1

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Rf:Lcom/eckom/xtlibrary/b/b/a;

    if-eqz v0, :cond_4

    .line 6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {p0, v0}, Lc/b/a/a/a/d;->a(Lc/b/a/a/a/a;)V

    goto :goto_0

    .line 7
    :cond_1
    instance-of v1, v0, Lcom/eckom/xtlibrary/b/f/e/a;

    if-eqz v1, :cond_2

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Tf:Lcom/eckom/xtlibrary/b/b/c;

    if-eqz v0, :cond_4

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {p0, v0}, Lc/b/a/a/a/d;->b(Lc/b/a/a/a/b;)V

    goto :goto_0

    .line 10
    :cond_2
    instance-of v1, v0, Lcom/eckom/xtlibrary/b/k/b/a;

    if-eqz v1, :cond_3

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Uf:Lcom/eckom/xtlibrary/b/b/e;

    if-eqz v0, :cond_4

    .line 12
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {p0, v0}, Lc/b/a/a/a/d;->a(Lc/b/a/a/a/f;)V

    goto :goto_0

    .line 13
    :cond_3
    instance-of v0, v0, Lcom/eckom/xtlibrary/b/d/b/a;

    if-eqz v0, :cond_4

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->Sf:Lcom/eckom/xtlibrary/b/b/b;

    if-eqz v0, :cond_4

    .line 15
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {p0, v0}, Lc/b/a/a/a/d;->a(Lc/b/a/a/a/e;)V

    :cond_4
    :goto_0
    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/b;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/b;->Je()V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/b;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/b;->Ie()V

    return-void
.end method

.method public static getInstant()Lcom/eckom/xtlibrary/b/b;
    .locals 1

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/b/b$a;->access$100()Lcom/eckom/xtlibrary/b/b;

    move-result-object v0

    return-object v0
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/g/a;)V
    .locals 0

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/b;->Pf:Lcom/eckom/xtlibrary/b/g/a;

    return-void
.end method

.method public db()V
    .locals 2

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->mConnection:Landroid/content/ServiceConnection;

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/b;->mContext:Landroid/content/Context;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/b;->mConnection:Landroid/content/ServiceConnection;

    invoke-virtual {v0, v1}, Landroid/content/Context;->unbindService(Landroid/content/ServiceConnection;)V

    .line 3
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->Pf:Lcom/eckom/xtlibrary/b/g/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/g/a;->onDestroy()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    return-void
.end method

.method public init(Landroid/content/Context;)V
    .locals 3

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/b;->mContext:Landroid/content/Context;

    const-string p1, "XTManage"

    const-string v0, "XTManage init: "

    .line 2
    invoke-static {p1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 3
    new-instance v0, Landroid/content/Intent;

    invoke-direct {v0}, Landroid/content/Intent;-><init>()V

    const-string v1, "com.tw.service.xt"

    const-string v2, "com.tw.service.xt.CommandService"

    .line 4
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->setClassName(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string v1, "com.tw.service.xt.CommandService.Bind"

    .line 5
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    .line 6
    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    .line 7
    invoke-virtual {v0}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    .line 8
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/b;->mContext:Landroid/content/Context;

    invoke-virtual {v1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object v1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->mConnection:Landroid/content/ServiceConnection;

    const/4 v2, 0x1

    invoke-virtual {v1, v0, p0, v2}, Landroid/content/Context;->bindService(Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z

    const-string p0, "XTManage init: bindService"

    .line 9
    invoke-static {p1, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

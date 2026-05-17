.class public Lcom/eckom/xtlibrary/b/h/b/e;
.super Lcom/eckom/xtlibrary/b/e/a;
.source "RadioModel.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Lcom/eckom/xtlibrary/b/e/a;"
    }
.end annotation


# static fields
.field private static volatile Li:Lcom/eckom/xtlibrary/b/h/b/e;


# instance fields
.field protected Ci:I

.field private Di:Z

.field private Ei:Z

.field private Fi:Lcom/eckom/xtlibrary/b/h/a;

.field private Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

.field private Hi:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

.field private Ii:Z

.field private Ji:Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

.field private if:I

.field location:I

.field protected mActivity:I

.field private mContext:Landroid/content/Context;

.field public mHandler:Landroid/os/Handler;

.field public mService:I

.field private pi:I

.field private wg:Z

.field private wh:Lcom/eckom/xtlibrary/b/h/d/b;

.field private yh:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Lcom/eckom/xtlibrary/b/h/b/f;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method private constructor <init>()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/e/a;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/4 v0, 0x1

    .line 3
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Di:Z

    const/4 v0, 0x0

    .line 4
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ei:Z

    .line 5
    new-instance v0, Ljava/util/concurrent/ConcurrentHashMap;

    invoke-direct {v0}, Ljava/util/concurrent/ConcurrentHashMap;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    .line 6
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/a;->getInstance()Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    const/4 v0, -0x1

    .line 7
    iput v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    .line 8
    new-instance v0, Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/h/b/b;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/h/b/b;-><init>(Lcom/eckom/xtlibrary/b/h/b/e;)V

    invoke-direct {v0, v1}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    .line 9
    new-instance v0, Lcom/eckom/xtlibrary/b/h/b/d;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/h/b/d;-><init>(Lcom/eckom/xtlibrary/b/h/b/e;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ji:Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    .line 10
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/d/b;->open()Lcom/eckom/xtlibrary/b/h/d/b;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    if-eqz v0, :cond_0

    const-string v0, "RadioModel"

    const-string v1, "RadioModel: Model create "

    .line 12
    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 13
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    invoke-virtual {v1, v0, v2}, Landroid/tw/john/TWUtil;->addHandler(Ljava/lang/String;Landroid/os/Handler;)V

    .line 14
    :cond_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->_e()V

    return-void
.end method

.method private M(Z)V
    .locals 2

    .line 1
    :try_start_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    if-eqz p0, :cond_0

    .line 2
    new-instance p0, Landroid/os/Bundle;

    invoke-direct {p0}, Landroid/os/Bundle;-><init>()V

    const-string v0, "dateType"

    const-string v1, "send"

    .line 3
    invoke-virtual {p0, v0, v1}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string v0, "action"

    const-string v1, "com.tw.radio.state"

    .line 4
    invoke-virtual {p0, v0, v1}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string v0, "RadioState"

    .line 5
    invoke-virtual {p0, v0, p1}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 6
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {p1, p0}, Lc/b/a/a/a/d;->a(Landroid/os/Bundle;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 7
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "sendRadioState: Error"

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {p1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "RadioModel"

    invoke-static {p1, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_0
    :goto_0
    return-void
.end method

.method private _e()V
    .locals 4

    const/16 v0, 0x12

    new-array v1, v0, [Lcom/eckom/xtlibrary/b/h/a/a;

    .line 1
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    const/4 v1, 0x0

    :goto_0
    if-ge v1, v0, :cond_0

    .line 2
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    new-instance v3, Lcom/eckom/xtlibrary/b/h/a/a;

    invoke-direct {v3}, Lcom/eckom/xtlibrary/b/h/a/a;-><init>()V

    aput-object v3, v2, v1

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    :cond_0
    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/d/b;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    return-object p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/h/b/e;Landroid/os/Message;)V
    .locals 0

    .line 3
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Landroid/os/Message;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/h/b/e;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->M(Z)V

    return-void
.end method

.method private af()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mContext:Landroid/content/Context;

    invoke-virtual {v0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v0

    const-string v1, "isWAZE"

    const/4 v2, 0x0

    invoke-static {v0, v1, v2}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 2
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/h/b/c;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/h/b/c;-><init>(Lcom/eckom/xtlibrary/b/h/b/e;)V

    const-wide/16 v2, 0x1f4

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    return-object p0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/h/b/e;Landroid/os/Message;)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->e(Landroid/os/Message;)V

    return-void
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/h/b/e;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->pi:I

    return p0
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    return-object p0
.end method

.method private d(Landroid/os/Message;)V
    .locals 7

    .line 2
    iget v0, p1, Landroid/os/Message;->arg1:I

    shr-int/lit8 v1, v0, 0x10

    const v2, 0xffff

    and-int/2addr v1, v2

    iput v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->pi:I

    .line 3
    iget v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    if-eq v1, v0, :cond_e

    xor-int/2addr v1, v0

    .line 4
    iput v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/lit8 v0, v1, 0x4

    const/4 v2, 0x4

    const/4 v3, 0x1

    const/4 v4, 0x0

    if-ne v0, v2, :cond_2

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v5, v2

    if-ne v5, v2, :cond_0

    move v5, v3

    goto :goto_0

    :cond_0
    move v5, v4

    :goto_0
    iput-boolean v5, v0, Lcom/eckom/xtlibrary/b/h/a;->fl:Z

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_2

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 7
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v6, v2

    if-ne v6, v2, :cond_1

    move v6, v3

    goto :goto_2

    :cond_1
    move v6, v4

    :goto_2
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->i(Z)V

    goto :goto_1

    :cond_2
    and-int/lit8 v0, v1, 0x20

    const/16 v2, 0x20

    if-ne v0, v2, :cond_5

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v5, v2

    if-ne v5, v2, :cond_3

    move v5, v3

    goto :goto_3

    :cond_3
    move v5, v4

    :goto_3
    iput-boolean v5, v0, Lcom/eckom/xtlibrary/b/h/a;->dl:Z

    .line 9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_4
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_5

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 10
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v6, v2

    if-ne v6, v2, :cond_4

    move v6, v3

    goto :goto_5

    :cond_4
    move v6, v4

    :goto_5
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->t(Z)V

    goto :goto_4

    :cond_5
    and-int/lit8 v0, v1, 0x40

    const/16 v2, 0x40

    if-ne v0, v2, :cond_8

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v5, v2

    if-ne v5, v2, :cond_6

    move v5, v3

    goto :goto_6

    :cond_6
    move v5, v4

    :goto_6
    iput-boolean v5, v0, Lcom/eckom/xtlibrary/b/h/a;->el:Z

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_7
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_8

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 13
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v6, v2

    if-ne v6, v2, :cond_7

    move v6, v3

    goto :goto_8

    :cond_7
    move v6, v4

    :goto_8
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->p(Z)V

    goto :goto_7

    :cond_8
    and-int/lit8 v0, v1, 0x2

    const/4 v2, 0x2

    if-ne v0, v2, :cond_b

    .line 14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v5, v2

    if-ne v5, v2, :cond_9

    move v5, v3

    goto :goto_9

    :cond_9
    move v5, v4

    :goto_9
    iput-boolean v5, v0, Lcom/eckom/xtlibrary/b/h/a;->Zk:Z

    .line 15
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_a
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_b

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 16
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v6, v2

    if-ne v6, v2, :cond_a

    move v6, v3

    goto :goto_b

    :cond_a
    move v6, v4

    :goto_b
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->s(Z)V

    goto :goto_a

    :cond_b
    const/16 v0, 0x80

    and-int/2addr v1, v0

    if-ne v1, v0, :cond_e

    .line 17
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v2, v0

    if-ne v2, v0, :cond_c

    move v2, v3

    goto :goto_c

    :cond_c
    move v2, v4

    :goto_c
    iput-boolean v2, v1, Lcom/eckom/xtlibrary/b/h/a;->_k:Z

    .line 18
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_d
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_e

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/Map$Entry;

    .line 19
    invoke-interface {v2}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    and-int/2addr v5, v0

    if-ne v5, v0, :cond_d

    move v5, v3

    goto :goto_e

    :cond_d
    move v5, v4

    :goto_e
    invoke-interface {v2, v5}, Lcom/eckom/xtlibrary/b/h/b/f;->e(Z)V

    goto :goto_d

    .line 20
    :cond_e
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    and-int/lit16 v1, p1, 0xff

    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->jl:I

    shr-int/lit8 p1, p1, 0x8

    and-int/lit16 p1, p1, 0xff

    .line 21
    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->kl:I

    .line 22
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "0404:"

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->pi:I

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v0, "   "

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v0, v0, Lcom/eckom/xtlibrary/b/h/a;->kl:I

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    .line 23
    iget-boolean p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ei:Z

    if-nez p1, :cond_f

    .line 24
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    const p1, 0xff03

    const-wide/16 v0, 0x0

    invoke-virtual {p0, p1, v0, v1}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_f
    return-void
.end method

.method private da(I)V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const v0, 0x9e11

    const/16 v1, 0xc0

    invoke-virtual {p0, v0, v1, p1}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/h/b/e;)Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mContext:Landroid/content/Context;

    return-object p0
.end method

.method private e(Landroid/os/Message;)V
    .locals 8

    .line 2
    iget v0, p1, Landroid/os/Message;->arg1:I

    const/4 v1, 0x1

    packed-switch v0, :pswitch_data_0

    goto/16 :goto_5

    .line 3
    :pswitch_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Qk:I

    .line 4
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    if-ne p1, v1, :cond_2

    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    const/4 v1, 0x5

    if-ne p1, v1, :cond_2

    .line 5
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Sk:I

    .line 6
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Tk:I

    .line 7
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Qk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Uk:I

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_0
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 9
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    move-object v1, v0

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    iget v3, v0, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    iget v4, v0, Lcom/eckom/xtlibrary/b/h/a;->Qk:I

    iget v5, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    iget v6, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    invoke-interface/range {v1 .. v6}, Lcom/eckom/xtlibrary/b/h/b/f;->a(IIIII)V

    goto :goto_0

    .line 10
    :pswitch_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    goto/16 :goto_5

    .line 11
    :pswitch_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    goto/16 :goto_5

    .line 12
    :pswitch_3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-boolean v0, v0, Lcom/eckom/xtlibrary/b/h/a;->Yk:Z

    iget v2, p1, Landroid/os/Message;->arg2:I

    const/4 v3, 0x0

    if-eqz v2, :cond_0

    move v2, v1

    goto :goto_1

    :cond_0
    move v2, v3

    :goto_1
    if-eq v0, v2, :cond_2

    .line 13
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    if-eqz p1, :cond_1

    goto :goto_2

    :cond_1
    move v1, v3

    :goto_2
    iput-boolean v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Yk:Z

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-boolean p1, p1, Lcom/eckom/xtlibrary/b/h/a;->Yk:Z

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->D(Z)V

    goto/16 :goto_5

    .line 15
    :pswitch_4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Rk:I

    .line 16
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    const/4 v1, 0x2

    if-ne p1, v1, :cond_2

    .line 17
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Ok:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Sk:I

    .line 18
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Nk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Tk:I

    .line 19
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Rk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Uk:I

    .line 20
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_3
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 21
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    move-object v1, v0

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Ok:I

    iget v3, v0, Lcom/eckom/xtlibrary/b/h/a;->Nk:I

    iget v4, v0, Lcom/eckom/xtlibrary/b/h/a;->Rk:I

    iget v5, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    iget v6, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    invoke-interface/range {v1 .. v6}, Lcom/eckom/xtlibrary/b/h/b/f;->a(IIIII)V

    goto :goto_3

    .line 22
    :pswitch_5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Pk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Qk:I

    .line 23
    iget p1, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    const/4 v0, 0x6

    .line 24
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Kk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Sk:I

    .line 25
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Jk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Tk:I

    .line 26
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Pk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Uk:I

    .line 27
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_4
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 28
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    move-object v1, v0

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Kk:I

    iget v3, v0, Lcom/eckom/xtlibrary/b/h/a;->Jk:I

    iget v4, v0, Lcom/eckom/xtlibrary/b/h/a;->Pk:I

    iget v5, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    iget v6, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    invoke-interface/range {v1 .. v6}, Lcom/eckom/xtlibrary/b/h/b/f;->a(IIIII)V

    goto :goto_4

    .line 29
    :pswitch_6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Ok:I

    goto :goto_5

    .line 30
    :pswitch_7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Nk:I

    goto :goto_5

    .line 31
    :pswitch_8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Kk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    goto :goto_5

    .line 32
    :pswitch_9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Jk:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    goto :goto_5

    .line 33
    :pswitch_a
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg2:I

    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    .line 34
    iget-boolean p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Yk:Z

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->D(Z)V

    .line 35
    :cond_2
    :goto_5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_6
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_3

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 36
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    move-object v1, v0

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Kk:I

    iget v3, v0, Lcom/eckom/xtlibrary/b/h/a;->Jk:I

    iget v4, v0, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    iget v5, v0, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    iget v6, v0, Lcom/eckom/xtlibrary/b/h/a;->Ok:I

    iget v7, v0, Lcom/eckom/xtlibrary/b/h/a;->Nk:I

    invoke-interface/range {v1 .. v7}, Lcom/eckom/xtlibrary/b/h/b/f;->a(IIIIII)V

    goto :goto_6

    :cond_3
    return-void

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_a
        :pswitch_9
        :pswitch_8
        :pswitch_7
        :pswitch_6
        :pswitch_5
        :pswitch_4
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/h/b/e;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    return p0
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/twproject/radio/utils/b;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Hi:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    return-object p0
.end method

.method public static getInstance()Lcom/eckom/xtlibrary/b/h/b/e;
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/h/b/e;->Li:Lcom/eckom/xtlibrary/b/h/b/e;

    if-nez v0, :cond_1

    .line 2
    const-class v0, Lcom/eckom/xtlibrary/b/h/b/e;

    monitor-enter v0

    .line 3
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/h/b/e;->Li:Lcom/eckom/xtlibrary/b/h/b/e;

    if-nez v1, :cond_0

    .line 4
    new-instance v1, Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-direct {v1}, Lcom/eckom/xtlibrary/b/h/b/e;-><init>()V

    sput-object v1, Lcom/eckom/xtlibrary/b/h/b/e;->Li:Lcom/eckom/xtlibrary/b/h/b/e;

    .line 5
    :cond_0
    monitor-exit v0

    goto :goto_0

    :catchall_0
    move-exception v1

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1

    .line 6
    :cond_1
    :goto_0
    sget-object v0, Lcom/eckom/xtlibrary/b/h/b/e;->Li:Lcom/eckom/xtlibrary/b/h/b/e;

    return-object v0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/b/h/b/e;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wg:Z

    return p0
.end method

.method private n(II)V
    .locals 1

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ei:Z

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 v0, 0x402

    invoke-virtual {p0, v0, p1, p2}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method


# virtual methods
.method public B(Z)V
    .locals 0

    if-eqz p1, :cond_0

    const/4 p1, 0x1

    goto :goto_0

    :cond_0
    const/16 p1, 0x81

    .line 1
    :goto_0
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->ca(I)V

    return-void
.end method

.method public C(Z)V
    .locals 0

    .line 1
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Di:Z

    return-void
.end method

.method protected D(Z)V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_0

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 2
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v0, p1}, Lcom/eckom/xtlibrary/b/h/b/f;->u(Z)V

    goto :goto_0

    :cond_0
    return-void
.end method

.method public Ha(Ljava/lang/String;)V
    .locals 2

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mService:I

    and-int/lit16 v0, v0, 0x8f

    const/4 v1, 0x1

    if-eq v0, v1, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p0, p1}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    :cond_0
    return-void
.end method

.method public Yb()V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/eckom/xtlibrary/b/h/b/a;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/h/b/a;-><init>(Lcom/eckom/xtlibrary/b/h/b/e;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    .line 2
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method public Zb()Z
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p0, p0, Lcom/eckom/xtlibrary/b/h/a;->mSource:I

    const/4 v0, 0x1

    if-ne p0, v0, :cond_0

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    :goto_0
    return v0
.end method

.method public _b()V
    .locals 6

    const/4 v0, 0x0

    move v1, v0

    .line 1
    :goto_0
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v3, v2

    const/4 v4, 0x0

    if-ge v1, v3, :cond_0

    .line 2
    aget-object v2, v2, v1

    iput-object v4, v2, Lcom/eckom/xtlibrary/b/h/a/a;->vl:Ljava/lang/String;

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 3
    :cond_0
    :try_start_0
    new-instance v1, Ljava/io/BufferedReader;

    new-instance v2, Ljava/io/FileReader;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "/data/tw/radio_name_"

    invoke-virtual {v3, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, v5, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-virtual {v3, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V

    invoke-direct {v1, v2}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 4
    :goto_1
    :try_start_1
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v2, v2

    if-ge v0, v2, :cond_1

    .line 5
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v2, v2, v0

    invoke-virtual {v1}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v3

    iput-object v3, v2, Lcom/eckom/xtlibrary/b/h/a/a;->vl:Ljava/lang/String;
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 6
    :cond_1
    :try_start_2
    invoke-virtual {v1}, Ljava/io/BufferedReader;->close()V

    goto :goto_4

    :catchall_0
    move-exception p0

    move-object v4, v1

    goto :goto_2

    :catch_0
    move-object v4, v1

    goto :goto_3

    :catchall_1
    move-exception p0

    :goto_2
    if-eqz v4, :cond_2

    invoke-virtual {v4}, Ljava/io/BufferedReader;->close()V

    .line 7
    :cond_2
    throw p0

    :catch_1
    :goto_3
    if-eqz v4, :cond_3

    .line 8
    invoke-virtual {v4}, Ljava/io/BufferedReader;->close()V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    :catch_2
    :cond_3
    :goto_4
    return-void
.end method

.method public a(Landroid/content/Context;)V
    .locals 1

    .line 4
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mContext:Landroid/content/Context;

    .line 5
    new-instance p1, Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mContext:Landroid/content/Context;

    invoke-direct {p1, v0}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;-><init>(Landroid/content/Context;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Hi:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Hi:Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ji:Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->a(Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;)V

    return-void
.end method

.method protected a(Landroid/os/Message;)V
    .locals 11

    .line 8
    iget v0, p1, Landroid/os/Message;->arg1:I

    const/4 v1, 0x4

    const/4 v2, 0x2

    const/4 v3, 0x1

    const/4 v4, 0x0

    if-eqz v0, :cond_f

    const/4 v5, -0x1

    if-eq v0, v3, :cond_6

    if-eq v0, v2, :cond_3

    const/4 v2, 0x3

    if-eq v0, v2, :cond_2

    if-eq v0, v1, :cond_0

    goto/16 :goto_18

    .line 9
    :cond_0
    iget p1, p1, Landroid/os/Message;->arg2:I

    if-ltz p1, :cond_1

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v1, v1

    if-ge p1, v1, :cond_1

    .line 10
    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->ll:I

    goto :goto_0

    .line 11
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iput v5, p1, Lcom/eckom/xtlibrary/b/h/a;->ll:I

    .line 12
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_1
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1c

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 13
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v1, v1, Lcom/eckom/xtlibrary/b/h/a;->ll:I

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/h/b/f;->I(I)V

    goto :goto_1

    .line 14
    :cond_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v1, p1, Landroid/os/Message;->arg2:I

    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->jl:I

    .line 15
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_2
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1c

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 16
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v1, p1, Landroid/os/Message;->arg2:I

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/h/b/f;->A(I)V

    goto :goto_2

    .line 17
    :cond_3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v1, p1, Landroid/os/Message;->arg2:I

    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Wk:I

    .line 18
    iget-object v1, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v2, v1, Ljava/lang/String;

    if-eqz v2, :cond_4

    .line 19
    check-cast v1, Ljava/lang/String;

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/h/a;->gl:Ljava/lang/String;

    .line 20
    :cond_4
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ei:Z

    if-eqz v0, :cond_5

    .line 21
    iput-boolean v4, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ei:Z

    .line 22
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    const v1, 0xff03

    const-wide/16 v2, 0x0

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 23
    :cond_5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_3
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1c

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/Map$Entry;

    .line 24
    invoke-interface {v1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v3, v3, Lcom/eckom/xtlibrary/b/h/a;->Wk:I

    invoke-interface {v2, v3}, Lcom/eckom/xtlibrary/b/h/b/f;->K(I)V

    .line 25
    invoke-interface {v1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v2, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v2, Ljava/lang/String;

    invoke-interface {v1, v2}, Lcom/eckom/xtlibrary/b/h/b/f;->ba(Ljava/lang/String;)V

    goto :goto_3

    .line 26
    :cond_6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    iget p1, p1, Landroid/os/Message;->arg2:I

    if-eq v1, p1, :cond_1c

    .line 27
    iput p1, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    .line 28
    :try_start_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    if-eqz p1, :cond_7

    .line 29
    new-instance p1, Landroid/os/Bundle;

    invoke-direct {p1}, Landroid/os/Bundle;-><init>()V

    const-string v0, "dateType"

    const-string v1, "send"

    .line 30
    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string v0, "action"

    const-string v1, "com.tw.radio.av"

    .line 31
    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string v0, "bandState"

    .line 32
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v1, v1, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    .line 33
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {v0, p1}, Lc/b/a/a/a/d;->a(Landroid/os/Bundle;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_4

    :catch_0
    move-exception p1

    .line 34
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "handleMessage: Error"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    const-string v0, "RadioModel"

    invoke-static {v0, p1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 35
    :cond_7
    :goto_4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_5
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_8

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 36
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v1, v1, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/h/b/f;->x(I)V

    goto :goto_5

    .line 37
    :cond_8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iput v5, p1, Lcom/eckom/xtlibrary/b/h/a;->ll:I

    .line 38
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    if-ne v0, v2, :cond_9

    .line 39
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Ok:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Sk:I

    .line 40
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Nk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Tk:I

    .line 41
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Rk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Uk:I

    .line 42
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_6
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_c

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 43
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    move-object v5, v0

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v6, v0, Lcom/eckom/xtlibrary/b/h/a;->Ok:I

    iget v7, v0, Lcom/eckom/xtlibrary/b/h/a;->Nk:I

    iget v8, v0, Lcom/eckom/xtlibrary/b/h/a;->Rk:I

    iget v9, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    iget v10, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    invoke-interface/range {v5 .. v10}, Lcom/eckom/xtlibrary/b/h/b/f;->a(IIIII)V

    goto :goto_6

    :cond_9
    if-eqz v0, :cond_b

    if-eq v0, v3, :cond_a

    goto :goto_9

    .line 44
    :cond_a
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Sk:I

    .line 45
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Tk:I

    .line 46
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Qk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Uk:I

    .line 47
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_7
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_c

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 48
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    move-object v5, v0

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v6, v0, Lcom/eckom/xtlibrary/b/h/a;->Mk:I

    iget v7, v0, Lcom/eckom/xtlibrary/b/h/a;->Lk:I

    iget v8, v0, Lcom/eckom/xtlibrary/b/h/a;->Qk:I

    iget v9, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    iget v10, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    invoke-interface/range {v5 .. v10}, Lcom/eckom/xtlibrary/b/h/b/f;->a(IIIII)V

    goto :goto_7

    .line 49
    :cond_b
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Kk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Sk:I

    .line 50
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Jk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Tk:I

    .line 51
    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Pk:I

    iput v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Uk:I

    .line 52
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_8
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_c

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 53
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    move-object v5, v0

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v6, v0, Lcom/eckom/xtlibrary/b/h/a;->Kk:I

    iget v7, v0, Lcom/eckom/xtlibrary/b/h/a;->Jk:I

    iget v8, v0, Lcom/eckom/xtlibrary/b/h/a;->Pk:I

    iget v9, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    iget v10, v0, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    invoke-interface/range {v5 .. v10}, Lcom/eckom/xtlibrary/b/h/b/f;->a(IIIII)V

    goto :goto_8

    .line 54
    :cond_c
    :goto_9
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    if-ne v0, v2, :cond_d

    .line 55
    iput v4, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    .line 56
    iput-boolean v4, p1, Lcom/eckom/xtlibrary/b/h/a;->fl:Z

    .line 57
    iput-boolean v4, p1, Lcom/eckom/xtlibrary/b/h/a;->dl:Z

    .line 58
    iput-boolean v4, p1, Lcom/eckom/xtlibrary/b/h/a;->el:Z

    .line 59
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_a
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_e

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 60
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/h/b/f;->i(Z)V

    .line 61
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v1, v4}, Lcom/eckom/xtlibrary/b/h/b/f;->t(Z)V

    .line 62
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v0, v4}, Lcom/eckom/xtlibrary/b/h/b/f;->p(Z)V

    goto :goto_a

    .line 63
    :cond_d
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 v0, 0x404

    const/16 v1, 0xff

    invoke-virtual {p1, v0, v1}, Landroid/tw/john/TWUtil;->write(II)I

    .line 64
    :cond_e
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_b
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1c

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 65
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v1, v1, Lcom/eckom/xtlibrary/b/h/a;->Wk:I

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/h/b/f;->K(I)V

    goto :goto_b

    .line 66
    :cond_f
    iget v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    iget p1, p1, Landroid/os/Message;->arg2:I

    if-eq v0, p1, :cond_1c

    xor-int/2addr v0, p1

    .line 67
    iput p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/lit8 p1, v0, 0x2

    if-ne p1, v2, :cond_11

    .line 68
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_c
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_11

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 69
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr v6, v2

    if-ne v6, v2, :cond_10

    move v6, v3

    goto :goto_d

    :cond_10
    move v6, v4

    :goto_d
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->r(Z)V

    goto :goto_c

    :cond_11
    and-int/lit8 p1, v0, 0x4

    if-ne p1, v1, :cond_14

    .line 70
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr v5, v1

    if-ne v5, v1, :cond_12

    move v5, v3

    goto :goto_e

    :cond_12
    move v5, v4

    :goto_e
    iput v5, p1, Lcom/eckom/xtlibrary/b/h/a;->nl:I

    .line 71
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_f
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_14

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 72
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr v6, v1

    if-ne v6, v1, :cond_13

    move v6, v3

    goto :goto_10

    :cond_13
    move v6, v4

    :goto_10
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->y(I)V

    goto :goto_f

    :cond_14
    and-int/lit8 p1, v0, 0x8

    const/16 v1, 0x8

    if-ne p1, v1, :cond_17

    .line 73
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr v5, v1

    if-ne v5, v1, :cond_15

    move v5, v3

    goto :goto_11

    :cond_15
    move v5, v4

    :goto_11
    iput v5, p1, Lcom/eckom/xtlibrary/b/h/a;->Vk:I

    .line 74
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_12
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_17

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 75
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr v6, v1

    if-ne v6, v1, :cond_16

    move v6, v3

    goto :goto_13

    :cond_16
    move v6, v4

    :goto_13
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->S(I)V

    goto :goto_12

    :cond_17
    and-int/lit8 p1, v0, 0x10

    const/16 v1, 0x10

    if-ne p1, v1, :cond_1a

    .line 76
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr v5, v1

    if-ne v5, v1, :cond_18

    move v5, v3

    goto :goto_14

    :cond_18
    move v5, v4

    :goto_14
    iput-boolean v5, p1, Lcom/eckom/xtlibrary/b/h/a;->cl:Z

    .line 77
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_15
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_1a

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 78
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr v6, v1

    if-ne v6, v1, :cond_19

    move v6, v3

    goto :goto_16

    :cond_19
    move v6, v4

    :goto_16
    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->m(Z)V

    goto :goto_15

    :cond_1a
    const/16 p1, 0x80

    and-int/2addr v0, p1

    if-ne v0, p1, :cond_1c

    .line 79
    iget v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    and-int/2addr p1, v0

    if-nez p1, :cond_1c

    .line 80
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v0, p1, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    if-eq v0, v2, :cond_1b

    .line 81
    iget p1, p1, Lcom/eckom/xtlibrary/b/h/a;->mRegion:I

    const/4 v1, 0x5

    if-eq p1, v1, :cond_1b

    move v0, v4

    .line 82
    :cond_1b
    invoke-direct {p0, v4, v0}, Lcom/eckom/xtlibrary/b/h/b/e;->n(II)V

    .line 83
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_17
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result p1

    if-eqz p1, :cond_1c

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/Map$Entry;

    .line 84
    invoke-interface {p1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {p1, v4}, Lcom/eckom/xtlibrary/b/h/b/f;->s(I)V

    goto :goto_17

    :cond_1c
    :goto_18
    return-void
.end method

.method public a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/h/b/f;)V
    .locals 0

    .line 7
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p0, p1, p2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method public ac()V
    .locals 6

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-boolean v1, v0, Lcom/eckom/xtlibrary/b/h/a;->il:Z

    if-eqz v1, :cond_0

    return-void

    .line 2
    :cond_0
    iget v0, v0, Lcom/eckom/xtlibrary/b/h/a;->ll:I

    const/4 v1, 0x1

    sub-int/2addr v0, v1

    const/4 v2, -0x1

    if-ge v0, v2, :cond_1

    move v0, v2

    :cond_1
    move v3, v0

    :goto_0
    if-le v3, v2, :cond_3

    .line 3
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v5, v4, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v5, v5, v3

    iget v5, v5, Lcom/eckom/xtlibrary/b/h/a/a;->tl:I

    if-eqz v5, :cond_2

    .line 4
    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-direct {p0, v3, v4}, Lcom/eckom/xtlibrary/b/h/b/e;->n(II)V

    goto :goto_1

    :cond_2
    add-int/lit8 v3, v3, -0x1

    goto :goto_0

    :cond_3
    :goto_1
    if-ne v3, v2, :cond_5

    .line 5
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v2, v2

    sub-int/2addr v2, v1

    :goto_2
    if-le v2, v0, :cond_5

    .line 6
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v4, v3, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v4, v4, v2

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a/a;->tl:I

    if-eqz v4, :cond_4

    .line 7
    iget v0, v3, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-direct {p0, v2, v0}, Lcom/eckom/xtlibrary/b/h/b/e;->n(II)V

    goto :goto_3

    :cond_4
    add-int/lit8 v2, v2, -0x1

    goto :goto_2

    .line 8
    :cond_5
    :goto_3
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object v0

    const-string v2, "KED"

    invoke-virtual {v0, v2}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_6

    .line 9
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->af()V

    .line 10
    :cond_6
    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/b/h/b/e;->w(Z)V

    return-void
.end method

.method protected b(Landroid/os/Message;)V
    .locals 0

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Landroid/os/Message;->arg1:I

    iput p1, p0, Lcom/eckom/xtlibrary/b/h/a;->mSource:I

    return-void
.end method

.method public bc()V
    .locals 6

    const-string v0, "/data/tw/radio_name_"

    const/4 v1, 0x0

    .line 1
    :try_start_0
    new-instance v2, Ljava/io/BufferedWriter;

    new-instance v3, Ljava/io/FileWriter;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v5, v5, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-direct {v3, v4}, Ljava/io/FileWriter;-><init>(Ljava/lang/String;)V

    invoke-direct {v2, v3}, Ljava/io/BufferedWriter;-><init>(Ljava/io/Writer;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    const/4 v1, 0x0

    .line 2
    :goto_0
    :try_start_1
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v3, v3

    if-ge v1, v3, :cond_1

    .line 3
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v3, v3, v1

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/h/a/a;->vl:Ljava/lang/String;

    if-eqz v3, :cond_0

    .line 4
    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    :cond_0
    const/16 v3, 0xa

    .line 5
    invoke-virtual {v2, v3}, Ljava/io/BufferedWriter;->write(I)V

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 6
    :cond_1
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->flush()V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 7
    :try_start_2
    invoke-virtual {v2}, Ljava/io/BufferedWriter;->close()V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    goto :goto_2

    :catchall_0
    move-exception p0

    move-object v1, v2

    goto :goto_3

    :catch_0
    move-object v1, v2

    goto :goto_1

    :catchall_1
    move-exception p0

    goto :goto_3

    .line 8
    :catch_1
    :goto_1
    :try_start_3
    new-instance v2, Ljava/io/File;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2}, Ljava/io/File;->delete()Z
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    if-eqz v1, :cond_2

    .line 9
    :try_start_4
    invoke-virtual {v1}, Ljava/io/BufferedWriter;->close()V

    .line 10
    :cond_2
    :goto_2
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p0, p0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const/16 v0, 0x1b6

    const/4 v1, -0x1

    invoke-static {p0, v0, v1, v1}, Landroid/os/FileUtils;->setPermissions(Ljava/lang/String;III)I

    goto :goto_4

    :goto_3
    if-eqz v1, :cond_3

    .line 11
    invoke-virtual {v1}, Ljava/io/BufferedWriter;->close()V

    .line 12
    :cond_3
    throw p0
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_2

    :catch_2
    :goto_4
    return-void
.end method

.method protected c(Landroid/os/Message;)V
    .locals 6

    .line 2
    iget v0, p1, Landroid/os/Message;->arg2:I

    const/16 v1, 0x21

    if-eq v0, v1, :cond_9

    const/16 v1, 0x4c

    if-eq v0, v1, :cond_9

    const/16 v1, 0x54

    const/4 v2, 0x0

    if-eq v0, v1, :cond_5

    const/16 v1, 0x60

    const/16 v3, 0x404

    const/4 v4, 0x2

    const/4 v5, 0x1

    if-eq v0, v1, :cond_3

    const/16 v1, 0x61

    if-eq v0, v1, :cond_1

    packed-switch v0, :pswitch_data_0

    goto/16 :goto_5

    .line 3
    :pswitch_0
    iget v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mActivity:I

    if-eq v0, v5, :cond_0

    const/16 v1, 0x81

    if-ne v0, v1, :cond_a

    .line 4
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_a

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 5
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v1, p1, Landroid/os/Message;->arg2:I

    add-int/lit8 v1, v1, -0x31

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/h/b/f;->n(I)V

    goto :goto_0

    .line 6
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    if-eq p1, v4, :cond_a

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    iget p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    const/16 v0, 0x40

    and-int/2addr p0, v0

    if-ne p0, v0, :cond_2

    move v5, v2

    :cond_2
    invoke-virtual {p1, v3, v2, v5}, Landroid/tw/john/TWUtil;->write(III)I

    goto/16 :goto_5

    .line 8
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget p1, p1, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    if-eq p1, v4, :cond_a

    .line 9
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    iget p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    const/16 v0, 0x20

    and-int/2addr p0, v0

    if-ne p0, v0, :cond_4

    goto :goto_1

    :cond_4
    move v2, v5

    :goto_1
    invoke-virtual {p1, v3, v5, v2}, Landroid/tw/john/TWUtil;->write(III)I

    goto :goto_5

    .line 10
    :cond_5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-boolean v0, p1, Lcom/eckom/xtlibrary/b/h/a;->il:Z

    if-eqz v0, :cond_8

    .line 11
    iput-boolean v2, p1, Lcom/eckom/xtlibrary/b/h/a;->il:Z

    .line 12
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_2
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_6

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 13
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/b/h/a;->il:Z

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/h/b/f;->k(Z)V

    goto :goto_2

    :cond_6
    move p1, v2

    .line 14
    :goto_3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v1, v0

    if-ge p1, v1, :cond_8

    .line 15
    aget-object v0, v0, p1

    const/4 v1, 0x0

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/h/a/a;->vl:Ljava/lang/String;

    .line 16
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_4
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_7

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/Map$Entry;

    .line 17
    invoke-interface {v1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v1, p1}, Lcom/eckom/xtlibrary/b/h/b/f;->r(I)V

    goto :goto_4

    :cond_7
    add-int/lit8 p1, p1, 0x1

    goto :goto_3

    .line 18
    :cond_8
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->bc()V

    .line 19
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 p1, 0x401

    invoke-virtual {p0, p1, v2, v2}, Landroid/tw/john/TWUtil;->write(III)I

    goto :goto_5

    .line 20
    :cond_9
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->cc()V

    :cond_a
    :goto_5
    return-void

    nop

    :pswitch_data_0
    .packed-switch 0x31
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
    .end packed-switch
.end method

.method public ca(I)V
    .locals 1

    .line 1
    iput p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mService:I

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const v0, 0x9e00

    invoke-virtual {p0, v0, p1}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method public cc()V
    .locals 5

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mService:I

    and-int/lit16 v0, v0, 0x8f

    const/4 v1, 0x1

    if-ne v0, v1, :cond_2

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget v2, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    add-int/2addr v2, v1

    .line 3
    iget v0, v0, Lcom/eckom/xtlibrary/b/h/a;->ql:I

    const/16 v3, 0x9

    const/4 v4, 0x0

    if-ne v0, v3, :cond_0

    if-le v2, v1, :cond_1

    goto :goto_0

    :cond_0
    const/4 v0, 0x2

    if-le v2, v0, :cond_1

    goto :goto_0

    :cond_1
    move v4, v2

    .line 4
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 v2, 0x401

    const/4 v3, 0x5

    invoke-virtual {v0, v2, v3, v4}, Landroid/tw/john/TWUtil;->write(III)I

    .line 5
    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/b/h/b/e;->w(Z)V

    :cond_2
    return-void
.end method

.method public dc()V
    .locals 3

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 v0, 0x401

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual {p0, v0, v1, v2}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public ec()V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/4 v0, 0x1

    const/16 v1, 0x401

    invoke-virtual {p0, v1, v0, v0}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public fc()V
    .locals 3

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 v0, 0x401

    const/4 v1, 0x2

    const/4 v2, 0x0

    invoke-virtual {p0, v0, v1, v2}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public hc()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/4 v1, 0x1

    const/16 v2, 0x401

    const/4 v3, 0x2

    invoke-virtual {v0, v2, v3, v1}, Landroid/tw/john/TWUtil;->write(III)I

    .line 2
    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/b/h/b/e;->w(Z)V

    return-void
.end method

.method public ma(I)V
    .locals 2

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mService:I

    and-int/lit16 v0, v0, 0x8f

    const/4 v1, 0x1

    if-ne v0, v1, :cond_0

    if-ltz p1, :cond_0

    const/4 v0, 0x2

    if-gt p1, v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 v0, 0x401

    const/4 v1, 0x5

    invoke-virtual {p0, v0, v1, p1}, Landroid/tw/john/TWUtil;->write(III)I

    :cond_0
    return-void
.end method

.method public na(I)V
    .locals 2

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    const/16 v0, 0x402

    const/16 v1, 0xff

    invoke-virtual {p0, v0, v1, p1}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public next()V
    .locals 6

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-boolean v1, v0, Lcom/eckom/xtlibrary/b/h/a;->il:Z

    if-eqz v1, :cond_0

    return-void

    .line 2
    :cond_0
    iget v1, v0, Lcom/eckom/xtlibrary/b/h/a;->ll:I

    const/4 v2, 0x1

    add-int/2addr v1, v2

    .line 3
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v3, v0

    if-le v1, v3, :cond_1

    .line 4
    array-length v1, v0

    :cond_1
    move v0, v1

    .line 5
    :goto_0
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v4, v3, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v5, v4

    if-ge v0, v5, :cond_3

    .line 6
    aget-object v4, v4, v0

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a/a;->tl:I

    if-eqz v4, :cond_2

    .line 7
    iget v3, v3, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-direct {p0, v0, v3}, Lcom/eckom/xtlibrary/b/h/b/e;->n(II)V

    goto :goto_1

    :cond_2
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 8
    :cond_3
    :goto_1
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v3, v3

    if-ne v0, v3, :cond_5

    const/4 v0, 0x0

    :goto_2
    if-ge v0, v1, :cond_5

    .line 9
    iget-object v3, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    iget-object v4, v3, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v4, v4, v0

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a/a;->tl:I

    if-eqz v4, :cond_4

    .line 10
    iget v1, v3, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    invoke-direct {p0, v0, v1}, Lcom/eckom/xtlibrary/b/h/b/e;->n(II)V

    goto :goto_3

    :cond_4
    add-int/lit8 v0, v0, 0x1

    goto :goto_2

    .line 11
    :cond_5
    :goto_3
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object v0

    const-string v1, "KED"

    invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_6

    .line 12
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->af()V

    .line 13
    :cond_6
    invoke-virtual {p0, v2}, Lcom/eckom/xtlibrary/b/h/b/e;->w(Z)V

    return-void
.end method

.method public onResume()V
    .locals 1

    const/4 v0, 0x1

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wg:Z

    .line 2
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/h/b/e;->w(Z)V

    .line 3
    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/h/b/e;->ca(I)V

    return-void
.end method

.method public w(Z)V
    .locals 1

    .line 1
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ii:Z

    if-eqz v0, :cond_0

    return-void

    :cond_0
    if-eqz p1, :cond_1

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->Zb()Z

    move-result p1

    if-nez p1, :cond_2

    const/4 p1, 0x1

    .line 3
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->da(I)V

    goto :goto_0

    :cond_1
    const/16 p1, 0x81

    .line 4
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->da(I)V

    :cond_2
    :goto_0
    return-void
.end method

.method public zb()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Fi:Lcom/eckom/xtlibrary/b/h/a;

    const/4 v1, 0x0

    iput-boolean v1, v0, Lcom/eckom/xtlibrary/b/h/a;->il:Z

    .line 2
    iget v2, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mService:I

    and-int/lit16 v2, v2, 0x8f

    const/4 v3, 0x1

    if-ne v2, v3, :cond_0

    return-void

    .line 3
    :cond_0
    iput v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Ci:I

    .line 4
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    .line 5
    iput v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->if:I

    .line 6
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->ql:I

    .line 7
    iput v1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mService:I

    .line 8
    iput v1, v0, Lcom/eckom/xtlibrary/b/h/a;->mSource:I

    const/4 v2, 0x0

    .line 9
    iput-object v2, v0, Lcom/eckom/xtlibrary/b/h/a;->pl:Ljava/lang/String;

    .line 10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->wh:Lcom/eckom/xtlibrary/b/h/d/b;

    if-eqz v0, :cond_1

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->yh:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->size()I

    move-result v0

    if-nez v0, :cond_1

    .line 12
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->Di:Z

    if-eqz v0, :cond_1

    .line 13
    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/b/h/b/e;->w(Z)V

    :cond_1
    return-void
.end method

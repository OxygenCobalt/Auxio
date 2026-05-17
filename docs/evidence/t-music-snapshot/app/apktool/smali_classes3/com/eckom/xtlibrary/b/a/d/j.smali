.class public Lcom/eckom/xtlibrary/b/a/d/j;
.super Lcom/eckom/xtlibrary/b/a/d/h;
.source "BuildInBTModel.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/a/a/b$b;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Lcom/eckom/xtlibrary/b/a/d/h;",
        "Lcom/eckom/xtlibrary/b/a/a/b$b;"
    }
.end annotation


# static fields
.field private static volatile Fh:Lcom/eckom/xtlibrary/b/a/d/j;


# instance fields
.field private Dh:Z

.field private ch:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Landroid/bluetooth/BluetoothDevice;",
            ">;"
        }
    .end annotation
.end field

.field private la:Lcom/eckom/xtlibrary/b/a/b/a;

.field private mContext:Landroid/content/Context;

.field public mHandler:Landroid/os/Handler;

.field private wh:Lcom/eckom/xtlibrary/b/a/b/c;

.field private yh:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Lcom/eckom/xtlibrary/b/a/d/g;",
            ">;"
        }
    .end annotation
.end field

.field private zh:Lcom/eckom/xtlibrary/b/a/h/d;


# direct methods
.method private constructor <init>()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/a/d/h;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    .line 3
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/a;->getInstance()Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    .line 4
    new-instance v0, Ljava/util/concurrent/ConcurrentHashMap;

    invoke-direct {v0}, Ljava/util/concurrent/ConcurrentHashMap;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->yh:Ljava/util/Map;

    .line 5
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->ch:Ljava/util/List;

    const/4 v0, 0x0

    .line 6
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->Dh:Z

    .line 7
    new-instance v0, Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/a/d/i;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/a/d/i;-><init>(Lcom/eckom/xtlibrary/b/a/d/j;)V

    invoke-direct {v0, v1}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->mHandler:Landroid/os/Handler;

    .line 8
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/c;->open()Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    .line 9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    if-eqz v0, :cond_0

    const-string v0, "BuildInBTModel"

    const-string v1, "BuildInBTModel: Model create "

    .line 10
    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 11
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/j;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/d/j;->mHandler:Landroid/os/Handler;

    invoke-virtual {v1, v0, v2}, Landroid/tw/john/TWUtil;->addHandler(Ljava/lang/String;Landroid/os/Handler;)V

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->wh:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v1, 0x10b

    const/16 v2, 0xff

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->write(II)I

    .line 13
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    return-void
.end method

.method public static getInstance()Lcom/eckom/xtlibrary/b/a/d/j;
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/a/d/j;->Fh:Lcom/eckom/xtlibrary/b/a/d/j;

    if-nez v0, :cond_1

    .line 2
    const-class v0, Lcom/eckom/xtlibrary/b/a/d/j;

    monitor-enter v0

    .line 3
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/a/d/j;->Fh:Lcom/eckom/xtlibrary/b/a/d/j;

    if-nez v1, :cond_0

    .line 4
    new-instance v1, Lcom/eckom/xtlibrary/b/a/d/j;

    invoke-direct {v1}, Lcom/eckom/xtlibrary/b/a/d/j;-><init>()V

    sput-object v1, Lcom/eckom/xtlibrary/b/a/d/j;->Fh:Lcom/eckom/xtlibrary/b/a/d/j;

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
    sget-object v0, Lcom/eckom/xtlibrary/b/a/d/j;->Fh:Lcom/eckom/xtlibrary/b/a/d/j;

    return-object v0
.end method


# virtual methods
.method public Aa(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public B(Z)V
    .locals 0

    return-void
.end method

.method public Ba(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->yh:Ljava/util/Map;

    invoke-interface {p0, p1}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method public Ca(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public a(Landroid/content/Context;)V
    .locals 1

    .line 1
    instance-of v0, p1, Landroid/app/Activity;

    .line 2
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/j;->mContext:Landroid/content/Context;

    .line 3
    iget-boolean p1, p0, Lcom/eckom/xtlibrary/b/a/d/j;->Dh:Z

    if-nez p1, :cond_0

    const/4 p1, 0x1

    .line 4
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/b/a/d/j;->Dh:Z

    .line 5
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/j;->zh:Lcom/eckom/xtlibrary/b/a/h/d;

    if-nez p1, :cond_1

    .line 6
    new-instance p1, Lcom/eckom/xtlibrary/b/a/h/d;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->mContext:Landroid/content/Context;

    invoke-direct {p1, v0, p0}, Lcom/eckom/xtlibrary/b/a/h/d;-><init>(Landroid/content/Context;Lcom/eckom/xtlibrary/b/a/d/h;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/j;->zh:Lcom/eckom/xtlibrary/b/a/h/d;

    .line 7
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/j;->mContext:Landroid/content/Context;

    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object p1

    sget v0, Lcom/eckom/xtlibrary/R$array;->enable_phone_call_record_version:I

    invoke-virtual {p1, v0}, Landroid/content/res/Resources;->getStringArray(I)[Ljava/lang/String;

    move-result-object p1

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->la:Lcom/eckom/xtlibrary/b/a/b/a;

    invoke-static {p1}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/b/a;->kh:Ljava/util/List;

    return-void
.end method

.method public a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/a/d/g;)V
    .locals 0

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/j;->yh:Ljava/util/Map;

    invoke-interface {p0, p1, p2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method public answer()V
    .locals 0

    return-void
.end method

.method public ba()V
    .locals 0

    return-void
.end method

.method public getCallState()I
    .locals 0

    const/4 p0, 0x0

    return p0
.end method

.method public mb()I
    .locals 0

    const/4 p0, 0x0

    return p0
.end method

.method public nb()V
    .locals 0

    return-void
.end method

.method public ob()V
    .locals 0

    return-void
.end method

.method public pb()V
    .locals 0

    return-void
.end method

.method public qb()V
    .locals 0

    return-void
.end method

.method public rb()V
    .locals 0

    return-void
.end method

.method public sb()V
    .locals 0

    return-void
.end method

.method public setDeviceName(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public t(I)V
    .locals 0

    return-void
.end method

.method public w(Z)V
    .locals 0

    return-void
.end method

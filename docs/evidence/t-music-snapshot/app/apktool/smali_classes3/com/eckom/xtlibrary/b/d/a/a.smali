.class public Lcom/eckom/xtlibrary/b/d/a/a;
.super Lcom/eckom/xtlibrary/b/e/a;
.source "LauncherModel.java"


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
.field private static volatile Gh:Lcom/eckom/xtlibrary/b/d/a/a;


# instance fields
.field private mContext:Landroid/content/Context;

.field private yh:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Lcom/eckom/xtlibrary/b/d/a/b;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method private constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/e/a;-><init>()V

    .line 2
    new-instance v0, Ljava/util/concurrent/ConcurrentHashMap;

    invoke-direct {v0}, Ljava/util/concurrent/ConcurrentHashMap;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/d/a/a;->yh:Ljava/util/Map;

    return-void
.end method

.method public static getInstance()Lcom/eckom/xtlibrary/b/d/a/a;
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/d/a/a;->Gh:Lcom/eckom/xtlibrary/b/d/a/a;

    if-nez v0, :cond_1

    .line 2
    const-class v0, Lcom/eckom/xtlibrary/b/d/a/a;

    monitor-enter v0

    .line 3
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/b/d/a/a;->Gh:Lcom/eckom/xtlibrary/b/d/a/a;

    if-nez v1, :cond_0

    .line 4
    new-instance v1, Lcom/eckom/xtlibrary/b/d/a/a;

    invoke-direct {v1}, Lcom/eckom/xtlibrary/b/d/a/a;-><init>()V

    sput-object v1, Lcom/eckom/xtlibrary/b/d/a/a;->Gh:Lcom/eckom/xtlibrary/b/d/a/a;

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
    sget-object v0, Lcom/eckom/xtlibrary/b/d/a/a;->Gh:Lcom/eckom/xtlibrary/b/d/a/a;

    return-object v0
.end method


# virtual methods
.method public Da(Ljava/lang/String;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/d/a/a;->yh:Ljava/util/Map;

    invoke-interface {p0, p1}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method public a(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/d/a/a;->mContext:Landroid/content/Context;

    return-void
.end method

.method public a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/d/a/b;)V
    .locals 0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/d/a/a;->yh:Ljava/util/Map;

    invoke-interface {p0, p1, p2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method public zb()V
    .locals 0

    return-void
.end method

.class public Lcom/eckom/xtlibrary/b/i/k;
.super Ljava/lang/Object;
.source "ThemeManager.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/b/i/k$b;,
        Lcom/eckom/xtlibrary/b/i/k$d;,
        Lcom/eckom/xtlibrary/b/i/k$c;,
        Lcom/eckom/xtlibrary/b/i/k$a;
    }
.end annotation


# instance fields
.field private Ul:Landroid/content/Context;

.field private Vl:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Lcom/eckom/xtlibrary/b/i/l;",
            ">;"
        }
    .end annotation
.end field

.field private Wl:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Ljava/lang/Object;",
            ">;"
        }
    .end annotation
.end field

.field private Xl:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/eckom/xtlibrary/b/i/c;",
            ">;"
        }
    .end annotation
.end field

.field private volatile Yl:Lcom/eckom/xtlibrary/b/i/l;


# direct methods
.method private constructor <init>()V
    .locals 1

    .line 2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 3
    new-instance v0, Ljava/util/concurrent/ConcurrentHashMap;

    invoke-direct {v0}, Ljava/util/concurrent/ConcurrentHashMap;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/i/k;->Vl:Ljava/util/Map;

    .line 4
    new-instance v0, Ljava/util/concurrent/CopyOnWriteArrayList;

    invoke-direct {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/i/k;->Wl:Ljava/util/List;

    .line 5
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/i/k;->Xl:Ljava/util/List;

    return-void
.end method

.method synthetic constructor <init>(Lcom/eckom/xtlibrary/b/i/i;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/i/k;-><init>()V

    return-void
.end method

.method private Gb(Ljava/lang/String;)Lcom/eckom/xtlibrary/b/i/l;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/k;->Vl:Ljava/util/Map;

    invoke-interface {p0, p1}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p0

    check-cast p0, Lcom/eckom/xtlibrary/b/i/l;

    return-object p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/i/k;)Ljava/util/List;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/k;->Xl:Ljava/util/List;

    return-object p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/i/k;Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;Z)V
    .locals 0

    .line 3
    invoke-direct {p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/i/k;->a(Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;Z)V

    return-void
.end method

.method private a(Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;Z)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/eckom/xtlibrary/b/i/c;",
            ">;",
            "Lcom/eckom/xtlibrary/b/i/m;",
            "Z)V"
        }
    .end annotation

    .line 27
    invoke-interface {p1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result p1

    if-eqz p1, :cond_0

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/i/c;

    .line 28
    invoke-interface {p1, p2, p3}, Lcom/eckom/xtlibrary/b/i/c;->a(Lcom/eckom/xtlibrary/b/i/m;Z)V

    goto :goto_0

    :cond_0
    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/i/k;Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;)Z
    .locals 0

    .line 2
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/i/k;->a(Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;)Z

    move-result p0

    return p0
.end method

.method private a(Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;)Z
    .locals 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/eckom/xtlibrary/b/i/c;",
            ">;",
            "Lcom/eckom/xtlibrary/b/i/m;",
            ")Z"
        }
    .end annotation

    const/4 p0, 0x0

    new-array v0, p0, [Ljava/lang/Object;

    const-string v1, "ThemeManager"

    const-string v2, "notifyThemeSwitching: start"

    .line 20
    invoke-static {v1, v2, v0}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    const/4 v0, 0x1

    .line 21
    :try_start_0
    invoke-interface {p1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :cond_0
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/eckom/xtlibrary/b/i/c;

    const-string v3, "notifyThemeSwitching: switch! %s"

    new-array v4, v0, [Ljava/lang/Object;

    aput-object v2, v4, p0

    .line 22
    invoke-static {v1, v3, v4}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    .line 23
    invoke-interface {v2, p2}, Lcom/eckom/xtlibrary/b/i/c;->b(Lcom/eckom/xtlibrary/b/i/m;)Z

    move-result v3

    if-nez v3, :cond_0

    const-string p1, "notifyThemeSwitching: switch fail! %s"

    new-array p2, v0, [Ljava/lang/Object;

    aput-object v2, p2, p0

    .line 24
    invoke-static {v1, p1, p2}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :cond_1
    move v0, p0

    goto :goto_0

    :catch_0
    move-exception p1

    .line 25
    invoke-virtual {p1}, Ljava/lang/Exception;->printStackTrace()V

    :goto_0
    new-array p0, p0, [Ljava/lang/Object;

    const-string p1, "notifyThemeSwitching: end"

    .line 26
    invoke-static {v1, p1, p0}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    return v0
.end method

.method private b(Ljava/io/File;)Lcom/eckom/xtlibrary/b/i/l;
    .locals 0

    .line 1
    invoke-virtual {p1}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object p1

    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/i/k;->Gb(Ljava/lang/String;)Lcom/eckom/xtlibrary/b/i/l;

    move-result-object p0

    return-object p0
.end method

.method private ef()V
    .locals 1

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/k;->Ul:Landroid/content/Context;

    if-eqz p0, :cond_0

    return-void

    .line 2
    :cond_0
    new-instance p0, Ljava/lang/IllegalStateException;

    const-string v0, "must be call init(Context ctx)."

    invoke-direct {p0, v0}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw p0
.end method

.method public static get()Lcom/eckom/xtlibrary/b/i/k;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/i/k$a;->INSTANCE:Lcom/eckom/xtlibrary/b/i/k;

    return-object v0
.end method


# virtual methods
.method public declared-synchronized Kc()Landroid/content/Context;
    .locals 1

    monitor-enter p0

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/i/k;->Yl:Lcom/eckom/xtlibrary/b/i/l;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/i/k;->Yl:Lcom/eckom/xtlibrary/b/i/l;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/i/l;->Mc()Landroid/content/Context;

    move-result-object v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    :goto_0
    monitor-exit p0

    return-object v0

    :catchall_0
    move-exception v0

    monitor-exit p0

    throw v0
.end method

.method public a(Ljava/io/File;)Lcom/eckom/xtlibrary/b/i/l;
    .locals 5

    .line 4
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/i/k;->ef()V

    const/4 v0, 0x1

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v2, 0x0

    aput-object p1, v1, v2

    const-string v3, "ThemeManager"

    const-string v4, "loadThemePlugin : %s"

    .line 5
    invoke-static {v3, v4, v1}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    const/4 v1, 0x0

    if-nez p1, :cond_0

    new-array p0, v2, [Ljava/lang/Object;

    const-string p1, "error : plugin is null."

    .line 6
    invoke-static {v3, p1, p0}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    return-object v1

    .line 7
    :cond_0
    invoke-virtual {p1}, Ljava/io/File;->exists()Z

    move-result v4

    if-nez v4, :cond_1

    new-array p0, v0, [Ljava/lang/Object;

    .line 8
    invoke-virtual {p1}, Ljava/io/File;->getAbsoluteFile()Ljava/io/File;

    move-result-object p1

    aput-object p1, p0, v2

    const-string p1, "plugin is not exists. path = %s"

    invoke-static {v3, p1, p0}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    return-object v1

    .line 9
    :cond_1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/i/k;->b(Ljava/io/File;)Lcom/eckom/xtlibrary/b/i/l;

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/i/k;->Ul:Landroid/content/Context;

    invoke-static {p0, v1, p1}, Lcom/eckom/xtlibrary/b/i/l;->a(Lcom/eckom/xtlibrary/b/i/k;Landroid/content/Context;Ljava/io/File;)Lcom/eckom/xtlibrary/b/i/l;

    move-result-object v1

    if-eqz v1, :cond_2

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/k;->Vl:Ljava/util/Map;

    invoke-virtual {p1}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-interface {p0, v4, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    :cond_2
    new-array p0, v0, [Ljava/lang/Object;

    .line 12
    invoke-virtual {p1}, Ljava/io/File;->getAbsoluteFile()Ljava/io/File;

    move-result-object p1

    aput-object p1, p0, v2

    const-string p1, "load theme plugin : %s"

    invoke-static {v3, p1, p0}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    .line 13
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0}, Ljava/lang/StringBuilder;-><init>()V

    const-string p1, "loadThemePlugin: themePlugin is null\uff1a"

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    if-nez v1, :cond_3

    goto :goto_0

    :cond_3
    move v0, v2

    :goto_0
    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v3, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-object v1
.end method

.method public a(Lcom/eckom/xtlibrary/b/i/c;)V
    .locals 2

    .line 17
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/i/k;->Xl:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    .line 18
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/i/k;->Xl:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    const/4 v0, 0x2

    new-array v0, v0, [Ljava/lang/Object;

    const/4 v1, 0x0

    aput-object p1, v0, v1

    const/4 p1, 0x1

    .line 19
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/k;->Xl:Ljava/util/List;

    invoke-interface {p0}, Ljava/util/List;->size()I

    move-result p0

    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p0

    aput-object p0, v0, p1

    const-string p0, "ThemeManager"

    const-string p1, "registerThemeSwitchStatus : %s, size : %s"

    invoke-static {p0, p1, v0}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    :cond_0
    return-void
.end method

.method public declared-synchronized a(Lcom/eckom/xtlibrary/b/i/l;)V
    .locals 3

    monitor-enter p0

    .line 14
    :try_start_0
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/k;->Yl:Lcom/eckom/xtlibrary/b/i/l;

    const-string v0, "ThemeManager"

    .line 15
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "updateCurThemePlugin: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v0, p1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 16
    monitor-exit p0

    return-void

    :catchall_0
    move-exception p1

    monitor-exit p0

    throw p1
.end method

.method public b(Lcom/eckom/xtlibrary/b/i/c;)V
    .locals 0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/k;->Xl:Ljava/util/List;

    invoke-interface {p0, p1}, Ljava/util/List;->remove(Ljava/lang/Object;)Z

    return-void
.end method

.method public e(Lcom/eckom/xtlibrary/b/i/m;)V
    .locals 1

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/i/j;

    invoke-direct {v0, p0, p1}, Lcom/eckom/xtlibrary/b/i/j;-><init>(Lcom/eckom/xtlibrary/b/i/k;Lcom/eckom/xtlibrary/b/i/m;)V

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/i/f;->runOnUiThread(Ljava/lang/Runnable;)V

    return-void
.end method

.method public init(Landroid/content/Context;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/k;->Ul:Landroid/content/Context;

    return-void
.end method

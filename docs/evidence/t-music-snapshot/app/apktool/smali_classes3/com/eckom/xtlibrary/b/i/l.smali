.class public Lcom/eckom/xtlibrary/b/i/l;
.super Ljava/lang/Object;
.source "ThemePlugin.java"


# instance fields
.field private Ul:Landroid/content/Context;

.field private Zl:Ljava/lang/String;

.field private _l:Lcom/eckom/xtlibrary/b/i/k;

.field private cm:Landroid/content/Context;

.field private final mLocation:Ljava/lang/String;

.field private mPackageInfo:Landroid/content/pm/PackageInfo;

.field private mResources:Landroid/content/res/Resources;


# direct methods
.method private constructor <init>(Lcom/eckom/xtlibrary/b/i/k;Landroid/content/Context;Ljava/io/File;)V
    .locals 2

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->_l:Lcom/eckom/xtlibrary/b/i/k;

    .line 3
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/i/l;->Ul:Landroid/content/Context;

    .line 4
    invoke-virtual {p3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->mLocation:Ljava/lang/String;

    .line 5
    new-instance p1, Lcom/eckom/xtlibrary/b/i/d;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/b/i/d;-><init>(Lcom/eckom/xtlibrary/b/i/l;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->cm:Landroid/content/Context;

    .line 6
    invoke-static {p2, p3}, Lcom/eckom/xtlibrary/b/i/l;->b(Landroid/content/Context;Ljava/io/File;)Landroid/content/res/Resources;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->mResources:Landroid/content/res/Resources;

    .line 7
    invoke-virtual {p2}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object p1

    invoke-virtual {p3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v0

    const/16 v1, 0x40

    invoke-virtual {p1, v0, v1}, Landroid/content/pm/PackageManager;->getPackageArchiveInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->mPackageInfo:Landroid/content/pm/PackageInfo;

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->mPackageInfo:Landroid/content/pm/PackageInfo;

    if-nez p1, :cond_0

    .line 9
    invoke-virtual {p2}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object p1

    invoke-virtual {p3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object p2

    const/4 p3, 0x1

    invoke-virtual {p1, p2, p3}, Landroid/content/pm/PackageManager;->getPackageArchiveInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->mPackageInfo:Landroid/content/pm/PackageInfo;

    .line 10
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->mPackageInfo:Landroid/content/pm/PackageInfo;

    iget-object p1, p1, Landroid/content/pm/PackageInfo;->packageName:Ljava/lang/String;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/l;->Zl:Ljava/lang/String;

    return-void
.end method

.method private static a(Landroid/content/Context;Ljava/io/File;)Landroid/content/res/AssetManager;
    .locals 3

    .line 3
    const-class p0, Landroid/content/res/AssetManager;

    invoke-virtual {p0}, Ljava/lang/Class;->newInstance()Ljava/lang/Object;

    move-result-object p0

    check-cast p0, Landroid/content/res/AssetManager;

    .line 4
    const-class v0, Landroid/content/res/AssetManager;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    invoke-virtual {p1}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object p1

    const/4 v2, 0x0

    aput-object p1, v1, v2

    const-string p1, "addAssetPath"

    invoke-static {v0, p0, p1, v1}, Lcom/eckom/xtlibrary/b/i/e;->a(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;

    return-object p0
.end method

.method public static a(Lcom/eckom/xtlibrary/b/i/k;Landroid/content/Context;Ljava/io/File;)Lcom/eckom/xtlibrary/b/i/l;
    .locals 1

    .line 1
    :try_start_0
    new-instance v0, Lcom/eckom/xtlibrary/b/i/l;

    invoke-direct {v0, p0, p1, p2}, Lcom/eckom/xtlibrary/b/i/l;-><init>(Lcom/eckom/xtlibrary/b/i/k;Landroid/content/Context;Ljava/io/File;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 2
    invoke-virtual {p0}, Ljava/lang/Exception;->printStackTrace()V

    const/4 v0, 0x0

    :goto_0
    return-object v0
.end method

.method private static b(Landroid/content/Context;Ljava/io/File;)Landroid/content/res/Resources;
    .locals 2

    .line 1
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    .line 2
    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/i/l;->a(Landroid/content/Context;Ljava/io/File;)Landroid/content/res/AssetManager;

    move-result-object p0

    .line 3
    new-instance p1, Landroid/content/res/Resources;

    invoke-virtual {v0}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v1

    invoke-virtual {v0}, Landroid/content/res/Resources;->getConfiguration()Landroid/content/res/Configuration;

    move-result-object v0

    invoke-direct {p1, p0, v1, v0}, Landroid/content/res/Resources;-><init>(Landroid/content/res/AssetManager;Landroid/util/DisplayMetrics;Landroid/content/res/Configuration;)V

    return-object p1
.end method


# virtual methods
.method public Lc()Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/l;->Ul:Landroid/content/Context;

    return-object p0
.end method

.method public Mc()Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/l;->cm:Landroid/content/Context;

    return-object p0
.end method

.method public getApplicationInfo()Landroid/content/pm/ApplicationInfo;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/l;->mPackageInfo:Landroid/content/pm/PackageInfo;

    iget-object p0, p0, Landroid/content/pm/PackageInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    return-object p0
.end method

.method public getAssets()Landroid/content/res/AssetManager;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/l;->getResources()Landroid/content/res/Resources;

    move-result-object p0

    invoke-virtual {p0}, Landroid/content/res/Resources;->getAssets()Landroid/content/res/AssetManager;

    move-result-object p0

    return-object p0
.end method

.method public getCodePath()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/l;->mPackageInfo:Landroid/content/pm/PackageInfo;

    iget-object p0, p0, Landroid/content/pm/PackageInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    iget-object p0, p0, Landroid/content/pm/ApplicationInfo;->sourceDir:Ljava/lang/String;

    return-object p0
.end method

.method public getPackageName()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/l;->Zl:Ljava/lang/String;

    return-object p0
.end method

.method public getPackageResourcePath()Ljava/lang/String;
    .locals 2

    .line 1
    invoke-static {}, Landroid/os/Process;->myUid()I

    move-result v0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/l;->mPackageInfo:Landroid/content/pm/PackageInfo;

    iget-object p0, p0, Landroid/content/pm/PackageInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    .line 3
    iget v1, p0, Landroid/content/pm/ApplicationInfo;->uid:I

    if-ne v1, v0, :cond_0

    iget-object p0, p0, Landroid/content/pm/ApplicationInfo;->sourceDir:Ljava/lang/String;

    goto :goto_0

    :cond_0
    iget-object p0, p0, Landroid/content/pm/ApplicationInfo;->publicSourceDir:Ljava/lang/String;

    :goto_0
    return-object p0
.end method

.method public getResources()Landroid/content/res/Resources;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/l;->mResources:Landroid/content/res/Resources;

    return-object p0
.end method

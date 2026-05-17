.class Lcom/eckom/xtlibrary/b/i/d;
.super Landroid/content/ContextWrapper;
.source "PluginContext.java"


# instance fields
.field private final Mc:Lcom/eckom/xtlibrary/b/i/l;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/i/l;)V
    .locals 1

    .line 1
    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/l;->Lc()Landroid/content/Context;

    move-result-object v0

    invoke-direct {p0, v0}, Landroid/content/ContextWrapper;-><init>(Landroid/content/Context;)V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/d;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    return-void
.end method


# virtual methods
.method public getApplicationInfo()Landroid/content/pm/ApplicationInfo;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/d;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/l;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    move-result-object p0

    return-object p0
.end method

.method public getAssets()Landroid/content/res/AssetManager;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/d;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/l;->getAssets()Landroid/content/res/AssetManager;

    move-result-object p0

    return-object p0
.end method

.method public getPackageCodePath()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/d;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/l;->getCodePath()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public getPackageName()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/d;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/l;->getPackageName()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public getPackageResourcePath()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/d;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/l;->getPackageResourcePath()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method public getResources()Landroid/content/res/Resources;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/d;->Mc:Lcom/eckom/xtlibrary/b/i/l;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/l;->getResources()Landroid/content/res/Resources;

    move-result-object p0

    return-object p0
.end method

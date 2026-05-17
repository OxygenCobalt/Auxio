.class public abstract Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.super Lcom/eckom/xtlibrary/twproject/activity/BaseActivity;
.source "XTActivity.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/i/c;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Lcom/eckom/xtlibrary/twproject/activity/BaseActivity;",
        "Lcom/eckom/xtlibrary/b/i/c;"
    }
.end annotation


# static fields
.field static final DEFAULT_FILTER:Landroid/support/v7/graphics/Palette$Filter;


# instance fields
.field private Ya:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field public Za:Z

.field protected db:Lcom/eckom/xtlibrary/b/i/g;

.field protected eb:Landroid/content/Context;

.field private fb:Z

.field private gb:Ljava/lang/String;

.field private hb:Ljava/lang/String;

.field public ib:Ljava/lang/String;

.field private jb:Ljava/lang/String;

.field private kb:Ljava/lang/String;

.field private lb:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/eckom/xtlibrary/twproject/activity/XTActivity<",
            "TP;>.a;"
        }
    .end annotation
.end field

.field private mHandler:Landroid/os/Handler;

.field public mPresenter:Lcom/eckom/xtlibrary/b/g/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "TP;"
        }
    .end annotation
.end field

.field private mb:Z

.field private nb:Z

.field public ob:Z

.field public pb:Ljava/lang/String;

.field private qb:Ljava/lang/String;

.field private rb:Ljava/lang/String;

.field private sb:Landroid/app/AlertDialog;

.field ub:Z

.field public vb:Landroid/content/BroadcastReceiver;

.field private wb:Landroid/support/v7/graphics/Palette$PaletteAsyncListener;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/twproject/activity/c;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/twproject/activity/c;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->DEFAULT_FILTER:Landroid/support/v7/graphics/Palette$Filter;

    return-void
.end method

.method public constructor <init>()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseActivity;-><init>()V

    .line 2
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ya:Ljava/util/ArrayList;

    const/4 v0, 0x0

    .line 3
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Za:Z

    .line 4
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->fb:Z

    const/4 v1, 0x1

    .line 5
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->nb:Z

    .line 6
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->ub:Z

    .line 7
    new-instance v0, Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/twproject/activity/a;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/activity/a;-><init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)V

    invoke-direct {v0, v1}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mHandler:Landroid/os/Handler;

    .line 8
    new-instance v0, Lcom/eckom/xtlibrary/twproject/activity/b;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/twproject/activity/b;-><init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->vb:Landroid/content/BroadcastReceiver;

    .line 9
    new-instance v0, Lcom/eckom/xtlibrary/twproject/activity/d;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/twproject/activity/d;-><init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->wb:Landroid/support/v7/graphics/Palette$PaletteAsyncListener;

    return-void
.end method

.method private I(Z)V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "setDarkStatusBar:dark:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "XTActivity"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    invoke-virtual {p0}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object p0

    if-nez p0, :cond_0

    return-void

    .line 3
    :cond_0
    invoke-virtual {p0}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    move-result-object p0

    if-nez p0, :cond_1

    return-void

    .line 4
    :cond_1
    invoke-virtual {p0}, Landroid/view/View;->getSystemUiVisibility()I

    move-result v0

    if-eqz p1, :cond_2

    and-int/lit16 p1, v0, -0x2001

    goto :goto_0

    :cond_2
    or-int/lit16 p1, v0, 0x2000

    .line 5
    :goto_0
    invoke-virtual {p0, p1}, Landroid/view/View;->setSystemUiVisibility(I)V

    const-string p0, "updateStatusBarLightDark:end"

    .line 6
    invoke-static {v1, p0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

.method private a(Landroid/view/View;Landroid/graphics/Rect;F)Landroid/graphics/Bitmap;
    .locals 3

    .line 5
    invoke-virtual {p2}, Landroid/graphics/Rect;->width()I

    move-result p0

    int-to-float p0, p0

    mul-float/2addr p0, p3

    invoke-static {p0}, Ljava/lang/Math;->round(F)I

    move-result p0

    .line 6
    invoke-virtual {p2}, Landroid/graphics/Rect;->height()I

    move-result v0

    int-to-float v0, v0

    mul-float/2addr v0, p3

    invoke-static {v0}, Ljava/lang/Math;->round(F)I

    move-result v0

    .line 7
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    move-result v1

    if-lez v1, :cond_0

    invoke-virtual {p1}, Landroid/view/View;->getHeight()I

    move-result v1

    if-lez v1, :cond_0

    if-lez p0, :cond_0

    if-lez v0, :cond_0

    .line 8
    iget v1, p2, Landroid/graphics/Rect;->left:I

    neg-int v1, v1

    int-to-float v1, v1

    mul-float/2addr v1, p3

    .line 9
    iget p2, p2, Landroid/graphics/Rect;->top:I

    neg-int p2, p2

    int-to-float p2, p2

    mul-float/2addr p2, p3

    .line 10
    sget-object v2, Landroid/graphics/Bitmap$Config;->ARGB_4444:Landroid/graphics/Bitmap$Config;

    invoke-static {p0, v0, v2}, Landroid/graphics/Bitmap;->createBitmap(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;

    move-result-object p0

    .line 11
    new-instance v0, Landroid/graphics/Canvas;

    invoke-direct {v0, p0}, Landroid/graphics/Canvas;-><init>(Landroid/graphics/Bitmap;)V

    .line 12
    new-instance v2, Landroid/graphics/Matrix;

    invoke-direct {v2}, Landroid/graphics/Matrix;-><init>()V

    .line 13
    invoke-virtual {v2, p3, p3}, Landroid/graphics/Matrix;->preScale(FF)Z

    .line 14
    invoke-virtual {v2, v1, p2}, Landroid/graphics/Matrix;->postTranslate(FF)Z

    .line 15
    invoke-virtual {v0, v2}, Landroid/graphics/Canvas;->setMatrix(Landroid/graphics/Matrix;)V

    .line 16
    invoke-virtual {p1, v0}, Landroid/view/View;->draw(Landroid/graphics/Canvas;)V

    return-object p0

    .line 17
    :cond_0
    new-instance p0, Ljava/lang/IllegalArgumentException;

    const-string p1, "No screen available (width or height = 0)"

    invoke-direct {p0, p1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Ljava/lang/String;)Ljava/lang/String;
    .locals 0

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->qb:Ljava/lang/String;

    return-object p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Z
    .locals 0

    .line 3
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->nb:Z

    return p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)Z
    .locals 0

    .line 1
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->fb:Z

    return p1
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Ljava/lang/String;
    .locals 0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->jb:Ljava/lang/String;

    return-object p0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)Z
    .locals 0

    .line 1
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->nb:Z

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/app/AlertDialog;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->sb:Landroid/app/AlertDialog;

    return-object p0
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->I(Z)V

    return-void
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/os/Handler;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mHandler:Landroid/os/Handler;

    return-object p0
.end method

.method private oe()V
    .locals 2
    .annotation build Landroid/annotation/TargetApi;
        value = 0x18
    .end annotation

    .line 1
    :try_start_0
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x1e

    if-lt v0, v1, :cond_0

    .line 2
    invoke-virtual {p0}, Landroid/app/Activity;->isInMultiWindowMode()Z

    move-result v0

    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Za:Z

    goto :goto_1

    .line 3
    :cond_0
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x18

    if-lt v0, v1, :cond_2

    .line 4
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    invoke-interface {v0}, Landroid/view/IWindowManager;->getDockedStackSide()I

    move-result v0

    if-lez v0, :cond_1

    const/4 v0, 0x1

    goto :goto_0

    :cond_1
    const/4 v0, 0x0

    :goto_0
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Za:Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    :catch_0
    move-exception p0

    .line 5
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "getMultiWindowMode:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string v0, "XTActivity"

    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_2
    :goto_1
    return-void
.end method


# virtual methods
.method protected Aa()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    if-nez v0, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->za()Lcom/eckom/xtlibrary/b/g/a;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    .line 3
    instance-of v0, p0, Lcom/eckom/xtlibrary/b/l/a;

    if-eqz v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/l/a;

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/g/a;->a(Lcom/eckom/xtlibrary/b/l/a;)V

    :cond_0
    return-void
.end method

.method protected Ea()V
    .locals 0

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseActivity;->Ea()V

    return-void
.end method

.method public Ga()V
    .locals 4

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->_c()Z

    move-result v0

    if-nez v0, :cond_1

    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Uc()Z

    move-result v0

    if-eqz v0, :cond_0

    goto :goto_0

    :cond_0
    const-string v0, "/system"

    goto :goto_1

    :cond_1
    :goto_0
    const-string v0, "/system_tw"

    .line 2
    :goto_1
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v2, "/etc/theme/theme_config.json"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    sput-object v1, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    if-eqz v1, :cond_2

    .line 4
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v3, "/etc/theme/default/Launcher/"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/i/g;->Wa(Ljava/lang/String;)V

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v3, "/etc/theme/default/Sub/"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/i/g;->Xa(Ljava/lang/String;)V

    .line 6
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v3, "/etc/theme/day/Launcher/"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/i/g;->Ya(Ljava/lang/String;)V

    .line 7
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v3, "/etc/theme/day/Sub/"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/i/g;->Za(Ljava/lang/String;)V

    .line 8
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v3, "/etc/theme/night/Launcher/"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/b/i/g;->bb(Ljava/lang/String;)V

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v0, "/etc/theme/night/Sub/"

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/b/i/g;->eb(Ljava/lang/String;)V

    :cond_2
    return-void
.end method

.method public abstract Ha()Ljava/lang/String;
.end method

.method public abstract Ia()Ljava/lang/String;
.end method

.method public abstract Ja()Ljava/lang/String;
.end method

.method public abstract Ka()Lcom/eckom/xtlibrary/b/i/m;
.end method

.method public La()V
    .locals 3

    const-string v0, "persist.tw.theme"

    const/4 v1, 0x0

    .line 1
    invoke-static {v0, v1}, Landroid/os/SystemProperties;->getBoolean(Ljava/lang/String;Z)Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 2
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v0

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/i/k;->a(Lcom/eckom/xtlibrary/b/i/c;)V

    .line 3
    new-instance v0, Landroid/content/IntentFilter;

    invoke-direct {v0}, Landroid/content/IntentFilter;-><init>()V

    const-string v1, "notify_theme_change"

    .line 4
    invoke-virtual {v0, v1}, Landroid/content/IntentFilter;->addAction(Ljava/lang/String;)V

    const-string v1, "XTActivity"

    const-string v2, "initThemeBroadCast"

    .line 5
    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 6
    invoke-virtual {p0}, Landroid/app/Activity;->getApplicationContext()Landroid/content/Context;

    move-result-object v1

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->vb:Landroid/content/BroadcastReceiver;

    invoke-virtual {v1, p0, v0}, Landroid/content/Context;->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;

    return-void
.end method

.method public M()I
    .locals 0

    const/4 p0, 0x0

    return p0
.end method

.method public Ma()V
    .locals 6

    const-string v0, "persist.tw.theme"

    const/4 v1, 0x0

    .line 1
    invoke-static {v0, v1}, Landroid/os/SystemProperties;->getBoolean(Ljava/lang/String;Z)Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 2
    :cond_0
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ha()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    return-void

    .line 3
    :cond_1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ja()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->hb:Ljava/lang/String;

    .line 4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ia()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->gb:Ljava/lang/String;

    .line 5
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "initThemePlugin:themeApkPath:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->hb:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, " themeApkPackage:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->gb:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "XTActivity"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ka()Lcom/eckom/xtlibrary/b/i/m;

    move-result-object v0

    if-nez v0, :cond_2

    return-void

    .line 7
    :cond_2
    iget-boolean v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->fb:Z

    if-eqz v2, :cond_3

    return-void

    .line 8
    :cond_3
    new-instance v2, Lcom/eckom/xtlibrary/b/i/g;

    invoke-direct {v2}, Lcom/eckom/xtlibrary/b/i/g;-><init>()V

    iput-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    .line 9
    new-instance v2, Ljava/io/File;

    sget-object v3, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-direct {v2, v3}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 10
    invoke-virtual {v2}, Ljava/io/File;->exists()Z

    move-result v3

    if-eqz v3, :cond_4

    invoke-virtual {v2}, Ljava/io/File;->canRead()Z

    move-result v3

    if-eqz v3, :cond_4

    invoke-virtual {v2}, Ljava/io/File;->length()J

    move-result-wide v2

    const-wide/16 v4, 0x0

    cmp-long v2, v2, v4

    if-nez v2, :cond_5

    .line 11
    :cond_4
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ga()V

    .line 12
    :cond_5
    iget v2, v0, Lcom/eckom/xtlibrary/b/i/m;->em:I

    const/4 v3, 0x1

    if-ne v2, v3, :cond_6

    .line 13
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ha()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v2, v4}, Lcom/eckom/xtlibrary/b/i/g;->_a(Ljava/lang/String;)V

    .line 14
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    sget-object v4, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-static {v2, v4}, Lcom/eckom/xtlibrary/b/i/n;->a(Lcom/eckom/xtlibrary/b/i/g;Ljava/lang/String;)V

    .line 15
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "initThemePlugin1: "

    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 16
    new-instance v1, Ljava/io/File;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {v2}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->exists()Z

    move-result v1

    if-eqz v1, :cond_7

    .line 17
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object v1

    invoke-static {v1, v0}, Lcom/eckom/xtlibrary/b/i/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/i/m;)Lcom/eckom/xtlibrary/b/i/m;

    .line 18
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v1

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/i/k;->e(Lcom/eckom/xtlibrary/b/i/m;)V

    .line 19
    iput-boolean v3, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->fb:Z

    goto :goto_0

    .line 20
    :cond_6
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ha()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v2, v4}, Lcom/eckom/xtlibrary/b/i/g;->fb(Ljava/lang/String;)V

    .line 21
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    sget-object v4, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-static {v2, v4}, Lcom/eckom/xtlibrary/b/i/n;->a(Lcom/eckom/xtlibrary/b/i/g;Ljava/lang/String;)V

    .line 22
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "initThemePlugin2: "

    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 23
    new-instance v1, Ljava/io/File;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {v2}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->exists()Z

    move-result v1

    if-eqz v1, :cond_7

    .line 24
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object v1

    invoke-static {v1, v0}, Lcom/eckom/xtlibrary/b/i/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/i/m;)Lcom/eckom/xtlibrary/b/i/m;

    .line 25
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v1

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/i/k;->e(Lcom/eckom/xtlibrary/b/i/m;)V

    .line 26
    iput-boolean v3, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->fb:Z

    :cond_7
    :goto_0
    return-void
.end method

.method public Na()V
    .locals 7

    const-string v0, "XTActivity"

    const-string v1, "updateStatusBarLightDark:start"

    .line 1
    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    invoke-virtual {p0}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object v1

    if-nez v1, :cond_0

    return-void

    .line 3
    :cond_0
    invoke-virtual {v1}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    move-result-object v1

    if-nez v1, :cond_1

    return-void

    .line 4
    :cond_1
    invoke-static {p0}, Lcom/eckom/xtlibrary/b/j/p;->b(Landroid/content/Context;)[I

    move-result-object v2

    const/4 v3, 0x0

    aget v2, v2, v3

    .line 5
    invoke-static {p0}, Lcom/eckom/xtlibrary/b/j/p;->c(Landroid/content/Context;)I

    move-result v4

    .line 6
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "updateStatusBarLightDark:screenWidth:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v6, " statusBarHeight:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v0, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 7
    new-instance v0, Landroid/graphics/Rect;

    invoke-direct {v0, v3, v3, v2, v4}, Landroid/graphics/Rect;-><init>(IIII)V

    const/high16 v2, 0x3f800000    # 1.0f

    invoke-direct {p0, v1, v0, v2}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->a(Landroid/view/View;Landroid/graphics/Rect;F)Landroid/graphics/Bitmap;

    move-result-object v0

    .line 8
    invoke-static {v0}, Landroid/support/v7/graphics/Palette;->from(Landroid/graphics/Bitmap;)Landroid/support/v7/graphics/Palette$Builder;

    move-result-object v0

    .line 9
    invoke-virtual {v0}, Landroid/support/v7/graphics/Palette$Builder;->clearFilters()Landroid/support/v7/graphics/Palette$Builder;

    move-result-object v0

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->wb:Landroid/support/v7/graphics/Palette$PaletteAsyncListener;

    invoke-virtual {v0, p0}, Landroid/support/v7/graphics/Palette$Builder;->generate(Landroid/support/v7/graphics/Palette$PaletteAsyncListener;)Landroid/os/AsyncTask;

    return-void
.end method

.method public V()I
    .locals 0

    const/4 p0, 0x0

    return p0
.end method

.method public a(Lcom/eckom/xtlibrary/b/i/m;)V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/i/m;Z)V
    .locals 0

    .line 4
    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/m;->Nc()Lcom/eckom/xtlibrary/b/i/l;

    move-result-object p1

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/l;->Mc()Landroid/content/Context;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->eb:Landroid/content/Context;

    return-void
.end method

.method public b(Lcom/eckom/xtlibrary/b/i/m;)Z
    .locals 0

    const/4 p0, 0x0

    return p0
.end method

.method public abstract c(Lcom/eckom/xtlibrary/b/i/m;)V
.end method

.method public abstract d(Lcom/eckom/xtlibrary/b/i/m;)V
.end method

.method public ia()I
    .locals 0

    const/4 p0, 0x0

    return p0
.end method

.method public ma(Ljava/lang/String;)V
    .locals 4

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->jb:Ljava/lang/String;

    .line 2
    sget-object v0, Landroid/os/Build;->MODEL:Ljava/lang/String;

    const/4 v1, 0x1

    .line 3
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mb:Z

    .line 4
    invoke-virtual {p0}, Landroid/support/v7/app/AppCompatActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2}, Landroid/content/res/Resources;->getConfiguration()Landroid/content/res/Configuration;

    move-result-object v2

    iget-object v2, v2, Landroid/content/res/Configuration;->locale:Ljava/util/Locale;

    .line 5
    invoke-virtual {v2}, Ljava/util/Locale;->getLanguage()Ljava/lang/String;

    move-result-object v2

    const-string v3, "zh"

    .line 6
    invoke-static {v2, v3}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v2

    if-eqz v2, :cond_0

    const-string v2, "UI \u672a\u6fc0\u6d3b\uff0c\u8bf7\u6fc0\u6d3b\uff01\uff01\uff01"

    .line 7
    iput-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->kb:Ljava/lang/String;

    const-string v2, "\u8f6f\u4ef6\u6fc0\u6d3b"

    .line 8
    iput-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->rb:Ljava/lang/String;

    goto :goto_0

    :cond_0
    const-string v2, "UI is not activated, please activate it !!!"

    .line 9
    iput-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->kb:Ljava/lang/String;

    const-string v2, "Software activation"

    .line 10
    iput-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->rb:Ljava/lang/String;

    .line 11
    :goto_0
    new-instance v2, Landroid/app/AlertDialog$Builder;

    invoke-direct {v2, p0}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->rb:Ljava/lang/String;

    .line 12
    invoke-virtual {v2, v3}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    move-result-object v2

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->kb:Ljava/lang/String;

    .line 13
    invoke-virtual {v2, v3}, Landroid/app/AlertDialog$Builder;->setMessage(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    move-result-object v2

    .line 14
    invoke-virtual {v2}, Landroid/app/AlertDialog$Builder;->create()Landroid/app/AlertDialog;

    move-result-object v2

    iput-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->sb:Landroid/app/AlertDialog;

    .line 15
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->sb:Landroid/app/AlertDialog;

    invoke-virtual {v2}, Landroid/app/AlertDialog;->getWindow()Landroid/view/Window;

    move-result-object v2

    const/16 v3, 0x50

    invoke-virtual {v2, v3}, Landroid/view/Window;->setGravity(I)V

    .line 16
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->lb:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    if-nez v2, :cond_1

    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object v2

    const-string v3, "MOHAWK"

    invoke-virtual {v2, v3}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v2

    if-nez v2, :cond_1

    const-string v2, "ro.tw.uiCheck"

    .line 17
    invoke-static {v2, v1}, Landroid/os/SystemProperties;->getBoolean(Ljava/lang/String;Z)Z

    move-result v1

    if-eqz v1, :cond_1

    .line 18
    invoke-static {p1, v0}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result p1

    if-nez p1, :cond_1

    .line 19
    new-instance p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    invoke-direct {p1, p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;-><init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)V

    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->lb:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    .line 20
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->lb:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    invoke-virtual {p0}, Ljava/lang/Thread;->start()V

    :cond_1
    return-void
.end method

.method public na(Ljava/lang/String;)Z
    .locals 7

    const-string v0, "UIAuthorizationCheck->getAuthorizationStation:"

    const-string v1, "HYH"

    const/4 v2, 0x0

    const/4 v3, 0x0

    .line 1
    :try_start_0
    new-instance v4, Ljava/io/File;

    const-string v5, "/twdataconfig"

    invoke-direct {v4, v5}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v4}, Ljava/io/File;->exists()Z

    move-result v4

    if-eqz v4, :cond_0

    .line 2
    new-instance v4, Ljava/io/File;

    const-string v5, "/twdataconfig/UIAuthorization"

    invoke-direct {v4, v5}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    goto :goto_0

    .line 3
    :cond_0
    new-instance v4, Ljava/io/File;

    const-string v5, "/sdcard/.Tcfg/UIAuthorization"

    invoke-direct {v4, v5}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 4
    :goto_0
    new-instance v5, Ljava/io/BufferedReader;

    new-instance v6, Ljava/io/FileReader;

    invoke-direct {v6, v4}, Ljava/io/FileReader;-><init>(Ljava/io/File;)V

    invoke-direct {v5, v6}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_3
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 5
    :try_start_1
    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ya:Ljava/util/ArrayList;

    invoke-virtual {v3}, Ljava/util/ArrayList;->clear()V

    .line 6
    :goto_1
    invoke-virtual {v5}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v3

    if-eqz v3, :cond_1

    .line 7
    iget-object v6, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ya:Ljava/util/ArrayList;

    invoke-virtual {v6, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_1

    .line 8
    :cond_1
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 9
    invoke-virtual {v4}, Ljava/io/File;->canRead()Z

    move-result v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v4, ","

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ya:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    move-result v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    .line 10
    invoke-static {v1, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 11
    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ya:Ljava/util/ArrayList;

    invoke-virtual {v3}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_2
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_3

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/lang/String;

    .line 12
    invoke-static {v4}, Lcom/eckom/xtlibrary/b/j/n;->decrypt(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    .line 13
    iget-object v6, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->qb:Ljava/lang/String;

    invoke-static {v4, p1, v6}, Lcom/eckom/xtlibrary/b/j/n;->h(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v4
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_2
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    if-eqz v4, :cond_2

    const/4 p0, 0x1

    .line 14
    :try_start_2
    invoke-virtual {v5}, Ljava/io/BufferedReader;->close()V
    :try_end_2
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_2

    :catch_0
    move-exception p1

    .line 15
    invoke-virtual {p1}, Ljava/io/IOException;->printStackTrace()V

    :goto_2
    return p0

    .line 16
    :cond_3
    :try_start_3
    invoke-virtual {v5}, Ljava/io/BufferedReader;->close()V
    :try_end_3
    .catch Ljava/io/IOException; {:try_start_3 .. :try_end_3} :catch_1

    goto :goto_3

    :catch_1
    move-exception p0

    .line 17
    invoke-virtual {p0}, Ljava/io/IOException;->printStackTrace()V

    :goto_3
    return v2

    :catchall_0
    move-exception p0

    goto :goto_6

    :catch_2
    move-exception p0

    move-object v3, v5

    goto :goto_4

    :catchall_1
    move-exception p0

    move-object v5, v3

    goto :goto_6

    :catch_3
    move-exception p0

    .line 18
    :goto_4
    :try_start_4
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 19
    invoke-virtual {p0}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    .line 20
    invoke-static {v1, p1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 21
    invoke-virtual {p0}, Ljava/lang/Exception;->printStackTrace()V
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_1

    if-eqz v3, :cond_4

    .line 22
    :try_start_5
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V
    :try_end_5
    .catch Ljava/io/IOException; {:try_start_5 .. :try_end_5} :catch_4

    goto :goto_5

    :catch_4
    move-exception p0

    .line 23
    invoke-virtual {p0}, Ljava/io/IOException;->printStackTrace()V

    :cond_4
    :goto_5
    return v2

    :goto_6
    if-eqz v5, :cond_5

    .line 24
    :try_start_6
    invoke-virtual {v5}, Ljava/io/BufferedReader;->close()V
    :try_end_6
    .catch Ljava/io/IOException; {:try_start_6 .. :try_end_6} :catch_5

    goto :goto_7

    :catch_5
    move-exception p1

    .line 25
    invoke-virtual {p1}, Ljava/io/IOException;->printStackTrace()V

    .line 26
    :cond_5
    :goto_7
    throw p0
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->oe()V

    const-string v0, "persist.sys.tw.current.theme.type"

    const-string v1, "day"

    .line 2
    invoke-static {v0, v1}, Landroid/os/SystemProperties;->get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->ib:Ljava/lang/String;

    const-string v0, "persist.sys.tw.theme_change_wallpaperOrPackage"

    const/4 v1, 0x0

    .line 3
    invoke-static {v0, v1}, Landroid/os/SystemProperties;->getBoolean(Ljava/lang/String;Z)Z

    move-result v0

    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->ob:Z

    const-string v0, "persist.sys.tw.theme_change_colorOrPath"

    .line 4
    invoke-static {v0}, Landroid/os/SystemProperties;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->pb:Ljava/lang/String;

    .line 5
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/BaseActivity;->onCreate(Landroid/os/Bundle;)V

    .line 6
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->La()V

    return-void
.end method

.method protected onDestroy()V
    .locals 3

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v0

    invoke-virtual {v0, p0}, Lcom/eckom/xtlibrary/b/i/k;->b(Lcom/eckom/xtlibrary/b/i/c;)V

    .line 2
    :try_start_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->vb:Landroid/content/BroadcastReceiver;

    if-eqz v0, :cond_0

    .line 3
    invoke-virtual {p0}, Landroid/app/Activity;->getApplicationContext()Landroid/content/Context;

    move-result-object v0

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->vb:Landroid/content/BroadcastReceiver;

    invoke-virtual {v0, v1}, Landroid/content/Context;->unregisterReceiver(Landroid/content/BroadcastReceiver;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    .line 4
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "onDestroy:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "XTActivity"

    invoke-static {v1, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 5
    :cond_0
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    const/4 v1, 0x0

    if-eqz v0, :cond_1

    .line 6
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/g/a;->delete()V

    .line 7
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    .line 8
    :cond_1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseActivity;->onDestroy()V

    .line 9
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v1}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    return-void
.end method

.method protected onPause()V
    .locals 2

    .line 1
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onPause()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->lb:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    const/4 v1, 0x0

    if-eqz v0, :cond_0

    .line 3
    invoke-virtual {v0}, Ljava/lang/Thread;->interrupt()V

    .line 4
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->lb:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    .line 5
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->sb:Landroid/app/AlertDialog;

    if-eqz v0, :cond_1

    .line 6
    invoke-virtual {v0}, Landroid/app/AlertDialog;->dismiss()V

    .line 7
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->sb:Landroid/app/AlertDialog;

    :cond_1
    return-void
.end method

.method protected onResume()V
    .locals 2

    .line 1
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onResume()V

    .line 2
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "onResume: isCheckingUI="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mb:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v1, ","

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->nb:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "HYH"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 3
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mb:Z

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->nb:Z

    if-nez v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->jb:Ljava/lang/String;

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->ma(Ljava/lang/String;)V

    :cond_0
    return-void
.end method

.method protected onStart()V
    .locals 0

    .line 1
    invoke-super {p0}, Landroid/support/v7/app/AppCompatActivity;->onStart()V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ma()V

    return-void
.end method

.method public onWindowFocusChanged(Z)V
    .locals 3

    .line 1
    invoke-super {p0, p1}, Landroid/app/Activity;->onWindowFocusChanged(Z)V

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->ub:Z

    .line 3
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Uc()Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    .line 4
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->ub:Z

    :cond_0
    if-eqz p1, :cond_1

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mHandler:Landroid/os/Handler;

    const v0, 0xff01

    invoke-virtual {p1, v0}, Landroid/os/Handler;->removeMessages(I)V

    .line 6
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mHandler:Landroid/os/Handler;

    const-wide/16 v1, 0x64

    invoke-virtual {p0, v0, v1, v2}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_1
    return-void
.end method

.method public abstract za()Lcom/eckom/xtlibrary/b/g/a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TP;"
        }
    .end annotation
.end method

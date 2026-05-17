.class public Lcom/eckom/xtlibrary/twproject/video/utils/a;
.super Landroid/app/Presentation;
.source "BionPresentation.java"


# static fields
.field private static ea:Lcom/eckom/xtlibrary/twproject/video/utils/a;


# instance fields
.field private da:Landroid/widget/FrameLayout;

.field private flag:Z


# direct methods
.method private constructor <init>(Landroid/content/Context;Landroid/view/Display;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1, p2}, Landroid/app/Presentation;-><init>(Landroid/content/Context;Landroid/view/Display;)V

    const/4 p1, 0x0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->flag:Z

    .line 3
    sget p1, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 p2, 0x21

    if-lt p1, p2, :cond_0

    .line 4
    invoke-virtual {p0}, Landroid/app/Presentation;->getWindow()Landroid/view/Window;

    move-result-object p0

    const/16 p1, 0x7f5

    invoke-virtual {p0, p1}, Landroid/view/Window;->setType(I)V

    goto :goto_0

    :cond_0
    const/16 p2, 0x1f

    if-lt p1, p2, :cond_1

    .line 5
    invoke-virtual {p0}, Landroid/app/Presentation;->getWindow()Landroid/view/Window;

    move-result-object p0

    const/16 p1, 0x7ee

    invoke-virtual {p0, p1}, Landroid/view/Window;->setType(I)V

    goto :goto_0

    .line 6
    :cond_1
    invoke-virtual {p0}, Landroid/app/Presentation;->getWindow()Landroid/view/Window;

    move-result-object p0

    const/16 p1, 0x7d3

    invoke-virtual {p0, p1}, Landroid/view/Window;->setType(I)V

    :goto_0
    return-void
.end method

.method public static a(Landroid/content/Context;Landroid/view/Display;)Lcom/eckom/xtlibrary/twproject/video/utils/a;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->ea:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/utils/a;

    invoke-direct {v0, p0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/a;-><init>(Landroid/content/Context;Landroid/view/Display;)V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->ea:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    .line 3
    :cond_0
    sget-object p0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->ea:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    return-object p0
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Landroid/app/Presentation;->onCreate(Landroid/os/Bundle;)V

    .line 2
    sget p1, Lcom/eckom/xtlibrary/R$layout;->layout_presentation:I

    invoke-virtual {p0, p1}, Landroid/app/Presentation;->setContentView(I)V

    return-void
.end method

.method public pa()Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->flag:Z

    return p0
.end method

.method public qa()Landroid/widget/FrameLayout;
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->da:Landroid/widget/FrameLayout;

    if-nez v0, :cond_0

    .line 2
    sget v0, Lcom/eckom/xtlibrary/R$id;->sv_video_sub:I

    invoke-virtual {p0, v0}, Landroid/app/Presentation;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/FrameLayout;

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->da:Landroid/widget/FrameLayout;

    .line 3
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->da:Landroid/widget/FrameLayout;

    return-object p0
.end method

.method public x(Z)V
    .locals 1

    if-nez p1, :cond_0

    const/4 v0, 0x0

    .line 1
    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->ea:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    .line 2
    :cond_0
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/utils/a;->flag:Z

    return-void
.end method

.class Lcom/eckom/xtlibrary/twproject/activity/c;
.super Ljava/lang/Object;
.source "XTActivity.java"

# interfaces
.implements Landroid/support/v7/graphics/Palette$Filter;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private a([F)Z
    .locals 2

    const/4 p0, 0x0

    .line 1
    aget v0, p1, p0

    const/high16 v1, 0x41700000    # 15.0f

    cmpl-float v0, v0, v1

    if-ltz v0, :cond_0

    aget p1, p1, p0

    const v0, 0x43ac8000    # 345.0f

    cmpg-float p1, p1, v0

    if-gtz p1, :cond_0

    const/4 p0, 0x1

    :cond_0
    return p0
.end method

.method private b([F)Z
    .locals 2

    const/4 p0, 0x1

    .line 1
    aget v0, p1, p0

    const v1, 0x3e4ccccd    # 0.2f

    cmpl-float v0, v0, v1

    if-ltz v0, :cond_0

    aget p1, p1, p0

    const v0, 0x3f1eb852    # 0.62f

    cmpg-float p1, p1, v0

    if-gtz p1, :cond_0

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method

.method private c([F)Z
    .locals 2

    const/4 p0, 0x2

    .line 1
    aget v0, p1, p0

    const v1, 0x3d4ccccd    # 0.05f

    cmpl-float v0, v0, v1

    if-ltz v0, :cond_0

    aget p0, p1, p0

    const p1, 0x3f733333    # 0.95f

    cmpg-float p0, p0, p1

    if-gtz p0, :cond_0

    const/4 p0, 0x1

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method


# virtual methods
.method public isAllowed(I[F)Z
    .locals 0

    .line 1
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/twproject/activity/c;->a([F)Z

    move-result p1

    if-eqz p1, :cond_0

    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/twproject/activity/c;->b([F)Z

    move-result p1

    if-eqz p1, :cond_0

    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/twproject/activity/c;->c([F)Z

    move-result p0

    if-eqz p0, :cond_0

    const/4 p0, 0x1

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method

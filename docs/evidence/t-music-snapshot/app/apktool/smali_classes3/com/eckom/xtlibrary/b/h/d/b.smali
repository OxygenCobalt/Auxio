.class public Lcom/eckom/xtlibrary/b/h/d/b;
.super Landroid/tw/john/TWUtil;
.source "TWRadio.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/b/h/d/b$a;
    }
.end annotation


# static fields
.field private static mCount:I


# direct methods
.method private constructor <init>(I)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Landroid/tw/john/TWUtil;-><init>(I)V

    return-void
.end method

.method synthetic constructor <init>(ILcom/eckom/xtlibrary/b/h/d/a;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/h/d/b;-><init>(I)V

    return-void
.end method

.method public static open()Lcom/eckom/xtlibrary/b/h/d/b;
    .locals 2

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/h/d/b;->mCount:I

    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/eckom/xtlibrary/b/h/d/b;->mCount:I

    if-nez v0, :cond_1

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/d/b$a;->access$100()Lcom/eckom/xtlibrary/b/h/d/b;

    move-result-object v0

    const/16 v1, 0xd

    new-array v1, v1, [S

    fill-array-data v1, :array_0

    invoke-virtual {v0, v1}, Landroid/tw/john/TWUtil;->open([S)I

    move-result v0

    if-eqz v0, :cond_0

    .line 3
    sget v0, Lcom/eckom/xtlibrary/b/h/d/b;->mCount:I

    add-int/lit8 v0, v0, -0x1

    sput v0, Lcom/eckom/xtlibrary/b/h/d/b;->mCount:I

    const/4 v0, 0x0

    return-object v0

    .line 4
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/d/b$a;->access$100()Lcom/eckom/xtlibrary/b/h/d/b;

    move-result-object v0

    invoke-virtual {v0}, Landroid/tw/john/TWUtil;->start()V

    .line 5
    :cond_1
    invoke-static {}, Lcom/eckom/xtlibrary/b/h/d/b$a;->access$100()Lcom/eckom/xtlibrary/b/h/d/b;

    move-result-object v0

    return-object v0

    nop

    :array_0
    .array-data 2
        0x109s
        0x10as
        0x112s
        0x201s
        0x203s
        0x301s
        0x401s
        0x402s
        0x404s
        0x405s
        0x406s
        -0x6200s
        -0x61ffs
    .end array-data
.end method


# virtual methods
.method public close()V
    .locals 1

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/h/d/b;->mCount:I

    if-lez v0, :cond_0

    add-int/lit8 v0, v0, -0x1

    .line 2
    sput v0, Lcom/eckom/xtlibrary/b/h/d/b;->mCount:I

    if-nez v0, :cond_0

    .line 3
    invoke-virtual {p0}, Landroid/tw/john/TWUtil;->stop()V

    .line 4
    invoke-super {p0}, Landroid/tw/john/TWUtil;->close()V

    :cond_0
    return-void
.end method

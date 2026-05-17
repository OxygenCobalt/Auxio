.class public Lcom/eckom/xtlibrary/b/f/f/t;
.super Landroid/tw/john/TWUtil;
.source "TWMusicIikID3.java"


# static fields
.field private static jd:Lcom/eckom/xtlibrary/b/f/f/t;

.field private static mCount:I


# instance fields
.field private mService:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/b/f/f/t;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/b/f/f/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/4 v0, 0x0

    .line 2
    sput v0, Lcom/eckom/xtlibrary/b/f/f/t;->mCount:I

    return-void
.end method

.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Landroid/tw/john/TWUtil;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/f/t;->mService:I

    return-void
.end method

.method public static open()Lcom/eckom/xtlibrary/b/f/f/t;
    .locals 2

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/f/f/t;->mCount:I

    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/eckom/xtlibrary/b/f/f/t;->mCount:I

    if-nez v0, :cond_1

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    const/16 v1, 0xb

    new-array v1, v1, [S

    fill-array-data v1, :array_0

    invoke-virtual {v0, v1}, Landroid/tw/john/TWUtil;->open([S)I

    move-result v0

    if-eqz v0, :cond_0

    .line 3
    sget v0, Lcom/eckom/xtlibrary/b/f/f/t;->mCount:I

    add-int/lit8 v0, v0, -0x1

    sput v0, Lcom/eckom/xtlibrary/b/f/f/t;->mCount:I

    const/4 v0, 0x0

    return-object v0

    .line 4
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    invoke-virtual {v0}, Landroid/tw/john/TWUtil;->start()V

    .line 5
    :cond_1
    sget-object v0, Lcom/eckom/xtlibrary/b/f/f/t;->jd:Lcom/eckom/xtlibrary/b/f/f/t;

    return-object v0

    nop

    :array_0
    .array-data 2
        0x201s
        0x202s
        0x203s
        0x20cs
        0x301s
        0x302s
        0x304s
        0x510s
        -0x61fds
        -0x61e1s
        -0x60e4s
    .end array-data
.end method


# virtual methods
.method public b(IIIII)V
    .locals 1

    shl-int/lit8 p3, p3, 0x10

    const v0, 0xffff

    and-int/2addr p2, v0

    or-int/2addr p2, p3

    shl-int/lit8 p1, p1, 0x1f

    and-int/lit8 p3, p5, 0x7f

    shl-int/lit8 p3, p3, 0x18

    or-int/2addr p1, p3

    const p3, 0xffffff

    and-int/2addr p3, p4

    or-int/2addr p1, p3

    const/16 p3, 0x502

    .line 1
    invoke-virtual {p0, p3, p2, p1}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public ca(I)V
    .locals 1

    .line 1
    iput p1, p0, Lcom/eckom/xtlibrary/b/f/f/t;->mService:I

    const v0, 0x9e00

    .line 2
    invoke-virtual {p0, v0, p1}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method public close()V
    .locals 1

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/f/f/t;->mCount:I

    if-lez v0, :cond_0

    add-int/lit8 v0, v0, -0x1

    .line 2
    sput v0, Lcom/eckom/xtlibrary/b/f/f/t;->mCount:I

    if-nez v0, :cond_0

    .line 3
    invoke-virtual {p0}, Landroid/tw/john/TWUtil;->stop()V

    .line 4
    invoke-super {p0}, Landroid/tw/john/TWUtil;->close()V

    :cond_0
    return-void
.end method

.method public da(I)V
    .locals 2

    const v0, 0x9e11

    const/16 v1, 0xc0

    .line 1
    invoke-virtual {p0, v0, v1, p1}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public getService()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/f/f/t;->mService:I

    return p0
.end method

.method public w(Z)V
    .locals 0

    if-eqz p1, :cond_0

    const/4 p1, 0x3

    goto :goto_0

    :cond_0
    const/16 p1, 0x83

    .line 1
    :goto_0
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/f/t;->da(I)V

    return-void
.end method

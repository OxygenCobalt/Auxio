.class public Lcom/eckom/xtlibrary/b/a/b/c;
.super Landroid/tw/john/TWUtil;
.source "TWAT.java"


# static fields
.field private static jd:Lcom/eckom/xtlibrary/b/a/b/c;

.field private static mCount:I


# instance fields
.field private mService:I

.field public mTime:I


# direct methods
.method static constructor <clinit>()V
    .locals 2

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v1, 0x8

    invoke-direct {v0, v1}, Lcom/eckom/xtlibrary/b/a/b/c;-><init>(I)V

    sput-object v0, Lcom/eckom/xtlibrary/b/a/b/c;->jd:Lcom/eckom/xtlibrary/b/a/b/c;

    const/4 v0, 0x0

    .line 2
    sput v0, Lcom/eckom/xtlibrary/b/a/b/c;->mCount:I

    return-void
.end method

.method public constructor <init>(I)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Landroid/tw/john/TWUtil;-><init>(I)V

    const/4 p1, 0x0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/b/a/b/c;->mService:I

    .line 3
    iput p1, p0, Lcom/eckom/xtlibrary/b/a/b/c;->mTime:I

    return-void
.end method

.method private da(I)V
    .locals 2

    const v0, 0x9e11

    const/16 v1, 0xc0

    .line 1
    invoke-virtual {p0, v0, v1, p1}, Landroid/tw/john/TWUtil;->write(III)I

    return-void
.end method

.method public static open()Lcom/eckom/xtlibrary/b/a/b/c;
    .locals 3

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/a/b/c;->mCount:I

    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/eckom/xtlibrary/b/a/b/c;->mCount:I

    if-nez v0, :cond_1

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/b/a/b/c;->jd:Lcom/eckom/xtlibrary/b/a/b/c;

    const/16 v1, 0x8

    new-array v1, v1, [S

    fill-array-data v1, :array_0

    const v2, 0x1c200

    invoke-virtual {v0, v1, v2}, Landroid/tw/john/TWUtil;->open([SI)I

    move-result v0

    if-eqz v0, :cond_0

    .line 3
    sget v0, Lcom/eckom/xtlibrary/b/a/b/c;->mCount:I

    add-int/lit8 v0, v0, -0x1

    sput v0, Lcom/eckom/xtlibrary/b/a/b/c;->mCount:I

    const/4 v0, 0x0

    return-object v0

    .line 4
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/b/a/b/c;->jd:Lcom/eckom/xtlibrary/b/a/b/c;

    invoke-virtual {v0}, Landroid/tw/john/TWUtil;->start()V

    .line 5
    :cond_1
    sget-object v0, Lcom/eckom/xtlibrary/b/a/b/c;->jd:Lcom/eckom/xtlibrary/b/a/b/c;

    return-object v0

    :array_0
    .array-data 2
        0x10bs
        0x112s
        0x201s
        0x203s
        0x301s
        0x510s
        -0x61f8s
        -0x61e3s
    .end array-data
.end method


# virtual methods
.method public ca(I)V
    .locals 1

    .line 1
    iput p1, p0, Lcom/eckom/xtlibrary/b/a/b/c;->mService:I

    const v0, 0x9e00

    .line 2
    invoke-virtual {p0, v0, p1}, Landroid/tw/john/TWUtil;->write(II)I

    return-void
.end method

.method public close()V
    .locals 1

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/a/b/c;->mCount:I

    if-lez v0, :cond_0

    add-int/lit8 v0, v0, -0x1

    .line 2
    sput v0, Lcom/eckom/xtlibrary/b/a/b/c;->mCount:I

    if-nez v0, :cond_0

    .line 3
    invoke-virtual {p0}, Landroid/tw/john/TWUtil;->stop()V

    .line 4
    invoke-super {p0}, Landroid/tw/john/TWUtil;->close()V

    :cond_0
    return-void
.end method

.method public w(Z)V
    .locals 0

    if-eqz p1, :cond_0

    const/16 p1, 0x8

    goto :goto_0

    :cond_0
    const/16 p1, 0x88

    .line 1
    :goto_0
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/a/b/c;->da(I)V

    return-void
.end method

.method public write(IILjava/lang/String;)I
    .locals 6

    const/4 v3, 0x0

    const/4 v5, 0x0

    move-object v0, p0

    move v1, p1

    move v2, p2

    move-object v4, p3

    .line 2
    invoke-virtual/range {v0 .. v5}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;Ljava/lang/Object;)I

    move-result p0

    return p0
.end method

.method public write(ILjava/lang/String;)I
    .locals 6

    const/4 v2, 0x0

    const/4 v3, 0x0

    const/4 v5, 0x0

    move-object v0, p0

    move v1, p1

    move-object v4, p2

    .line 1
    invoke-virtual/range {v0 .. v5}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;Ljava/lang/Object;)I

    move-result p0

    return p0
.end method

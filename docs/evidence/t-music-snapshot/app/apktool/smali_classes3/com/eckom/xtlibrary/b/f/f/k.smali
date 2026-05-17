.class public Lcom/eckom/xtlibrary/b/f/f/k;
.super Ljava/lang/Object;
.source "PinyinConv.java"


# static fields
.field private static final Dk:[C

.field private static final Ek:[C

.field private static final table:[I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    const/16 v0, 0x1a

    new-array v1, v0, [C

    .line 1
    fill-array-data v1, :array_0

    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/k;->Dk:[C

    const/16 v1, 0x1b

    new-array v1, v1, [I

    .line 2
    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/k;->table:[I

    new-array v1, v0, [C

    .line 3
    fill-array-data v1, :array_1

    sput-object v1, Lcom/eckom/xtlibrary/b/f/f/k;->Ek:[C

    const/4 v1, 0x0

    :goto_0
    if-ge v1, v0, :cond_0

    .line 4
    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/k;->table:[I

    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/k;->Dk:[C

    aget-char v3, v3, v1

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/f/k;->c(C)I

    move-result v3

    aput v3, v2, v1

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 5
    :cond_0
    sget-object v1, Lcom/eckom/xtlibrary/b/f/f/k;->table:[I

    const v2, 0xf7fe

    aput v2, v1, v0

    return-void

    :array_0
    .array-data 2
        0x554as
        -0x7d53s
        0x64e6s
        0x642ds
        -0x7902s
        0x53d1s
        0x5676s
        0x54c8s
        0x54c8s
        0x51fbs
        0x5580s
        0x5783s
        0x5988s
        0x62ffs
        0x54e6s
        0x556as
        0x671fs
        0x7136s
        0x6492s
        0x584cs
        0x584cs
        0x584cs
        0x6316s
        0x6614s
        0x538bs
        0x531ds
    .end array-data

    :array_1
    .array-data 2
        0x61s
        0x62s
        0x63s
        0x64s
        0x65s
        0x66s
        0x67s
        0x68s
        0x68s
        0x6as
        0x6bs
        0x6cs
        0x6ds
        0x6es
        0x6fs
        0x70s
        0x71s
        0x72s
        0x73s
        0x74s
        0x74s
        0x74s
        0x77s
        0x78s
        0x79s
        0x7as
    .end array-data
.end method

.method private static b(C)C
    .locals 6

    const/16 v0, 0x41

    const/16 v1, 0x61

    if-lt p0, v1, :cond_0

    const/16 v2, 0x7a

    if-gt p0, v2, :cond_0

    sub-int/2addr p0, v1

    add-int/2addr p0, v0

    int-to-char p0, p0

    return p0

    :cond_0
    if-lt p0, v0, :cond_1

    const/16 v2, 0x5a

    if-gt p0, v2, :cond_1

    return p0

    .line 1
    :cond_1
    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/f/k;->c(C)I

    move-result v2

    const v3, 0xb0a1

    if-lt v2, v3, :cond_8

    const v3, 0xf7fe

    if-gt v2, v3, :cond_8

    const/4 p0, 0x0

    :goto_0
    const/16 v4, 0x1a

    if-ge p0, v4, :cond_3

    .line 2
    sget-object v4, Lcom/eckom/xtlibrary/b/f/f/k;->table:[I

    aget v5, v4, p0

    if-lt v2, v5, :cond_2

    add-int/lit8 v5, p0, 0x1

    aget v4, v4, v5

    if-lt v2, v4, :cond_3

    :cond_2
    add-int/lit8 p0, p0, 0x1

    goto :goto_0

    :cond_3
    const/16 v4, 0x19

    if-ne v2, v3, :cond_4

    move p0, v4

    .line 3
    :cond_4
    sget-object v3, Lcom/eckom/xtlibrary/b/f/f/k;->Ek:[C

    aget-char v3, v3, p0

    if-ne p0, v4, :cond_6

    const p0, 0xe6c3

    if-eq v2, p0, :cond_5

    goto :goto_1

    :cond_5
    const/16 p0, 0x74

    move v3, p0

    goto :goto_1

    :cond_6
    const p0, 0xdde6

    if-ne v2, p0, :cond_7

    const/16 v3, 0x78

    :cond_7
    :goto_1
    sub-int/2addr v3, v1

    add-int/2addr v3, v0

    int-to-char p0, v3

    :cond_8
    return p0
.end method

.method private static c(C)I
    .locals 3

    .line 1
    new-instance v0, Ljava/lang/String;

    invoke-direct {v0}, Ljava/lang/String;-><init>()V

    .line 2
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const/4 v0, 0x0

    :try_start_0
    const-string v1, "GB2312"

    .line 3
    invoke-virtual {p0, v1}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object p0

    .line 4
    array-length v1, p0

    const/4 v2, 0x2

    if-ge v1, v2, :cond_0

    goto :goto_0

    :cond_0
    aget-byte v1, p0, v0

    shl-int/lit8 v1, v1, 0x8

    const v2, 0xff00

    and-int/2addr v1, v2

    const/4 v2, 0x1

    aget-byte p0, p0, v2
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    and-int/lit16 p0, p0, 0xff

    add-int v0, v1, p0

    :catch_0
    :goto_0
    return v0
.end method

.method public static cn2py(Ljava/lang/String;)Ljava/lang/String;
    .locals 5

    const-string v0, ""

    .line 1
    :try_start_0
    invoke-virtual {p0}, Ljava/lang/String;->length()I

    move-result v1

    const/4 v2, 0x0

    move-object v3, v0

    :goto_0
    if-ge v2, v1, :cond_0

    .line 2
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, v2}, Ljava/lang/String;->charAt(I)C

    move-result v3

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/f/f/k;->b(C)C

    move-result v3

    invoke-virtual {v4, v3}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    :cond_0
    move-object v0, v3

    :catch_0
    return-object v0
.end method

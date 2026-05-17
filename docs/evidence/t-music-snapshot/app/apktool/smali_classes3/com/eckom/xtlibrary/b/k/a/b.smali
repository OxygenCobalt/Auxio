.class public Lcom/eckom/xtlibrary/b/k/a/b;
.super Ljava/lang/Object;
.source "Record.java"


# instance fields
.field public ik:I

.field public jk:[Lcom/eckom/xtlibrary/b/k/a/a;

.field public kk:I

.field lk:Lcom/eckom/xtlibrary/b/k/a/b;

.field public mIndex:I

.field public mLength:I

.field public mName:Ljava/lang/String;

.field mk:Lcom/eckom/xtlibrary/b/k/a/b;

.field public nk:Lcom/eckom/xtlibrary/b/k/a/b;


# direct methods
.method public constructor <init>(Ljava/lang/String;II)V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 2
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mName:Ljava/lang/String;

    .line 3
    iput p2, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mIndex:I

    .line 4
    iput p3, p0, Lcom/eckom/xtlibrary/b/k/a/b;->ik:I

    return-void
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/k/a/a;)V
    .locals 3

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    iget v1, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mLength:I

    if-ge v0, v1, :cond_0

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/k/a/b;->jk:[Lcom/eckom/xtlibrary/b/k/a/a;

    add-int/lit8 v2, v0, 0x1

    iput v2, p0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    aput-object p1, v1, v0

    :cond_0
    return-void
.end method

.method public a(Ljava/lang/String;Ljava/lang/String;Z)V
    .locals 3

    .line 3
    iget v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    iget v1, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mLength:I

    if-ge v0, v1, :cond_0

    .line 4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/k/a/b;->jk:[Lcom/eckom/xtlibrary/b/k/a/a;

    add-int/lit8 v2, v0, 0x1

    iput v2, p0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    new-instance p0, Lcom/eckom/xtlibrary/b/k/a/a;

    invoke-direct {p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/k/a/a;-><init>(Ljava/lang/String;Ljava/lang/String;Z)V

    aput-object p0, v1, v0

    :cond_0
    return-void
.end method

.method public setLength(I)V
    .locals 1

    .line 1
    iget v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mLength:I

    if-eq v0, p1, :cond_0

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    if-lez p1, :cond_0

    .line 3
    new-array v0, p1, [Lcom/eckom/xtlibrary/b/k/a/a;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->jk:[Lcom/eckom/xtlibrary/b/k/a/a;

    .line 4
    iput p1, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mLength:I

    :cond_0
    return-void
.end method

.method public wc()V
    .locals 4

    const/4 v0, 0x0

    move v1, v0

    .line 1
    :goto_0
    iget v2, p0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    const/4 v3, 0x0

    if-ge v1, v2, :cond_0

    .line 2
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/k/a/b;->jk:[Lcom/eckom/xtlibrary/b/k/a/a;

    aput-object v3, v2, v1

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 3
    :cond_0
    iput v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    .line 4
    iput-object v3, p0, Lcom/eckom/xtlibrary/b/k/a/b;->jk:[Lcom/eckom/xtlibrary/b/k/a/a;

    .line 5
    iput v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mLength:I

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->lk:Lcom/eckom/xtlibrary/b/k/a/b;

    if-eqz v0, :cond_1

    .line 7
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    .line 8
    iput-object v3, p0, Lcom/eckom/xtlibrary/b/k/a/b;->lk:Lcom/eckom/xtlibrary/b/k/a/b;

    .line 9
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mk:Lcom/eckom/xtlibrary/b/k/a/b;

    if-eqz v0, :cond_2

    .line 10
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/k/a/b;->wc()V

    .line 11
    iput-object v3, p0, Lcom/eckom/xtlibrary/b/k/a/b;->mk:Lcom/eckom/xtlibrary/b/k/a/b;

    :cond_2
    return-void
.end method

.class public Lcom/eckom/xtlibrary/b/f/b/f;
.super Ljava/lang/Object;
.source "MusicName.java"


# instance fields
.field public ek:Z

.field public fk:Ljava/lang/String;

.field public gk:Ljava/lang/String;

.field public hk:Landroid/graphics/Bitmap;

.field public mLength:I

.field public mName:Ljava/lang/String;

.field public mPath:Ljava/lang/String;


# direct methods
.method public constructor <init>(Lcom/eckom/xtlibrary/b/f/b/f;)V
    .locals 2

    .line 37
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 38
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    const-string v1, ""

    .line 39
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    .line 40
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    const/4 v1, 0x0

    .line 41
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    .line 42
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    .line 43
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 44
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    .line 45
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    .line 46
    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    .line 47
    iget-boolean v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    .line 48
    iget v0, p1, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    iput v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    .line 49
    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    return-void
.end method

.method public constructor <init>(Ljava/lang/String;I)V
    .locals 2

    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 11
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    const-string v1, ""

    .line 12
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    .line 13
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    const/4 v1, 0x0

    .line 14
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    .line 15
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    .line 16
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 17
    iput p2, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    return-void
.end method

.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;)V
    .locals 2

    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 19
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    const-string v1, ""

    .line 20
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    .line 21
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    const/4 v1, 0x0

    .line 22
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    .line 23
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    .line 24
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 25
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;)V
    .locals 2

    .line 26
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 27
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    const-string v1, ""

    .line 28
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    .line 29
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    const/4 v1, 0x0

    .line 30
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    .line 31
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    .line 32
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 33
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    .line 34
    iput-object p3, p0, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    .line 35
    iput-object p4, p0, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    .line 36
    iput-object p5, p0, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    return-void
.end method

.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Z)V
    .locals 2

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    const-string v1, ""

    .line 3
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->fk:Ljava/lang/String;

    .line 4
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->gk:Ljava/lang/String;

    const/4 v1, 0x0

    .line 5
    iput-object v1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->hk:Landroid/graphics/Bitmap;

    .line 6
    iput v0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mLength:I

    .line 7
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 8
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    .line 9
    iput-boolean p3, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    return-void
.end method

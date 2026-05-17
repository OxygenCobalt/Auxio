.class public Lcom/eckom/xtlibrary/b/f/b/c;
.super Ljava/lang/Object;
.source "LMedia.java"


# instance fields
.field public album:Ljava/lang/String;

.field public artist:Ljava/lang/String;

.field public displayName:Ljava/lang/String;

.field public duration:I

.field public id:I

.field public mediaType:Ljava/lang/String;

.field public name:Ljava/lang/String;

.field public oj:Z

.field pj:Landroid/graphics/Bitmap;

.field public size:Ljava/lang/String;

.field public url:Ljava/lang/String;


# direct methods
.method public constructor <init>()V
    .locals 2

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const-string v0, ""

    .line 2
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->name:Ljava/lang/String;

    .line 3
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->size:Ljava/lang/String;

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->url:Ljava/lang/String;

    const/4 v1, 0x0

    .line 5
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/b/c;->duration:I

    .line 6
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/b/c;->id:I

    .line 7
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->mediaType:Ljava/lang/String;

    .line 8
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->album:Ljava/lang/String;

    .line 9
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->artist:Ljava/lang/String;

    .line 10
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->displayName:Ljava/lang/String;

    .line 11
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/b/c;->oj:Z

    return-void
.end method

.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;)V
    .locals 2

    .line 12
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const-string v0, ""

    .line 13
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->name:Ljava/lang/String;

    .line 14
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->size:Ljava/lang/String;

    .line 15
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->url:Ljava/lang/String;

    const/4 v1, 0x0

    .line 16
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/b/c;->duration:I

    .line 17
    iput v1, p0, Lcom/eckom/xtlibrary/b/f/b/c;->id:I

    .line 18
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->mediaType:Ljava/lang/String;

    .line 19
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->album:Ljava/lang/String;

    .line 20
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->artist:Ljava/lang/String;

    .line 21
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->displayName:Ljava/lang/String;

    .line 22
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/b/f/b/c;->oj:Z

    .line 23
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/c;->name:Ljava/lang/String;

    .line 24
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/b/c;->url:Ljava/lang/String;

    .line 25
    iput-object p3, p0, Lcom/eckom/xtlibrary/b/f/b/c;->album:Ljava/lang/String;

    .line 26
    iput-object p4, p0, Lcom/eckom/xtlibrary/b/f/b/c;->artist:Ljava/lang/String;

    .line 27
    iput-object p5, p0, Lcom/eckom/xtlibrary/b/f/b/c;->pj:Landroid/graphics/Bitmap;

    return-void
.end method


# virtual methods
.method public getUrl()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/c;->url:Ljava/lang/String;

    return-object p0
.end method

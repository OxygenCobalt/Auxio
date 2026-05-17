.class public Lcom/eckom/xtlibrary/b/k/a/a;
.super Ljava/lang/Object;
.source "LName.java"


# instance fields
.field public ek:Z

.field public mName:Ljava/lang/String;

.field public mPath:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1

    .line 6
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 7
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/k/a/a;->ek:Z

    .line 8
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/k/a/a;->mName:Ljava/lang/String;

    .line 9
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/k/a/a;->mPath:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Z)V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/b/k/a/a;->ek:Z

    .line 3
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/k/a/a;->mName:Ljava/lang/String;

    .line 4
    iput-object p2, p0, Lcom/eckom/xtlibrary/b/k/a/a;->mPath:Ljava/lang/String;

    .line 5
    iput-boolean p3, p0, Lcom/eckom/xtlibrary/b/k/a/a;->ek:Z

    return-void
.end method

.class public Lcom/eckom/xtlibrary/b/f/b/d;
.super Ljava/lang/Object;
.source "MediaFolderBean.java"


# instance fields
.field private key:Ljava/lang/String;

.field private name:Ljava/lang/String;

.field private qj:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/f;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 2
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/d;->qj:Ljava/util/ArrayList;

    .line 3
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/d;->name:Ljava/lang/String;

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/d;->qj:Ljava/util/ArrayList;

    invoke-virtual {p0, p2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    return-void
.end method


# virtual methods
.method public getKey()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/d;->key:Ljava/lang/String;

    return-object p0
.end method

.method public getName()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/d;->name:Ljava/lang/String;

    return-object p0
.end method

.method public setKey(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/d;->key:Ljava/lang/String;

    return-void
.end method

.method public tc()Ljava/util/ArrayList;
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/f;",
            ">;"
        }
    .end annotation

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/d;->qj:Ljava/util/ArrayList;

    return-object p0
.end method

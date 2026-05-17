.class public Lcom/eckom/xtlibrary/b/f/b/b;
.super Ljava/lang/Object;
.source "ArtistMedia.java"


# instance fields
.field private name:Ljava/lang/String;

.field private nj:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/c;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/c;)V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 2
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/f/b/b;->nj:Ljava/util/ArrayList;

    .line 3
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/b/b;->name:Ljava/lang/String;

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/b;->nj:Ljava/util/ArrayList;

    invoke-virtual {p0, p2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    return-void
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/b;->name:Ljava/lang/String;

    return-object p0
.end method

.method public sc()Ljava/util/ArrayList;
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/b/f/b/c;",
            ">;"
        }
    .end annotation

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/b;->nj:Ljava/util/ArrayList;

    return-object p0
.end method

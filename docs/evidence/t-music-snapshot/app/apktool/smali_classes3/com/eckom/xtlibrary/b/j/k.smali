.class Lcom/eckom/xtlibrary/b/j/k;
.super Ljava/lang/Object;
.source "MediaScanMedia.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/j/m;->d(Ljava/lang/String;Lcom/eckom/xtlibrary/b/f/b/e;ZLcom/eckom/xtlibrary/b/f/f/h$f;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic hm:Lcom/eckom/xtlibrary/b/f/b/e;

.field final synthetic im:Lcom/eckom/xtlibrary/b/f/f/h$f;

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/j/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/j/m;Lcom/eckom/xtlibrary/b/f/b/e;Lcom/eckom/xtlibrary/b/f/f/h$f;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/j/k;->this$0:Lcom/eckom/xtlibrary/b/j/m;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/j/k;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p3, p0, Lcom/eckom/xtlibrary/b/j/k;->im:Lcom/eckom/xtlibrary/b/f/f/h$f;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public S()V
    .locals 0

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Ljava/lang/String;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/j/k;->hm:Lcom/eckom/xtlibrary/b/f/b/e;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/f/b/e;->Sj:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/j/k;->im:Lcom/eckom/xtlibrary/b/f/f/h$f;

    invoke-interface {p0, p2}, Lcom/eckom/xtlibrary/b/f/f/h$f;->ia(Ljava/lang/String;)V

    return-void
.end method

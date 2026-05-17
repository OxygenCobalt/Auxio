.class Lcom/eckom/xtlibrary/b/i/k$d;
.super Ljava/lang/Object;
.source "ThemeManager.java"

# interfaces
.implements Ljava/util/Comparator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/i/k;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "d"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljava/util/Comparator<",
        "Lcom/eckom/xtlibrary/b/i/c;",
        ">;"
    }
.end annotation


# direct methods
.method private constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lcom/eckom/xtlibrary/b/i/i;)V
    .locals 0

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/i/k$d;-><init>()V

    return-void
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/i/c;Lcom/eckom/xtlibrary/b/i/c;)I
    .locals 0

    .line 1
    invoke-interface {p2}, Lcom/eckom/xtlibrary/b/i/c;->ia()I

    move-result p0

    invoke-interface {p1}, Lcom/eckom/xtlibrary/b/i/c;->ia()I

    move-result p1

    sub-int/2addr p0, p1

    return p0
.end method

.method public bridge synthetic compare(Ljava/lang/Object;Ljava/lang/Object;)I
    .locals 0

    .line 1
    check-cast p1, Lcom/eckom/xtlibrary/b/i/c;

    check-cast p2, Lcom/eckom/xtlibrary/b/i/c;

    invoke-virtual {p0, p1, p2}, Lcom/eckom/xtlibrary/b/i/k$d;->a(Lcom/eckom/xtlibrary/b/i/c;Lcom/eckom/xtlibrary/b/i/c;)I

    move-result p0

    return p0
.end method

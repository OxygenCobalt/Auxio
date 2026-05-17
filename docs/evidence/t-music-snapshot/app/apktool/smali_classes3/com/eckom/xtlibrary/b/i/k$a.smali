.class Lcom/eckom/xtlibrary/b/i/k$a;
.super Ljava/lang/Object;
.source "ThemeManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/i/k;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "a"
.end annotation


# static fields
.field static INSTANCE:Lcom/eckom/xtlibrary/b/i/k;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/i/k;

    const/4 v1, 0x0

    invoke-direct {v0, v1}, Lcom/eckom/xtlibrary/b/i/k;-><init>(Lcom/eckom/xtlibrary/b/i/i;)V

    sput-object v0, Lcom/eckom/xtlibrary/b/i/k$a;->INSTANCE:Lcom/eckom/xtlibrary/b/i/k;

    return-void
.end method

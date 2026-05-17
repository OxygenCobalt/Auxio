.class Lcom/eckom/xtlibrary/b/h/d/b$a;
.super Ljava/lang/Object;
.source "TWRadio.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/h/d/b;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "a"
.end annotation


# static fields
.field private static final jd:Lcom/eckom/xtlibrary/b/h/d/b;


# direct methods
.method static constructor <clinit>()V
    .locals 3

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/h/d/b;

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-direct {v0, v1, v2}, Lcom/eckom/xtlibrary/b/h/d/b;-><init>(ILcom/eckom/xtlibrary/b/h/d/a;)V

    sput-object v0, Lcom/eckom/xtlibrary/b/h/d/b$a;->jd:Lcom/eckom/xtlibrary/b/h/d/b;

    return-void
.end method

.method static synthetic access$100()Lcom/eckom/xtlibrary/b/h/d/b;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/h/d/b$a;->jd:Lcom/eckom/xtlibrary/b/h/d/b;

    return-object v0
.end method

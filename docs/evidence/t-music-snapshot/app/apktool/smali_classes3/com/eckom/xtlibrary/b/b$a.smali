.class Lcom/eckom/xtlibrary/b/b$a;
.super Ljava/lang/Object;
.source "XTManage.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/b;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "a"
.end annotation


# static fields
.field private static final sInstance:Lcom/eckom/xtlibrary/b/b;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    .line 1
    new-instance v0, Lcom/eckom/xtlibrary/b/b;

    const/4 v1, 0x0

    invoke-direct {v0, v1}, Lcom/eckom/xtlibrary/b/b;-><init>(Lcom/eckom/xtlibrary/b/a;)V

    sput-object v0, Lcom/eckom/xtlibrary/b/b$a;->sInstance:Lcom/eckom/xtlibrary/b/b;

    return-void
.end method

.method static synthetic access$100()Lcom/eckom/xtlibrary/b/b;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/b$a;->sInstance:Lcom/eckom/xtlibrary/b/b;

    return-object v0
.end method

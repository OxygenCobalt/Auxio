.class Lcom/tw/music/utils/b$a;
.super Ljava/lang/Object;
.source "SPUtil.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/utils/b;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "a"
.end annotation


# static fields
.field private static final Bn:Lcom/tw/music/utils/b;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 1
    new-instance v0, Lcom/tw/music/utils/b;

    invoke-direct {v0}, Lcom/tw/music/utils/b;-><init>()V

    sput-object v0, Lcom/tw/music/utils/b$a;->Bn:Lcom/tw/music/utils/b;

    return-void
.end method

.method static synthetic access$000()Lcom/tw/music/utils/b;
    .locals 1

    .line 1
    sget-object v0, Lcom/tw/music/utils/b$a;->Bn:Lcom/tw/music/utils/b;

    return-object v0
.end method

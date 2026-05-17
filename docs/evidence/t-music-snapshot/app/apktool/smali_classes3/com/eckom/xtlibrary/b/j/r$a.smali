.class Lcom/eckom/xtlibrary/b/j/r$a;
.super Landroid/os/Handler;
.source "ToastCustom.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/j/r;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "a"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/j/r;


# direct methods
.method private constructor <init>(Lcom/eckom/xtlibrary/b/j/r;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/j/r$a;->this$0:Lcom/eckom/xtlibrary/b/j/r;

    invoke-direct {p0}, Landroid/os/Handler;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lcom/eckom/xtlibrary/b/j/r;Lcom/eckom/xtlibrary/b/j/q;)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/j/r$a;-><init>(Lcom/eckom/xtlibrary/b/j/r;)V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .locals 1

    .line 1
    iget p1, p1, Landroid/os/Message;->what:I

    if-eqz p1, :cond_0

    goto :goto_0

    :cond_0
    const-string p1, "ToastCustom"

    const-string v0, "dismiss:"

    .line 2
    invoke-static {p1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/j/r$a;->this$0:Lcom/eckom/xtlibrary/b/j/r;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/j/r;->a(Lcom/eckom/xtlibrary/b/j/r;)V

    :goto_0
    return-void
.end method

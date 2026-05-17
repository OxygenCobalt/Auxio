.class Lcom/eckom/xtlibrary/b/h/b/d;
.super Ljava/lang/Object;
.source "RadioModel.java"

# interfaces
.implements Lcom/eckom/xtlibrary/twproject/radio/utils/b$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/h/b/e;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/h/b/e;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/h/b/e;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public O()V
    .locals 6

    const-string v0, "RadioModel"

    const-string v1, "onUpdateFinished: "

    .line 1
    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const/4 v0, 0x0

    .line 2
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v1, v1

    if-ge v0, v1, :cond_0

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v1, v1, v0

    .line 4
    iget v2, v1, Lcom/eckom/xtlibrary/b/h/a/a;->wl:I

    iget v3, v1, Lcom/eckom/xtlibrary/b/h/a/a;->tl:I

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->g(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    move-result-object v4

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v5, v5, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    invoke-static {v2, v3, v4, v5}, Lcom/eckom/xtlibrary/b/j/b;->a(IILcom/eckom/xtlibrary/twproject/radio/utils/b;I)Landroid/graphics/drawable/Drawable;

    move-result-object v2

    .line 5
    iput-object v2, v1, Lcom/eckom/xtlibrary/b/h/a/a;->xl:Landroid/graphics/drawable/Drawable;

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 6
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    const v1, 0xff02

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 7
    invoke-static {}, Landroid/os/Message;->obtain()Landroid/os/Message;

    move-result-object v0

    .line 8
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v2

    iget v2, v2, Lcom/eckom/xtlibrary/b/h/a;->ll:I

    iput v2, v0, Landroid/os/Message;->arg1:I

    .line 9
    iput v1, v0, Landroid/os/Message;->what:I

    .line 10
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/d;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z

    return-void
.end method

.method public Q()V
    .locals 1

    const-string p0, "RadioModel"

    const-string v0, "onUpdateFinished: "

    .line 1
    invoke-static {p0, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

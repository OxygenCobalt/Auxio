.class Lcom/eckom/xtlibrary/b/a/d/f$a;
.super Landroid/os/AsyncTask;
.source "BTModel.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/a/d/f;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "a"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/os/AsyncTask<",
        "Ljava/lang/Integer;",
        "Ljava/lang/Void;",
        "Ljava/lang/Integer;",
        ">;"
    }
.end annotation


# instance fields
.field Sc:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;",
            ">;"
        }
    .end annotation
.end field

.field final synthetic this$0:Lcom/eckom/xtlibrary/b/a/d/f;


# direct methods
.method private constructor <init>(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    .line 2
    new-instance p1, Ljava/util/ArrayList;

    invoke-direct {p1}, Ljava/util/ArrayList;-><init>()V

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->Sc:Ljava/util/ArrayList;

    return-void
.end method

.method synthetic constructor <init>(Lcom/eckom/xtlibrary/b/a/d/f;Lcom/eckom/xtlibrary/b/a/d/c;)V
    .locals 0

    .line 3
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/f$a;-><init>(Lcom/eckom/xtlibrary/b/a/d/f;)V

    return-void
.end method


# virtual methods
.method protected varargs a([Ljava/lang/Integer;)Ljava/lang/Integer;
    .locals 3

    const/4 v0, 0x0

    .line 1
    aget-object v1, p1, v0

    invoke-virtual {v1}, Ljava/lang/Integer;->intValue()I

    move-result v1

    if-eqz v1, :cond_1

    const/4 v2, 0x1

    if-eq v1, v2, :cond_0

    goto :goto_0

    .line 2
    :cond_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v2

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/a/c/b;->a(Landroid/content/Context;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/a/c/b;

    move-result-object v1

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/a/c/b;->kb()Ljava/util/ArrayList;

    move-result-object v1

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->Sc:Ljava/util/ArrayList;

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->clear()V

    .line 4
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->Sc:Ljava/util/ArrayList;

    iput-object p0, v1, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    goto :goto_0

    .line 5
    :cond_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v2

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/a/c/b;->a(Landroid/content/Context;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/a/c/b;

    move-result-object v1

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/a/c/b;->jb()Ljava/util/ArrayList;

    move-result-object v1

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->Sc:Ljava/util/ArrayList;

    .line 6
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v1}, Ljava/util/ArrayList;->clear()V

    .line 7
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->Sc:Ljava/util/ArrayList;

    iput-object p0, v1, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    .line 8
    :goto_0
    aget-object p0, p1, v0

    return-object p0
.end method

.method protected bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    .line 1
    check-cast p1, [Ljava/lang/Integer;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/f$a;->a([Ljava/lang/Integer;)Ljava/lang/Integer;

    move-result-object p0

    return-object p0
.end method

.method protected onPostExecute(Ljava/lang/Integer;)V
    .locals 2

    .line 2
    invoke-super {p0, p1}, Landroid/os/AsyncTask;->onPostExecute(Ljava/lang/Object;)V

    .line 3
    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    if-eqz p1, :cond_1

    const/4 v0, 0x1

    if-eq p1, v0, :cond_0

    goto :goto_1

    .line 4
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {p1}, Ljava/util/ArrayList;->clear()V

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->Sc:Ljava/util/ArrayList;

    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->addAll(Ljava/util/Collection;)Z

    .line 6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const p1, 0xff09

    invoke-virtual {p0, p1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    goto :goto_1

    .line 7
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p1

    :goto_0
    invoke-interface {p1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_2

    invoke-interface {p1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 8
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/a/d/g;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/d/f$a;->Sc:Ljava/util/ArrayList;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/a/d/g;->b(Ljava/util/ArrayList;)V

    goto :goto_0

    :cond_2
    :goto_1
    return-void
.end method

.method protected bridge synthetic onPostExecute(Ljava/lang/Object;)V
    .locals 0

    .line 1
    check-cast p1, Ljava/lang/Integer;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/a/d/f$a;->onPostExecute(Ljava/lang/Integer;)V

    return-void
.end method

.method protected onPreExecute()V
    .locals 0

    .line 1
    invoke-super {p0}, Landroid/os/AsyncTask;->onPreExecute()V

    return-void
.end method

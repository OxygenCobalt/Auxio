.class Lcom/tw/music/lrc/d;
.super Landroid/os/AsyncTask;
.source "LrcView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/lrc/e;->run()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/os/AsyncTask<",
        "Ljava/lang/String;",
        "Ljava/lang/Integer;",
        "Ljava/util/List<",
        "Lcom/tw/music/lrc/a;",
        ">;>;"
    }
.end annotation


# instance fields
.field final synthetic this$1:Lcom/tw/music/lrc/e;


# direct methods
.method constructor <init>(Lcom/tw/music/lrc/e;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/d;->this$1:Lcom/tw/music/lrc/e;

    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    return-void
.end method


# virtual methods
.method protected varargs a([Ljava/lang/String;)Ljava/util/List;
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "([",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/List<",
            "Lcom/tw/music/lrc/a;",
            ">;"
        }
    .end annotation

    const/4 p0, 0x0

    .line 1
    aget-object p0, p1, p0

    invoke-static {p0}, Lcom/tw/music/lrc/a;->ob(Ljava/lang/String;)Ljava/util/List;

    move-result-object p0

    return-object p0
.end method

.method protected bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    .line 1
    check-cast p1, [Ljava/lang/String;

    invoke-virtual {p0, p1}, Lcom/tw/music/lrc/d;->a([Ljava/lang/String;)Ljava/util/List;

    move-result-object p0

    return-object p0
.end method

.method protected e(Ljava/util/List;)V
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/tw/music/lrc/a;",
            ">;)V"
        }
    .end annotation

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/d;->this$1:Lcom/tw/music/lrc/e;

    iget-object v0, v0, Lcom/tw/music/lrc/e;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v0}, Lcom/tw/music/lrc/LrcView;->f(Lcom/tw/music/lrc/LrcView;)Ljava/lang/Object;

    move-result-object v0

    iget-object v1, p0, Lcom/tw/music/lrc/d;->this$1:Lcom/tw/music/lrc/e;

    iget-object v2, v1, Lcom/tw/music/lrc/e;->Hm:Ljava/lang/String;

    if-ne v0, v2, :cond_0

    .line 2
    iget-object v0, v1, Lcom/tw/music/lrc/e;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v0, p1}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;Ljava/util/List;)V

    .line 3
    iget-object p0, p0, Lcom/tw/music/lrc/d;->this$1:Lcom/tw/music/lrc/e;

    iget-object p0, p0, Lcom/tw/music/lrc/e;->this$0:Lcom/tw/music/lrc/LrcView;

    const/4 p1, 0x0

    invoke-static {p0, p1}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;Ljava/lang/Object;)V

    :cond_0
    return-void
.end method

.method protected bridge synthetic onPostExecute(Ljava/lang/Object;)V
    .locals 0

    .line 1
    check-cast p1, Ljava/util/List;

    invoke-virtual {p0, p1}, Lcom/tw/music/lrc/d;->e(Ljava/util/List;)V

    return-void
.end method

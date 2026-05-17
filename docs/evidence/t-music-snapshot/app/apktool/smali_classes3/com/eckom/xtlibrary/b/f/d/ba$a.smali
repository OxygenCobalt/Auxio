.class Lcom/eckom/xtlibrary/b/f/d/ba$a;
.super Landroid/os/AsyncTask;
.source "MusicModel.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/f/d/ba;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "a"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/os/AsyncTask<",
        "Ljava/lang/String;",
        "Ljava/lang/Void;",
        "Ljava/lang/Void;",
        ">;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/ba;


# direct methods
.method private constructor <init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba$a;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lcom/eckom/xtlibrary/b/f/d/ba;Lcom/eckom/xtlibrary/b/f/d/V;)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba$a;-><init>(Lcom/eckom/xtlibrary/b/f/d/ba;)V

    return-void
.end method


# virtual methods
.method protected varargs a([Ljava/lang/String;)Ljava/lang/Void;
    .locals 1

    const/4 p0, 0x0

    .line 1
    aget-object p0, p1, p0

    .line 2
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "MediaScanTask doInBackground:path: "

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    const-string v0, "MusicModel"

    invoke-static {v0, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 3
    new-instance p1, Lcom/eckom/xtlibrary/b/j/c;

    invoke-direct {p1}, Lcom/eckom/xtlibrary/b/j/c;-><init>()V

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/j/c;->jb(Ljava/lang/String;)V

    const/4 p0, 0x0

    return-object p0
.end method

.method protected bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    .line 1
    check-cast p1, [Ljava/lang/String;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba$a;->a([Ljava/lang/String;)Ljava/lang/Void;

    move-result-object p0

    return-object p0
.end method

.method protected bridge synthetic onPostExecute(Ljava/lang/Object;)V
    .locals 0

    .line 1
    check-cast p1, Ljava/lang/Void;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/d/ba$a;->onPostExecute(Ljava/lang/Void;)V

    return-void
.end method

.method protected onPostExecute(Ljava/lang/Void;)V
    .locals 1

    .line 2
    invoke-super {p0, p1}, Landroid/os/AsyncTask;->onPostExecute(Ljava/lang/Object;)V

    const-string p1, "MusicModel"

    const-string v0, "MediaScanTask onPostExecute: "

    .line 3
    invoke-static {p1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/ba$a;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(Lcom/eckom/xtlibrary/b/f/d/ba;)Landroid/os/Handler;

    move-result-object p1

    const v0, 0xff05

    invoke-virtual {p1, v0}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/ba$a;->this$0:Lcom/eckom/xtlibrary/b/f/d/ba;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/f/d/ba;->a(Lcom/eckom/xtlibrary/b/f/d/ba;)Landroid/os/Handler;

    move-result-object p0

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

.method protected onPreExecute()V
    .locals 1

    .line 1
    invoke-super {p0}, Landroid/os/AsyncTask;->onPreExecute()V

    const-string p0, "MusicModel"

    const-string v0, "MediaScanTask onPreExecute: "

    .line 2
    invoke-static {p0, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return-void
.end method

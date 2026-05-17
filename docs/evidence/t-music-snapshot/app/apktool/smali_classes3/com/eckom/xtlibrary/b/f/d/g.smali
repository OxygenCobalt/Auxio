.class Lcom/eckom/xtlibrary/b/f/d/g;
.super Ljava/lang/Object;
.source "MusicID3Model.java"

# interfaces
.implements Lcom/eckom/xtlibrary/b/f/f/h$f;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/f/d/t;->Fa(Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/f/d/t;

.field final synthetic tk:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/f/d/t;Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public ia(Ljava/lang/String;)V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p1, " \u7ed3\u675f\u6574\u7406,musicBean.mCList.mName\uff1a"

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1}, Lcom/eckom/xtlibrary/a/b;->e(Ljava/lang/Object;)V

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_5

    .line 3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    const-string v0, "usb"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_0

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Db()V

    goto :goto_0

    .line 5
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    const-string v0, "sd"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_1

    .line 6
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Cb()V

    goto :goto_0

    .line 7
    :cond_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    const-string v0, "iNand"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_2

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Eb()V

    .line 9
    :cond_2
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    const-string v0, "/.all"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_3

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    const/4 v1, 0x1

    if-ne v0, v1, :cond_3

    .line 10
    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Rb()V

    goto :goto_1

    .line 11
    :cond_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    const-string v0, "artist/"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_4

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object v0, p1, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    const/4 v1, 0x2

    if-ne v0, v1, :cond_4

    .line 12
    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/f/d/t;->Sb()V

    goto :goto_1

    .line 13
    :cond_4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/g;->tk:Ljava/lang/String;

    const-string v0, "album/"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_5

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/d/g;->this$0:Lcom/eckom/xtlibrary/b/f/d/t;

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/f/d/t;->Yc:Lcom/eckom/xtlibrary/b/f/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/f/b/e;->ud:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->qk:I

    const/4 v0, 0x3

    if-ne p1, v0, :cond_5

    .line 14
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/d/t;->Qb()V

    :cond_5
    :goto_1
    return-void
.end method

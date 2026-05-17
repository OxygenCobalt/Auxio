.class Lcom/eckom/xtlibrary/b/h/b/c;
.super Ljava/lang/Object;
.source "RadioModel.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/h/b/e;->af()V
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/c;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 6

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "FM - "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    sget-object v1, Ljava/util/Locale;->US:Ljava/util/Locale;

    const/4 v2, 0x1

    new-array v3, v2, [Ljava/lang/Object;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/c;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Wk:I

    int-to-float v4, v4

    const/high16 v5, 0x42c80000    # 100.0f

    div-float/2addr v4, v5

    invoke-static {v4}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v4

    const/4 v5, 0x0

    aput-object v4, v3, v5

    const-string v4, "%.1f"

    invoke-static {v1, v4, v3}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "MHz"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 2
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "AM - "

    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    sget-object v3, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v2, v2, [Ljava/lang/Object;

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/c;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Wk:I

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    aput-object v4, v2, v5

    const-string v4, "%d"

    invoke-static {v3, v4, v2}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v2, "KHz"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    .line 3
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/h/b/c;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/h/b/e;->e(Lcom/eckom/xtlibrary/b/h/b/e;)Landroid/content/Context;

    move-result-object v2

    invoke-virtual {v2}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object v2

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/c;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object p0

    iget p0, p0, Lcom/eckom/xtlibrary/b/h/a;->Xk:I

    const/4 v3, 0x2

    if-eq p0, v3, :cond_0

    goto :goto_0

    :cond_0
    move-object v0, v1

    :goto_0
    const-wide v3, 0x40ab580000000000L    # 3500.0

    invoke-static {v2, v0, v3, v4}, Lcom/eckom/xtlibrary/b/j/r;->a(Landroid/content/Context;Ljava/lang/CharSequence;D)Lcom/eckom/xtlibrary/b/j/r;

    move-result-object p0

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/j/r;->show()V

    return-void
.end method

.class Lcom/eckom/xtlibrary/b/i/j;
.super Ljava/lang/Object;
.source "ThemeManager.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/i/k;->e(Lcom/eckom/xtlibrary/b/i/m;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/i/k;

.field final synthetic val$info:Lcom/eckom/xtlibrary/b/i/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/i/k;Lcom/eckom/xtlibrary/b/i/m;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/i/j;->this$0:Lcom/eckom/xtlibrary/b/i/k;

    iput-object p2, p0, Lcom/eckom/xtlibrary/b/i/j;->val$info:Lcom/eckom/xtlibrary/b/i/m;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 8

    const/4 v0, 0x1

    new-array v1, v0, [Ljava/lang/Object;

    .line 1
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/i/j;->this$0:Lcom/eckom/xtlibrary/b/i/k;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/i/k;->a(Lcom/eckom/xtlibrary/b/i/k;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/List;->size()I

    move-result v2

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    const/4 v3, 0x0

    aput-object v2, v1, v3

    const-string v2, "ThemeManager"

    const-string v4, "theme switch start. size = %s"

    invoke-static {v2, v4, v1}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/i/j;->this$0:Lcom/eckom/xtlibrary/b/i/k;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/i/k;->a(Lcom/eckom/xtlibrary/b/i/k;)Ljava/util/List;

    move-result-object v1

    .line 3
    new-instance v4, Lcom/eckom/xtlibrary/b/i/k$c;

    const/4 v5, 0x0

    invoke-direct {v4, v5}, Lcom/eckom/xtlibrary/b/i/k$c;-><init>(Lcom/eckom/xtlibrary/b/i/i;)V

    invoke-static {v1, v4}, Ljava/util/Collections;->sort(Ljava/util/List;Ljava/util/Comparator;)V

    .line 4
    invoke-interface {v1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_0
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_0

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/i/c;

    .line 5
    iget-object v7, p0, Lcom/eckom/xtlibrary/b/i/j;->val$info:Lcom/eckom/xtlibrary/b/i/m;

    invoke-interface {v6, v7}, Lcom/eckom/xtlibrary/b/i/c;->a(Lcom/eckom/xtlibrary/b/i/m;)V

    goto :goto_0

    .line 6
    :cond_0
    new-instance v4, Lcom/eckom/xtlibrary/b/i/k$d;

    invoke-direct {v4, v5}, Lcom/eckom/xtlibrary/b/i/k$d;-><init>(Lcom/eckom/xtlibrary/b/i/i;)V

    invoke-static {v1, v4}, Ljava/util/Collections;->sort(Ljava/util/List;Ljava/util/Comparator;)V

    .line 7
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/i/j;->this$0:Lcom/eckom/xtlibrary/b/i/k;

    iget-object v6, p0, Lcom/eckom/xtlibrary/b/i/j;->val$info:Lcom/eckom/xtlibrary/b/i/m;

    invoke-static {v4, v1, v6}, Lcom/eckom/xtlibrary/b/i/k;->a(Lcom/eckom/xtlibrary/b/i/k;Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;)Z

    move-result v4

    .line 8
    new-instance v6, Lcom/eckom/xtlibrary/b/i/k$b;

    invoke-direct {v6, v5}, Lcom/eckom/xtlibrary/b/i/k$b;-><init>(Lcom/eckom/xtlibrary/b/i/i;)V

    invoke-static {v1, v6}, Ljava/util/Collections;->sort(Ljava/util/List;Ljava/util/Comparator;)V

    .line 9
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/i/j;->this$0:Lcom/eckom/xtlibrary/b/i/k;

    iget-object v6, p0, Lcom/eckom/xtlibrary/b/i/j;->val$info:Lcom/eckom/xtlibrary/b/i/m;

    xor-int/2addr v4, v0

    invoke-static {v5, v1, v6, v4}, Lcom/eckom/xtlibrary/b/i/k;->a(Lcom/eckom/xtlibrary/b/i/k;Ljava/util/List;Lcom/eckom/xtlibrary/b/i/m;Z)V

    new-array v0, v0, [Ljava/lang/Object;

    .line 10
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/i/j;->this$0:Lcom/eckom/xtlibrary/b/i/k;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/i/k;->a(Lcom/eckom/xtlibrary/b/i/k;)Ljava/util/List;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/List;->size()I

    move-result p0

    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p0

    aput-object p0, v0, v3

    const-string p0, "theme switch finshed. size = %s"

    invoke-static {v2, p0, v0}, Lcom/eckom/xtlibrary/b/i/a;->a(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V

    return-void
.end method

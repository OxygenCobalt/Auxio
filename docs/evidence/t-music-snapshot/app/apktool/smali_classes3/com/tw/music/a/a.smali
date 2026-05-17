.class Lcom/tw/music/a/a;
.super Ljava/lang/Object;
.source "MusicAdapter.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/a/c;->a(Landroid/view/View;ILandroid/view/ViewGroup;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic rm:Lcom/tw/music/a/c$c;

.field final synthetic this$0:Lcom/tw/music/a/c;

.field final synthetic tk:Ljava/lang/String;

.field final synthetic val$name:Ljava/lang/String;

.field final synthetic val$position:I


# direct methods
.method constructor <init>(Lcom/tw/music/a/c;ILjava/lang/String;Ljava/lang/String;Lcom/tw/music/a/c$c;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    iput p2, p0, Lcom/tw/music/a/a;->val$position:I

    iput-object p3, p0, Lcom/tw/music/a/a;->val$name:Ljava/lang/String;

    iput-object p4, p0, Lcom/tw/music/a/a;->tk:Ljava/lang/String;

    iput-object p5, p0, Lcom/tw/music/a/a;->rm:Lcom/tw/music/a/c$c;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 5

    .line 1
    iget-object p1, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    invoke-static {p1}, Lcom/tw/music/a/c;->a(Lcom/tw/music/a/c;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p1

    iget p1, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v0, 0x1

    const/4 v1, 0x4

    if-ne p1, v1, :cond_0

    .line 2
    iget p1, p0, Lcom/tw/music/a/a;->val$position:I

    goto :goto_0

    .line 3
    :cond_0
    iget p1, p0, Lcom/tw/music/a/a;->val$position:I

    sub-int/2addr p1, v0

    .line 4
    :goto_0
    new-instance v1, Lcom/eckom/xtlibrary/b/f/b/f;

    iget-object v2, p0, Lcom/tw/music/a/a;->val$name:Ljava/lang/String;

    iget-object v3, p0, Lcom/tw/music/a/a;->tk:Ljava/lang/String;

    iget-object v4, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    invoke-static {v4}, Lcom/tw/music/a/c;->a(Lcom/tw/music/a/c;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v4, v4, p1

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    invoke-direct {v1, v2, v3, v4}, Lcom/eckom/xtlibrary/b/f/b/f;-><init>(Ljava/lang/String;Ljava/lang/String;Z)V

    .line 5
    iget-boolean v2, v1, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    if-nez v2, :cond_1

    .line 6
    iget-object v2, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    invoke-static {v2}, Lcom/tw/music/a/c;->a(Lcom/tw/music/a/c;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v2

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v2, v2, p1

    iput-boolean v0, v2, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    .line 7
    iget-object v2, p0, Lcom/tw/music/a/a;->rm:Lcom/tw/music/a/c$c;

    iget-object v2, v2, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    invoke-virtual {v2, v0}, Landroid/widget/ImageView;->setImageLevel(I)V

    goto :goto_1

    .line 8
    :cond_1
    iget-object v0, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    invoke-static {v0}, Lcom/tw/music/a/c;->a(Lcom/tw/music/a/c;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v0, v0, p1

    const/4 v2, 0x0

    iput-boolean v2, v0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    .line 9
    iget-object v0, p0, Lcom/tw/music/a/a;->rm:Lcom/tw/music/a/c$c;

    iget-object v0, v0, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    invoke-virtual {v0, v2}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 10
    :goto_1
    iget-object v0, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    invoke-static {v0}, Lcom/tw/music/a/c;->b(Lcom/tw/music/a/c;)Lcom/tw/music/a/c$a;

    move-result-object v0

    if-eqz v0, :cond_2

    .line 11
    iget-object v0, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    invoke-static {v0}, Lcom/tw/music/a/c;->b(Lcom/tw/music/a/c;)Lcom/tw/music/a/c$a;

    move-result-object v0

    iget-object p0, p0, Lcom/tw/music/a/a;->this$0:Lcom/tw/music/a/c;

    invoke-static {p0}, Lcom/tw/music/a/c;->a(Lcom/tw/music/a/c;)Lcom/eckom/xtlibrary/b/f/b/g;

    move-result-object p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object p0, p0, p1

    iget-boolean p0, p0, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    invoke-interface {v0, v1, p0}, Lcom/tw/music/a/c$a;->a(Lcom/eckom/xtlibrary/b/f/b/f;Z)V

    :cond_2
    return-void
.end method

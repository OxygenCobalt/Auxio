.class Lcom/tw/music/a/b;
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
.field final synthetic this$0:Lcom/tw/music/a/c;

.field final synthetic val$position:I


# direct methods
.method constructor <init>(Lcom/tw/music/a/c;I)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/a/b;->this$0:Lcom/tw/music/a/c;

    iput p2, p0, Lcom/tw/music/a/b;->val$position:I

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 0

    .line 1
    iget-object p1, p0, Lcom/tw/music/a/b;->this$0:Lcom/tw/music/a/c;

    invoke-static {p1}, Lcom/tw/music/a/c;->c(Lcom/tw/music/a/c;)Lcom/tw/music/a/c$b;

    move-result-object p1

    if-eqz p1, :cond_0

    .line 2
    iget-object p1, p0, Lcom/tw/music/a/b;->this$0:Lcom/tw/music/a/c;

    invoke-static {p1}, Lcom/tw/music/a/c;->c(Lcom/tw/music/a/c;)Lcom/tw/music/a/c$b;

    move-result-object p1

    iget p0, p0, Lcom/tw/music/a/b;->val$position:I

    invoke-interface {p1, p0}, Lcom/tw/music/a/c$b;->U(I)V

    :cond_0
    return-void
.end method

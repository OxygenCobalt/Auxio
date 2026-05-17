.class Lcom/tw/music/lrc/f;
.super Ljava/lang/Object;
.source "LrcView.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/lrc/LrcView;->fa(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/lrc/LrcView;

.field final synthetic val$position:I


# direct methods
.method constructor <init>(Lcom/tw/music/lrc/LrcView;I)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    iput p2, p0, Lcom/tw/music/lrc/f;->val$position:I

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {v0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 2
    :cond_0
    iget-object v0, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    iget v1, p0, Lcom/tw/music/lrc/f;->val$position:I

    invoke-static {v0, v1}, Lcom/tw/music/lrc/LrcView;->b(Lcom/tw/music/lrc/LrcView;I)I

    move-result v0

    .line 3
    iget-object v1, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v1}, Lcom/tw/music/lrc/LrcView;->g(Lcom/tw/music/lrc/LrcView;)I

    move-result v1

    if-eq v0, v1, :cond_2

    .line 4
    iget-object v1, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v1, v0}, Lcom/tw/music/lrc/LrcView;->c(Lcom/tw/music/lrc/LrcView;I)I

    .line 5
    iget-object v1, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v1}, Lcom/tw/music/lrc/LrcView;->h(Lcom/tw/music/lrc/LrcView;)Z

    move-result v1

    if-nez v1, :cond_1

    .line 6
    iget-object p0, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p0, v0}, Lcom/tw/music/lrc/LrcView;->d(Lcom/tw/music/lrc/LrcView;I)V

    goto :goto_0

    .line 7
    :cond_1
    iget-object p0, p0, Lcom/tw/music/lrc/f;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    :cond_2
    :goto_0
    return-void
.end method

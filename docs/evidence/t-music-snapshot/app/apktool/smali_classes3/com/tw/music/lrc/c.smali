.class Lcom/tw/music/lrc/c;
.super Ljava/lang/Object;
.source "LrcView.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/lrc/LrcView;->setLabel(Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/lrc/LrcView;

.field final synthetic val$label:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/tw/music/lrc/LrcView;Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/c;->this$0:Lcom/tw/music/lrc/LrcView;

    iput-object p2, p0, Lcom/tw/music/lrc/c;->val$label:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/c;->this$0:Lcom/tw/music/lrc/LrcView;

    iget-object v1, p0, Lcom/tw/music/lrc/c;->val$label:Ljava/lang/String;

    invoke-static {v0, v1}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;Ljava/lang/String;)Ljava/lang/String;

    .line 2
    iget-object p0, p0, Lcom/tw/music/lrc/c;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    return-void
.end method

.class Lcom/tw/music/lrc/e;
.super Ljava/lang/Object;
.source "LrcView.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/lrc/LrcView;->va(Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic Hm:Ljava/lang/String;

.field final synthetic this$0:Lcom/tw/music/lrc/LrcView;


# direct methods
.method constructor <init>(Lcom/tw/music/lrc/LrcView;Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/e;->this$0:Lcom/tw/music/lrc/LrcView;

    iput-object p2, p0, Lcom/tw/music/lrc/e;->Hm:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/e;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {v0}, Lcom/tw/music/lrc/LrcView;->reset()V

    .line 2
    iget-object v0, p0, Lcom/tw/music/lrc/e;->this$0:Lcom/tw/music/lrc/LrcView;

    iget-object v1, p0, Lcom/tw/music/lrc/e;->Hm:Ljava/lang/String;

    invoke-static {v0, v1}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;Ljava/lang/Object;)V

    .line 3
    new-instance v0, Lcom/tw/music/lrc/d;

    invoke-direct {v0, p0}, Lcom/tw/music/lrc/d;-><init>(Lcom/tw/music/lrc/e;)V

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/String;

    iget-object p0, p0, Lcom/tw/music/lrc/e;->Hm:Ljava/lang/String;

    const/4 v2, 0x0

    aput-object p0, v1, v2

    .line 4
    invoke-virtual {v0, v1}, Landroid/os/AsyncTask;->execute([Ljava/lang/Object;)Landroid/os/AsyncTask;

    return-void
.end method

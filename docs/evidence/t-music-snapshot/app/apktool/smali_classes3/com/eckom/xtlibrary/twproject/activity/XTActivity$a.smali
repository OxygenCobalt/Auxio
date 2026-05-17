.class Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;
.super Ljava/lang/Thread;
.source "XTActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = "a"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-direct {p0}, Ljava/lang/Thread;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    .line 1
    invoke-super {p0}, Ljava/lang/Thread;->run()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Qc()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Ljava/lang/String;)Ljava/lang/String;

    .line 3
    :cond_0
    :goto_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->b(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->na(Ljava/lang/String;)Z

    move-result v1

    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->b(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)Z

    if-nez v1, :cond_1

    invoke-virtual {p0}, Ljava/lang/Thread;->isInterrupted()Z

    move-result v0

    if-nez v0, :cond_1

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->d(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/os/Handler;

    move-result-object v0

    new-instance v1, Lcom/eckom/xtlibrary/twproject/activity/e;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/activity/e;-><init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;)V

    const-wide/16 v2, 0xbb8

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 5
    :try_start_0
    invoke-virtual {p0}, Ljava/lang/Thread;->isInterrupted()Z

    move-result v0

    if-nez v0, :cond_0

    .line 6
    invoke-static {v2, v3}, Ljava/lang/Thread;->sleep(J)V
    :try_end_0
    .catch Ljava/lang/InterruptedException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    .line 7
    invoke-virtual {v0}, Ljava/lang/InterruptedException;->printStackTrace()V

    .line 8
    invoke-static {}, Ljava/lang/Thread;->currentThread()Ljava/lang/Thread;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Thread;->interrupt()V

    goto :goto_0

    .line 9
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Z

    move-result v0

    if-eqz v0, :cond_2

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->c(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/app/AlertDialog;

    move-result-object v0

    if-eqz v0, :cond_2

    .line 10
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->c(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/app/AlertDialog;

    move-result-object p0

    invoke-virtual {p0}, Landroid/app/AlertDialog;->dismiss()V

    :cond_2
    return-void
.end method

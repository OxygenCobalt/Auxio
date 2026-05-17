.class Lcom/eckom/xtlibrary/twproject/activity/e;
.super Ljava/lang/Object;
.source "XTActivity.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->run()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/e;->this$1:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/e;->this$1:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->c(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/app/AlertDialog;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/e;->this$1:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->c(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/app/AlertDialog;

    move-result-object v0

    invoke-virtual {v0}, Landroid/app/AlertDialog;->isShowing()Z

    move-result v0

    if-nez v0, :cond_0

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/e;->this$1:Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity$a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->c(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)Landroid/app/AlertDialog;

    move-result-object p0

    invoke-virtual {p0}, Landroid/app/AlertDialog;->show()V

    :cond_0
    return-void
.end method

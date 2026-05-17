.class Lcom/eckom/xtlibrary/twproject/video/model/u;
.super Landroid/content/BroadcastReceiver;
.source "VideoModel.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/z;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/z;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/u;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    return-void
.end method


# virtual methods
.method public onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 2

    .line 1
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "android.intent.action.CLOSE_SYSTEM_DIALOGS"

    .line 2
    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    const/4 v1, 0x0

    if-eqz v0, :cond_1

    const-string p1, "reason"

    .line 3
    invoke-virtual {p2, p1}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    const-string p2, "homekey"

    .line 4
    invoke-virtual {p2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p2

    if-eqz p2, :cond_0

    .line 5
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/u;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    const/4 v0, 0x1

    invoke-static {p2, v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->g(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)Z

    :cond_0
    const-string p2, "recentapps"

    .line 6
    invoke-virtual {p2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_2

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/u;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1, v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->g(Lcom/eckom/xtlibrary/twproject/video/model/z;Z)Z

    .line 8
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/u;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    goto :goto_0

    :cond_1
    const-string p2, "android.intent.action.LOCALE_CHANGED"

    .line 9
    invoke-static {p1, p2}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_2

    .line 10
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/u;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->r(Lcom/eckom/xtlibrary/twproject/video/model/z;)Z

    move-result p1

    if-eqz p1, :cond_2

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/u;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    :cond_2
    :goto_0
    return-void
.end method

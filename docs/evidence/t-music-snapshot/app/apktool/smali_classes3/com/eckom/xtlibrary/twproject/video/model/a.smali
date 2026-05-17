.class Lcom/eckom/xtlibrary/twproject/video/model/a;
.super Ljava/lang/Object;
.source "VideoIjkModel.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/m;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/a;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 3

    .line 1
    new-instance p1, Landroid/content/Intent;

    invoke-direct {p1}, Landroid/content/Intent;-><init>()V

    const-string v0, "com.tw.video"

    const-string v1, "com.tw.video.VideoActivity"

    .line 2
    invoke-virtual {p1, v0, v1}, Landroid/content/Intent;->setClassName(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const/high16 v0, 0x10000000

    .line 3
    invoke-virtual {p1, v0}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    .line 4
    :try_start_0
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x1f

    const/4 v2, 0x0

    if-lt v0, v1, :cond_0

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/a;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->h(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/content/Context;

    move-result-object p0

    const/high16 v0, 0x4000000

    invoke-static {p0, v2, p1, v0}, Landroid/app/PendingIntent;->getActivity(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object p0

    goto :goto_0

    .line 6
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/a;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->h(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/content/Context;

    move-result-object p0

    invoke-static {p0, v2, p1, v2}, Landroid/app/PendingIntent;->getActivity(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object p0

    .line 7
    :goto_0
    invoke-virtual {p0}, Landroid/app/PendingIntent;->send()V
    :try_end_0
    .catch Landroid/app/PendingIntent$CanceledException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    :catch_0
    move-exception p0

    .line 8
    invoke-virtual {p0}, Landroid/app/PendingIntent$CanceledException;->printStackTrace()V

    :goto_1
    return-void
.end method

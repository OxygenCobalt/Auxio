.class Lcom/eckom/xtlibrary/twproject/activity/a;
.super Ljava/lang/Object;
.source "XTActivity.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 1

    .line 1
    iget p1, p1, Landroid/os/Message;->what:I

    const v0, 0xff01

    if-eq p1, v0, :cond_0

    goto :goto_0

    .line 2
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/a;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->ub:Z

    if-eqz p1, :cond_1

    .line 3
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Na()V

    :cond_1
    :goto_0
    const/4 p0, 0x1

    return p0
.end method

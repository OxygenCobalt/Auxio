.class Lcom/eckom/xtlibrary/twproject/video/model/j;
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/j;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/j;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    const/4 p1, 0x0

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    .line 2
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/m;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object p0

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->w(Z)V

    return-void
.end method

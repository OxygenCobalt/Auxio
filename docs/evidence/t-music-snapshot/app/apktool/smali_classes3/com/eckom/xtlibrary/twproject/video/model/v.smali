.class Lcom/eckom/xtlibrary/twproject/video/model/v;
.super Ljava/lang/Object;
.source "VideoModel.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/v;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/v;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    const/4 p1, 0x0

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->E(Z)V

    return-void
.end method

.class Lcom/eckom/xtlibrary/twproject/video/model/w;
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 8

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p1

    iget p1, p1, Landroid/view/WindowManager$LayoutParams;->width:I

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v0

    iget v0, v0, Landroid/view/WindowManager$LayoutParams;->height:I

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->width:I

    int-to-double v2, v2

    const-wide v4, 0x3fe8f5c28f5c28f6L    # 0.78

    mul-double/2addr v2, v4

    double-to-int v2, v2

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->width:I

    .line 4
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->width:I

    int-to-double v2, v2

    const-wide v4, 0x3fe3333333333333L    # 0.6

    mul-double/2addr v2, v4

    double-to-int v2, v2

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->height:I

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget v1, v1, Landroid/view/WindowManager$LayoutParams;->width:I

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v2

    iget v2, v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Nd:I

    int-to-double v2, v2

    const-wide v4, 0x3fd3333333333333L    # 0.3

    mul-double/2addr v2, v4

    double-to-int v2, v2

    const-wide v6, 0x3fc999999999999aL    # 0.2

    if-lt v1, v2, :cond_0

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    .line 6
    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget v1, v1, Landroid/view/WindowManager$LayoutParams;->height:I

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v2

    iget v2, v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Nd:I

    int-to-double v2, v2

    mul-double/2addr v2, v6

    double-to-int v2, v2

    if-ge v1, v2, :cond_1

    .line 7
    :cond_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v2

    iget v2, v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Nd:I

    int-to-double v2, v2

    mul-double/2addr v2, v4

    double-to-int v2, v2

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->width:I

    .line 8
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/model/z;->kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;

    move-result-object v2

    iget v2, v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Nd:I

    int-to-double v2, v2

    mul-double/2addr v2, v6

    double-to-int v2, v2

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->height:I

    .line 9
    :cond_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->x:I

    div-int/lit8 p1, p1, 0x2

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v3

    iget v3, v3, Landroid/view/WindowManager$LayoutParams;->width:I

    div-int/lit8 v3, v3, 0x2

    sub-int/2addr p1, v3

    add-int/2addr v2, p1

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->x:I

    .line 10
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p1

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget v1, v1, Landroid/view/WindowManager$LayoutParams;->y:I

    div-int/lit8 v0, v0, 0x2

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->height:I

    div-int/lit8 v2, v2, 0x2

    sub-int/2addr v0, v2

    add-int/2addr v1, v0

    iput v1, p1, Landroid/view/WindowManager$LayoutParams;->y:I

    .line 11
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->p(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager;

    move-result-object p1

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->i(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v0

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    invoke-interface {p1, v0, v1}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 12
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/w;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->q(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    return-void
.end method

.class Lcom/eckom/xtlibrary/twproject/video/model/x;
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 6

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p1

    iget p1, p1, Landroid/view/WindowManager$LayoutParams;->width:I

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v0

    iget v0, v0, Landroid/view/WindowManager$LayoutParams;->height:I

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->width:I

    int-to-double v2, v2

    const-wide v4, 0x3ff3851eb851eb85L    # 1.22

    mul-double/2addr v2, v4

    double-to-int v2, v2

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->width:I

    .line 4
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->width:I

    int-to-double v2, v2

    const-wide v4, 0x3fe3333333333333L    # 0.6

    mul-double/2addr v2, v4

    double-to-int v2, v2

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->height:I

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget v1, v1, Landroid/view/WindowManager$LayoutParams;->width:I

    sget v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Rd:I

    if-gt v1, v2, :cond_1

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget v1, v1, Landroid/view/WindowManager$LayoutParams;->height:I

    sget v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Sd:I

    if-le v1, v2, :cond_0

    goto :goto_0

    .line 6
    :cond_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->x:I

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v3}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v3

    iget v3, v3, Landroid/view/WindowManager$LayoutParams;->width:I

    div-int/lit8 v3, v3, 0x2

    div-int/lit8 p1, p1, 0x2

    sub-int/2addr v3, p1

    sub-int/2addr v2, v3

    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->x:I

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p1

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    iget v1, v1, Landroid/view/WindowManager$LayoutParams;->y:I

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v2

    iget v2, v2, Landroid/view/WindowManager$LayoutParams;->height:I

    div-int/lit8 v2, v2, 0x2

    div-int/lit8 v0, v0, 0x2

    sub-int/2addr v2, v0

    sub-int/2addr v1, v2

    iput v1, p1, Landroid/view/WindowManager$LayoutParams;->y:I

    goto :goto_1

    .line 8
    :cond_1
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p1

    sget v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Rd:I

    add-int/lit8 v0, v0, -0x1

    iput v0, p1, Landroid/view/WindowManager$LayoutParams;->width:I

    .line 9
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p1

    sget v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Sd:I

    int-to-double v0, v0

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/z;->s(Lcom/eckom/xtlibrary/twproject/video/model/z;)D

    move-result-wide v2

    sub-double/2addr v0, v2

    double-to-int v0, v0

    iput v0, p1, Landroid/view/WindowManager$LayoutParams;->height:I

    .line 10
    :goto_1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->p(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager;

    move-result-object p1

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->i(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/View;

    move-result-object v0

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {v1}, Lcom/eckom/xtlibrary/twproject/video/model/z;->o(Lcom/eckom/xtlibrary/twproject/video/model/z;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v1

    invoke-interface {p1, v0, v1}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/x;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/z;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/z;->q(Lcom/eckom/xtlibrary/twproject/video/model/z;)V

    return-void
.end method

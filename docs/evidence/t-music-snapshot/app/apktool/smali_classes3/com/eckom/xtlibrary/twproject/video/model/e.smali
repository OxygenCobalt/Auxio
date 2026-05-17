.class Lcom/eckom/xtlibrary/twproject/video/model/e;
.super Ljava/lang/Object;
.source "VideoIjkModel.java"

# interfaces
.implements Landroid/view/View$OnTouchListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/video/model/m;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field private startX:F

.field private startY:F

.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

.field private uh:F

.field private vh:F


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 p1, 0x0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startX:F

    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startY:F

    .line 3
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->uh:F

    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->vh:F

    return-void
.end method


# virtual methods
.method public onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 5

    .line 1
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->r(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z

    move-result p1

    if-eqz p1, :cond_6

    const/4 p1, 0x1

    .line 2
    :try_start_0
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawX()F

    move-result v0

    .line 3
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result v1

    float-to-double v1, v1

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->s(Lcom/eckom/xtlibrary/twproject/video/model/m;)D

    move-result-wide v3

    sub-double/2addr v1, v3

    double-to-float v1, v1

    .line 4
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getAction()I

    move-result v2

    if-eqz v2, :cond_5

    if-eq v2, p1, :cond_1

    const/4 p2, 0x2

    if-eq v2, p2, :cond_0

    goto/16 :goto_1

    .line 5
    :cond_0
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->o(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p2

    iget v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startX:F

    sub-float/2addr v0, v2

    float-to-int v0, v0

    iput v0, p2, Landroid/view/WindowManager$LayoutParams;->x:I

    .line 6
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->o(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p2

    iget v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startY:F

    sub-float/2addr v1, v0

    float-to-int v0, v1

    iput v0, p2, Landroid/view/WindowManager$LayoutParams;->y:I

    .line 7
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->p(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager;

    move-result-object p2

    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->i(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v0

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->o(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p0

    invoke-interface {p2, v0, p0}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    goto/16 :goto_1

    .line 8
    :cond_1
    iget v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->uh:F

    sub-float/2addr v0, v1

    float-to-int v0, v0

    .line 9
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result v1

    iget v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->vh:F

    sub-float/2addr v1, v2

    float-to-int v1, v1

    .line 10
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawX()F

    move-result v2

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getX()F

    move-result p2

    sub-float/2addr v2, p2

    float-to-int p2, v2

    if-nez p2, :cond_2

    .line 11
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->o(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p2

    iput p1, p2, Landroid/view/WindowManager$LayoutParams;->x:I

    goto :goto_0

    .line 12
    :cond_2
    sget v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Rd:I

    if-ne p2, v2, :cond_3

    .line 13
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->o(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p2

    sget v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Rd:I

    sub-int/2addr v2, p1

    iput v2, p2, Landroid/view/WindowManager$LayoutParams;->x:I

    .line 14
    :cond_3
    :goto_0
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->p(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager;

    move-result-object p2

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->i(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;

    move-result-object v2

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {v3}, Lcom/eckom/xtlibrary/twproject/video/model/m;->o(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager$LayoutParams;

    move-result-object v3

    invoke-interface {p2, v2, v3}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    const/16 p2, -0xa

    if-le v0, p2, :cond_4

    const/16 v2, 0xa

    if-ge v0, v2, :cond_4

    if-le v1, p2, :cond_4

    if-ge v1, v2, :cond_4

    .line 15
    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->this$0:Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-static {p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;

    move-result-object p2

    const v0, 0xff06

    invoke-virtual {p2, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    :cond_4
    const/4 p2, 0x0

    .line 16
    iput p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startX:F

    .line 17
    iput p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startY:F

    .line 18
    iput p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->uh:F

    .line 19
    iput p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->vh:F

    goto :goto_1

    .line 20
    :cond_5
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getX()F

    move-result v0

    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startX:F

    .line 21
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getY()F

    move-result v0

    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->startY:F

    .line 22
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawX()F

    move-result v0

    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->uh:F

    .line 23
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result p2

    iput p2, p0, Lcom/eckom/xtlibrary/twproject/video/model/e;->vh:F
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    :goto_1
    return p1

    :cond_6
    const/4 p0, 0x0

    return p0
.end method

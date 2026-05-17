.class Lcom/eckom/xtlibrary/b/a/h/b;
.super Ljava/lang/Object;
.source "VoiceCallView.java"

# interfaces
.implements Landroid/view/View$OnTouchListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/a/h/d;-><init>(Landroid/content/Context;Lcom/eckom/xtlibrary/b/a/d/h;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/a/h/d;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/a/h/d;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 5

    .line 1
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawX()F

    move-result p1

    .line 2
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result v0

    float-to-double v0, v0

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {v2}, Lcom/eckom/xtlibrary/b/a/h/d;->a(Lcom/eckom/xtlibrary/b/a/h/d;)D

    move-result-wide v2

    sub-double/2addr v0, v2

    double-to-float v0, v0

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/h/d;->b(Lcom/eckom/xtlibrary/b/a/h/d;)F

    move-result v1

    sub-float v1, p1, v1

    float-to-int v1, v1

    .line 4
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result v2

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/a/h/d;->d(Lcom/eckom/xtlibrary/b/a/h/d;)F

    move-result v3

    sub-float/2addr v2, v3

    float-to-int v2, v2

    .line 5
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getAction()I

    move-result v3

    const/4 v4, 0x1

    if-eqz v3, :cond_2

    if-eq v3, v4, :cond_3

    const/4 p2, 0x2

    if-eq v3, p2, :cond_0

    goto :goto_0

    :cond_0
    const/16 p2, -0xa

    if-lt v1, p2, :cond_1

    const/16 v3, 0xa

    if-gt v1, v3, :cond_1

    if-lt v2, p2, :cond_1

    if-le v2, v3, :cond_3

    .line 6
    :cond_1
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {p2}, Lcom/eckom/xtlibrary/b/a/h/d;->e(Lcom/eckom/xtlibrary/b/a/h/d;)F

    move-result v1

    sub-float/2addr p1, v1

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/h/d;->f(Lcom/eckom/xtlibrary/b/a/h/d;)F

    move-result p0

    sub-float/2addr v0, p0

    invoke-static {p2, p1, v0}, Lcom/eckom/xtlibrary/b/a/h/d;->a(Lcom/eckom/xtlibrary/b/a/h/d;FF)V

    goto :goto_0

    .line 7
    :cond_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getX()F

    move-result v0

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/b/a/h/d;->a(Lcom/eckom/xtlibrary/b/a/h/d;F)F

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getY()F

    move-result p2

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/a/h/d;->b(Lcom/eckom/xtlibrary/b/a/h/d;F)F

    .line 9
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/h/d;->i(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/view/WindowManager;

    move-result-object p1

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {p2}, Lcom/eckom/xtlibrary/b/a/h/d;->g(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/view/View;

    move-result-object p2

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/b;->this$0:Lcom/eckom/xtlibrary/b/a/h/d;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/h/d;->h(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/view/WindowManager$LayoutParams;

    move-result-object p0

    invoke-interface {p1, p2, p0}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    :cond_3
    :goto_0
    return v4
.end method

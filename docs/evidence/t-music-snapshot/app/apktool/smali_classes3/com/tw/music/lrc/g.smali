.class Lcom/tw/music/lrc/g;
.super Landroid/view/GestureDetector$SimpleOnGestureListener;
.source "LrcView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/lrc/LrcView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/lrc/LrcView;


# direct methods
.method constructor <init>(Lcom/tw/music/lrc/LrcView;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-direct {p0}, Landroid/view/GestureDetector$SimpleOnGestureListener;-><init>()V

    return-void
.end method


# virtual methods
.method public onDown(Landroid/view/MotionEvent;)Z
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {v0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v0}, Lcom/tw/music/lrc/LrcView;->i(Lcom/tw/music/lrc/LrcView;)Lcom/tw/music/lrc/LrcView$a;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->j(Lcom/tw/music/lrc/LrcView;)Landroid/widget/Scroller;

    move-result-object p1

    const/4 v0, 0x1

    invoke-virtual {p1, v0}, Landroid/widget/Scroller;->forceFinished(Z)V

    .line 3
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;)Ljava/lang/Runnable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    .line 4
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1, v0}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;Z)Z

    .line 5
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1, v0}, Lcom/tw/music/lrc/LrcView;->c(Lcom/tw/music/lrc/LrcView;Z)Z

    .line 6
    iget-object p0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    return v0

    .line 7
    :cond_0
    invoke-super {p0, p1}, Landroid/view/GestureDetector$SimpleOnGestureListener;->onDown(Landroid/view/MotionEvent;)Z

    move-result p0

    return p0
.end method

.method public onFling(Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z
    .locals 9

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {v0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->j(Lcom/tw/music/lrc/LrcView;)Landroid/widget/Scroller;

    move-result-object v0

    const/4 v1, 0x0

    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->b(Lcom/tw/music/lrc/LrcView;)F

    move-result p1

    float-to-int v2, p1

    const/4 v3, 0x0

    float-to-int v4, p4

    const/4 v5, 0x0

    const/4 v6, 0x0

    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->c(Lcom/tw/music/lrc/LrcView;)Ljava/util/List;

    move-result-object p2

    invoke-interface {p2}, Ljava/util/List;->size()I

    move-result p2

    const/4 p3, 0x1

    sub-int/2addr p2, p3

    invoke-static {p1, p2}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;I)F

    move-result p1

    float-to-int v7, p1

    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    const/4 p2, 0x0

    invoke-static {p1, p2}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;I)F

    move-result p1

    float-to-int v8, p1

    invoke-virtual/range {v0 .. v8}, Landroid/widget/Scroller;->fling(IIIIIIII)V

    .line 3
    iget-object p0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p0, p3}, Lcom/tw/music/lrc/LrcView;->b(Lcom/tw/music/lrc/LrcView;Z)Z

    return p3

    .line 4
    :cond_0
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/GestureDetector$SimpleOnGestureListener;->onFling(Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z

    move-result p0

    return p0
.end method

.method public onScroll(Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {v0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    neg-float p2, p4

    invoke-static {p1, p2}, Lcom/tw/music/lrc/LrcView;->b(Lcom/tw/music/lrc/LrcView;F)F

    .line 3
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->b(Lcom/tw/music/lrc/LrcView;)F

    move-result p2

    iget-object p3, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    const/4 p4, 0x0

    invoke-static {p3, p4}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;I)F

    move-result p3

    invoke-static {p2, p3}, Ljava/lang/Math;->min(FF)F

    move-result p2

    invoke-static {p1, p2}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;F)F

    .line 4
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->b(Lcom/tw/music/lrc/LrcView;)F

    move-result p2

    iget-object p3, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p3}, Lcom/tw/music/lrc/LrcView;->c(Lcom/tw/music/lrc/LrcView;)Ljava/util/List;

    move-result-object p4

    invoke-interface {p4}, Ljava/util/List;->size()I

    move-result p4

    const/4 v0, 0x1

    sub-int/2addr p4, v0

    invoke-static {p3, p4}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;I)F

    move-result p3

    invoke-static {p2, p3}, Ljava/lang/Math;->max(FF)F

    move-result p2

    invoke-static {p1, p2}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;F)F

    .line 5
    iget-object p0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    return v0

    .line 6
    :cond_0
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/GestureDetector$SimpleOnGestureListener;->onScroll(Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z

    move-result p0

    return p0
.end method

.method public onSingleTapConfirmed(Landroid/view/MotionEvent;)Z
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {v0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v0}, Lcom/tw/music/lrc/LrcView;->h(Lcom/tw/music/lrc/LrcView;)Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v0}, Lcom/tw/music/lrc/LrcView;->d(Lcom/tw/music/lrc/LrcView;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getBounds()Landroid/graphics/Rect;

    move-result-object v0

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    move-result v1

    float-to-int v1, v1

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    move-result v2

    float-to-int v2, v2

    invoke-virtual {v0, v1, v2}, Landroid/graphics/Rect;->contains(II)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v0}, Lcom/tw/music/lrc/LrcView;->e(Lcom/tw/music/lrc/LrcView;)I

    move-result v0

    .line 3
    iget-object v1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v1}, Lcom/tw/music/lrc/LrcView;->c(Lcom/tw/music/lrc/LrcView;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/tw/music/lrc/a;

    invoke-virtual {v1}, Lcom/tw/music/lrc/a;->getTime()J

    move-result-wide v1

    .line 4
    iget-object v3, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v3}, Lcom/tw/music/lrc/LrcView;->i(Lcom/tw/music/lrc/LrcView;)Lcom/tw/music/lrc/LrcView$a;

    move-result-object v3

    if-eqz v3, :cond_0

    iget-object v3, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {v3}, Lcom/tw/music/lrc/LrcView;->i(Lcom/tw/music/lrc/LrcView;)Lcom/tw/music/lrc/LrcView$a;

    move-result-object v3

    invoke-interface {v3, v1, v2}, Lcom/tw/music/lrc/LrcView$a;->f(J)Z

    move-result v1

    if-eqz v1, :cond_0

    .line 5
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    const/4 v1, 0x0

    invoke-static {p1, v1}, Lcom/tw/music/lrc/LrcView;->c(Lcom/tw/music/lrc/LrcView;Z)Z

    .line 6
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;)Ljava/lang/Runnable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    .line 7
    iget-object p1, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1, v0}, Lcom/tw/music/lrc/LrcView;->c(Lcom/tw/music/lrc/LrcView;I)I

    .line 8
    iget-object p0, p0, Lcom/tw/music/lrc/g;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    const/4 p0, 0x1

    return p0

    .line 9
    :cond_0
    invoke-super {p0, p1}, Landroid/view/GestureDetector$SimpleOnGestureListener;->onSingleTapConfirmed(Landroid/view/MotionEvent;)Z

    move-result p0

    return p0
.end method

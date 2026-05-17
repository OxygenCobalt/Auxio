.class Lcom/tw/music/lrc/i;
.super Ljava/lang/Object;
.source "LrcView.java"

# interfaces
.implements Landroid/animation/ValueAnimator$AnimatorUpdateListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/lrc/LrcView;->a(IJ)V
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
    iput-object p1, p0, Lcom/tw/music/lrc/i;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onAnimationUpdate(Landroid/animation/ValueAnimator;)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/i;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p1}, Landroid/animation/ValueAnimator;->getAnimatedValue()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/lang/Float;

    invoke-virtual {p1}, Ljava/lang/Float;->floatValue()F

    move-result p1

    invoke-static {v0, p1}, Lcom/tw/music/lrc/LrcView;->a(Lcom/tw/music/lrc/LrcView;F)F

    .line 2
    iget-object p0, p0, Lcom/tw/music/lrc/i;->this$0:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    return-void
.end method

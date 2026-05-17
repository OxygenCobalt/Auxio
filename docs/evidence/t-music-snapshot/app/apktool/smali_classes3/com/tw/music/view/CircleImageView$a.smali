.class public Lcom/tw/music/view/CircleImageView$a;
.super Landroid/view/animation/RotateAnimation;
.source "CircleImageView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/view/CircleImageView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = "a"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/view/CircleImageView;


# direct methods
.method public constructor <init>(Lcom/tw/music/view/CircleImageView;FFFF)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/view/CircleImageView$a;->this$0:Lcom/tw/music/view/CircleImageView;

    .line 2
    invoke-direct {p0, p2, p3, p4, p5}, Landroid/view/animation/RotateAnimation;-><init>(FFFF)V

    return-void
.end method


# virtual methods
.method protected applyTransformation(FLandroid/view/animation/Transformation;)V
    .locals 0

    .line 1
    invoke-super {p0, p1, p2}, Landroid/view/animation/RotateAnimation;->applyTransformation(FLandroid/view/animation/Transformation;)V

    .line 2
    iget-object p0, p0, Lcom/tw/music/view/CircleImageView$a;->this$0:Lcom/tw/music/view/CircleImageView;

    const/high16 p2, 0x43b40000    # 360.0f

    mul-float/2addr p1, p2

    invoke-static {p0, p1}, Lcom/tw/music/view/CircleImageView;->a(Lcom/tw/music/view/CircleImageView;F)F

    return-void
.end method

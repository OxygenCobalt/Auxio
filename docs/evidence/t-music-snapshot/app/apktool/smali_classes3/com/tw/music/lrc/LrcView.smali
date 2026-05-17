.class public Lcom/tw/music/lrc/LrcView;
.super Landroid/view/View;
.source "LrcView.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/tw/music/lrc/LrcView$a;
    }
.end annotation


# instance fields
.field private Ne:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/tw/music/lrc/a;",
            ">;"
        }
    .end annotation
.end field

.field private Oe:Landroid/text/TextPaint;

.field private Pe:Landroid/text/TextPaint;

.field private Qe:Landroid/graphics/Paint$FontMetrics;

.field private Re:Landroid/graphics/drawable/Drawable;

.field private Se:J

.field private Te:I

.field private Ue:F

.field private Ve:I

.field private We:F

.field private Xe:I

.field private Ye:I

.field private Ze:I

.field private df:I

.field private ef:I

.field private ff:Ljava/lang/String;

.field private gf:F

.field private hf:Lcom/tw/music/lrc/LrcView$a;

.field private if:Ljava/lang/Object;

.field private jf:Z

.field private kf:Z

.field private lf:Z

.field private mAnimator:Landroid/animation/ValueAnimator;

.field private mCurrentLine:I

.field private mDividerHeight:F

.field private mGestureDetector:Landroid/view/GestureDetector;

.field private mOffset:F

.field private mScroller:Landroid/widget/Scroller;

.field private mf:I

.field private nf:Landroid/view/GestureDetector$SimpleOnGestureListener;

.field private of:Ljava/lang/Runnable;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1

    const/4 v0, 0x0

    .line 1
    invoke-direct {p0, p1, v0}, Lcom/tw/music/lrc/LrcView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1

    const/4 v0, 0x0

    .line 2
    invoke-direct {p0, p1, p2, v0}, Lcom/tw/music/lrc/LrcView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 0

    .line 3
    invoke-direct {p0, p1, p2, p3}, Landroid/view/View;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    .line 4
    new-instance p1, Ljava/util/ArrayList;

    invoke-direct {p1}, Ljava/util/ArrayList;-><init>()V

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    .line 5
    new-instance p1, Landroid/text/TextPaint;

    invoke-direct {p1}, Landroid/text/TextPaint;-><init>()V

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    .line 6
    new-instance p1, Landroid/text/TextPaint;

    invoke-direct {p1}, Landroid/text/TextPaint;-><init>()V

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    .line 7
    new-instance p1, Lcom/tw/music/lrc/g;

    invoke-direct {p1, p0}, Lcom/tw/music/lrc/g;-><init>(Lcom/tw/music/lrc/LrcView;)V

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->nf:Landroid/view/GestureDetector$SimpleOnGestureListener;

    .line 8
    new-instance p1, Lcom/tw/music/lrc/h;

    invoke-direct {p1, p0}, Lcom/tw/music/lrc/h;-><init>(Lcom/tw/music/lrc/LrcView;)V

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->of:Ljava/lang/Runnable;

    .line 9
    invoke-direct {p0, p2}, Lcom/tw/music/lrc/LrcView;->b(Landroid/util/AttributeSet;)V

    return-void
.end method

.method private Ee()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->getCenterLine()I

    move-result v0

    const-wide/16 v1, 0x3e8

    invoke-direct {p0, v0, v1, v2}, Lcom/tw/music/lrc/LrcView;->a(IJ)V

    return-void
.end method

.method private Fe()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->mAnimator:Landroid/animation/ValueAnimator;

    if-eqz v0, :cond_0

    invoke-virtual {v0}, Landroid/animation/ValueAnimator;->isRunning()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->mAnimator:Landroid/animation/ValueAnimator;

    invoke-virtual {p0}, Landroid/animation/ValueAnimator;->end()V

    :cond_0
    return-void
.end method

.method private Ge()V
    .locals 5

    .line 1
    invoke-virtual {p0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-eqz v0, :cond_2

    invoke-virtual {p0}, Landroid/view/View;->getWidth()I

    move-result v0

    if-nez v0, :cond_0

    goto :goto_1

    .line 2
    :cond_0
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-static {v0}, Ljava/util/Collections;->sort(Ljava/util/List;)V

    .line 3
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/tw/music/lrc/a;

    .line 4
    iget-object v2, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->getLrcWidth()F

    move-result v3

    float-to-int v3, v3

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->mf:I

    invoke-virtual {v1, v2, v3, v4}, Lcom/tw/music/lrc/a;->a(Landroid/text/TextPaint;II)V

    goto :goto_0

    .line 5
    :cond_1
    invoke-virtual {p0}, Landroid/view/View;->getHeight()I

    move-result v0

    div-int/lit8 v0, v0, 0x2

    int-to-float v0, v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    :cond_2
    :goto_1
    return-void
.end method

.method private He()V
    .locals 2

    .line 1
    :try_start_0
    const-class p0, Landroid/animation/ValueAnimator;

    const-string v0, "sDurationScale"

    invoke-virtual {p0, v0}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object p0

    const/4 v0, 0x1

    .line 2
    invoke-virtual {p0, v0}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    const/4 v0, 0x0

    const/high16 v1, 0x3f800000    # 1.0f

    .line 3
    invoke-virtual {p0, v0, v1}, Ljava/lang/reflect/Field;->setFloat(Ljava/lang/Object;F)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 4
    invoke-virtual {p0}, Ljava/lang/Exception;->printStackTrace()V

    :goto_0
    return-void
.end method

.method private Ja(I)I
    .locals 8

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    const/4 v1, 0x0

    move v2, v0

    move v0, v1

    :cond_0
    :goto_0
    if-gt v0, v2, :cond_3

    add-int v3, v0, v2

    .line 2
    div-int/lit8 v3, v3, 0x2

    .line 3
    iget-object v4, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v4, v3}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/tw/music/lrc/a;

    invoke-virtual {v4}, Lcom/tw/music/lrc/a;->getTime()J

    move-result-wide v4

    int-to-long v6, p1

    cmp-long v4, v6, v4

    if-gez v4, :cond_1

    add-int/lit8 v3, v3, -0x1

    move v2, v3

    goto :goto_0

    :cond_1
    add-int/lit8 v0, v3, 0x1

    .line 4
    iget-object v4, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v4}, Ljava/util/List;->size()I

    move-result v4

    if-ge v0, v4, :cond_2

    iget-object v4, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v4, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/tw/music/lrc/a;

    invoke-virtual {v4}, Lcom/tw/music/lrc/a;->getTime()J

    move-result-wide v4

    cmp-long v4, v6, v4

    if-gez v4, :cond_0

    :cond_2
    return v3

    :cond_3
    return v1
.end method

.method private Ka(I)F
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/tw/music/lrc/a;

    invoke-virtual {v0}, Lcom/tw/music/lrc/a;->getOffset()F

    move-result v0

    const/4 v1, 0x1

    cmpl-float v0, v0, v1

    if-nez v0, :cond_1

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->getHeight()I

    move-result v0

    div-int/lit8 v0, v0, 0x2

    int-to-float v0, v0

    const/4 v1, 0x1

    :goto_0
    if-gt v1, p1, :cond_0

    .line 3
    iget-object v2, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    add-int/lit8 v3, v1, -0x1

    invoke-interface {v2, v3}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/tw/music/lrc/a;

    invoke-virtual {v2}, Lcom/tw/music/lrc/a;->getHeight()I

    move-result v2

    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v3, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/tw/music/lrc/a;

    invoke-virtual {v3}, Lcom/tw/music/lrc/a;->getHeight()I

    move-result v3

    add-int/2addr v2, v3

    div-int/lit8 v2, v2, 0x2

    int-to-float v2, v2

    iget v3, p0, Lcom/tw/music/lrc/LrcView;->mDividerHeight:F

    add-float/2addr v2, v3

    sub-float/2addr v0, v2

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 4
    :cond_0
    iget-object v1, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v1, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/tw/music/lrc/a;

    invoke-virtual {v1, v0}, Lcom/tw/music/lrc/a;->setOffset(F)V

    .line 5
    :cond_1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {p0, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object p0

    check-cast p0, Lcom/tw/music/lrc/a;

    invoke-virtual {p0}, Lcom/tw/music/lrc/a;->getOffset()F

    move-result p0

    return p0
.end method

.method private La(I)V
    .locals 2

    .line 1
    iget-wide v0, p0, Lcom/tw/music/lrc/LrcView;->Se:J

    invoke-direct {p0, p1, v0, v1}, Lcom/tw/music/lrc/LrcView;->a(IJ)V

    return-void
.end method

.method static synthetic a(Lcom/tw/music/lrc/LrcView;F)F
    .locals 0

    .line 5
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    return p1
.end method

.method static synthetic a(Lcom/tw/music/lrc/LrcView;I)F
    .locals 0

    .line 6
    invoke-direct {p0, p1}, Lcom/tw/music/lrc/LrcView;->Ka(I)F

    move-result p0

    return p0
.end method

.method static synthetic a(Lcom/tw/music/lrc/LrcView;)Ljava/lang/Runnable;
    .locals 0

    .line 3
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->of:Ljava/lang/Runnable;

    return-object p0
.end method

.method static synthetic a(Lcom/tw/music/lrc/LrcView;Ljava/lang/String;)Ljava/lang/String;
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->ff:Ljava/lang/String;

    return-object p1
.end method

.method private a(IJ)V
    .locals 3

    .line 13
    invoke-direct {p0, p1}, Lcom/tw/music/lrc/LrcView;->Ka(I)F

    move-result p1

    .line 14
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->Fe()V

    const/4 v0, 0x2

    new-array v0, v0, [F

    .line 15
    iget v1, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    const/4 v2, 0x0

    aput v1, v0, v2

    const/4 v1, 0x1

    aput p1, v0, v1

    invoke-static {v0}, Landroid/animation/ValueAnimator;->ofFloat([F)Landroid/animation/ValueAnimator;

    move-result-object p1

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->mAnimator:Landroid/animation/ValueAnimator;

    .line 16
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->mAnimator:Landroid/animation/ValueAnimator;

    invoke-virtual {p1, p2, p3}, Landroid/animation/ValueAnimator;->setDuration(J)Landroid/animation/ValueAnimator;

    .line 17
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->mAnimator:Landroid/animation/ValueAnimator;

    new-instance p2, Landroid/view/animation/LinearInterpolator;

    invoke-direct {p2}, Landroid/view/animation/LinearInterpolator;-><init>()V

    invoke-virtual {p1, p2}, Landroid/animation/ValueAnimator;->setInterpolator(Landroid/animation/TimeInterpolator;)V

    .line 18
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->mAnimator:Landroid/animation/ValueAnimator;

    new-instance p2, Lcom/tw/music/lrc/i;

    invoke-direct {p2, p0}, Lcom/tw/music/lrc/i;-><init>(Lcom/tw/music/lrc/LrcView;)V

    invoke-virtual {p1, p2}, Landroid/animation/ValueAnimator;->addUpdateListener(Landroid/animation/ValueAnimator$AnimatorUpdateListener;)V

    .line 19
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->He()V

    .line 20
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->mAnimator:Landroid/animation/ValueAnimator;

    invoke-virtual {p0}, Landroid/animation/ValueAnimator;->start()V

    return-void
.end method

.method private a(Landroid/graphics/Canvas;Landroid/text/StaticLayout;F)V
    .locals 1

    .line 8
    :try_start_0
    invoke-virtual {p1}, Landroid/graphics/Canvas;->save()I

    .line 9
    iget p0, p0, Lcom/tw/music/lrc/LrcView;->gf:F

    invoke-virtual {p2}, Landroid/text/StaticLayout;->getHeight()I

    move-result v0

    div-int/lit8 v0, v0, 0x2

    int-to-float v0, v0

    sub-float/2addr p3, v0

    invoke-virtual {p1, p0, p3}, Landroid/graphics/Canvas;->translate(FF)V

    .line 10
    invoke-virtual {p2, p1}, Landroid/text/StaticLayout;->draw(Landroid/graphics/Canvas;)V

    .line 11
    invoke-virtual {p1}, Landroid/graphics/Canvas;->restore()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 12
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, ""

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {p1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "md"

    invoke-static {p1, p0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :goto_0
    return-void
.end method

.method static synthetic a(Lcom/tw/music/lrc/LrcView;Ljava/lang/Object;)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/tw/music/lrc/LrcView;->setFlag(Ljava/lang/Object;)V

    return-void
.end method

.method static synthetic a(Lcom/tw/music/lrc/LrcView;Ljava/util/List;)V
    .locals 0

    .line 7
    invoke-direct {p0, p1}, Lcom/tw/music/lrc/LrcView;->f(Ljava/util/List;)V

    return-void
.end method

.method static synthetic a(Lcom/tw/music/lrc/LrcView;Z)Z
    .locals 0

    .line 4
    iput-boolean p1, p0, Lcom/tw/music/lrc/LrcView;->kf:Z

    return p1
.end method

.method static synthetic b(Lcom/tw/music/lrc/LrcView;)F
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    return p0
.end method

.method static synthetic b(Lcom/tw/music/lrc/LrcView;F)F
    .locals 1

    .line 2
    iget v0, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    add-float/2addr v0, p1

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    return v0
.end method

.method static synthetic b(Lcom/tw/music/lrc/LrcView;I)I
    .locals 0

    .line 4
    invoke-direct {p0, p1}, Lcom/tw/music/lrc/LrcView;->Ja(I)I

    move-result p0

    return p0
.end method

.method private b(Landroid/util/AttributeSet;)V
    .locals 7

    .line 5
    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v0

    sget-object v1, Lcom/tw/music/R$styleable;->LrcView:[I

    invoke-virtual {v0, p1, v1}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    move-result-object p1

    .line 6
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v1, 0x7f06005b

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getDimension(I)F

    move-result v0

    const/16 v1, 0x9

    invoke-virtual {p1, v1, v0}, Landroid/content/res/TypedArray;->getDimension(IF)F

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->We:F

    .line 7
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v1, 0x7f06005e

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getDimension(I)F

    move-result v0

    const/4 v1, 0x5

    invoke-virtual {p1, v1, v0}, Landroid/content/res/TypedArray;->getDimension(IF)F

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->Ue:F

    .line 8
    iget v0, p0, Lcom/tw/music/lrc/LrcView;->Ue:F

    const/4 v1, 0x0

    cmpl-float v0, v0, v1

    if-nez v0, :cond_0

    .line 9
    iget v0, p0, Lcom/tw/music/lrc/LrcView;->We:F

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->Ue:F

    .line 10
    :cond_0
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v2, 0x7f06005c

    invoke-virtual {v0, v2}, Landroid/content/res/Resources;->getDimension(I)F

    move-result v0

    const/4 v2, 0x2

    invoke-virtual {p1, v2, v0}, Landroid/content/res/TypedArray;->getDimension(IF)F

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->mDividerHeight:F

    const/16 v0, 0x3e8

    const/4 v3, 0x0

    .line 11
    invoke-virtual {p1, v3, v0}, Landroid/content/res/TypedArray;->getInt(II)I

    move-result v0

    int-to-long v4, v0

    iput-wide v4, p0, Lcom/tw/music/lrc/LrcView;->Se:J

    const/4 v0, 0x4

    .line 12
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v4

    const v5, 0x106000b

    invoke-virtual {v4, v5}, Landroid/content/res/Resources;->getColor(I)I

    move-result v4

    invoke-virtual {p1, v0, v4}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->Te:I

    .line 13
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v4, 0x7f050060

    invoke-virtual {v0, v4}, Landroid/content/res/Resources;->getColor(I)I

    move-result v0

    const/4 v4, 0x1

    invoke-virtual {p1, v4, v0}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->Ve:I

    const/16 v0, 0xe

    .line 14
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v6

    invoke-virtual {v6, v5}, Landroid/content/res/Resources;->getColor(I)I

    move-result v5

    invoke-virtual {p1, v0, v5}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->Xe:I

    const/4 v0, 0x3

    .line 15
    invoke-virtual {p1, v0}, Landroid/content/res/TypedArray;->getString(I)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/lrc/LrcView;->ff:Ljava/lang/String;

    .line 16
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->ff:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const-string v0, "nolrc"

    goto :goto_0

    :cond_1
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->ff:Ljava/lang/String;

    :goto_0
    iput-object v0, p0, Lcom/tw/music/lrc/LrcView;->ff:Ljava/lang/String;

    const/4 v0, 0x6

    .line 17
    invoke-virtual {p1, v0, v1}, Landroid/content/res/TypedArray;->getDimension(IF)F

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->gf:F

    const/16 v0, 0xc

    .line 18
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    const/high16 v5, 0x1060000

    invoke-virtual {v1, v5}, Landroid/content/res/Resources;->getColor(I)I

    move-result v1

    invoke-virtual {p1, v0, v1}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->Ye:I

    const/16 v0, 0xd

    const/high16 v1, 0x3f800000    # 1.0f

    .line 19
    invoke-virtual {p1, v0, v1}, Landroid/content/res/TypedArray;->getDimension(IF)F

    move-result v0

    const/4 v1, 0x7

    .line 20
    invoke-virtual {p1, v1}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    iput-object v1, p0, Lcom/tw/music/lrc/LrcView;->Re:Landroid/graphics/drawable/Drawable;

    .line 21
    iget-object v1, p0, Lcom/tw/music/lrc/LrcView;->Re:Landroid/graphics/drawable/Drawable;

    if-nez v1, :cond_2

    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    const v6, 0x7f070162

    invoke-virtual {v1, v6}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    :cond_2
    iput-object v1, p0, Lcom/tw/music/lrc/LrcView;->Re:Landroid/graphics/drawable/Drawable;

    const/16 v1, 0xa

    .line 22
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v6

    invoke-virtual {v6, v5}, Landroid/content/res/Resources;->getColor(I)I

    move-result v5

    invoke-virtual {p1, v1, v5}, Landroid/content/res/TypedArray;->getColor(II)I

    move-result v1

    iput v1, p0, Lcom/tw/music/lrc/LrcView;->Ze:I

    const/16 v1, 0xb

    const/high16 v5, 0x41600000    # 14.0f

    .line 23
    invoke-virtual {p1, v1, v5}, Landroid/content/res/TypedArray;->getDimension(IF)F

    move-result v1

    const/16 v5, 0x8

    .line 24
    invoke-virtual {p1, v5, v2}, Landroid/content/res/TypedArray;->getInteger(II)I

    move-result v2

    iput v2, p0, Lcom/tw/music/lrc/LrcView;->mf:I

    .line 25
    invoke-virtual {p1}, Landroid/content/res/TypedArray;->recycle()V

    .line 26
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object p1

    const v2, 0x7f06005d

    invoke-virtual {p1, v2}, Landroid/content/res/Resources;->getDimension(I)F

    move-result p1

    float-to-int p1, p1

    iput p1, p0, Lcom/tw/music/lrc/LrcView;->df:I

    .line 27
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object p1

    const v2, 0x7f06005f

    invoke-virtual {p1, v2}, Landroid/content/res/Resources;->getDimension(I)F

    move-result p1

    float-to-int p1, p1

    iput p1, p0, Lcom/tw/music/lrc/LrcView;->ef:I

    .line 28
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    invoke-virtual {p1, v4}, Landroid/text/TextPaint;->setAntiAlias(Z)V

    .line 29
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    iget v2, p0, Lcom/tw/music/lrc/LrcView;->We:F

    invoke-virtual {p1, v2}, Landroid/text/TextPaint;->setTextSize(F)V

    .line 30
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    sget-object v2, Landroid/graphics/Paint$Align;->LEFT:Landroid/graphics/Paint$Align;

    invoke-virtual {p1, v2}, Landroid/text/TextPaint;->setTextAlign(Landroid/graphics/Paint$Align;)V

    .line 31
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    invoke-virtual {p1, v4}, Landroid/text/TextPaint;->setAntiAlias(Z)V

    .line 32
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    invoke-virtual {p1, v1}, Landroid/text/TextPaint;->setTextSize(F)V

    .line 33
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    sget-object v1, Landroid/graphics/Paint$Align;->CENTER:Landroid/graphics/Paint$Align;

    invoke-virtual {p1, v1}, Landroid/text/TextPaint;->setTextAlign(Landroid/graphics/Paint$Align;)V

    .line 34
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    invoke-virtual {p1, v0}, Landroid/text/TextPaint;->setStrokeWidth(F)V

    .line 35
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    sget-object v0, Landroid/graphics/Paint$Cap;->ROUND:Landroid/graphics/Paint$Cap;

    invoke-virtual {p1, v0}, Landroid/text/TextPaint;->setStrokeCap(Landroid/graphics/Paint$Cap;)V

    .line 36
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    invoke-virtual {p1}, Landroid/text/TextPaint;->getFontMetrics()Landroid/graphics/Paint$FontMetrics;

    move-result-object p1

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->Qe:Landroid/graphics/Paint$FontMetrics;

    .line 37
    new-instance p1, Landroid/view/GestureDetector;

    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v0

    iget-object v1, p0, Lcom/tw/music/lrc/LrcView;->nf:Landroid/view/GestureDetector$SimpleOnGestureListener;

    invoke-direct {p1, v0, v1}, Landroid/view/GestureDetector;-><init>(Landroid/content/Context;Landroid/view/GestureDetector$OnGestureListener;)V

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->mGestureDetector:Landroid/view/GestureDetector;

    .line 38
    iget-object p1, p0, Lcom/tw/music/lrc/LrcView;->mGestureDetector:Landroid/view/GestureDetector;

    invoke-virtual {p1, v3}, Landroid/view/GestureDetector;->setIsLongpressEnabled(Z)V

    .line 39
    new-instance p1, Landroid/widget/Scroller;

    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v0

    invoke-direct {p1, v0}, Landroid/widget/Scroller;-><init>(Landroid/content/Context;)V

    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->mScroller:Landroid/widget/Scroller;

    return-void
.end method

.method static synthetic b(Lcom/tw/music/lrc/LrcView;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/tw/music/lrc/LrcView;->lf:Z

    return p1
.end method

.method static synthetic c(Lcom/tw/music/lrc/LrcView;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->mCurrentLine:I

    return p1
.end method

.method static synthetic c(Lcom/tw/music/lrc/LrcView;)Ljava/util/List;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    return-object p0
.end method

.method private c(Ljava/lang/Runnable;)V
    .locals 2

    .line 4
    invoke-static {}, Landroid/os/Looper;->myLooper()Landroid/os/Looper;

    move-result-object v0

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v1

    if-ne v0, v1, :cond_0

    .line 5
    invoke-interface {p1}, Ljava/lang/Runnable;->run()V

    goto :goto_0

    .line 6
    :cond_0
    invoke-virtual {p0, p1}, Landroid/view/View;->post(Ljava/lang/Runnable;)Z

    :goto_0
    return-void
.end method

.method static synthetic c(Lcom/tw/music/lrc/LrcView;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/tw/music/lrc/LrcView;->jf:Z

    return p1
.end method

.method static synthetic d(Lcom/tw/music/lrc/LrcView;)Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->Re:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method static synthetic d(Lcom/tw/music/lrc/LrcView;I)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/tw/music/lrc/LrcView;->La(I)V

    return-void
.end method

.method static synthetic e(Lcom/tw/music/lrc/LrcView;)I
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->getCenterLine()I

    move-result p0

    return p0
.end method

.method static synthetic f(Lcom/tw/music/lrc/LrcView;)Ljava/lang/Object;
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->getFlag()Ljava/lang/Object;

    move-result-object p0

    return-object p0
.end method

.method private f(Ljava/util/List;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/tw/music/lrc/a;",
            ">;)V"
        }
    .end annotation

    if-eqz p1, :cond_0

    .line 2
    invoke-interface {p1}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_0

    .line 3
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 4
    :cond_0
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->Ge()V

    .line 5
    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    return-void
.end method

.method static synthetic g(Lcom/tw/music/lrc/LrcView;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/lrc/LrcView;->mCurrentLine:I

    return p0
.end method

.method private getCenterLine()I
    .locals 5

    const/4 v0, 0x0

    const v1, 0x7f7fffff    # Float.MAX_VALUE

    move v2, v1

    move v1, v0

    .line 1
    :goto_0
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v3

    if-ge v0, v3, :cond_1

    .line 2
    iget v3, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    invoke-direct {p0, v0}, Lcom/tw/music/lrc/LrcView;->Ka(I)F

    move-result v4

    sub-float/2addr v3, v4

    invoke-static {v3}, Ljava/lang/Math;->abs(F)F

    move-result v3

    cmpg-float v3, v3, v2

    if-gez v3, :cond_0

    .line 3
    iget v1, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    invoke-direct {p0, v0}, Lcom/tw/music/lrc/LrcView;->Ka(I)F

    move-result v2

    sub-float/2addr v1, v2

    invoke-static {v1}, Ljava/lang/Math;->abs(F)F

    move-result v1

    move v2, v1

    move v1, v0

    :cond_0
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_1
    return v1
.end method

.method private getFlag()Ljava/lang/Object;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->if:Ljava/lang/Object;

    return-object p0
.end method

.method private getLrcWidth()F
    .locals 2

    .line 1
    invoke-virtual {p0}, Landroid/view/View;->getWidth()I

    move-result v0

    int-to-float v0, v0

    iget p0, p0, Lcom/tw/music/lrc/LrcView;->gf:F

    const/high16 v1, 0x40000000    # 2.0f

    mul-float/2addr p0, v1

    sub-float/2addr v0, p0

    return v0
.end method

.method static synthetic h(Lcom/tw/music/lrc/LrcView;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/tw/music/lrc/LrcView;->jf:Z

    return p0
.end method

.method static synthetic i(Lcom/tw/music/lrc/LrcView;)Lcom/tw/music/lrc/LrcView$a;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->hf:Lcom/tw/music/lrc/LrcView$a;

    return-object p0
.end method

.method static synthetic j(Lcom/tw/music/lrc/LrcView;)Landroid/widget/Scroller;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->mScroller:Landroid/widget/Scroller;

    return-object p0
.end method

.method private setFlag(Ljava/lang/Object;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->if:Ljava/lang/Object;

    return-void
.end method


# virtual methods
.method public Wa()Z
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {p0}, Ljava/util/List;->isEmpty()Z

    move-result p0

    xor-int/lit8 p0, p0, 0x1

    return p0
.end method

.method public computeScroll()V
    .locals 3

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->mScroller:Landroid/widget/Scroller;

    invoke-virtual {v0}, Landroid/widget/Scroller;->computeScrollOffset()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->mScroller:Landroid/widget/Scroller;

    invoke-virtual {v0}, Landroid/widget/Scroller;->getCurrY()I

    move-result v0

    int-to-float v0, v0

    iput v0, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    .line 3
    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    .line 4
    :cond_0
    iget-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->lf:Z

    if-eqz v0, :cond_1

    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->mScroller:Landroid/widget/Scroller;

    invoke-virtual {v0}, Landroid/widget/Scroller;->isFinished()Z

    move-result v0

    if-eqz v0, :cond_1

    const/4 v0, 0x0

    .line 5
    iput-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->lf:Z

    .line 6
    invoke-virtual {p0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-eqz v0, :cond_1

    iget-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->kf:Z

    if-nez v0, :cond_1

    .line 7
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->Ee()V

    .line 8
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->of:Ljava/lang/Runnable;

    const-wide/16 v1, 0xfa0

    invoke-virtual {p0, v0, v1, v2}, Landroid/view/View;->postDelayed(Ljava/lang/Runnable;J)Z

    :cond_1
    return-void
.end method

.method public fa(I)V
    .locals 1

    .line 1
    new-instance v0, Lcom/tw/music/lrc/f;

    invoke-direct {v0, p0, p1}, Lcom/tw/music/lrc/f;-><init>(Lcom/tw/music/lrc/LrcView;I)V

    invoke-direct {p0, v0}, Lcom/tw/music/lrc/LrcView;->c(Ljava/lang/Runnable;)V

    return-void
.end method

.method protected onDetachedFromWindow()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->of:Ljava/lang/Runnable;

    invoke-virtual {p0, v0}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    .line 2
    invoke-super {p0}, Landroid/view/View;->onDetachedFromWindow()V

    return-void
.end method

.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 11

    .line 1
    invoke-super {p0, p1}, Landroid/view/View;->onDraw(Landroid/graphics/Canvas;)V

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->getHeight()I

    move-result v0

    div-int/lit8 v0, v0, 0x2

    .line 3
    invoke-virtual {p0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v1

    if-nez v1, :cond_0

    .line 4
    iget-object v1, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    iget v2, p0, Lcom/tw/music/lrc/LrcView;->Ve:I

    invoke-virtual {v1, v2}, Landroid/text/TextPaint;->setColor(I)V

    .line 5
    new-instance v1, Landroid/text/StaticLayout;

    iget-object v4, p0, Lcom/tw/music/lrc/LrcView;->ff:Ljava/lang/String;

    iget-object v5, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    .line 6
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->getLrcWidth()F

    move-result v2

    float-to-int v6, v2

    sget-object v7, Landroid/text/Layout$Alignment;->ALIGN_CENTER:Landroid/text/Layout$Alignment;

    const/high16 v8, 0x3f800000    # 1.0f

    const/4 v9, 0x0

    const/4 v10, 0x0

    move-object v3, v1

    invoke-direct/range {v3 .. v10}, Landroid/text/StaticLayout;-><init>(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V

    int-to-float v0, v0

    .line 7
    invoke-direct {p0, p1, v1, v0}, Lcom/tw/music/lrc/LrcView;->a(Landroid/graphics/Canvas;Landroid/text/StaticLayout;F)V

    return-void

    .line 8
    :cond_0
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->getCenterLine()I

    move-result v1

    .line 9
    iget-boolean v2, p0, Lcom/tw/music/lrc/LrcView;->jf:Z

    if-eqz v2, :cond_1

    .line 10
    iget-object v2, p0, Lcom/tw/music/lrc/LrcView;->Re:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v2, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    .line 11
    iget-object v2, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    iget v3, p0, Lcom/tw/music/lrc/LrcView;->Ye:I

    invoke-virtual {v2, v3}, Landroid/text/TextPaint;->setColor(I)V

    .line 12
    iget v2, p0, Lcom/tw/music/lrc/LrcView;->ef:I

    int-to-float v4, v2

    int-to-float v0, v0

    invoke-virtual {p0}, Landroid/view/View;->getWidth()I

    move-result v2

    iget v3, p0, Lcom/tw/music/lrc/LrcView;->ef:I

    sub-int/2addr v2, v3

    int-to-float v6, v2

    iget-object v8, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    move-object v3, p1

    move v5, v0

    move v7, v0

    invoke-virtual/range {v3 .. v8}, Landroid/graphics/Canvas;->drawLine(FFFFLandroid/graphics/Paint;)V

    .line 13
    iget-object v2, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    iget v3, p0, Lcom/tw/music/lrc/LrcView;->Ze:I

    invoke-virtual {v2, v3}, Landroid/text/TextPaint;->setColor(I)V

    .line 14
    iget-object v2, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/tw/music/lrc/a;

    invoke-virtual {v2}, Lcom/tw/music/lrc/a;->getTime()J

    move-result-wide v2

    invoke-static {v2, v3}, Lcom/tw/music/lrc/b;->g(J)Ljava/lang/String;

    move-result-object v2

    .line 15
    invoke-virtual {p0}, Landroid/view/View;->getWidth()I

    move-result v3

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->ef:I

    div-int/lit8 v4, v4, 0x2

    sub-int/2addr v3, v4

    int-to-float v3, v3

    .line 16
    iget-object v4, p0, Lcom/tw/music/lrc/LrcView;->Qe:Landroid/graphics/Paint$FontMetrics;

    iget v5, v4, Landroid/graphics/Paint$FontMetrics;->descent:F

    iget v4, v4, Landroid/graphics/Paint$FontMetrics;->ascent:F

    add-float/2addr v5, v4

    const/high16 v4, 0x40000000    # 2.0f

    div-float/2addr v5, v4

    sub-float/2addr v0, v5

    .line 17
    iget-object v4, p0, Lcom/tw/music/lrc/LrcView;->Pe:Landroid/text/TextPaint;

    invoke-virtual {p1, v2, v3, v0, v4}, Landroid/graphics/Canvas;->drawText(Ljava/lang/String;FFLandroid/graphics/Paint;)V

    .line 18
    :cond_1
    iget v0, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    const/4 v2, 0x0

    invoke-virtual {p1, v2, v0}, Landroid/graphics/Canvas;->translate(FF)V

    const/4 v0, 0x0

    .line 19
    :goto_0
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v3

    if-ge v0, v3, :cond_5

    if-lez v0, :cond_2

    .line 20
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    add-int/lit8 v4, v0, -0x1

    invoke-interface {v3, v4}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/tw/music/lrc/a;

    invoke-virtual {v3}, Lcom/tw/music/lrc/a;->getHeight()I

    move-result v3

    iget-object v4, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v4, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/tw/music/lrc/a;

    invoke-virtual {v4}, Lcom/tw/music/lrc/a;->getHeight()I

    move-result v4

    add-int/2addr v3, v4

    div-int/lit8 v3, v3, 0x2

    int-to-float v3, v3

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->mDividerHeight:F

    add-float/2addr v3, v4

    add-float/2addr v2, v3

    .line 21
    :cond_2
    iget v3, p0, Lcom/tw/music/lrc/LrcView;->mCurrentLine:I

    if-ne v0, v3, :cond_3

    .line 22
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->We:F

    invoke-virtual {v3, v4}, Landroid/text/TextPaint;->setTextSize(F)V

    .line 23
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->Ve:I

    invoke-virtual {v3, v4}, Landroid/text/TextPaint;->setColor(I)V

    goto :goto_1

    .line 24
    :cond_3
    iget-boolean v3, p0, Lcom/tw/music/lrc/LrcView;->jf:Z

    if-eqz v3, :cond_4

    if-ne v0, v1, :cond_4

    .line 25
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->Xe:I

    invoke-virtual {v3, v4}, Landroid/text/TextPaint;->setColor(I)V

    goto :goto_1

    .line 26
    :cond_4
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->Ue:F

    invoke-virtual {v3, v4}, Landroid/text/TextPaint;->setTextSize(F)V

    .line 27
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Oe:Landroid/text/TextPaint;

    iget v4, p0, Lcom/tw/music/lrc/LrcView;->Te:I

    invoke-virtual {v3, v4}, Landroid/text/TextPaint;->setColor(I)V

    .line 28
    :goto_1
    iget-object v3, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v3, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/tw/music/lrc/a;

    invoke-virtual {v3}, Lcom/tw/music/lrc/a;->kd()Landroid/text/StaticLayout;

    move-result-object v3

    invoke-direct {p0, p1, v3, v2}, Lcom/tw/music/lrc/LrcView;->a(Landroid/graphics/Canvas;Landroid/text/StaticLayout;F)V

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_5
    const-wide/16 v0, 0x64

    .line 29
    invoke-virtual {p0, v0, v1}, Landroid/view/View;->postInvalidateDelayed(J)V

    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 0

    .line 1
    invoke-super/range {p0 .. p5}, Landroid/view/View;->onLayout(ZIIII)V

    if-eqz p1, :cond_0

    .line 2
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->Ge()V

    .line 3
    iget p1, p0, Lcom/tw/music/lrc/LrcView;->ef:I

    iget p2, p0, Lcom/tw/music/lrc/LrcView;->df:I

    sub-int/2addr p1, p2

    div-int/lit8 p1, p1, 0x2

    .line 4
    invoke-virtual {p0}, Landroid/view/View;->getHeight()I

    move-result p2

    div-int/lit8 p2, p2, 0x2

    iget p3, p0, Lcom/tw/music/lrc/LrcView;->df:I

    div-int/lit8 p4, p3, 0x2

    sub-int/2addr p2, p4

    add-int p4, p1, p3

    add-int/2addr p3, p2

    .line 5
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->Re:Landroid/graphics/drawable/Drawable;

    invoke-virtual {p0, p1, p2, p4, p3}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    :cond_0
    return-void
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 3

    .line 1
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    move-result v0

    const/4 v1, 0x1

    if-eq v0, v1, :cond_0

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    move-result v0

    const/4 v1, 0x3

    if-ne v0, v1, :cond_1

    :cond_0
    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->kf:Z

    .line 3
    invoke-virtual {p0}, Lcom/tw/music/lrc/LrcView;->Wa()Z

    move-result v0

    if-eqz v0, :cond_1

    iget-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->lf:Z

    if-nez v0, :cond_1

    .line 4
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->Ee()V

    .line 5
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->of:Ljava/lang/Runnable;

    const-wide/16 v1, 0xfa0

    invoke-virtual {p0, v0, v1, v2}, Landroid/view/View;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 6
    :cond_1
    iget-object p0, p0, Lcom/tw/music/lrc/LrcView;->mGestureDetector:Landroid/view/GestureDetector;

    invoke-virtual {p0, p1}, Landroid/view/GestureDetector;->onTouchEvent(Landroid/view/MotionEvent;)Z

    move-result p0

    return p0
.end method

.method public reset()V
    .locals 2

    .line 1
    invoke-direct {p0}, Lcom/tw/music/lrc/LrcView;->Fe()V

    .line 2
    iget-object v0, p0, Lcom/tw/music/lrc/LrcView;->mScroller:Landroid/widget/Scroller;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Landroid/widget/Scroller;->forceFinished(Z)V

    const/4 v0, 0x0

    .line 3
    iput-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->jf:Z

    .line 4
    iput-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->kf:Z

    .line 5
    iput-boolean v0, p0, Lcom/tw/music/lrc/LrcView;->lf:Z

    .line 6
    iget-object v1, p0, Lcom/tw/music/lrc/LrcView;->of:Ljava/lang/Runnable;

    invoke-virtual {p0, v1}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    .line 7
    iget-object v1, p0, Lcom/tw/music/lrc/LrcView;->Ne:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->clear()V

    const/4 v1, 0x0

    .line 8
    iput v1, p0, Lcom/tw/music/lrc/LrcView;->mOffset:F

    .line 9
    iput v0, p0, Lcom/tw/music/lrc/LrcView;->mCurrentLine:I

    .line 10
    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    return-void
.end method

.method public setCurrentColor(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->Ve:I

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->postInvalidate()V

    return-void
.end method

.method public setCurrentTextSize(F)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->We:F

    return-void
.end method

.method public setLabel(Ljava/lang/String;)V
    .locals 1

    .line 1
    new-instance v0, Lcom/tw/music/lrc/c;

    invoke-direct {v0, p0, p1}, Lcom/tw/music/lrc/c;-><init>(Lcom/tw/music/lrc/LrcView;Ljava/lang/String;)V

    invoke-direct {p0, v0}, Lcom/tw/music/lrc/LrcView;->c(Ljava/lang/Runnable;)V

    return-void
.end method

.method public setNormalColor(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->Te:I

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->postInvalidate()V

    return-void
.end method

.method public setNormalTextSize(F)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->Ue:F

    return-void
.end method

.method public setOnPlayClickListener(Lcom/tw/music/lrc/LrcView$a;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/lrc/LrcView;->hf:Lcom/tw/music/lrc/LrcView$a;

    return-void
.end method

.method public setTimeTextColor(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->Ze:I

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->postInvalidate()V

    return-void
.end method

.method public setTimelineColor(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->Ye:I

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->postInvalidate()V

    return-void
.end method

.method public setTimelineTextColor(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/lrc/LrcView;->Xe:I

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->postInvalidate()V

    return-void
.end method

.method public va(Ljava/lang/String;)V
    .locals 1

    .line 1
    new-instance v0, Lcom/tw/music/lrc/e;

    invoke-direct {v0, p0, p1}, Lcom/tw/music/lrc/e;-><init>(Lcom/tw/music/lrc/LrcView;Ljava/lang/String;)V

    invoke-direct {p0, v0}, Lcom/tw/music/lrc/LrcView;->c(Ljava/lang/Runnable;)V

    return-void
.end method

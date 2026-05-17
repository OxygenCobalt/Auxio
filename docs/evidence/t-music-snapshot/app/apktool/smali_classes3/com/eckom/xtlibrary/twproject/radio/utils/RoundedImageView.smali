.class public Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;
.super Landroid/widget/ImageView;
.source "RoundedImageView.java"


# instance fields
.field private cornerRadius:F

.field private drawable:Landroid/graphics/drawable/Drawable;

.field private padding:I

.field private ve:Landroid/graphics/drawable/Drawable;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Landroid/widget/ImageView;-><init>(Landroid/content/Context;)V

    .line 2
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->init()V

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 0

    .line 3
    invoke-direct {p0, p1, p2}, Landroid/widget/ImageView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    .line 4
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->init()V

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 0

    .line 5
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/ImageView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    .line 6
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->init()V

    return-void
.end method

.method private init()V
    .locals 1

    const/high16 v0, 0x41200000    # 10.0f

    .line 1
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->cornerRadius:F

    const/4 v0, 0x4

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->padding:I

    return-void
.end method


# virtual methods
.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 9

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->drawable:Landroid/graphics/drawable/Drawable;

    if-nez v0, :cond_0

    .line 2
    invoke-super {p0, p1}, Landroid/widget/ImageView;->onDraw(Landroid/graphics/Canvas;)V

    return-void

    .line 3
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->ve:Landroid/graphics/drawable/Drawable;

    if-nez v0, :cond_1

    .line 4
    invoke-super {p0, p1}, Landroid/widget/ImageView;->onDraw(Landroid/graphics/Canvas;)V

    return-void

    .line 5
    :cond_1
    invoke-virtual {p0}, Landroid/widget/ImageView;->getWidth()I

    move-result v1

    invoke-virtual {p0}, Landroid/widget/ImageView;->getHeight()I

    move-result v2

    const/4 v3, 0x0

    invoke-virtual {v0, v3, v3, v1, v2}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->ve:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    .line 7
    new-instance v0, Landroid/graphics/Path;

    invoke-direct {v0}, Landroid/graphics/Path;-><init>()V

    .line 8
    new-instance v1, Landroid/graphics/RectF;

    iget v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->padding:I

    add-int/lit8 v4, v2, 0x0

    int-to-float v4, v4

    add-int/2addr v2, v3

    int-to-float v2, v2

    invoke-virtual {p0}, Landroid/widget/ImageView;->getWidth()I

    move-result v3

    iget v5, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->padding:I

    sub-int/2addr v3, v5

    int-to-float v3, v3

    invoke-virtual {p0}, Landroid/widget/ImageView;->getHeight()I

    move-result v5

    iget v6, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->padding:I

    sub-int/2addr v5, v6

    int-to-float v5, v5

    invoke-direct {v1, v4, v2, v3, v5}, Landroid/graphics/RectF;-><init>(FFFF)V

    .line 9
    iget v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->cornerRadius:F

    sget-object v3, Landroid/graphics/Path$Direction;->CW:Landroid/graphics/Path$Direction;

    invoke-virtual {v0, v1, v2, v2, v3}, Landroid/graphics/Path;->addRoundRect(Landroid/graphics/RectF;FFLandroid/graphics/Path$Direction;)V

    .line 10
    invoke-virtual {p1}, Landroid/graphics/Canvas;->save()I

    move-result v1

    .line 11
    invoke-virtual {p1, v0}, Landroid/graphics/Canvas;->clipPath(Landroid/graphics/Path;)Z

    .line 12
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->drawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    move-result v0

    .line 13
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->drawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v2}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    move-result v2

    .line 14
    invoke-virtual {p0}, Landroid/widget/ImageView;->getWidth()I

    move-result v3

    .line 15
    invoke-virtual {p0}, Landroid/widget/ImageView;->getHeight()I

    move-result v4

    int-to-float v5, v3

    int-to-float v6, v0

    div-float/2addr v5, v6

    int-to-float v7, v4

    int-to-float v8, v2

    div-float/2addr v7, v8

    .line 16
    invoke-static {v5, v7}, Ljava/lang/Math;->max(FF)F

    move-result v5

    mul-float/2addr v6, v5

    float-to-int v6, v6

    mul-float/2addr v8, v5

    float-to-int v5, v8

    sub-int/2addr v3, v6

    .line 17
    div-int/lit8 v3, v3, 0x2

    sub-int/2addr v4, v5

    .line 18
    div-int/lit8 v4, v4, 0x2

    .line 19
    new-instance v7, Landroid/graphics/Rect;

    invoke-direct {v7, v3, v4, v0, v2}, Landroid/graphics/Rect;-><init>(IIII)V

    .line 20
    new-instance v0, Landroid/graphics/Rect;

    iget v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->padding:I

    add-int v7, v3, v2

    add-int v8, v4, v2

    add-int/2addr v3, v6

    sub-int/2addr v3, v2

    add-int/2addr v4, v5

    sub-int/2addr v4, v2

    invoke-direct {v0, v7, v8, v3, v4}, Landroid/graphics/Rect;-><init>(IIII)V

    .line 21
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->drawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v2, v0}, Landroid/graphics/drawable/Drawable;->setBounds(Landroid/graphics/Rect;)V

    .line 22
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->drawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {p0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    .line 23
    invoke-virtual {p1, v1}, Landroid/graphics/Canvas;->restoreToCount(I)V

    return-void
.end method

.method public setBackground(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->ve:Landroid/graphics/drawable/Drawable;

    .line 2
    invoke-super {p0, p1}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    return-void
.end method

.method public setImageDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/radio/utils/RoundedImageView;->drawable:Landroid/graphics/drawable/Drawable;

    .line 2
    invoke-super {p0, p1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    return-void
.end method

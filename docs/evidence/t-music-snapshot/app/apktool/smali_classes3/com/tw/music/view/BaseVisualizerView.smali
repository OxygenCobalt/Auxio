.class public Lcom/tw/music/view/BaseVisualizerView;
.super Landroid/view/View;
.source "BaseVisualizerView.java"

# interfaces
.implements Landroid/media/audiofx/Visualizer$OnDataCaptureListener;


# instance fields
.field protected mData:[B

.field protected mPaint:Landroid/graphics/Paint;

.field private pf:I

.field private qf:I

.field private rf:I

.field private sf:F

.field private strokeWidth:F

.field protected tf:Landroid/media/audiofx/Visualizer;

.field uf:Z

.field private vf:I

.field private wf:I


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    const/4 p1, 0x0

    .line 2
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->pf:I

    .line 3
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->qf:I

    .line 4
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->rf:I

    const/4 p1, 0x0

    .line 5
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->strokeWidth:F

    .line 6
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->sf:F

    const/4 p1, 0x0

    .line 7
    iput-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->tf:Landroid/media/audiofx/Visualizer;

    .line 8
    iput-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->mPaint:Landroid/graphics/Paint;

    const/16 p1, 0xd

    new-array p1, p1, [B

    .line 9
    iput-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->mData:[B

    const/4 p1, 0x1

    .line 10
    iput-boolean p1, p0, Lcom/tw/music/view/BaseVisualizerView;->uf:Z

    const p1, -0xd3070

    .line 11
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->vf:I

    const p1, -0xb0b7f4

    .line 12
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->wf:I

    .line 13
    new-instance p1, Landroid/graphics/Paint;

    invoke-direct {p1}, Landroid/graphics/Paint;-><init>()V

    iput-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->mPaint:Landroid/graphics/Paint;

    return-void
.end method

.method public static ha(I)V
    .locals 5

    const/high16 v0, -0x1000000

    if-ne p0, v0, :cond_0

    return-void

    .line 1
    :cond_0
    :try_start_0
    new-instance v0, Ljava/io/FileOutputStream;

    const-string v1, "/sys/class/tw/misc/led"

    invoke-direct {v0, v1}, Ljava/io/FileOutputStream;-><init>(Ljava/lang/String;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    .line 2
    :try_start_1
    sget-object v1, Ljava/util/Locale;->US:Ljava/util/Locale;

    const-string v2, "0x%08x"

    const/4 v3, 0x1

    new-array v3, v3, [Ljava/lang/Object;

    const/4 v4, 0x0

    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p0

    aput-object p0, v3, v4

    invoke-static {v1, v2, v3}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/io/FileOutputStream;->write([B)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 3
    :catch_0
    :try_start_2
    invoke-virtual {v0}, Ljava/io/FileOutputStream;->close()V

    goto :goto_0

    :catchall_0
    move-exception p0

    invoke-virtual {v0}, Ljava/io/FileOutputStream;->close()V

    throw p0
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_1

    :catch_1
    :goto_0
    return-void
.end method


# virtual methods
.method protected a(Landroid/graphics/Canvas;FB)V
    .locals 8

    if-nez p3, :cond_0

    const/4 p3, 0x1

    :cond_0
    const/4 v0, 0x0

    :goto_0
    if-ge v0, p3, :cond_1

    .line 1
    invoke-virtual {p0}, Landroid/view/View;->getHeight()I

    move-result v1

    iget v2, p0, Lcom/tw/music/view/BaseVisualizerView;->qf:I

    mul-int v3, v0, v2

    sub-int/2addr v1, v3

    sub-int/2addr v1, v2

    int-to-float v6, v1

    .line 2
    invoke-virtual {p0}, Landroid/view/View;->getHeight()I

    move-result v1

    div-int/lit8 v1, v1, 0x2

    .line 3
    iget-object v1, p0, Lcom/tw/music/view/BaseVisualizerView;->mPaint:Landroid/graphics/Paint;

    const/4 v2, -0x1

    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setColor(I)V

    .line 4
    iget v1, p0, Lcom/tw/music/view/BaseVisualizerView;->sf:F

    add-float v5, p2, v1

    iget-object v7, p0, Lcom/tw/music/view/BaseVisualizerView;->mPaint:Landroid/graphics/Paint;

    move-object v2, p1

    move v3, p2

    move v4, v6

    invoke-virtual/range {v2 .. v7}, Landroid/graphics/Canvas;->drawLine(FFFFLandroid/graphics/Paint;)V

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_1
    return-void
.end method

.method public ga(I)I
    .locals 0

    mul-int/lit8 p1, p1, 0xc

    .line 1
    div-int/lit8 p1, p1, 0xa

    const/16 p0, 0xff

    if-le p1, p0, :cond_0

    goto :goto_0

    :cond_0
    move p0, p1

    :goto_0
    return p0
.end method

.method public j(II)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->vf:I

    .line 2
    iput p2, p0, Lcom/tw/music/view/BaseVisualizerView;->wf:I

    .line 3
    iget p1, p0, Lcom/tw/music/view/BaseVisualizerView;->vf:I

    if-nez p1, :cond_0

    const p1, -0x683b05

    .line 4
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->vf:I

    .line 5
    :cond_0
    iget p1, p0, Lcom/tw/music/view/BaseVisualizerView;->wf:I

    if-nez p1, :cond_1

    const p1, -0xeaca52

    .line 6
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->wf:I

    .line 7
    :cond_1
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "setCylinderColor: "

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget p2, p0, Lcom/tw/music/view/BaseVisualizerView;->vf:I

    invoke-static {p2}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    const-string p2, "BaseVisualizerView"

    invoke-static {p2, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 8
    invoke-virtual {p0}, Landroid/view/View;->invalidate()V

    return-void
.end method

.method public onDraw(Landroid/graphics/Canvas;)V
    .locals 12

    .line 1
    new-instance v8, Landroid/graphics/LinearGradient;

    sget v0, Lcom/tw/music/MusicActivity;->Dc:I

    int-to-float v4, v0

    const/4 v9, 0x2

    new-array v5, v9, [I

    iget v0, p0, Lcom/tw/music/view/BaseVisualizerView;->vf:I

    const/4 v10, 0x0

    aput v0, v5, v10

    iget v0, p0, Lcom/tw/music/view/BaseVisualizerView;->wf:I

    const/4 v11, 0x1

    aput v0, v5, v11

    sget-object v7, Landroid/graphics/Shader$TileMode;->MIRROR:Landroid/graphics/Shader$TileMode;

    const/4 v1, 0x0

    const/4 v2, 0x0

    const/4 v3, 0x0

    const/4 v6, 0x0

    move-object v0, v8

    invoke-direct/range {v0 .. v7}, Landroid/graphics/LinearGradient;-><init>(FFFF[I[FLandroid/graphics/Shader$TileMode;)V

    .line 2
    iget-object v0, p0, Lcom/tw/music/view/BaseVisualizerView;->mPaint:Landroid/graphics/Paint;

    invoke-virtual {v0, v8}, Landroid/graphics/Paint;->setShader(Landroid/graphics/Shader;)Landroid/graphics/Shader;

    :goto_0
    const/high16 v0, 0x40000000    # 2.0f

    if-ge v10, v9, :cond_0

    .line 3
    iget v1, p0, Lcom/tw/music/view/BaseVisualizerView;->strokeWidth:F

    div-float/2addr v1, v0

    iget v0, p0, Lcom/tw/music/view/BaseVisualizerView;->pf:I

    int-to-float v2, v0

    add-float/2addr v1, v2

    int-to-float v2, v10

    int-to-float v0, v0

    iget v3, p0, Lcom/tw/music/view/BaseVisualizerView;->sf:F

    add-float/2addr v0, v3

    mul-float/2addr v2, v0

    add-float/2addr v1, v2

    iget-object v0, p0, Lcom/tw/music/view/BaseVisualizerView;->mData:[B

    aget-byte v0, v0, v10

    invoke-virtual {p0, p1, v1, v0}, Lcom/tw/music/view/BaseVisualizerView;->a(Landroid/graphics/Canvas;FB)V

    add-int/lit8 v10, v10, 0x1

    goto :goto_0

    :cond_0
    const/16 v1, 0xd

    const/4 v2, -0x4

    :goto_1
    if-lt v1, v9, :cond_1

    add-int/2addr v2, v11

    .line 4
    iget v3, p0, Lcom/tw/music/view/BaseVisualizerView;->strokeWidth:F

    div-float/2addr v3, v0

    iget v4, p0, Lcom/tw/music/view/BaseVisualizerView;->pf:I

    int-to-float v5, v4

    add-float/2addr v3, v5

    add-int/lit8 v5, v2, 0x6

    sub-int/2addr v5, v11

    int-to-float v5, v5

    int-to-float v4, v4

    iget v6, p0, Lcom/tw/music/view/BaseVisualizerView;->sf:F

    add-float/2addr v4, v6

    mul-float/2addr v5, v4

    add-float/2addr v3, v5

    iget-object v4, p0, Lcom/tw/music/view/BaseVisualizerView;->mData:[B

    add-int/lit8 v5, v1, -0x1

    aget-byte v4, v4, v5

    invoke-virtual {p0, p1, v3, v4}, Lcom/tw/music/view/BaseVisualizerView;->a(Landroid/graphics/Canvas;FB)V

    add-int/lit8 v1, v1, -0x1

    goto :goto_1

    :cond_1
    return-void
.end method

.method public onFftDataCapture(Landroid/media/audiofx/Visualizer;[BI)V
    .locals 8

    .line 1
    array-length p1, p2

    const/4 p3, 0x2

    div-int/2addr p1, p3

    const/4 v0, 0x1

    add-int/2addr p1, v0

    new-array p1, p1, [B

    .line 2
    iget-boolean v1, p0, Lcom/tw/music/view/BaseVisualizerView;->uf:Z

    const/16 v2, 0xd

    const/4 v3, 0x0

    if-eqz v1, :cond_0

    .line 3
    aget-byte v1, p2, v0

    invoke-static {v1}, Ljava/lang/Math;->abs(I)I

    move-result v1

    int-to-byte v1, v1

    aput-byte v1, p1, v3

    move v1, v0

    .line 4
    :goto_0
    array-length v4, p2

    if-ge p3, v4, :cond_1

    .line 5
    aget-byte v4, p2, p3

    int-to-double v4, v4

    add-int/lit8 v6, p3, 0x1

    aget-byte v6, p2, v6

    int-to-double v6, v6

    invoke-static {v4, v5, v6, v7}, Ljava/lang/Math;->hypot(DD)D

    move-result-wide v4

    double-to-int v4, v4

    int-to-byte v4, v4

    aput-byte v4, p1, v1

    add-int/lit8 p3, p3, 0x2

    add-int/2addr v1, v0

    goto :goto_0

    :cond_0
    move p2, v3

    :goto_1
    if-ge p2, v2, :cond_1

    .line 6
    aput-byte v3, p1, p2

    add-int/lit8 p2, p2, 0x1

    goto :goto_1

    :cond_1
    move p2, v3

    :goto_2
    if-ge p2, v2, :cond_4

    rsub-int/lit8 p3, p2, 0xd

    .line 7
    aget-byte p3, p1, p3

    invoke-static {p3}, Ljava/lang/Math;->abs(I)I

    move-result p3

    iget v1, p0, Lcom/tw/music/view/BaseVisualizerView;->rf:I

    div-int/2addr p3, v1

    int-to-byte p3, p3

    .line 8
    iget-object v1, p0, Lcom/tw/music/view/BaseVisualizerView;->mData:[B

    aget-byte v4, v1, p2

    if-le p3, v4, :cond_2

    .line 9
    aput-byte p3, v1, p2

    goto :goto_3

    :cond_2
    if-lez v4, :cond_3

    .line 10
    aget-byte p3, v1, p2

    sub-int/2addr p3, v0

    int-to-byte p3, p3

    aput-byte p3, v1, p2

    :cond_3
    :goto_3
    add-int/lit8 p2, p2, 0x1

    goto :goto_2

    .line 11
    :cond_4
    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object p1

    invoke-virtual {p1}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object p1

    const-string p2, "Ambientlight"

    invoke-static {p1, p2, v3}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result p1

    const/4 p2, 0x3

    if-ne p1, p2, :cond_5

    .line 12
    iget-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->mData:[B

    aget-byte p1, p1, p2

    mul-int/lit16 p1, p1, 0xff

    div-int/lit8 p1, p1, 0x10

    invoke-virtual {p0, p1}, Lcom/tw/music/view/BaseVisualizerView;->ga(I)I

    move-result p1

    iget-object p2, p0, Lcom/tw/music/view/BaseVisualizerView;->mData:[B

    const/4 p3, 0x7

    aget-byte p2, p2, p3

    mul-int/lit16 p2, p2, 0xff

    div-int/lit8 p2, p2, 0x10

    invoke-virtual {p0, p2}, Lcom/tw/music/view/BaseVisualizerView;->ga(I)I

    move-result p2

    iget-object p3, p0, Lcom/tw/music/view/BaseVisualizerView;->mData:[B

    const/16 v0, 0xa

    aget-byte p3, p3, v0

    mul-int/lit16 p3, p3, 0xff

    div-int/lit8 p3, p3, 0x10

    invoke-virtual {p0, p3}, Lcom/tw/music/view/BaseVisualizerView;->ga(I)I

    move-result p3

    invoke-static {p1, p2, p3}, Landroid/graphics/Color;->rgb(III)I

    move-result p1

    invoke-static {p1}, Lcom/tw/music/view/BaseVisualizerView;->ha(I)V

    .line 13
    :cond_5
    invoke-virtual {p0}, Landroid/view/View;->postInvalidate()V

    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 0

    .line 1
    invoke-super/range {p0 .. p5}, Landroid/view/View;->onLayout(ZIIII)V

    sub-int/2addr p4, p2

    int-to-float p1, p4

    sub-int/2addr p5, p3

    int-to-float p2, p5

    const/high16 p3, 0x43eb0000    # 470.0f

    div-float p3, p1, p3

    const/high16 p4, 0x43b40000    # 360.0f

    div-float p4, p2, p4

    const/high16 p5, 0x41c80000    # 25.0f

    mul-float/2addr p4, p5

    .line 2
    iput p4, p0, Lcom/tw/music/view/BaseVisualizerView;->strokeWidth:F

    const/high16 p4, 0x41700000    # 15.0f

    mul-float/2addr p3, p4

    .line 3
    iput p3, p0, Lcom/tw/music/view/BaseVisualizerView;->sf:F

    .line 4
    iget p3, p0, Lcom/tw/music/view/BaseVisualizerView;->sf:F

    const/high16 p4, 0x41500000    # 13.0f

    mul-float/2addr p3, p4

    sub-float/2addr p1, p3

    const/high16 p3, 0x41600000    # 14.0f

    div-float/2addr p1, p3

    float-to-int p1, p1

    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->pf:I

    const/high16 p1, 0x42000000    # 32.0f

    div-float/2addr p2, p1

    const/high16 p1, 0x40000000    # 2.0f

    mul-float/2addr p2, p1

    float-to-int p1, p2

    .line 5
    iput p1, p0, Lcom/tw/music/view/BaseVisualizerView;->qf:I

    .line 6
    iget-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->mPaint:Landroid/graphics/Paint;

    iget p0, p0, Lcom/tw/music/view/BaseVisualizerView;->strokeWidth:F

    invoke-virtual {p1, p0}, Landroid/graphics/Paint;->setStrokeWidth(F)V

    return-void
.end method

.method public onWaveFormDataCapture(Landroid/media/audiofx/Visualizer;[BI)V
    .locals 0

    return-void
.end method

.method public setVisualizer(Landroid/media/audiofx/Visualizer;)V
    .locals 3

    const/4 v0, 0x0

    if-eqz p1, :cond_1

    .line 1
    invoke-virtual {p1}, Landroid/media/audiofx/Visualizer;->getEnabled()Z

    move-result v1

    if-nez v1, :cond_0

    .line 2
    invoke-static {}, Landroid/media/audiofx/Visualizer;->getCaptureSizeRange()[I

    move-result-object v1

    aget v1, v1, v0

    invoke-virtual {p1, v1}, Landroid/media/audiofx/Visualizer;->setCaptureSize(I)I

    :cond_0
    const/4 v1, 0x7

    .line 3
    iput v1, p0, Lcom/tw/music/view/BaseVisualizerView;->rf:I

    .line 4
    invoke-static {}, Landroid/media/audiofx/Visualizer;->getMaxCaptureRate()I

    move-result v1

    div-int/lit8 v1, v1, 0x2

    const/4 v2, 0x1

    invoke-virtual {p1, p0, v1, v0, v2}, Landroid/media/audiofx/Visualizer;->setDataCaptureListener(Landroid/media/audiofx/Visualizer$OnDataCaptureListener;IZZ)I

    .line 5
    invoke-virtual {p1, v2}, Landroid/media/audiofx/Visualizer;->setEnabled(Z)I

    .line 6
    iput-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->tf:Landroid/media/audiofx/Visualizer;

    goto :goto_0

    .line 7
    :cond_1
    iget-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->tf:Landroid/media/audiofx/Visualizer;

    if-eqz p1, :cond_2

    .line 8
    invoke-virtual {p1, v0}, Landroid/media/audiofx/Visualizer;->setEnabled(Z)I

    .line 9
    iget-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->tf:Landroid/media/audiofx/Visualizer;

    invoke-virtual {p1}, Landroid/media/audiofx/Visualizer;->release()V

    const/4 p1, 0x0

    .line 10
    iput-object p1, p0, Lcom/tw/music/view/BaseVisualizerView;->tf:Landroid/media/audiofx/Visualizer;

    :cond_2
    :goto_0
    return-void
.end method

.class public Lcom/eckom/xtlibrary/b/j/r;
.super Ljava/lang/Object;
.source "ToastCustom.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/b/j/r$a;
    }
.end annotation


# static fields
.field private static ce:Landroid/widget/TextView;

.field private static mView:Landroid/view/View;

.field private static params:Landroid/view/WindowManager$LayoutParams;

.field private static qm:Lcom/eckom/xtlibrary/b/j/r;

.field private static rh:Landroid/view/WindowManager;


# instance fields
.field private mHandler:Lcom/eckom/xtlibrary/b/j/r$a;

.field private time:D


# direct methods
.method private constructor <init>(Landroid/content/Context;Ljava/lang/CharSequence;D)V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 2
    iput-wide p3, p0, Lcom/eckom/xtlibrary/b/j/r;->time:D

    .line 3
    sget-object p3, Lcom/eckom/xtlibrary/b/j/r;->rh:Landroid/view/WindowManager;

    if-nez p3, :cond_0

    const-string p3, "window"

    .line 4
    invoke-virtual {p1, p3}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object p3

    check-cast p3, Landroid/view/WindowManager;

    sput-object p3, Lcom/eckom/xtlibrary/b/j/r;->rh:Landroid/view/WindowManager;

    .line 5
    :cond_0
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/j/r;->a(Landroid/content/Context;Ljava/lang/CharSequence;)Landroid/view/View;

    move-result-object p1

    sput-object p1, Lcom/eckom/xtlibrary/b/j/r;->mView:Landroid/view/View;

    const/4 p1, -0x1

    .line 6
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/b/j/r;->Ra(I)V

    return-void
.end method

.method private Ra(I)V
    .locals 1

    .line 1
    new-instance p0, Landroid/view/WindowManager$LayoutParams;

    invoke-direct {p0}, Landroid/view/WindowManager$LayoutParams;-><init>()V

    sput-object p0, Lcom/eckom/xtlibrary/b/j/r;->params:Landroid/view/WindowManager$LayoutParams;

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/b/j/r;->params:Landroid/view/WindowManager$LayoutParams;

    const/4 v0, -0x2

    iput v0, p0, Landroid/view/WindowManager$LayoutParams;->height:I

    .line 3
    iput v0, p0, Landroid/view/WindowManager$LayoutParams;->width:I

    const/4 v0, 0x1

    .line 4
    iput v0, p0, Landroid/view/WindowManager$LayoutParams;->format:I

    .line 5
    iput p1, p0, Landroid/view/WindowManager$LayoutParams;->windowAnimations:I

    const/16 p1, 0x7d2

    .line 6
    iput p1, p0, Landroid/view/WindowManager$LayoutParams;->type:I

    const/16 p1, 0x8

    .line 7
    iput p1, p0, Landroid/view/WindowManager$LayoutParams;->flags:I

    const/16 p1, 0x11

    .line 8
    iput p1, p0, Landroid/view/WindowManager$LayoutParams;->gravity:I

    const/4 p1, 0x0

    .line 9
    iput p1, p0, Landroid/view/WindowManager$LayoutParams;->y:I

    .line 10
    iput p1, p0, Landroid/view/WindowManager$LayoutParams;->x:I

    return-void
.end method

.method private a(Landroid/content/Context;Ljava/lang/CharSequence;)Landroid/view/View;
    .locals 1

    .line 2
    new-instance p0, Landroid/widget/LinearLayout;

    invoke-direct {p0, p1}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    const/4 v0, 0x1

    .line 3
    invoke-virtual {p0, v0}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 4
    sget v0, Lcom/eckom/xtlibrary/R$drawable;->toast_view_shape:I

    invoke-virtual {p0, v0}, Landroid/widget/LinearLayout;->setBackgroundResource(I)V

    .line 5
    new-instance v0, Landroid/widget/TextView;

    invoke-direct {v0, p1}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    sput-object v0, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    .line 6
    sget-object p1, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    invoke-virtual {p1, p2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 7
    sget-object p1, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    const-string p2, "#000000"

    invoke-static {p2}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result p2

    invoke-virtual {p1, p2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 8
    sget-object p1, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    const/high16 p2, 0x41a00000    # 20.0f

    invoke-virtual {p1, p2}, Landroid/widget/TextView;->setTextSize(F)V

    .line 9
    sget-object p1, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    const/16 p2, 0xc

    const/16 v0, 0x14

    invoke-virtual {p1, v0, p2, v0, p2}, Landroid/widget/TextView;->setPadding(IIII)V

    .line 10
    sget-object p1, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    const/16 p2, 0x11

    invoke-virtual {p1, p2}, Landroid/widget/TextView;->setGravity(I)V

    .line 11
    sget-object p1, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    const/4 p2, 0x0

    invoke-virtual {p0, p1, p2}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;I)V

    return-object p0
.end method

.method public static a(Landroid/content/Context;Ljava/lang/CharSequence;D)Lcom/eckom/xtlibrary/b/j/r;
    .locals 1

    .line 12
    sget-object v0, Lcom/eckom/xtlibrary/b/j/r;->qm:Lcom/eckom/xtlibrary/b/j/r;

    if-nez v0, :cond_0

    .line 13
    new-instance v0, Lcom/eckom/xtlibrary/b/j/r;

    invoke-direct {v0, p0, p1, p2, p3}, Lcom/eckom/xtlibrary/b/j/r;-><init>(Landroid/content/Context;Ljava/lang/CharSequence;D)V

    sput-object v0, Lcom/eckom/xtlibrary/b/j/r;->qm:Lcom/eckom/xtlibrary/b/j/r;

    goto :goto_0

    .line 14
    :cond_0
    invoke-static {p1}, Lcom/eckom/xtlibrary/b/j/r;->setText(Ljava/lang/CharSequence;)V

    .line 15
    :goto_0
    sget-object p0, Lcom/eckom/xtlibrary/b/j/r;->qm:Lcom/eckom/xtlibrary/b/j/r;

    return-object p0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/j/r;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/j/r;->cancel()V

    return-void
.end method

.method private cancel()V
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/j/r;->rh:Landroid/view/WindowManager;

    sget-object v1, Lcom/eckom/xtlibrary/b/j/r;->mView:Landroid/view/View;

    invoke-interface {v0, v1}, Landroid/view/WindowManager;->removeView(Landroid/view/View;)V

    const/4 v0, 0x0

    .line 2
    sput-object v0, Lcom/eckom/xtlibrary/b/j/r;->mView:Landroid/view/View;

    .line 3
    sput-object v0, Lcom/eckom/xtlibrary/b/j/r;->qm:Lcom/eckom/xtlibrary/b/j/r;

    .line 4
    iput-object v0, p0, Lcom/eckom/xtlibrary/b/j/r;->mHandler:Lcom/eckom/xtlibrary/b/j/r$a;

    return-void
.end method

.method private static setText(Ljava/lang/CharSequence;)V
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/b/j/r;->ce:Landroid/widget/TextView;

    invoke-virtual {v0, p0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    return-void
.end method


# virtual methods
.method public show()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/j/r;->mHandler:Lcom/eckom/xtlibrary/b/j/r$a;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/j/r$a;

    const/4 v1, 0x0

    invoke-direct {v0, p0, v1}, Lcom/eckom/xtlibrary/b/j/r$a;-><init>(Lcom/eckom/xtlibrary/b/j/r;Lcom/eckom/xtlibrary/b/j/q;)V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/j/r;->mHandler:Lcom/eckom/xtlibrary/b/j/r$a;

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/b/j/r;->rh:Landroid/view/WindowManager;

    sget-object v1, Lcom/eckom/xtlibrary/b/j/r;->mView:Landroid/view/View;

    sget-object v2, Lcom/eckom/xtlibrary/b/j/r;->params:Landroid/view/WindowManager$LayoutParams;

    invoke-interface {v0, v1, v2}, Landroid/view/WindowManager;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 4
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "show:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-wide v1, p0, Lcom/eckom/xtlibrary/b/j/r;->time:D

    invoke-virtual {v0, v1, v2}, Ljava/lang/StringBuilder;->append(D)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "ToastCustom"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/j/r;->mHandler:Lcom/eckom/xtlibrary/b/j/r$a;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/j/r;->mHandler:Lcom/eckom/xtlibrary/b/j/r$a;

    iget-wide v2, p0, Lcom/eckom/xtlibrary/b/j/r;->time:D

    double-to-long v2, v2

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_0
    return-void
.end method

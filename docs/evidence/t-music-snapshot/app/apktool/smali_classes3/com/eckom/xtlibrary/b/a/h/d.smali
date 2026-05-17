.class public Lcom/eckom/xtlibrary/b/a/h/d;
.super Ljava/lang/Object;
.source "VoiceCallView.java"


# instance fields
.field private handler:Landroid/os/Handler;

.field private ll_anim_all:Landroid/widget/LinearLayout;

.field private ll_anim_list:Landroid/widget/LinearLayout;

.field private mContext:Landroid/content/Context;

.field private mLayoutParams:Landroid/view/WindowManager$LayoutParams;

.field private mView:Landroid/view/View;

.field private rh:Landroid/view/WindowManager;

.field private sh:Landroid/content/SharedPreferences;

.field private startX:F

.field private startY:F

.field private th:D

.field private uh:F

.field private vh:F


# direct methods
.method public constructor <init>(Landroid/content/Context;Lcom/eckom/xtlibrary/b/a/d/h;)V
    .locals 12

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->startX:F

    iput v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->startY:F

    .line 3
    iput v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->uh:F

    iput v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->vh:F

    .line 4
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->handler:Landroid/os/Handler;

    .line 5
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mContext:Landroid/content/Context;

    const/4 v0, 0x0

    const-string v1, "VoiceCallView"

    .line 6
    invoke-virtual {p1, v1, v0}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v1

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->sh:Landroid/content/SharedPreferences;

    const-string v1, "window"

    .line 7
    invoke-virtual {p1, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/view/WindowManager;

    iput-object v1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->rh:Landroid/view/WindowManager;

    .line 8
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    sget v2, Lcom/eckom/xtlibrary/R$dimen;->tw_dp_w220:I

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    move-result v1

    .line 9
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    sget v3, Lcom/eckom/xtlibrary/R$dimen;->tw_dp_w60:I

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    move-result v2

    const/4 v3, 0x2

    new-array v3, v3, [I

    aput v1, v3, v0

    const/4 v1, 0x1

    aput v2, v3, v1

    .line 10
    new-instance v2, Landroid/view/WindowManager$LayoutParams;

    aget v5, v3, v0

    aget v6, v3, v1

    const/4 v7, 0x0

    const/4 v8, 0x0

    const/16 v9, 0x7d2

    const/16 v10, 0x28

    const/4 v11, 0x1

    move-object v4, v2

    invoke-direct/range {v4 .. v11}, Landroid/view/WindowManager$LayoutParams;-><init>(IIIIIII)V

    iput-object v2, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    .line 11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    const/16 v1, 0x33

    iput v1, v0, Landroid/view/WindowManager$LayoutParams;->gravity:I

    .line 12
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    invoke-virtual {v0}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v0

    iget v0, v0, Landroid/util/DisplayMetrics;->density:F

    const/high16 v1, 0x41c80000    # 25.0f

    mul-float/2addr v0, v1

    float-to-double v0, v0

    invoke-static {v0, v1}, Ljava/lang/Math;->ceil(D)D

    move-result-wide v0

    iput-wide v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->th:D

    const-string v0, "layout_inflater"

    .line 13
    invoke-virtual {p1, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Landroid/view/LayoutInflater;

    sget v0, Lcom/eckom/xtlibrary/R$layout;->anim_voice_call:I

    const/4 v1, 0x0

    invoke-virtual {p1, v0, v1}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    move-result-object p1

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    sget v0, Lcom/eckom/xtlibrary/R$id;->ll_anim_list:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/LinearLayout;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->ll_anim_list:Landroid/widget/LinearLayout;

    .line 15
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    sget v0, Lcom/eckom/xtlibrary/R$id;->ll_anim_all:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/LinearLayout;

    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->ll_anim_all:Landroid/widget/LinearLayout;

    .line 16
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    sget v0, Lcom/eckom/xtlibrary/R$id;->iv_voice_call:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p1

    new-instance v0, Lcom/eckom/xtlibrary/b/a/h/a;

    invoke-direct {v0, p0, p2}, Lcom/eckom/xtlibrary/b/a/h/a;-><init>(Lcom/eckom/xtlibrary/b/a/h/d;Lcom/eckom/xtlibrary/b/a/d/h;)V

    invoke-virtual {p1, v0}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 17
    invoke-direct {p0, v3}, Lcom/eckom/xtlibrary/b/a/h/d;->c([I)V

    .line 18
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    new-instance p2, Lcom/eckom/xtlibrary/b/a/h/b;

    invoke-direct {p2, p0}, Lcom/eckom/xtlibrary/b/a/h/b;-><init>(Lcom/eckom/xtlibrary/b/a/h/d;)V

    invoke-virtual {p1, p2}, Landroid/view/View;->setOnTouchListener(Landroid/view/View$OnTouchListener;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/a/h/d;)D
    .locals 2

    .line 1
    iget-wide v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->th:D

    return-wide v0
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/a/h/d;F)F
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->startX:F

    return p1
.end method

.method private a(FF)V
    .locals 2

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    float-to-int p1, p1

    iput p1, v0, Landroid/view/WindowManager$LayoutParams;->x:I

    float-to-int p2, p2

    .line 5
    iput p2, v0, Landroid/view/WindowManager$LayoutParams;->y:I

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->sh:Landroid/content/SharedPreferences;

    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v1, "CallingViewX"

    invoke-interface {v0, v1, p1}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    move-result-object p1

    invoke-interface {p1}, Landroid/content/SharedPreferences$Editor;->apply()V

    .line 7
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->sh:Landroid/content/SharedPreferences;

    invoke-interface {p1}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object p1

    const-string v0, "CallingViewY"

    invoke-interface {p1, v0, p2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    move-result-object p1

    invoke-interface {p1}, Landroid/content/SharedPreferences$Editor;->apply()V

    .line 8
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->rh:Landroid/view/WindowManager;

    iget-object p2, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    invoke-interface {p1, p2, p0}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/b/a/h/d;FF)V
    .locals 0

    .line 3
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/h/d;->a(FF)V

    return-void
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/a/h/d;)F
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->uh:F

    return p0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/b/a/h/d;F)F
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->startY:F

    return p1
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/widget/LinearLayout;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->ll_anim_list:Landroid/widget/LinearLayout;

    return-object p0
.end method

.method private c([I)V
    .locals 5

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->sh:Landroid/content/SharedPreferences;

    const/4 v1, -0x1

    const-string v2, "CallingViewX"

    invoke-interface {v0, v2, v1}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    .line 3
    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/h/d;->sh:Landroid/content/SharedPreferences;

    const-string v3, "CallingViewY"

    invoke-interface {v2, v3, v1}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v2

    if-eq v0, v1, :cond_0

    .line 4
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    iput v0, p1, Landroid/view/WindowManager$LayoutParams;->x:I

    goto :goto_0

    .line 5
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    iget-object v3, p0, Lcom/eckom/xtlibrary/b/a/h/d;->rh:Landroid/view/WindowManager;

    invoke-interface {v3}, Landroid/view/WindowManager;->getDefaultDisplay()Landroid/view/Display;

    move-result-object v3

    invoke-virtual {v3}, Landroid/view/Display;->getWidth()I

    move-result v3

    const/4 v4, 0x0

    aget p1, p1, v4

    sub-int/2addr v3, p1

    div-int/lit8 v3, v3, 0x2

    iput v3, v0, Landroid/view/WindowManager$LayoutParams;->x:I

    :goto_0
    if-eq v2, v1, :cond_1

    .line 6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    iput v2, p0, Landroid/view/WindowManager$LayoutParams;->y:I

    :cond_1
    return-void
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/b/a/h/d;)F
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->vh:F

    return p0
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/b/a/h/d;)F
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->startX:F

    return p0
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/b/a/h/d;)F
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->startY:F

    return p0
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/view/View;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    return-object p0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/view/WindowManager$LayoutParams;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    return-object p0
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/view/WindowManager;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->rh:Landroid/view/WindowManager;

    return-object p0
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/b/a/h/d;)Landroid/widget/LinearLayout;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->ll_anim_all:Landroid/widget/LinearLayout;

    return-object p0
.end method


# virtual methods
.method public hide()V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->rh:Landroid/view/WindowManager;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    invoke-interface {v0, p0}, Landroid/view/WindowManager;->removeView(Landroid/view/View;)V

    :cond_0
    return-void
.end method

.method public show()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    move-result-object v0

    if-nez v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->ll_anim_all:Landroid/widget/LinearLayout;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setVisibility(I)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->rh:Landroid/view/WindowManager;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mView:Landroid/view/View;

    iget-object v2, p0, Lcom/eckom/xtlibrary/b/a/h/d;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    invoke-interface {v0, v1, v2}, Landroid/view/WindowManager;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->ll_anim_list:Landroid/widget/LinearLayout;

    invoke-virtual {v0}, Landroid/widget/LinearLayout;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    check-cast v0, Landroid/graphics/drawable/AnimationDrawable;

    .line 5
    invoke-virtual {v0}, Landroid/graphics/drawable/AnimationDrawable;->isRunning()Z

    move-result v1

    if-nez v1, :cond_0

    .line 6
    invoke-virtual {v0}, Landroid/graphics/drawable/AnimationDrawable;->start()V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/h/d;->handler:Landroid/os/Handler;

    new-instance v1, Lcom/eckom/xtlibrary/b/a/h/c;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/b/a/h/c;-><init>(Lcom/eckom/xtlibrary/b/a/h/d;)V

    const/16 p0, 0x2710

    const-string v2, "persist.bt.voiceView.delayed"

    .line 8
    invoke-static {v2, p0}, Landroid/os/SystemProperties;->getInt(Ljava/lang/String;I)I

    move-result p0

    int-to-long v2, p0

    .line 9
    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    :cond_0
    return-void
.end method

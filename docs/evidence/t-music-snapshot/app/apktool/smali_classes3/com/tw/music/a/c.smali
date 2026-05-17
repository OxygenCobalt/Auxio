.class public Lcom/tw/music/a/c;
.super Landroid/widget/BaseAdapter;
.source "MusicAdapter.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/tw/music/a/c$a;,
        Lcom/tw/music/a/c$b;,
        Lcom/tw/music/a/c$c;
    }
.end annotation


# instance fields
.field private Pa:Lcom/tw/music/b/a;

.field private mContext:Landroid/content/Context;

.field private mOnItemClickListener:Lcom/tw/music/a/c$b;

.field private mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

.field private xf:Lcom/tw/music/a/c$a;

.field private yf:I

.field private zf:Landroid/graphics/drawable/Drawable;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Landroid/widget/BaseAdapter;-><init>()V

    .line 2
    iput-object p1, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    return-void
.end method

.method private a(Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 3

    .line 7
    new-instance v0, Lcom/tw/music/a/c$c;

    const/4 v1, 0x0

    invoke-direct {v0, p0, v1}, Lcom/tw/music/a/c$c;-><init>(Lcom/tw/music/a/c;Lcom/tw/music/a/a;)V

    .line 8
    iget-object p0, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-static {p0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object p0

    const v1, 0x7f0a0028

    const/4 v2, 0x0

    invoke-virtual {p0, v1, p1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object p0

    iput-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    .line 9
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f0800d9

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/TextView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->sm:Landroid/widget/TextView;

    .line 10
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f080067

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/ImageView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->tm:Landroid/widget/ImageView;

    .line 11
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f0800e1

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/TextView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    .line 12
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f080062

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    iput-object p0, v0, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    .line 13
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f080063

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    iput-object p0, v0, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    .line 14
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f0800da

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/TextView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    .line 15
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f080068

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/ImageView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    .line 16
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f080027

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/ImageView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    .line 17
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f080047

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/ImageView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->file_ic:Landroid/widget/ImageView;

    .line 18
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    const p1, 0x7f08008d

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/ImageView;

    iput-object p0, v0, Lcom/tw/music/a/c$c;->zm:Landroid/widget/ImageView;

    .line 19
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    invoke-virtual {p0, v0}, Landroid/view/View;->setTag(Ljava/lang/Object;)V

    .line 20
    iget-object p0, v0, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    invoke-virtual {p0}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object p0

    check-cast p0, Landroid/graphics/drawable/AnimationDrawable;

    if-eqz p0, :cond_0

    .line 21
    invoke-virtual {p0}, Landroid/graphics/drawable/AnimationDrawable;->start()V

    .line 22
    :cond_0
    iget-object p0, v0, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    return-object p0
.end method

.method static synthetic a(Lcom/tw/music/a/c;)Lcom/eckom/xtlibrary/b/f/b/g;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    return-object p0
.end method

.method private a(Landroid/view/View;ILandroid/view/ViewGroup;)V
    .locals 10

    .line 23
    invoke-virtual {p1}, Landroid/view/View;->getTag()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/tw/music/a/c$c;

    .line 24
    :try_start_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p3

    invoke-virtual {p3}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object p3

    const-string v0, "selector_item_bg"

    invoke-static {p3, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object p3

    if-eqz p3, :cond_0

    .line 25
    iget-object v0, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v0, p3}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 26
    iget-object v0, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    invoke-virtual {v0, p3}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 27
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p3

    invoke-virtual {p3}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object p3

    const-string v0, "ic_wenjianjia"

    invoke-static {p3, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object p3

    if-eqz p3, :cond_1

    .line 28
    iget-object v0, p1, Lcom/tw/music/a/c$c;->file_ic:Landroid/widget/ImageView;

    invoke-virtual {v0, p3}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 29
    :cond_1
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p3

    invoke-virtual {p3}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object p3

    const-string v0, "selector_btn_icon"

    invoke-static {p3, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object p3

    if-eqz p3, :cond_2

    .line 30
    iget-object v0, p1, Lcom/tw/music/a/c$c;->zm:Landroid/widget/ImageView;

    invoke-virtual {v0, p3}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 31
    :cond_2
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p3

    invoke-virtual {p3}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object p3

    const-string v0, "lev_play_now"

    invoke-static {p3, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object p3

    if-eqz p3, :cond_3

    .line 32
    iget-object v0, p1, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    invoke-virtual {v0, p3}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 33
    iget-object p3, p1, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    invoke-virtual {p3}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object p3

    check-cast p3, Landroid/graphics/drawable/AnimationDrawable;

    if-eqz p3, :cond_3

    .line 34
    invoke-virtual {p3}, Landroid/graphics/drawable/AnimationDrawable;->start()V

    .line 35
    :cond_3
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p3

    invoke-virtual {p3}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object p3

    const-string v0, "selector_btn_collect_list"

    invoke-static {p3, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object p3

    if-eqz p3, :cond_4

    .line 36
    iget-object v0, p1, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    invoke-virtual {v0, p3}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 37
    :catch_0
    :cond_4
    iget-object p3, p0, Lcom/tw/music/a/c;->zf:Landroid/graphics/drawable/Drawable;

    if-eqz p3, :cond_5

    .line 38
    iget-object v0, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v0, p3}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 39
    iget-object p3, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    iget-object v0, p0, Lcom/tw/music/a/c;->zf:Landroid/graphics/drawable/Drawable;

    invoke-virtual {p3, v0}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 40
    :cond_5
    iget-object p3, p1, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    const/4 v0, 0x0

    invoke-virtual {p3, v0}, Landroid/widget/ImageView;->setVisibility(I)V

    .line 41
    iget-object p3, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, p3, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v2, 0x4

    const v3, 0x7f050035

    const v4, 0x7f050062

    const/16 v5, 0x8

    const/4 v6, 0x1

    if-ne v1, v2, :cond_9

    .line 42
    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, p3, p2

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 43
    aget-object p3, p3, p2

    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    .line 44
    iget-object v7, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v7, v5}, Landroid/view/View;->setVisibility(I)V

    .line 45
    iget-object v5, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    invoke-virtual {v5, v0}, Landroid/view/View;->setVisibility(I)V

    .line 46
    iget-object v5, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    add-int/lit8 v7, p2, 0x1

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v5, v7}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 47
    iget-object v5, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    invoke-virtual {v5, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 48
    iget-object v5, p0, Lcom/tw/music/a/c;->Pa:Lcom/tw/music/b/a;

    if-eqz v5, :cond_7

    .line 49
    invoke-virtual {v5}, Lcom/tw/music/b/a;->gd()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {p3, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_6

    .line 50
    iget-object v2, p1, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    invoke-virtual {v2, v0}, Landroid/widget/ImageView;->setVisibility(I)V

    .line 51
    iget-object v0, p1, Lcom/tw/music/a/c$c;->zm:Landroid/widget/ImageView;

    invoke-virtual {v0, v6}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 52
    iget-object v0, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 53
    iget-object v0, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    goto :goto_0

    .line 54
    :cond_6
    iget-object v3, p1, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    invoke-virtual {v3, v2}, Landroid/widget/ImageView;->setVisibility(I)V

    .line 55
    iget-object v2, p1, Lcom/tw/music/a/c$c;->zm:Landroid/widget/ImageView;

    invoke-virtual {v2, v0}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 56
    iget-object v0, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v4}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 57
    iget-object v0, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v4}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 58
    :cond_7
    :goto_0
    iget-object v0, p1, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    invoke-virtual {v0}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    iget-object v2, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v2, v2, p2

    iget-boolean v2, v2, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    invoke-virtual {v0, v2}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    :cond_8
    :goto_1
    move-object v4, p3

    move-object v3, v1

    goto/16 :goto_7

    :cond_9
    if-nez v1, :cond_a

    .line 59
    iget-object p3, p1, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    invoke-virtual {p3, v5}, Landroid/widget/ImageView;->setVisibility(I)V

    .line 60
    :cond_a
    iget-object p3, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, p3, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-nez v1, :cond_b

    .line 61
    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, p3, p2

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 62
    aget-object p3, p3, p2

    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    goto :goto_2

    :cond_b
    if-nez p2, :cond_c

    .line 63
    iget-object v1, p3, Lcom/eckom/xtlibrary/b/f/b/g;->mName:Ljava/lang/String;

    const/4 p3, 0x0

    goto :goto_2

    .line 64
    :cond_c
    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    add-int/lit8 v1, p2, -0x1

    aget-object v7, p3, v1

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/f/b/f;->mName:Ljava/lang/String;

    .line 65
    aget-object p3, p3, v1

    iget-object p3, p3, Lcom/eckom/xtlibrary/b/f/b/f;->mPath:Ljava/lang/String;

    .line 66
    iget-object v8, p1, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    invoke-virtual {v8}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v8

    iget-object v9, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v1, v9, v1

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    invoke-virtual {v8, v1}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    move-object v1, v7

    .line 67
    :goto_2
    iget-object v7, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v7, v7, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-eqz v7, :cond_d

    if-nez p2, :cond_d

    .line 68
    iget-object v2, p1, Lcom/tw/music/a/c$c;->sm:Landroid/widget/TextView;

    invoke-virtual {v2, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 69
    iget-object v2, p1, Lcom/tw/music/a/c$c;->tm:Landroid/widget/ImageView;

    invoke-virtual {v2}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {v2, v6}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 70
    iget-object v2, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v2, v0}, Landroid/view/View;->setVisibility(I)V

    .line 71
    iget-object v0, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    invoke-virtual {v0, v5}, Landroid/view/View;->setVisibility(I)V

    .line 72
    iget-object v0, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v0, v6}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 73
    iget-object v0, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    if-eqz v0, :cond_8

    .line 74
    iget-object v0, p1, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    invoke-virtual {v0}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    iget-object v2, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object v2, v2, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    aget-object v2, v2, p2

    iget-boolean v2, v2, Lcom/eckom/xtlibrary/b/f/b/f;->ek:Z

    invoke-virtual {v0, v2}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    goto/16 :goto_1

    .line 75
    :cond_d
    iget-object v7, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v8, v7, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-eq v8, v6, :cond_10

    iget v7, v7, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-nez v7, :cond_e

    goto :goto_3

    .line 76
    :cond_e
    iget-object v2, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v2, v0}, Landroid/view/View;->setVisibility(I)V

    .line 77
    iget-object v2, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    invoke-virtual {v2, v5}, Landroid/view/View;->setVisibility(I)V

    .line 78
    iget-object v2, p1, Lcom/tw/music/a/c$c;->sm:Landroid/widget/TextView;

    invoke-virtual {v2, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 79
    iget-object v2, p1, Lcom/tw/music/a/c$c;->tm:Landroid/widget/ImageView;

    invoke-virtual {v2}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {v2, v0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 80
    iget-object v2, p0, Lcom/tw/music/a/c;->Pa:Lcom/tw/music/b/a;

    if-eqz v2, :cond_8

    .line 81
    sget-object v2, Lcom/eckom/xtlibrary/b/f/f/s;->Cd:Ljava/lang/String;

    invoke-virtual {p3, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_f

    .line 82
    iget-object v0, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v0, v6}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 83
    iget-object v0, p1, Lcom/tw/music/a/c$c;->sm:Landroid/widget/TextView;

    invoke-virtual {v0, v6}, Landroid/widget/TextView;->setSelected(Z)V

    .line 84
    iget-object v0, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    invoke-virtual {v0, v6}, Landroid/widget/TextView;->setSelected(Z)V

    goto/16 :goto_1

    .line 85
    :cond_f
    iget-object v2, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v2}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {v2, v0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 86
    iget-object v2, p1, Lcom/tw/music/a/c$c;->sm:Landroid/widget/TextView;

    invoke-virtual {v2, v0}, Landroid/widget/TextView;->setSelected(Z)V

    .line 87
    iget-object v2, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    invoke-virtual {v2, v0}, Landroid/widget/TextView;->setSelected(Z)V

    goto/16 :goto_1

    .line 88
    :cond_10
    :goto_3
    iget-object v7, p1, Lcom/tw/music/a/c$c;->vm:Landroid/view/View;

    invoke-virtual {v7, v5}, Landroid/view/View;->setVisibility(I)V

    .line 89
    iget-object v5, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    invoke-virtual {v5, v0}, Landroid/view/View;->setVisibility(I)V

    .line 90
    iget-object v5, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    invoke-virtual {v5}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v5

    invoke-virtual {v5, v0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 91
    iget-object v5, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    invoke-virtual {v5, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 92
    iget-object v5, p0, Lcom/tw/music/a/c;->Pa:Lcom/tw/music/b/a;

    if-eqz v5, :cond_8

    .line 93
    sget-object v5, Lcom/eckom/xtlibrary/b/f/f/s;->Bd:Ljava/lang/String;

    invoke-virtual {p3, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_12

    .line 94
    iget-object v2, p1, Lcom/tw/music/a/c$c;->wm:Landroid/view/View;

    invoke-virtual {v2}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {v2, v6}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 95
    iget-object v2, p1, Lcom/tw/music/a/c$c;->sm:Landroid/widget/TextView;

    invoke-virtual {v2, v6}, Landroid/widget/TextView;->setSelected(Z)V

    .line 96
    iget-object v2, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    invoke-virtual {v2, v6}, Landroid/widget/TextView;->setSelected(Z)V

    .line 97
    iget-object v2, p1, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    invoke-virtual {v2, v0}, Landroid/widget/ImageView;->setVisibility(I)V

    .line 98
    iget-object v0, p1, Lcom/tw/music/a/c$c;->zm:Landroid/widget/ImageView;

    invoke-virtual {v0, v6}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 99
    :try_start_1
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object v0

    const-string v2, "color_song_selector"

    invoke-static {v0, v2}, Lcom/eckom/xtlibrary/b/i/h;->c(Landroid/content/Context;Ljava/lang/String;)I

    move-result v0

    iput v0, p0, Lcom/tw/music/a/c;->yf:I
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    goto :goto_4

    :catch_1
    move-exception v0

    .line 100
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    .line 101
    :goto_4
    iget v0, p0, Lcom/tw/music/a/c;->yf:I

    if-eqz v0, :cond_11

    .line 102
    iget-object v2, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    invoke-virtual {v2, v0}, Landroid/widget/TextView;->setTextColor(I)V

    .line 103
    iget-object v0, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    iget v2, p0, Lcom/tw/music/a/c;->yf:I

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    goto/16 :goto_6

    .line 104
    :cond_11
    iget-object v0, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 105
    iget-object v0, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    goto :goto_6

    .line 106
    :cond_12
    iget-object v3, p1, Lcom/tw/music/a/c$c;->sm:Landroid/widget/TextView;

    invoke-virtual {v3, v0}, Landroid/widget/TextView;->setSelected(Z)V

    .line 107
    iget-object v3, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    invoke-virtual {v3, v0}, Landroid/widget/TextView;->setSelected(Z)V

    .line 108
    iget-object v3, p1, Lcom/tw/music/a/c$c;->iv_isPlaying:Landroid/widget/ImageView;

    invoke-virtual {v3, v2}, Landroid/widget/ImageView;->setVisibility(I)V

    .line 109
    iget-object v2, p1, Lcom/tw/music/a/c$c;->zm:Landroid/widget/ImageView;

    invoke-virtual {v2, v0}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 110
    :try_start_2
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object v0

    const-string v2, "color_song_normal"

    invoke-static {v0, v2}, Lcom/eckom/xtlibrary/b/i/h;->c(Landroid/content/Context;Ljava/lang/String;)I

    move-result v0

    iput v0, p0, Lcom/tw/music/a/c;->yf:I
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    goto :goto_5

    :catch_2
    move-exception v0

    .line 111
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    .line 112
    :goto_5
    iget v0, p0, Lcom/tw/music/a/c;->yf:I

    if-eqz v0, :cond_13

    .line 113
    iget-object v2, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    invoke-virtual {v2, v0}, Landroid/widget/TextView;->setTextColor(I)V

    .line 114
    iget-object v0, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    iget v2, p0, Lcom/tw/music/a/c;->yf:I

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    goto :goto_6

    .line 115
    :cond_13
    iget-object v0, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v4}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 116
    iget-object v0, p1, Lcom/tw/music/a/c$c;->um:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/tw/music/a/c;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-virtual {v2, v4}, Landroid/content/res/Resources;->getColor(I)I

    move-result v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 117
    :goto_6
    iget-object v0, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v0, v0, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-eqz v0, :cond_14

    if-eqz p2, :cond_14

    .line 118
    iget-object v0, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    invoke-static {p2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto/16 :goto_1

    .line 119
    :cond_14
    iget-object v0, p1, Lcom/tw/music/a/c$c;->xm:Landroid/widget/TextView;

    add-int/lit8 v2, p2, 0x1

    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto/16 :goto_1

    .line 120
    :goto_7
    iget-object p3, p1, Lcom/tw/music/a/c$c;->ym:Landroid/widget/ImageView;

    new-instance v6, Lcom/tw/music/a/a;

    move-object v0, v6

    move-object v1, p0

    move v2, p2

    move-object v5, p1

    invoke-direct/range {v0 .. v5}, Lcom/tw/music/a/a;-><init>(Lcom/tw/music/a/c;ILjava/lang/String;Ljava/lang/String;Lcom/tw/music/a/c$c;)V

    invoke-virtual {p3, v6}, Landroid/widget/ImageView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 121
    iget-object p1, p1, Lcom/tw/music/a/c$c;->itemView:Landroid/view/View;

    new-instance p3, Lcom/tw/music/a/b;

    invoke-direct {p3, p0, p2}, Lcom/tw/music/a/b;-><init>(Lcom/tw/music/a/c;I)V

    invoke-virtual {p1, p3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    return-void
.end method

.method static synthetic b(Lcom/tw/music/a/c;)Lcom/tw/music/a/c$a;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/a/c;->xf:Lcom/tw/music/a/c$a;

    return-object p0
.end method

.method static synthetic c(Lcom/tw/music/a/c;)Lcom/tw/music/a/c$b;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/a/c;->mOnItemClickListener:Lcom/tw/music/a/c$b;

    return-object p0
.end method


# virtual methods
.method public Xa()V
    .locals 0

    .line 1
    invoke-virtual {p0}, Landroid/widget/BaseAdapter;->notifyDataSetChanged()V

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;Lcom/tw/music/b/a;)V
    .locals 0

    .line 2
    iput-object p1, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 3
    iput-object p2, p0, Lcom/tw/music/a/c;->Pa:Lcom/tw/music/b/a;

    .line 4
    invoke-virtual {p0}, Landroid/widget/BaseAdapter;->notifyDataSetChanged()V

    return-void
.end method

.method public a(Lcom/tw/music/a/c$a;)V
    .locals 0

    .line 6
    iput-object p1, p0, Lcom/tw/music/a/c;->xf:Lcom/tw/music/a/c$a;

    return-void
.end method

.method public a(Lcom/tw/music/a/c$b;)V
    .locals 0

    .line 5
    iput-object p1, p0, Lcom/tw/music/a/c;->mOnItemClickListener:Lcom/tw/music/a/c$b;

    return-void
.end method

.method public getCount()I
    .locals 3

    const/4 v0, 0x0

    .line 1
    :try_start_0
    iget-object v1, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    const/4 v2, 0x4

    if-ne v1, v2, :cond_0

    .line 2
    iget-object p0, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/f/b/g;->jk:[Lcom/eckom/xtlibrary/b/f/b/f;

    array-length p0, p0

    return p0

    .line 3
    :cond_0
    iget-object v1, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    if-nez v1, :cond_1

    return v0

    .line 4
    :cond_1
    iget-object v1, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget v1, v1, Lcom/eckom/xtlibrary/b/f/b/g;->ik:I

    if-nez v1, :cond_2

    .line 5
    iget-object p0, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I

    return p0

    .line 6
    :cond_2
    iget-object p0, p0, Lcom/tw/music/a/c;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    iget p0, p0, Lcom/eckom/xtlibrary/b/f/b/g;->kk:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    add-int/lit8 p0, p0, 0x1

    return p0

    :catch_0
    move-exception p0

    .line 7
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "An exception occurred at getMusicCount : "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {p0}, Lcom/tw/music/utils/a;->e(Ljava/lang/String;)V

    return v0
.end method

.method public getItem(I)Ljava/lang/Object;
    .locals 0

    .line 1
    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p0

    return-object p0
.end method

.method public getItemId(I)J
    .locals 0

    int-to-long p0, p1

    return-wide p0
.end method

.method public getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 0

    if-nez p2, :cond_0

    .line 1
    invoke-direct {p0, p3}, Lcom/tw/music/a/c;->a(Landroid/view/ViewGroup;)Landroid/view/View;

    move-result-object p2

    .line 2
    :cond_0
    invoke-direct {p0, p2, p1, p3}, Lcom/tw/music/a/c;->a(Landroid/view/View;ILandroid/view/ViewGroup;)V

    return-object p2
.end method

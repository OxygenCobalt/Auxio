.class public Lcom/eckom/xtlibrary/twproject/video/model/m;
.super Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode;
.source "VideoIjkModel.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eckom/xtlibrary/twproject/video/model/m$a;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<P:",
        "Lcom/eckom/xtlibrary/b/g/a;",
        ">",
        "Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode;"
    }
.end annotation


# static fields
.field private static dj:Lcom/eckom/xtlibrary/b/k/c/b;

.field private static ej:Lcom/eckom/xtlibrary/twproject/video/model/m;

.field public static fj:Lcom/eckom/xtlibrary/twproject/video/utils/j;

.field private static jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;


# instance fields
.field private Bi:Lcom/eckom/xtlibrary/twproject/video/model/m$a;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/eckom/xtlibrary/twproject/video/model/m<",
            "TP;>.a;"
        }
    .end annotation
.end field

.field private Mi:Z

.field private Ni:Z

.field private Oi:Z

.field private Pi:Z

.field private Qh:Z

.field private Qi:D

.field private Ri:Z

.field private Ti:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

.field private Uh:I

.field Ui:Z

.field private Vh:Ljava/lang/String;

.field private Vi:I

.field public Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

.field Xi:Ljava/lang/String;

.field private Yi:Landroid/content/BroadcastReceiver;

.field Zi:Landroid/view/View$OnTouchListener;

.field _i:Z

.field cj:Ljava/lang/Boolean;

.field private floatframelayout:Landroid/widget/FrameLayout;

.field img_suspension_bigger:Landroid/view/View$OnClickListener;

.field img_suspension_finish:Landroid/view/View$OnClickListener;

.field img_suspension_next:Landroid/view/View$OnClickListener;

.field img_suspension_pp:Landroid/view/View$OnClickListener;

.field img_suspension_prve:Landroid/view/View$OnClickListener;

.field img_suspension_smaller:Landroid/view/View$OnClickListener;

.field img_suspension_video:Landroid/view/View$OnClickListener;

.field kf:I

.field private layout_suspension:Landroid/view/View;

.field private mContext:Landroid/content/Context;

.field public mDisplay:Landroid/view/Display;

.field private mHandler:Landroid/os/Handler;

.field private mHints:[J

.field private mLayoutParams:Landroid/view/WindowManager$LayoutParams;

.field public mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

.field private mRoot:Landroid/view/View;

.field private mService:I

.field private r:Lcom/eckom/xtlibrary/b/k/a/b;

.field private rh:Landroid/view/WindowManager;

.field private ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

.field startX:F

.field startY:F

.field private ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

.field private warning_driving:Landroid/view/View;

.field private warning_tx:Landroid/widget/TextView;

.field private wi:I

.field private xi:Z


# direct methods
.method public constructor <init>()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Qh:Z

    const/4 v1, 0x0

    .line 3
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    .line 4
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->xi:Z

    .line 5
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Mi:Z

    .line 6
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ni:Z

    .line 7
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Oi:Z

    .line 8
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Pi:Z

    .line 9
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mRoot:Landroid/view/View;

    .line 10
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->rh:Landroid/view/WindowManager;

    .line 11
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    .line 12
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->floatframelayout:Landroid/widget/FrameLayout;

    const-wide/high16 v1, -0x4010000000000000L    # -1.0

    .line 13
    iput-wide v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Qi:D

    .line 14
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mService:I

    .line 15
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->wi:I

    .line 16
    new-instance v1, Landroid/os/Handler;

    new-instance v2, Lcom/eckom/xtlibrary/twproject/video/model/f;

    invoke-direct {v2, p0}, Lcom/eckom/xtlibrary/twproject/video/model/f;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Handler$Callback;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    .line 17
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/g;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/g;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->ri:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnCompletionListener;

    const/4 v1, 0x7

    .line 18
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Vi:I

    .line 19
    iget v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Vi:I

    new-array v1, v1, [J

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHints:[J

    .line 20
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/h;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/h;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->ti:Ltv/danmaku/ijk/media/player/IMediaPlayer$OnErrorListener;

    const/4 v1, -0x1

    .line 21
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Uh:I

    const-string v1, ""

    .line 22
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Vh:Ljava/lang/String;

    .line 23
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Xi:Ljava/lang/String;

    .line 24
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/i;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/i;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Yi:Landroid/content/BroadcastReceiver;

    .line 25
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/j;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/j;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->img_suspension_finish:Landroid/view/View$OnClickListener;

    .line 26
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/k;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/k;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->img_suspension_smaller:Landroid/view/View$OnClickListener;

    .line 27
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/l;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/l;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->img_suspension_bigger:Landroid/view/View$OnClickListener;

    .line 28
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/a;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/a;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->img_suspension_video:Landroid/view/View$OnClickListener;

    .line 29
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/b;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/b;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->img_suspension_prve:Landroid/view/View$OnClickListener;

    .line 30
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/c;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/c;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->img_suspension_next:Landroid/view/View$OnClickListener;

    .line 31
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/d;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/d;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->img_suspension_pp:Landroid/view/View$OnClickListener;

    .line 32
    new-instance v1, Lcom/eckom/xtlibrary/twproject/video/model/e;

    invoke-direct {v1, p0}, Lcom/eckom/xtlibrary/twproject/video/model/e;-><init>(Lcom/eckom/xtlibrary/twproject/video/model/m;)V

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Zi:Landroid/view/View$OnTouchListener;

    const/4 v1, 0x0

    .line 33
    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->startX:F

    iput v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->startY:F

    .line 34
    iput v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->kf:I

    const/4 v1, 0x1

    .line 35
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->_i:Z

    .line 36
    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->cj:Ljava/lang/Boolean;

    return-void
.end method

.method private L(Z)V
    .locals 0

    if-eqz p1, :cond_0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    const/high16 p1, 0x3f000000    # 0.5f

    invoke-virtual {p0, p1, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->setVolume(FF)V

    goto :goto_0

    .line 2
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    const/high16 p1, 0x3f800000    # 1.0f

    invoke-virtual {p0, p1, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->setVolume(FF)V

    :goto_0
    return-void
.end method

.method private N(Z)V
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->warning_driving:Landroid/view/View;

    if-eqz v0, :cond_1

    if-eqz p1, :cond_0

    const/16 p1, 0x8

    .line 2
    invoke-virtual {v0, p1}, Landroid/view/View;->setVisibility(I)V

    goto :goto_0

    :cond_0
    const/4 p1, 0x0

    .line 3
    invoke-virtual {v0, p1}, Landroid/view/View;->setVisibility(I)V

    .line 4
    :cond_1
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->warning_tx:Landroid/widget/TextView;

    if-eqz p1, :cond_2

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Xi:Ljava/lang/String;

    invoke-virtual {p1, p0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    :cond_2
    return-void
.end method

.method private Re()V
    .locals 2

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    iget v0, v0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    if-lez v0, :cond_1

    .line 2
    sget v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    const/4 v1, 0x2

    if-eq v0, v1, :cond_0

    .line 3
    sget v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    :cond_0
    const/4 v0, 0x0

    .line 4
    invoke-direct {p0, v0, v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Sa()V
    .locals 0

    .line 1
    invoke-static {}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Sa()V

    return-void
.end method

.method private Se()V
    .locals 3

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    iget v0, v0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    if-lez v0, :cond_1

    .line 2
    sget v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    const/4 v1, 0x2

    const/4 v2, 0x1

    if-eq v0, v1, :cond_0

    .line 3
    sget v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    sub-int/2addr v0, v2

    sput v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    :cond_0
    const/4 v0, 0x0

    .line 4
    invoke-direct {p0, v0, v2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->c(IZ)V

    :cond_1
    return-void
.end method

.method private Ve()Z
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v1, 0xff02

    invoke-virtual {v0, v1}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v0

    if-nez v0, :cond_0

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v0, 0xff03

    invoke-virtual {p0, v0}, Landroid/os/Handler;->hasMessages(I)Z

    move-result p0

    if-nez p0, :cond_0

    const/4 p0, 0x1

    goto :goto_0

    :cond_0
    const/4 p0, 0x0

    :goto_0
    return p0
.end method

.method private Xe()V
    .locals 7

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getDuration()I

    move-result v0

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getCurrentPosition()I

    move-result v1

    const/4 v2, 0x0

    if-gez v0, :cond_0

    move v0, v2

    :cond_0
    if-gez v1, :cond_1

    move v1, v2

    :cond_1
    if-lez v0, :cond_2

    if-gt v1, v0, :cond_2

    mul-int/lit8 v1, v1, 0x64

    .line 3
    div-int v0, v1, v0

    goto :goto_0

    :cond_2
    move v0, v2

    .line 4
    :goto_0
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    const v3, 0x9f00

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v4

    const/16 v5, 0x80

    if-eqz v4, :cond_3

    move v4, v5

    goto :goto_1

    :cond_3
    move v4, v2

    :goto_1
    and-int/lit8 v0, v0, 0x7f

    or-int/2addr v4, v0

    const/16 v6, 0x9

    invoke-virtual {v1, v3, v6, v4}, Landroid/tw/john/TWUtil;->write(III)I

    .line 5
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    const/16 v3, 0x303

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v4

    if-eqz v4, :cond_4

    move v2, v5

    :cond_4
    or-int/2addr v0, v2

    invoke-virtual {v1, v3, v6, v0}, Landroid/tw/john/TWUtil;->write(III)I

    .line 6
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v0, 0x9e06

    invoke-virtual {p0, v0}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    return-void
.end method

.method private Ze()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Vh:Ljava/lang/String;

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_0

    return-void

    .line 2
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mContext:Landroid/content/Context;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->r:Lcom/eckom/xtlibrary/b/k/a/b;

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Vh:Ljava/lang/String;

    invoke-virtual {v0, v1, v2, v3}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->a(Landroid/content/Context;Lcom/eckom/xtlibrary/b/k/a/b;Ljava/lang/String;)V

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->r:Lcom/eckom/xtlibrary/b/k/a/b;

    iput-object p0, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/model/m;I)I
    .locals 0

    .line 2
    iput p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->wi:I

    return p1
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Sa()V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/model/m;IZ)V
    .locals 0

    .line 3
    invoke-direct {p0, p1, p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->c(IZ)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V
    .locals 0

    .line 4
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->L(Z)V

    return-void
.end method

.method static synthetic a(Lcom/eckom/xtlibrary/twproject/video/model/m;[J)[J
    .locals 0

    .line 5
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHints:[J

    return-object p1
.end method

.method static synthetic access$200()Lcom/eckom/xtlibrary/b/k/c/b;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    return-object v0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/twproject/video/model/m;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->wi:I

    return p0
.end method

.method static synthetic b(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->mute(Z)V

    return-void
.end method

.method private bf()V
    .locals 2

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Landroid/widget/FrameLayout;->getParent()Landroid/view/ViewParent;

    move-result-object v0

    check-cast v0, Landroid/view/ViewGroup;

    if-eqz v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0, p0}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 3
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "[273]"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string v0, "md"

    invoke-static {v0, p0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :cond_0
    :goto_0
    return-void
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/twproject/video/model/m;)I
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->getService()I

    move-result p0

    return p0
.end method

.method private c(IZ)V
    .locals 7

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    monitor-enter v0

    .line 4
    :try_start_0
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->kd:[I

    if-eqz v1, :cond_d

    .line 5
    array-length v2, v1

    if-lez v2, :cond_d

    .line 6
    sget v3, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    const/4 v4, 0x0

    if-eqz p2, :cond_6

    const/4 p2, -0x1

    if-ge v3, p2, :cond_0

    move v3, p2

    :cond_0
    move v5, p1

    move p1, v3

    :goto_0
    if-le p1, p2, :cond_2

    .line 7
    aget v6, v1, p1

    sput v6, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    .line 8
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/twproject/video/model/m;->play(I)Z

    move-result v5

    if-eqz v5, :cond_1

    .line 9
    sput p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    move v5, v4

    goto :goto_1

    :cond_1
    add-int/lit8 p1, p1, -0x1

    move v5, v4

    goto :goto_0

    .line 10
    :cond_2
    :goto_1
    sget v6, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    if-eqz v6, :cond_5

    if-ne p1, p2, :cond_5

    add-int/lit8 v2, v2, -0x1

    :goto_2
    if-le v2, v3, :cond_4

    .line 11
    aget p1, v1, v2

    sput p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    .line 12
    invoke-direct {p0, v5}, Lcom/eckom/xtlibrary/twproject/video/model/m;->play(I)Z

    move-result p1

    if-eqz p1, :cond_3

    .line 13
    sput v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    goto :goto_3

    :cond_3
    add-int/lit8 v2, v2, -0x1

    move v5, v4

    goto :goto_2

    :cond_4
    :goto_3
    if-ne v2, v3, :cond_5

    .line 14
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->stop()V

    .line 15
    :cond_5
    sget p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    if-ne p1, p2, :cond_d

    .line 16
    sput v4, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    .line 17
    sget p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    aget p1, v1, p1

    sput p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    .line 18
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->stop()V

    goto :goto_8

    :cond_6
    if-le v3, v2, :cond_7

    move v3, v2

    :cond_7
    move p2, p1

    move p1, v3

    :goto_4
    if-ge p1, v2, :cond_9

    .line 19
    aget v5, v1, p1

    sput v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    .line 20
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->play(I)Z

    move-result p2

    if-eqz p2, :cond_8

    .line 21
    sput p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    move p2, v4

    goto :goto_5

    :cond_8
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_4

    .line 22
    :cond_9
    :goto_5
    sget v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    if-eqz v5, :cond_c

    if-ne p1, v2, :cond_c

    move p1, v4

    :goto_6
    if-ge p1, v3, :cond_b

    .line 23
    aget v5, v1, p1

    sput v5, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    .line 24
    invoke-direct {p0, p2}, Lcom/eckom/xtlibrary/twproject/video/model/m;->play(I)Z

    move-result p2

    if-eqz p2, :cond_a

    .line 25
    sput p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    goto :goto_7

    :cond_a
    add-int/lit8 p1, p1, 0x1

    move p2, v4

    goto :goto_6

    :cond_b
    :goto_7
    if-ne p1, v3, :cond_c

    .line 26
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->stop()V

    .line 27
    :cond_c
    sget p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    if-ne p1, v2, :cond_d

    add-int/lit8 v2, v2, -0x1

    .line 28
    sput v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    .line 29
    sget p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ld:I

    aget p1, v1, p1

    sput p1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    .line 30
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->stop()V

    .line 31
    :cond_d
    :goto_8
    monitor-exit v0

    return-void

    :catchall_0
    move-exception p0

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw p0
.end method

.method static synthetic c(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ni:Z

    return p1
.end method

.method private cf()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v1, 0xff08

    invoke-virtual {v0, v1}, Landroid/os/Handler;->hasMessages(I)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0xfa0

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_0
    return-void
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Re()V

    return-void
.end method

.method static synthetic d(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Qh:Z

    return p1
.end method

.method private df()V
    .locals 2

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mContext:Landroid/content/Context;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Yi:Landroid/content/BroadcastReceiver;

    invoke-virtual {v0, p0}, Landroid/content/Context;->unregisterReceiver(Landroid/content/BroadcastReceiver;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 2
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "unregisterHomeKeyReceiver:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string v0, "VideoIjkModel"

    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :goto_0
    return-void
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Se()V

    return-void
.end method

.method static synthetic e(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Pi:Z

    return p1
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->stop()V

    return-void
.end method

.method static synthetic f(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)V
    .locals 0

    .line 2
    invoke-direct {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->N(Z)V

    return-void
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->bf()V

    return-void
.end method

.method static synthetic g(Lcom/eckom/xtlibrary/twproject/video/model/m;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Mi:Z

    return p1
.end method

.method private getFileName()Ljava/lang/String;
    .locals 2

    .line 1
    sget p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    iget v1, v0, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    if-ge p0, v1, :cond_0

    .line 2
    iget-object v0, v0, Lcom/eckom/xtlibrary/b/k/a/b;->jk:[Lcom/eckom/xtlibrary/b/k/a/a;

    aget-object p0, v0, p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/k/a/a;->mName:Ljava/lang/String;

    return-object p0

    :cond_0
    const-string p0, ""

    return-object p0
.end method

.method public static getInstant()Lcom/eckom/xtlibrary/twproject/video/model/m;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->ej:Lcom/eckom/xtlibrary/twproject/video/model/m;

    if-nez v0, :cond_0

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/twproject/video/model/m;

    invoke-direct {v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;-><init>()V

    sput-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->ej:Lcom/eckom/xtlibrary/twproject/video/model/m;

    .line 3
    :cond_0
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->ej:Lcom/eckom/xtlibrary/twproject/video/model/m;

    return-object v0
.end method

.method private getService()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mService:I

    return p0
.end method

.method static synthetic h(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/content/Context;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mContext:Landroid/content/Context;

    return-object p0
.end method

.method private hd()I
    .locals 2

    .line 1
    sget p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->hc:I

    const/4 v0, 0x1

    if-nez p0, :cond_0

    sget p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    if-ne p0, v0, :cond_0

    const/4 p0, 0x0

    return p0

    .line 2
    :cond_0
    sget p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->hc:I

    const/4 v1, 0x2

    if-nez p0, :cond_1

    sget p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    if-ne p0, v1, :cond_1

    return v0

    .line 3
    :cond_1
    sget p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->hc:I

    if-ne p0, v0, :cond_2

    sget p0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ic:I

    if-ne p0, v0, :cond_2

    return v1

    :cond_2
    const/4 p0, -0x1

    return p0
.end method

.method static synthetic i(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mRoot:Landroid/view/View;

    return-object p0
.end method

.method static synthetic j(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/View;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->layout_suspension:Landroid/view/View;

    return-object p0
.end method

.method static synthetic k(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ni:Z

    return p0
.end method

.method static synthetic kc()Lcom/eckom/xtlibrary/twproject/video/utils/l;
    .locals 1

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    return-object v0
.end method

.method static synthetic l(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ri:Z

    return p0
.end method

.method static synthetic m(Lcom/eckom/xtlibrary/twproject/video/model/m;)[J
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHints:[J

    return-object p0
.end method

.method private mute(Z)V
    .locals 0

    if-eqz p1, :cond_0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    const/4 p1, 0x0

    invoke-virtual {p0, p1, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->setVolume(FF)V

    goto :goto_0

    .line 2
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    const/high16 p1, 0x3f800000    # 1.0f

    invoke-virtual {p0, p1, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->setVolume(FF)V

    :goto_0
    return-void
.end method

.method static synthetic n(Lcom/eckom/xtlibrary/twproject/video/model/m;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Vi:I

    return p0
.end method

.method static synthetic o(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager$LayoutParams;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    return-object p0
.end method

.method static synthetic p(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/view/WindowManager;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->rh:Landroid/view/WindowManager;

    return-object p0
.end method

.method private play(I)Z
    .locals 3

    const/4 v0, 0x0

    .line 1
    :try_start_0
    sget v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    const/4 v2, -0x1

    if-le v1, v2, :cond_0

    sget v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    sget-object v2, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v2, v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    iget v2, v2, Lcom/eckom/xtlibrary/b/k/a/b;->kk:I

    if-ge v1, v2, :cond_0

    .line 2
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/k/a/b;->jk:[Lcom/eckom/xtlibrary/b/k/a/a;

    sget v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ad:I

    aget-object v1, v1, v2

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/k/a/a;->mPath:Ljava/lang/String;

    sput-object v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Bd:Ljava/lang/String;

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    sget-object v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Bd:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->setMPPath(Ljava/lang/String;)V

    .line 4
    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->seekTo(I)V

    .line 5
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    .line 6
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Sa()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const/4 p0, 0x1

    return p0

    :cond_0
    return v0

    :catch_0
    move-exception p0

    .line 7
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "play error:"

    invoke-virtual {p1, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {p1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "VideoIjkModel"

    invoke-static {p1, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    return v0
.end method

.method static synthetic q(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->cf()V

    return-void
.end method

.method static synthetic r(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Oi:Z

    return p0
.end method

.method static synthetic s(Lcom/eckom/xtlibrary/twproject/video/model/m;)D
    .locals 2

    .line 1
    iget-wide v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Qi:D

    return-wide v0
.end method

.method private stop()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->stopPlayback()V

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Xe()V

    return-void
.end method

.method static synthetic t(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Pi:Z

    return p0
.end method

.method static synthetic u(Lcom/eckom/xtlibrary/twproject/video/model/m;)Landroid/os/Handler;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    return-object p0
.end method

.method static synthetic v(Lcom/eckom/xtlibrary/twproject/video/model/m;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ze()V

    return-void
.end method

.method static synthetic w(Lcom/eckom/xtlibrary/twproject/video/model/m;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Mi:Z

    return p0
.end method


# virtual methods
.method public E(Z)V
    .locals 5

    const/4 v0, 0x0

    .line 1
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Mi:Z

    .line 2
    iget-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Oi:Z

    const/16 v2, 0x8

    if-eq p1, v1, :cond_3

    if-eqz p1, :cond_2

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->floatframelayout:Landroid/widget/FrameLayout;

    invoke-virtual {v1, v0}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 4
    :try_start_0
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->bf()V

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->floatframelayout:Landroid/widget/FrameLayout;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v1, v2}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;)V

    .line 6
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->rh:Landroid/view/WindowManager;

    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mRoot:Landroid/view/View;

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mLayoutParams:Landroid/view/WindowManager$LayoutParams;

    invoke-interface {v1, v2, v3}, Landroid/view/WindowManager;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v1

    .line 7
    invoke-static {v1}, Landroid/util/Log;->getStackTraceString(Ljava/lang/Throwable;)Ljava/lang/String;

    move-result-object v1

    const-string v2, "VideoIjkModel"

    invoke-static {v2, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 8
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v1, v0}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 9
    sget v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->seekTo(I)V

    .line 10
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->ma()V

    .line 11
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v2, 0xff08

    const-wide/16 v3, 0xfa0

    invoke-virtual {v1, v2, v3, v4}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 12
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mRoot:Landroid/view/View;

    sget v2, Lcom/eckom/xtlibrary/R$id;->img_suspension_pp:I

    invoke-virtual {v1, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/ImageView;

    invoke-virtual {v1}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 13
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-boolean v3, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    if-nez v3, :cond_0

    iget v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Md:I

    if-nez v1, :cond_1

    :cond_0
    move v0, v2

    :cond_1
    invoke-direct {p0, v0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->N(Z)V

    goto :goto_1

    .line 14
    :cond_2
    :try_start_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->rh:Landroid/view/WindowManager;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mRoot:Landroid/view/View;

    invoke-interface {v0, v1}, Landroid/view/WindowManager;->removeView(Landroid/view/View;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    .line 15
    :catch_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->floatframelayout:Landroid/widget/FrameLayout;

    invoke-virtual {v0, v2}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 16
    :goto_1
    iput-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Oi:Z

    .line 17
    sget-object p1, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    if-eqz p1, :cond_4

    .line 18
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Oi:Z

    invoke-interface {p1, p0}, Lcom/eckom/xtlibrary/b/k/c/b;->n(Z)V

    goto :goto_2

    :cond_3
    if-nez p1, :cond_4

    .line 19
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->floatframelayout:Landroid/widget/FrameLayout;

    invoke-virtual {p1, v2}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 20
    :try_start_2
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->rh:Landroid/view/WindowManager;

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mRoot:Landroid/view/View;

    invoke-interface {p1, v1}, Landroid/view/WindowManager;->removeView(Landroid/view/View;)V
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    .line 21
    :catch_2
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Oi:Z

    .line 22
    sget-object p1, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    if-eqz p1, :cond_4

    .line 23
    iget-boolean p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Oi:Z

    invoke-interface {p1, p0}, Lcom/eckom/xtlibrary/b/k/c/b;->n(Z)V

    :cond_4
    :goto_2
    return-void
.end method

.method public F(Z)V
    .locals 5

    const-string v0, "VideoIjkModel"

    .line 1
    :try_start_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mContext:Landroid/content/Context;

    const-string v2, "display"

    invoke-virtual {v1, v2}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/hardware/display/DisplayManager;

    .line 2
    invoke-virtual {v1}, Landroid/hardware/display/DisplayManager;->getDisplays()[Landroid/view/Display;

    move-result-object v1

    .line 3
    array-length v2, v1

    const/4 v3, 0x2

    if-ge v2, v3, :cond_0

    return-void

    :cond_0
    const/4 v2, 0x1

    .line 4
    aget-object v1, v1, v2

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mDisplay:Landroid/view/Display;

    if-eqz p1, :cond_3

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mDisplay:Landroid/view/Display;

    if-eqz v1, :cond_1

    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    if-nez v1, :cond_1

    .line 6
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mContext:Landroid/content/Context;

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mDisplay:Landroid/view/Display;

    invoke-static {v1, v3}, Lcom/eckom/xtlibrary/twproject/video/utils/a;->a(Landroid/content/Context;Landroid/view/Display;)Lcom/eckom/xtlibrary/twproject/video/utils/a;

    move-result-object v1

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    .line 7
    :cond_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    if-eqz v1, :cond_5

    .line 8
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->bf()V

    .line 9
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/a;->pa()Z

    move-result v1

    if-nez v1, :cond_2

    .line 10
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    invoke-virtual {v1}, Landroid/app/Presentation;->show()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    .line 11
    :try_start_1
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/a;->qa()Landroid/widget/FrameLayout;

    move-result-object v1

    iget-object v3, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v1, v3}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;)V

    .line 12
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/twproject/video/utils/a;->x(Z)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    goto :goto_0

    :catch_0
    move-exception v1

    .line 13
    :try_start_2
    invoke-static {v1}, Landroid/util/Log;->getStackTraceString(Ljava/lang/Throwable;)Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 14
    :cond_2
    :goto_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v2, 0xff0e

    invoke-virtual {v1, v2}, Landroid/os/Handler;->removeMessages(I)V

    .line 15
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const-wide/16 v3, 0x3e8

    invoke-virtual {p0, v2, v3, v4}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto :goto_1

    .line 16
    :cond_3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    if-eqz v1, :cond_5

    .line 17
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/a;->pa()Z

    move-result v1

    if-eqz v1, :cond_4

    .line 18
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    invoke-virtual {v1}, Landroid/app/Presentation;->dismiss()V

    .line 19
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;

    const/4 v2, 0x0

    invoke-virtual {v1, v2}, Lcom/eckom/xtlibrary/twproject/video/utils/a;->x(Z)V

    :cond_4
    const/4 v1, 0x0

    .line 20
    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Wi:Lcom/eckom/xtlibrary/twproject/video/utils/a;
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_1

    goto :goto_1

    :catch_1
    move-exception p0

    .line 21
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "showMultiScreen:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 22
    :cond_5
    :goto_1
    sget-object p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    if-eqz p0, :cond_6

    .line 23
    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/k/c/b;->v(Z)V

    :cond_6
    return-void
.end method

.method public P()V
    .locals 1

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getCurrentPosition()I

    move-result v0

    sput v0, Lcom/eckom/xtlibrary/twproject/video/utils/l;->md:I

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->pause()V

    .line 4
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Xe()V

    :cond_0
    return-void
.end method

.method public Pb()V
    .locals 4

    const-string v0, "VideoIjkModel"

    .line 1
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->fj:Lcom/eckom/xtlibrary/twproject/video/utils/j;

    const/4 v2, 0x0

    if-nez v1, :cond_1

    .line 2
    :try_start_0
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Bi:Lcom/eckom/xtlibrary/twproject/video/model/m$a;

    if-eqz v1, :cond_0

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Bi:Lcom/eckom/xtlibrary/twproject/video/model/m$a;

    const/4 v3, 0x1

    invoke-virtual {v1, v3}, Landroid/os/AsyncTask;->cancel(Z)Z

    .line 4
    :cond_0
    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;->VIDEO_MODEL_DESTROY:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    iput-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ti:Lcom/eckom/xtlibrary/twproject/video/model/BaseVideoMode$VIDEO_MODEL_STATE;

    const/4 v1, 0x0

    .line 5
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ni:Z

    .line 6
    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->F(Z)V

    .line 7
    invoke-virtual {p0, v1}, Lcom/eckom/xtlibrary/twproject/video/model/m;->E(Z)V

    .line 8
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v3, 0xff0e

    invoke-virtual {v1, v3}, Landroid/os/Handler;->removeMessages(I)V

    .line 9
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v3, 0xff11

    invoke-virtual {v1, v3}, Landroid/os/Handler;->removeMessages(I)V

    .line 10
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Sa()V

    .line 11
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->df()V

    .line 12
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    const/16 v3, 0x8

    invoke-virtual {v1, v3}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 13
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->stopPlayback()V

    .line 14
    sget-object p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    invoke-virtual {p0, v0}, Landroid/tw/john/TWUtil;->removeHandler(Ljava/lang/String;)V

    .line 15
    sget-object p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->close()V

    .line 16
    sput-object v2, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 17
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "onDestory:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    :goto_0
    return-void

    .line 18
    :cond_1
    invoke-virtual {v1}, Lcom/eckom/xtlibrary/twproject/video/utils/j;->onDestroy()V

    throw v2
.end method

.method public ic()V
    .locals 4

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ve()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Qh:Z

    if-nez v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v1, 0xff02

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_0
    return-void
.end method

.method public isPlaying()Z
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {p0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->isPlaying()Z

    move-result p0

    return p0
.end method

.method public jc()V
    .locals 4

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ve()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Qh:Z

    if-nez v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v1, 0xff03

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x1f4

    invoke-virtual {p0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    :cond_0
    return-void
.end method

.method public lc()V
    .locals 3

    const-string v0, "0"

    const-string v1, "persist.tw.forcepip"

    .line 1
    invoke-static {v1, v0}, Landroid/os/SystemProperties;->get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 2
    invoke-static {v1, v0}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v0

    const/4 v1, 0x0

    if-eqz v0, :cond_0

    .line 3
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ri:Z

    return-void

    :cond_0
    const-string v0, "sys.df.desktop"

    const-string v2, ""

    .line 4
    invoke-static {v0, v2}, Landroid/os/SystemProperties;->get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    const-string v2, "video"

    .line 5
    invoke-virtual {v0, v2}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const/4 v0, 0x1

    .line 6
    iput-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ri:Z

    goto :goto_0

    .line 7
    :cond_1
    iput-boolean v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->Ri:Z

    :goto_0
    return-void
.end method

.method public ma()V
    .locals 4

    .line 1
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v0

    if-nez v0, :cond_0

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->w(Z)V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->start()V

    .line 4
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const v1, 0xff01

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    .line 5
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mHandler:Landroid/os/Handler;

    const-wide/16 v2, 0x3e8

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 6
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->Xe()V

    :cond_0
    return-void
.end method

.method public mc()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, 0x3a98

    if-lez v0, :cond_0

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {p0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public nc()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getCurrentPosition()I

    move-result v0

    add-int/lit16 v0, v0, -0x2710

    if-lez v0, :cond_0

    .line 3
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {v1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->getDuration()I

    move-result v1

    if-ge v0, v1, :cond_0

    .line 4
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {p0, v0}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->seekTo(I)V

    :cond_0
    return-void
.end method

.method public oc()V
    .locals 3

    .line 1
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Bd:Ljava/lang/String;

    sget-object v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Cd:Ljava/lang/String;

    invoke-interface {v0, v1, v2}, Lcom/eckom/xtlibrary/b/k/c/b;->u(Ljava/lang/String;Ljava/lang/String;)V

    .line 2
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Dd:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->b(Lcom/eckom/xtlibrary/b/k/a/b;)V

    .line 3
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->ud:Lcom/eckom/xtlibrary/b/k/a/b;

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->a(Lcom/eckom/xtlibrary/b/k/a/b;)V

    .line 4
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->getFileName()Ljava/lang/String;

    move-result-object v1

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->fa(Ljava/lang/String;)V

    .line 5
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->isPlaying()Z

    move-result v1

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->c(Z)V

    .line 6
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-boolean v1, v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Ld:Z

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->l(Z)V

    .line 7
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/video/model/m;->hd()I

    move-result v1

    invoke-interface {v0, v1}, Lcom/eckom/xtlibrary/b/k/c/b;->D(I)V

    .line 8
    sget-object v0, Lcom/eckom/xtlibrary/twproject/video/model/m;->dj:Lcom/eckom/xtlibrary/b/k/c/b;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mContext:Landroid/content/Context;

    sget-object v1, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Bd:Ljava/lang/String;

    sget-object v2, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    iget-object v2, v2, Lcom/eckom/xtlibrary/twproject/video/utils/l;->Pd:Ljava/util/ArrayList;

    invoke-static {p0, v1, v2}, Lcom/eckom/xtlibrary/twproject/video/utils/b;->b(Landroid/content/Context;Ljava/lang/String;Ljava/util/ArrayList;)Z

    move-result p0

    invoke-interface {v0, p0}, Lcom/eckom/xtlibrary/b/k/c/b;->h(Z)V

    return-void
.end method

.method public seekTo(I)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->mMediaPlayer:Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;

    invoke-virtual {p0, p1}, Ltv/danmaku/ijk/media/player/tw/TWMediaPlayerView;->seekTo(I)V

    return-void
.end method

.method public w(Z)V
    .locals 0

    .line 2
    sget-object p0, Lcom/eckom/xtlibrary/twproject/video/model/m;->jd:Lcom/eckom/xtlibrary/twproject/video/utils/l;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/video/utils/l;->w(Z)V

    return-void
.end method

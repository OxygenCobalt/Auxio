.class public Lcom/tw/music/MusicActivity;
.super Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;
# NOTE: JADX incorrect hotspots reported for this class (see docs/reports/jadx-remediation-checklist.md Priority A). Prefer smali control-flow when behavior appears contradictory.

.source "MusicActivity.java"

# interfaces
.implements Landroid/view/View$OnLongClickListener;


# static fields
.field public static Dc:I


# instance fields
.field private Ab:Landroid/view/View;

.field private Ac:I

.field private Bb:Lcom/tw/music/view/CircleImageView;

.field Bc:Lcom/tw/music/c/b;

.field private Cb:Landroid/widget/SeekBar;

.field private Cc:Lcom/tw/preference/TogglePreference$a;

.field private Db:Landroid/widget/TextView;

.field private Eb:Landroid/widget/TextView;

.field private Fb:Lcom/tw/music/view/CircleImageView;

.field private Gb:Landroid/widget/ImageView;

.field private Hb:Landroid/widget/TextView;

.field private Ib:Landroid/widget/TextView;

.field private Jb:Landroid/widget/TextView;

.field private Kb:Landroid/widget/TextView;

.field private Lb:Lcom/tw/music/utils/ImagViewAndTextView;

.field private Mb:Landroid/widget/TextView;

.field private Nb:Lcom/tw/music/utils/ImagViewAndTextView;

.field private Ob:Landroid/widget/TextView;

.field private Pb:Landroid/widget/ImageView;

.field private Qb:Landroid/widget/ImageView;

.field private Rb:Landroid/widget/ImageView;

.field private Sb:Landroid/widget/ImageView;

.field private Tb:Landroid/widget/ImageView;

.field private Ub:Landroid/widget/ImageView;

.field private Vb:Landroid/widget/ImageView;

.field private Wa:I

.field private Wb:Landroid/widget/ImageView;

.field private Xb:Landroid/widget/ImageView;

.field private Yb:Landroid/widget/ImageView;

.field private Zb:Landroid/widget/RelativeLayout;

.field c:I

.field private cc:Landroid/widget/ImageView;

.field private ec:Lcom/tw/music/view/BaseVisualizerView;

.field private fc:Z

.field private fromUser:Z

.field gc:[I

.field hc:I

.field ic:I

.field private final jc:I

.field private final kc:I

.field private layout_player:Landroid/view/View;

.field private layout_settings:Landroid/view/View;

.field private layout_settings_bg:Landroid/widget/LinearLayout;

.field private final lc:I

.field private ll_fx:Landroid/widget/LinearLayout;

.field public lrc_view:Lcom/tw/music/lrc/LrcView;

.field private mListView:Landroid/widget/ListView;

.field private mReceiver:Landroid/content/BroadcastReceiver;

.field private mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

.field private mService:Lcom/tw/music/MusicService;

.field private final mc:I

.field private final nc:I

.field private final oc:I

.field private pref_lrc:Lcom/tw/preference/TogglePreference;

.field private final qc:Ljava/lang/String;

.field rc:[Ljava/lang/String;

.field sc:Z

.field private uc:Lcom/tw/music/c/c;

.field private vc:Landroid/content/ServiceConnection;

.field private wc:Lcom/tw/music/a/c$b;

.field private xb:Lcom/tw/music/a/c;

.field private xc:Lcom/tw/music/a/c$a;

.field private yb:Lcom/tw/music/utils/b;

.field private yc:Landroid/widget/SeekBar$OnSeekBarChangeListener;

.field private zb:Landroid/view/View;

.field zc:[I


# direct methods
.method public constructor <init>()V
    .locals 3

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-object v0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    .line 3
    iput-object v0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    const/4 v0, 0x1

    .line 4
    iput-boolean v0, p0, Lcom/tw/music/MusicActivity;->fc:Z

    const/4 v1, 0x5

    new-array v1, v1, [I

    .line 5
    fill-array-data v1, :array_0

    iput-object v1, p0, Lcom/tw/music/MusicActivity;->gc:[I

    const/4 v1, 0x0

    .line 6
    iput v1, p0, Lcom/tw/music/MusicActivity;->hc:I

    .line 7
    iput v1, p0, Lcom/tw/music/MusicActivity;->ic:I

    .line 8
    iput v1, p0, Lcom/tw/music/MusicActivity;->jc:I

    .line 9
    iput v0, p0, Lcom/tw/music/MusicActivity;->kc:I

    const/4 v2, 0x2

    .line 10
    iput v2, p0, Lcom/tw/music/MusicActivity;->lc:I

    .line 11
    iput v1, p0, Lcom/tw/music/MusicActivity;->mc:I

    .line 12
    iput v0, p0, Lcom/tw/music/MusicActivity;->nc:I

    .line 13
    iput v2, p0, Lcom/tw/music/MusicActivity;->oc:I

    const-string v0, "music_view"

    .line 14
    iput-object v0, p0, Lcom/tw/music/MusicActivity;->qc:Ljava/lang/String;

    .line 15
    iput v1, p0, Lcom/tw/music/MusicActivity;->Wa:I

    const-string v0, "android.permission.WRITE_EXTERNAL_STORAGE"

    const-string v2, "android.permission.READ_EXTERNAL_STORAGE"

    .line 16
    filled-new-array {v0, v2}, [Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->rc:[Ljava/lang/String;

    .line 17
    new-instance v0, Lcom/tw/music/d;

    invoke-direct {v0, p0}, Lcom/tw/music/d;-><init>(Lcom/tw/music/MusicActivity;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->vc:Landroid/content/ServiceConnection;

    .line 18
    new-instance v0, Lcom/tw/music/e;

    invoke-direct {v0, p0}, Lcom/tw/music/e;-><init>(Lcom/tw/music/MusicActivity;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->wc:Lcom/tw/music/a/c$b;

    .line 19
    new-instance v0, Lcom/tw/music/f;

    invoke-direct {v0, p0}, Lcom/tw/music/f;-><init>(Lcom/tw/music/MusicActivity;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->xc:Lcom/tw/music/a/c$a;

    .line 20
    new-instance v0, Lcom/tw/music/g;

    invoke-direct {v0, p0}, Lcom/tw/music/g;-><init>(Lcom/tw/music/MusicActivity;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->yc:Landroid/widget/SeekBar$OnSeekBarChangeListener;

    .line 21
    iput v1, p0, Lcom/tw/music/MusicActivity;->c:I

    const/4 v0, 0x6

    new-array v0, v0, [I

    .line 22
    fill-array-data v0, :array_1

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->zc:[I

    .line 23
    iput v1, p0, Lcom/tw/music/MusicActivity;->Ac:I

    .line 24
    new-instance v0, Lcom/tw/music/i;

    invoke-direct {v0, p0}, Lcom/tw/music/i;-><init>(Lcom/tw/music/MusicActivity;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Cc:Lcom/tw/preference/TogglePreference$a;

    .line 25
    new-instance v0, Lcom/tw/music/j;

    invoke-direct {v0, p0}, Lcom/tw/music/j;-><init>(Lcom/tw/music/MusicActivity;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->mReceiver:Landroid/content/BroadcastReceiver;

    return-void

    :array_0
    .array-data 4
        0x7f080029
        0x7f08002a
        0x7f08002b
        0x7f080028
        0x7f080036
    .end array-data

    :array_1
    .array-data 4
        0x7f070079
        0x7f07007b
        0x7f07007c
        0x7f07007e
        0x7f07007f
        0x7f070080
    .end array-data
.end method

.method private Ba(I)I
    .locals 0

    .line 1
    div-int/lit16 p1, p1, 0x3e8

    .line 2
    div-int/lit8 p1, p1, 0x3c

    div-int/lit8 p1, p1, 0x3c

    .line 3
    rem-int/lit8 p1, p1, 0x18

    return p1
.end method

.method private Ca(I)I
    .locals 0

    .line 1
    div-int/lit16 p1, p1, 0x3e8

    .line 2
    div-int/lit8 p1, p1, 0x3c

    .line 3
    rem-int/lit8 p1, p1, 0x3c

    return p1
.end method

.method private Da(I)I
    .locals 0

    .line 1
    div-int/lit16 p1, p1, 0x3e8

    .line 2
    rem-int/lit8 p1, p1, 0x3c

    return p1
.end method

.method private Ea(I)V
    .locals 3

    const/16 v0, 0x8

    const/4 v1, 0x0

    if-eqz p1, :cond_1

    const/4 v2, 0x2

    if-eq p1, v2, :cond_0

    goto :goto_0

    .line 1
    :cond_0
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->zb:Landroid/view/View;

    invoke-virtual {p1, v0}, Landroid/view/View;->setVisibility(I)V

    .line 2
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Ab:Landroid/view/View;

    invoke-virtual {p0, v1}, Landroid/view/View;->setVisibility(I)V

    goto :goto_0

    .line 3
    :cond_1
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->zb:Landroid/view/View;

    invoke-virtual {p1, v1}, Landroid/view/View;->setVisibility(I)V

    .line 4
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Ab:Landroid/view/View;

    invoke-virtual {p0, v0}, Landroid/view/View;->setVisibility(I)V

    :goto_0
    return-void
.end method

.method private Fa(I)V
    .locals 1

    if-eqz p1, :cond_3

    const/4 v0, 0x1

    if-eq p1, v0, :cond_1

    const/4 v0, 0x2

    if-eq p1, v0, :cond_0

    goto :goto_0

    .line 1
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->pb()V

    goto :goto_0

    .line 2
    :cond_1
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1}, Lcom/tw/music/b/a;->isPlaying()Z

    move-result p1

    if-eqz p1, :cond_2

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->ba()V

    goto :goto_0

    .line 4
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->fa()V

    goto :goto_0

    .line 5
    :cond_3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->rb()V

    :goto_0
    return-void
.end method

.method private Ga(I)V
    .locals 3

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Lcom/tw/music/view/BaseVisualizerView;->setVisualizer(Landroid/media/audiofx/Visualizer;)V

    .line 3
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ll_fx:Landroid/widget/LinearLayout;

    invoke-virtual {v0}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 4
    :cond_0
    new-instance v0, Lcom/tw/music/view/BaseVisualizerView;

    invoke-direct {v0, p0}, Lcom/tw/music/view/BaseVisualizerView;-><init>(Landroid/content/Context;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    .line 5
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    new-instance v1, Landroid/view/ViewGroup$LayoutParams;

    const/4 v2, -0x1

    invoke-direct {v1, v2, v2}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    invoke-virtual {v0, v1}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 6
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    if-eqz v0, :cond_1

    .line 7
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Rd()I

    move-result v1

    iget-object v2, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v2}, Lcom/tw/music/c/b;->Qd()I

    move-result v2

    invoke-virtual {v0, v1, v2}, Lcom/tw/music/view/BaseVisualizerView;->j(II)V

    .line 8
    :cond_1
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ll_fx:Landroid/widget/LinearLayout;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 9
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ll_fx:Landroid/widget/LinearLayout;

    iget-object v2, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    invoke-virtual {v0, v2}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 10
    new-instance v0, Landroid/media/audiofx/Visualizer;

    invoke-direct {v0, p1}, Landroid/media/audiofx/Visualizer;-><init>(I)V

    const/4 p1, 0x0

    .line 11
    invoke-virtual {v0, p1}, Landroid/media/audiofx/Visualizer;->setEnabled(Z)I

    .line 12
    invoke-static {}, Landroid/media/audiofx/Visualizer;->getCaptureSizeRange()[I

    move-result-object p1

    aget p1, p1, v1

    invoke-virtual {v0, p1}, Landroid/media/audiofx/Visualizer;->setCaptureSize(I)I

    .line 13
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    invoke-virtual {p0, v0}, Lcom/tw/music/view/BaseVisualizerView;->setVisualizer(Landroid/media/audiofx/Visualizer;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 14
    invoke-virtual {p0}, Ljava/lang/Exception;->printStackTrace()V

    .line 15
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "setupVisualizerFxAndUi->Exception:"

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {p1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string p1, "MusicActivity"

    invoke-static {p1, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :goto_0
    return-void
.end method

.method private J(Z)V
    .locals 0

    .line 1
    iput-boolean p1, p0, Lcom/tw/music/MusicActivity;->fromUser:Z

    return-void
.end method

.method private K(Z)V
    .locals 5

    const v0, 0x7f080066

    .line 1
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    invoke-virtual {v0}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 2
    iget-boolean v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Za:Z

    const v1, 0x7f080086

    const v2, 0x7f080082

    const/16 v3, 0x8

    if-eqz v0, :cond_0

    .line 3
    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    invoke-virtual {p1, v3}, Landroid/view/View;->setVisibility(I)V

    .line 4
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v3}, Landroid/view/View;->setVisibility(I)V

    goto :goto_2

    .line 5
    :cond_0
    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    const/4 v2, 0x0

    if-eqz p1, :cond_1

    move v4, v3

    goto :goto_0

    :cond_1
    move v4, v2

    :goto_0
    invoke-virtual {v0, v4}, Landroid/view/View;->setVisibility(I)V

    .line 6
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    if-eqz p1, :cond_2

    goto :goto_1

    :cond_2
    move v2, v3

    :goto_1
    invoke-virtual {p0, v2}, Landroid/view/View;->setVisibility(I)V

    :goto_2
    return-void
.end method

.method static synthetic a(Lcom/tw/music/MusicActivity;)Lcom/tw/music/MusicService;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    return-object p0
.end method

.method static synthetic a(Lcom/tw/music/MusicActivity;Lcom/tw/music/MusicService;)Lcom/tw/music/MusicService;
    .locals 0

    .line 2
    iput-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    return-object p1
.end method

.method private a(Landroid/widget/ImageView;)V
    .locals 1

    .line 4
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v0, v0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v0}, Lcom/tw/music/b/a;->ed()Landroid/graphics/Bitmap;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 5
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p0}, Lcom/tw/music/b/a;->ed()Landroid/graphics/Bitmap;

    move-result-object p0

    invoke-virtual {p1, p0}, Landroid/widget/ImageView;->setImageBitmap(Landroid/graphics/Bitmap;)V

    goto :goto_0

    :cond_0
    const p0, 0x7f070054

    .line 6
    invoke-virtual {p1, p0}, Landroid/widget/ImageView;->setImageResource(I)V

    :goto_0
    return-void
.end method

.method static synthetic a(Lcom/tw/music/MusicActivity;Z)V
    .locals 0

    .line 3
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->J(Z)V

    return-void
.end method

.method static synthetic b(Lcom/tw/music/MusicActivity;Z)V
    .locals 0

    .line 1
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->K(Z)V

    return-void
.end method

.method private b(Lcom/tw/music/c/c;)V
    .locals 3
    .annotation build Landroid/support/annotation/RequiresApi;
        api = 0x15
    .end annotation

    .line 2
    iget-object p1, p1, Lcom/tw/music/c/c;->Bc:Lcom/tw/music/c/b;

    iput-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    .line 3
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    if-eqz p1, :cond_16

    .line 4
    invoke-virtual {p1}, Lcom/tw/music/c/b;->yd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_0

    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object p1

    const-string v0, "-IPS"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-nez p1, :cond_0

    const p1, 0x7f080041

    .line 5
    invoke-virtual {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->yd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 6
    :cond_0
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Hd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_1

    .line 7
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->layout_player:Landroid/view/View;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Hd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 8
    :cond_1
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Wd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_2

    iget-object p1, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    if-eqz p1, :cond_2

    .line 9
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->layout_settings_bg:Landroid/widget/LinearLayout;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Wd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/LinearLayout;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 10
    :cond_2
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Yd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_3

    iget-object p1, p0, Lcom/tw/music/MusicActivity;->pref_lrc:Lcom/tw/preference/TogglePreference;

    if-eqz p1, :cond_3

    .line 11
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Yd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Lcom/tw/preference/TogglePreference;->setRightIcon(Landroid/graphics/drawable/Drawable;)V

    .line 12
    :cond_3
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->vd()Landroid/graphics/drawable/Drawable;

    .line 13
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->vd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_4

    .line 14
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->vd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Lcom/tw/music/view/CircleImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 15
    :cond_4
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "(mainPlugin.getAlbum_bg() != null)  "

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->vd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    if-eqz v0, :cond_5

    const/4 v0, 0x1

    goto :goto_0

    :cond_5
    const/4 v0, 0x0

    :goto_0
    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    const-string v0, "MainPlugin"

    invoke-static {v0, p1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 16
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Xd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_6

    const p1, 0x7f0800c7

    .line 17
    invoke-virtual {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Xd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 18
    :cond_6
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->xd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_7

    .line 19
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->cc:Landroid/widget/ImageView;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->xd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 20
    :cond_7
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Sd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_8

    .line 21
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Yb:Landroid/widget/ImageView;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Sd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 22
    :cond_8
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->wd()Landroid/graphics/drawable/Drawable;

    .line 23
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->wd()Landroid/graphics/drawable/Drawable;

    .line 24
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Ud()Landroid/graphics/drawable/Drawable;

    .line 25
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Vd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_9

    .line 26
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Vd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/SeekBar;->setThumb(Landroid/graphics/drawable/Drawable;)V

    .line 27
    :cond_9
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Td()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_a

    .line 28
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    invoke-virtual {p1}, Landroid/widget/SeekBar;->getProgressDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    invoke-virtual {p1}, Landroid/graphics/drawable/Drawable;->getBounds()Landroid/graphics/Rect;

    move-result-object p1

    .line 29
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Td()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/SeekBar;->setProgressDrawableTiled(Landroid/graphics/drawable/Drawable;)V

    .line 30
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    invoke-virtual {v0}, Landroid/widget/SeekBar;->getProgressDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setBounds(Landroid/graphics/Rect;)V

    .line 31
    :cond_a
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Md()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_b

    .line 32
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Rb:Landroid/widget/ImageView;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Md()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 33
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Rb:Landroid/widget/ImageView;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Nd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 34
    :cond_b
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Kd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_c

    .line 35
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Qb:Landroid/widget/ImageView;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Kd()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 36
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Qb:Landroid/widget/ImageView;

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v0}, Lcom/tw/music/c/b;->Ld()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 37
    :cond_c
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Dd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    const v0, 0x7f080066

    if-eqz p1, :cond_d

    .line 38
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Sb:Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Dd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 39
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Sb:Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Ed()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 40
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v1

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object v1

    const-string v2, "music_playlist_bg"

    invoke-static {v1, v2}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 41
    :cond_d
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Od()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_e

    .line 42
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Tb:Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Pd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 43
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Tb:Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Od()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 44
    :cond_e
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->zd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    const v1, 0x7f080026

    if-eqz p1, :cond_f

    .line 45
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object v2, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v2}, Lcom/tw/music/c/b;->zd()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {p1, v2}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 46
    :cond_f
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Ad()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_10

    .line 47
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Ad()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 48
    :cond_10
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Bd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    const v1, 0x7f080065

    if-eqz p1, :cond_11

    .line 49
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object v2, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v2}, Lcom/tw/music/c/b;->Bd()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {p1, v2}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 50
    :cond_11
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Cd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_12

    .line 51
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Cd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 52
    :cond_12
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Id()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    const v1, 0x7f080097

    if-eqz p1, :cond_13

    .line 53
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object v2, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v2}, Lcom/tw/music/c/b;->Id()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {p1, v2}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 54
    :cond_13
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Jd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_14

    .line 55
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Jd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 56
    :cond_14
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Fd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_15

    .line 57
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {v1}, Lcom/tw/music/c/b;->Fd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 58
    :cond_15
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p1}, Lcom/tw/music/c/b;->Gd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_16

    .line 59
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    check-cast p1, Landroid/widget/ImageView;

    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p0}, Lcom/tw/music/c/b;->Gd()Landroid/graphics/drawable/Drawable;

    move-result-object p0

    invoke-virtual {p1, p0}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    :cond_16
    return-void
.end method

.method private c(Lcom/tw/music/c/c;)V
    .locals 4

    .line 1
    iget-object p1, p1, Lcom/tw/music/c/c;->fm:Lcom/tw/music/c/a;

    const v0, 0x7f0800cb

    .line 2
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/LinearLayout;

    .line 3
    invoke-virtual {p1}, Lcom/tw/music/c/a;->nd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    .line 4
    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setBackground(Landroid/graphics/drawable/Drawable;)V

    const/4 v1, 0x0

    .line 5
    :goto_0
    invoke-virtual {v0}, Landroid/widget/LinearLayout;->getChildCount()I

    move-result v2

    if-ge v1, v2, :cond_1

    .line 6
    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v2

    .line 7
    instance-of v3, v2, Landroid/widget/ImageView;

    if-eqz v3, :cond_0

    .line 8
    check-cast v2, Landroid/widget/ImageView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->od()Ljava/util/List;

    move-result-object v3

    invoke-interface {v3, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/tw/music/c/a$a;

    invoke-virtual {v3}, Lcom/tw/music/c/a$a;->md()Landroid/graphics/drawable/Drawable;

    move-result-object v3

    invoke-virtual {v2, v3}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 9
    invoke-virtual {p1}, Lcom/tw/music/c/a;->od()Ljava/util/List;

    move-result-object v3

    invoke-interface {v3, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/tw/music/c/a$a;

    invoke-virtual {v3}, Lcom/tw/music/c/a$a;->ld()Landroid/graphics/drawable/Drawable;

    move-result-object v3

    invoke-virtual {v2, v3}, Landroid/widget/ImageView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    :cond_0
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 10
    :cond_1
    invoke-virtual {p1}, Lcom/tw/music/c/a;->ud()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    const v1, 0x7f080024

    .line 11
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1, v0}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 12
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Gb:Landroid/widget/ImageView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->getAlbum()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 13
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->getAlbum()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/tw/music/view/CircleImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    const v0, 0x7f08009b

    .line 14
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->rd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    const v0, 0x7f080099

    .line 15
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->qd()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    const v0, 0x7f080090

    .line 16
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->getNext()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 17
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result v1

    invoke-virtual {v0, v1}, Lcom/tw/music/lrc/LrcView;->setNormalColor(I)V

    .line 18
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->td()I

    move-result v1

    invoke-virtual {v0, v1}, Lcom/tw/music/lrc/LrcView;->setCurrentColor(I)V

    .line 19
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Hb:Landroid/widget/TextView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setTextColor(I)V

    .line 20
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Lb:Lcom/tw/music/utils/ImagViewAndTextView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result v1

    invoke-virtual {v0, v1}, Lcom/tw/music/utils/ImagViewAndTextView;->setTxColor(I)V

    .line 21
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Nb:Lcom/tw/music/utils/ImagViewAndTextView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result v1

    invoke-virtual {v0, v1}, Lcom/tw/music/utils/ImagViewAndTextView;->setTxColor(I)V

    .line 22
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Db:Landroid/widget/TextView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setTextColor(I)V

    .line 23
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Eb:Landroid/widget/TextView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setTextColor(I)V

    const v0, 0x7f0800e0

    .line 24
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setTextColor(I)V

    const v0, 0x7f0800df

    .line 25
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    check-cast p0, Landroid/widget/TextView;

    invoke-virtual {p1}, Lcom/tw/music/c/a;->sd()I

    move-result p1

    invoke-virtual {p0, p1}, Landroid/widget/TextView;->setTextColor(I)V

    return-void
.end method

.method private l(II)V
    .locals 2

    const/4 v0, 0x2

    const/4 v1, 0x1

    if-ne p1, v1, :cond_0

    goto :goto_0

    :cond_0
    if-ne p2, v0, :cond_1

    move v0, v1

    goto :goto_0

    :cond_1
    const/4 v0, 0x0

    .line 1
    :goto_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p1, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/f/e/a;->pa(I)V

    .line 2
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p0, v0}, Lcom/tw/music/b/a;->va(I)V

    return-void
.end method

.method private pe()V
    .locals 2

    .line 1
    invoke-virtual {p0}, Landroid/app/Activity;->getWindowManager()Landroid/view/WindowManager;

    move-result-object v0

    .line 2
    new-instance v1, Landroid/util/DisplayMetrics;

    invoke-direct {v1}, Landroid/util/DisplayMetrics;-><init>()V

    .line 3
    invoke-interface {v0}, Landroid/view/WindowManager;->getDefaultDisplay()Landroid/view/Display;

    move-result-object v0

    invoke-virtual {v0, v1}, Landroid/view/Display;->getMetrics(Landroid/util/DisplayMetrics;)V

    .line 4
    iget v0, v1, Landroid/util/DisplayMetrics;->widthPixels:I

    iput v0, p0, Lcom/tw/music/MusicActivity;->Wa:I

    return-void
.end method

.method private qe()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Jb:Landroid/widget/TextView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1}, Lcom/tw/music/b/a;->jd()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Ib:Landroid/widget/TextView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1}, Lcom/tw/music/b/a;->Nb()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 3
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Hb:Landroid/widget/TextView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1}, Lcom/tw/music/b/a;->Nb()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 4
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Lb:Lcom/tw/music/utils/ImagViewAndTextView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1}, Lcom/tw/music/b/a;->jd()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/tw/music/utils/ImagViewAndTextView;->setTx(Ljava/lang/String;)V

    .line 5
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Nb:Lcom/tw/music/utils/ImagViewAndTextView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1}, Lcom/tw/music/b/a;->fd()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/tw/music/utils/ImagViewAndTextView;->setTx(Ljava/lang/String;)V

    .line 6
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v0, v0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v0}, Lcom/tw/music/b/a;->gd()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/tw/music/MusicActivity;->wb(Ljava/lang/String;)V

    .line 7
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    invoke-direct {p0, v0}, Lcom/tw/music/MusicActivity;->a(Landroid/widget/ImageView;)V

    .line 8
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bb:Lcom/tw/music/view/CircleImageView;

    invoke-direct {p0, v0}, Lcom/tw/music/MusicActivity;->a(Landroid/widget/ImageView;)V

    .line 9
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Kb:Landroid/widget/TextView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1}, Lcom/tw/music/b/a;->Nb()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 10
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Mb:Landroid/widget/TextView;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1}, Lcom/tw/music/b/a;->jd()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 11
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Ob:Landroid/widget/TextView;

    iget-object p0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p0}, Lcom/tw/music/b/a;->fd()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v0, p0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    return-void
.end method

.method private re()V
    .locals 2

    .line 1
    invoke-static {}, Lcom/tw/music/utils/b;->getInstance()Lcom/tw/music/utils/b;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->yb:Lcom/tw/music/utils/b;

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->yb:Lcom/tw/music/utils/b;

    invoke-virtual {v0, p0}, Lcom/tw/music/utils/b;->init(Landroid/content/Context;)V

    .line 3
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->yc:Landroid/widget/SeekBar$OnSeekBarChangeListener;

    invoke-virtual {v0, v1}, Landroid/widget/SeekBar;->setOnSeekBarChangeListener(Landroid/widget/SeekBar$OnSeekBarChangeListener;)V

    .line 4
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Rb:Landroid/widget/ImageView;

    invoke-virtual {v0, p0}, Landroid/widget/ImageView;->setOnLongClickListener(Landroid/view/View$OnLongClickListener;)V

    .line 5
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Sb:Landroid/widget/ImageView;

    invoke-virtual {v0, p0}, Landroid/widget/ImageView;->setOnLongClickListener(Landroid/view/View$OnLongClickListener;)V

    return-void
.end method

.method private se()V
    .locals 4

    .line 1
    invoke-static {p0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object v0

    const v1, 0x7f0a0024

    const/4 v2, 0x0

    const/4 v3, 0x0

    invoke-virtual {v0, v1, v2, v3}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    new-instance v1, Landroid/view/ViewGroup$LayoutParams;

    const/4 v2, -0x1

    invoke-direct {v1, v2, v2}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    invoke-virtual {v0, v1}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 3
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    const v1, 0x7f080075

    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/LinearLayout;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->layout_settings_bg:Landroid/widget/LinearLayout;

    .line 4
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    const v1, 0x7f08009a

    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/tw/preference/TogglePreference;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->pref_lrc:Lcom/tw/preference/TogglePreference;

    .line 5
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->pref_lrc:Lcom/tw/preference/TogglePreference;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Cc:Lcom/tw/preference/TogglePreference$a;

    invoke-virtual {v0, v1}, Lcom/tw/preference/TogglePreference;->setOnToggleStateChange(Lcom/tw/preference/TogglePreference$a;)V

    .line 6
    invoke-virtual {p0}, Landroid/app/Activity;->getApplicationContext()Landroid/content/Context;

    move-result-object v0

    const-string v1, "MusicActivity"

    const-string v2, "lrcorVisible"

    invoke-static {v0, v1, v2}, Lcom/tw/music/utils/c;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v0

    .line 7
    iget-object v1, p0, Lcom/tw/music/MusicActivity;->pref_lrc:Lcom/tw/preference/TogglePreference;

    invoke-virtual {v1, v0}, Lcom/tw/preference/TogglePreference;->setToggleState(Z)V

    const v0, 0x7f080084

    .line 8
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/RelativeLayout;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/widget/RelativeLayout;->addView(Landroid/view/View;)V

    .line 9
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->uc:Lcom/tw/music/c/c;

    if-eqz v0, :cond_0

    .line 10
    invoke-direct {p0, v0}, Lcom/tw/music/MusicActivity;->b(Lcom/tw/music/c/c;)V

    :cond_0
    return-void
.end method

.method private te()Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/tw/music/MusicActivity;->fromUser:Z

    return p0
.end method

.method private ue()V
    .locals 3

    .line 1
    iget v0, p0, Lcom/tw/music/MusicActivity;->c:I

    const v1, 0x7f080041

    if-eqz v0, :cond_5

    const/4 v2, 0x1

    if-eq v0, v2, :cond_4

    const/4 v2, 0x2

    if-eq v0, v2, :cond_3

    const/4 v2, 0x3

    if-eq v0, v2, :cond_2

    const/4 v2, 0x4

    if-eq v0, v2, :cond_1

    const/4 v2, 0x5

    if-eq v0, v2, :cond_0

    goto :goto_0

    .line 2
    :cond_0
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    const v0, 0x7f070080

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundResource(I)V

    goto :goto_0

    .line 3
    :cond_1
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    const v0, 0x7f07007f

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundResource(I)V

    goto :goto_0

    .line 4
    :cond_2
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    const v0, 0x7f07007e

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundResource(I)V

    goto :goto_0

    .line 5
    :cond_3
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    const v0, 0x7f07007c

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundResource(I)V

    goto :goto_0

    .line 6
    :cond_4
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    const v0, 0x7f07007b

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundResource(I)V

    goto :goto_0

    .line 7
    :cond_5
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    const v0, 0x7f070079

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundResource(I)V

    :goto_0
    return-void
.end method

.method private ve()V
    .locals 1

    .line 1
    sget v0, Lcom/eckom/xtlibrary/b/f/f/s;->Ad:I

    if-ltz v0, :cond_0

    .line 2
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->mListView:Landroid/widget/ListView;

    invoke-virtual {p0, v0}, Landroid/widget/ListView;->setSelection(I)V

    :cond_0
    return-void
.end method

.method private wb(Ljava/lang/String;)V
    .locals 3

    if-eqz p1, :cond_1

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const/4 v1, 0x0

    const-string v2, "."

    invoke-virtual {p1, v2}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v2

    invoke-virtual {p1, v1, v2}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object p1

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p1, ".lrc"

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    .line 2
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 3
    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 4
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/f/f/b;->Oa(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    invoke-virtual {v0, p1}, Lcom/tw/music/lrc/LrcView;->va(Ljava/lang/String;)V

    .line 5
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    new-instance v0, Lcom/tw/music/h;

    invoke-direct {v0, p0}, Lcom/tw/music/h;-><init>(Lcom/tw/music/MusicActivity;)V

    invoke-virtual {p1, v0}, Lcom/tw/music/lrc/LrcView;->setOnPlayClickListener(Lcom/tw/music/lrc/LrcView$a;)V

    goto :goto_0

    .line 6
    :cond_0
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0}, Lcom/tw/music/lrc/LrcView;->reset()V

    const-string p0, "MusicActivity"

    const-string p1, "LRC NOT EXISTS"

    .line 7
    invoke-static {p0, p1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    :cond_1
    :goto_0
    return-void
.end method

.method private we()V
    .locals 4

    .line 1
    :try_start_0
    new-instance v0, Landroid/content/Intent;

    invoke-direct {v0}, Landroid/content/Intent;-><init>()V

    .line 2
    new-instance v1, Landroid/content/ComponentName;

    const-string v2, "com.tw.eq"

    const-string v3, "com.tw.eq.EQActivity"

    invoke-direct {v1, v2, v3}, Landroid/content/ComponentName;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v0, v1}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    .line 3
    invoke-virtual {p0, v0}, Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    const v0, 0x7f0b0027

    const/4 v1, 0x0

    .line 4
    invoke-static {p0, v0, v1}, Landroid/widget/Toast;->makeText(Landroid/content/Context;II)Landroid/widget/Toast;

    move-result-object p0

    invoke-virtual {p0}, Landroid/widget/Toast;->show()V

    :goto_0
    return-void
.end method

.method private xe()V
    .locals 4

    .line 1
    invoke-virtual {p0}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object v0

    const/4 v1, -0x3

    invoke-virtual {v0, v1}, Landroid/view/Window;->setFormat(I)V

    .line 2
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x15

    if-lt v0, v1, :cond_0

    .line 3
    invoke-virtual {p0}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object p0

    .line 4
    invoke-virtual {p0}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    move-result-object v0

    const/16 v1, 0x500

    .line 5
    invoke-virtual {v0, v1}, Landroid/view/View;->setSystemUiVisibility(I)V

    const/high16 v0, -0x80000000

    .line 6
    invoke-virtual {p0, v0}, Landroid/view/Window;->addFlags(I)V

    const/4 v0, 0x0

    .line 7
    invoke-virtual {p0, v0}, Landroid/view/Window;->setStatusBarColor(I)V

    goto :goto_0

    .line 8
    :cond_0
    invoke-virtual {p0}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object p0

    .line 9
    invoke-virtual {p0}, Landroid/view/Window;->getAttributes()Landroid/view/WindowManager$LayoutParams;

    move-result-object v0

    const/high16 v1, 0x4000000

    const/high16 v2, 0x8000000

    .line 10
    iget v3, v0, Landroid/view/WindowManager$LayoutParams;->flags:I

    or-int/2addr v1, v3

    iput v1, v0, Landroid/view/WindowManager$LayoutParams;->flags:I

    .line 11
    iget v1, v0, Landroid/view/WindowManager$LayoutParams;->flags:I

    or-int/2addr v1, v2

    iput v1, v0, Landroid/view/WindowManager$LayoutParams;->flags:I

    .line 12
    invoke-virtual {p0, v0}, Landroid/view/Window;->setAttributes(Landroid/view/WindowManager$LayoutParams;)V

    :goto_0
    return-void
.end method


# virtual methods
.method public Aa()V
    .locals 4

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Aa()V

    const v0, 0x7f080073

    .line 2
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->zb:Landroid/view/View;

    const v0, 0x7f080076

    .line 3
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Ab:Landroid/view/View;

    .line 4
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object v0

    const-string v1, "-IPS"

    invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    const/high16 v2, -0x1000000

    if-eqz v0, :cond_0

    .line 5
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->zb:Landroid/view/View;

    invoke-virtual {v0, v2}, Landroid/view/View;->setBackgroundColor(I)V

    :cond_0
    const v0, 0x7f08001b

    .line 6
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/tw/music/view/CircleImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    const v0, 0x7f080069

    .line 7
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Gb:Landroid/widget/ImageView;

    const v0, 0x7f080064

    .line 8
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->cc:Landroid/widget/ImageView;

    const v0, 0x7f0800e0

    .line 9
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Ib:Landroid/widget/TextView;

    .line 10
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Ib:Landroid/widget/TextView;

    const/4 v3, 0x1

    invoke-virtual {v0, v3}, Landroid/widget/TextView;->setSelected(Z)V

    const v0, 0x7f0800df

    .line 11
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Jb:Landroid/widget/TextView;

    .line 12
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Jb:Landroid/widget/TextView;

    invoke-virtual {v0, v3}, Landroid/widget/TextView;->setSelected(Z)V

    const v0, 0x7f0800de

    .line 13
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Hb:Landroid/widget/TextView;

    .line 14
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Hb:Landroid/widget/TextView;

    invoke-virtual {v0, v3}, Landroid/widget/TextView;->setSelected(Z)V

    const v0, 0x7f0800dd

    .line 15
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/tw/music/utils/ImagViewAndTextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Lb:Lcom/tw/music/utils/ImagViewAndTextView;

    .line 16
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Lb:Lcom/tw/music/utils/ImagViewAndTextView;

    invoke-virtual {v0, v3}, Landroid/widget/FrameLayout;->setSelected(Z)V

    const v0, 0x7f0800dc

    .line 17
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/tw/music/utils/ImagViewAndTextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Nb:Lcom/tw/music/utils/ImagViewAndTextView;

    .line 18
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Nb:Lcom/tw/music/utils/ImagViewAndTextView;

    invoke-virtual {v0, v3}, Landroid/widget/FrameLayout;->setSelected(Z)V

    const v0, 0x7f080099

    .line 19
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Pb:Landroid/widget/ImageView;

    const v0, 0x7f08006c

    .line 20
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Qb:Landroid/widget/ImageView;

    const v0, 0x7f08006e

    .line 21
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Rb:Landroid/widget/ImageView;

    const v0, 0x7f08006a

    .line 22
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Sb:Landroid/widget/ImageView;

    const v0, 0x7f080070

    .line 23
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Tb:Landroid/widget/ImageView;

    const v0, 0x7f0800d7

    .line 24
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Db:Landroid/widget/TextView;

    const v0, 0x7f0800d8

    .line 25
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Eb:Landroid/widget/TextView;

    const v0, 0x7f0800b4

    .line 26
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/SeekBar;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    const v0, 0x7f0800ba

    .line 27
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Xb:Landroid/widget/ImageView;

    const v0, 0x7f0800a0

    .line 28
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Yb:Landroid/widget/ImageView;

    const v0, 0x7f08004d

    .line 29
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/RelativeLayout;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Zb:Landroid/widget/RelativeLayout;

    const v0, 0x7f080086

    .line 30
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/tw/music/lrc/LrcView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    const v0, 0x7f080082

    .line 31
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/LinearLayout;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->ll_fx:Landroid/widget/LinearLayout;

    .line 32
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ll_fx:Landroid/widget/LinearLayout;

    invoke-virtual {v0}, Landroid/widget/LinearLayout;->getHeight()I

    move-result v0

    sput v0, Lcom/tw/music/MusicActivity;->Dc:I

    const v0, 0x7f08007c

    .line 33
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ListView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->mListView:Landroid/widget/ListView;

    .line 34
    new-instance v0, Lcom/tw/music/a/c;

    invoke-direct {v0, p0}, Lcom/tw/music/a/c;-><init>(Landroid/content/Context;)V

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->xb:Lcom/tw/music/a/c;

    .line 35
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mListView:Landroid/widget/ListView;

    iget-object v3, p0, Lcom/tw/music/MusicActivity;->xb:Lcom/tw/music/a/c;

    invoke-virtual {v0, v3}, Landroid/widget/ListView;->setAdapter(Landroid/widget/ListAdapter;)V

    .line 36
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->xb:Lcom/tw/music/a/c;

    iget-object v3, p0, Lcom/tw/music/MusicActivity;->wc:Lcom/tw/music/a/c$b;

    invoke-virtual {v0, v3}, Lcom/tw/music/a/c;->a(Lcom/tw/music/a/c$b;)V

    .line 37
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->xb:Lcom/tw/music/a/c;

    iget-object v3, p0, Lcom/tw/music/MusicActivity;->xc:Lcom/tw/music/a/c$a;

    invoke-virtual {v0, v3}, Lcom/tw/music/a/c;->a(Lcom/tw/music/a/c$a;)V

    const v0, 0x7f08006d

    .line 38
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Ub:Landroid/widget/ImageView;

    const v0, 0x7f08006f

    .line 39
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Vb:Landroid/widget/ImageView;

    const v0, 0x7f08006b

    .line 40
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Wb:Landroid/widget/ImageView;

    const v0, 0x7f0800e4

    .line 41
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Kb:Landroid/widget/TextView;

    const v0, 0x7f0800e3

    .line 42
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Mb:Landroid/widget/TextView;

    const v0, 0x7f0800e2

    .line 43
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Ob:Landroid/widget/TextView;

    const v0, 0x7f080071

    .line 44
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Lcom/tw/music/view/CircleImageView;

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->Bb:Lcom/tw/music/view/CircleImageView;

    .line 45
    invoke-virtual {p0}, Landroid/support/v7/app/AppCompatActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v3, 0x7f040003

    invoke-virtual {v0, v3}, Landroid/content/res/Resources;->getBoolean(I)Z

    move-result v0

    iput-boolean v0, p0, Lcom/tw/music/MusicActivity;->sc:Z

    const v0, 0x7f080074

    .line 46
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/MusicActivity;->layout_player:Landroid/view/View;

    .line 47
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    const v0, 0x7f080041

    .line 48
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v2}, Landroid/view/View;->setBackgroundColor(I)V

    :cond_1
    return-void
.end method

.method public B(I)V
    .locals 2

    .line 1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "onAudioSessionId: audioSessionId:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v1, p0, Lcom/tw/music/MusicActivity;->Ac:I

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v1, " sessionId:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "MusicActivity"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget v0, p0, Lcom/tw/music/MusicActivity;->Ac:I

    if-eq v0, p1, :cond_0

    .line 3
    iput p1, p0, Lcom/tw/music/MusicActivity;->Ac:I

    .line 4
    iget p1, p0, Lcom/tw/music/MusicActivity;->Ac:I

    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->Ga(I)V

    :cond_0
    return-void
.end method

.method public D(I)V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v0, v0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v0, p1}, Lcom/tw/music/b/a;->va(I)V

    const/4 v0, 0x1

    if-eqz p1, :cond_2

    const/4 v1, 0x2

    if-eq p1, v0, :cond_1

    if-eq p1, v1, :cond_0

    goto :goto_0

    :cond_0
    const/4 p1, 0x3

    .line 2
    iput p1, p0, Lcom/tw/music/MusicActivity;->ic:I

    goto :goto_0

    .line 3
    :cond_1
    iput v1, p0, Lcom/tw/music/MusicActivity;->ic:I

    goto :goto_0

    .line 4
    :cond_2
    iput v0, p0, Lcom/tw/music/MusicActivity;->ic:I

    .line 5
    :goto_0
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Xb:Landroid/widget/ImageView;

    invoke-virtual {p1}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    iget v0, p0, Lcom/tw/music/MusicActivity;->hc:I

    invoke-virtual {p1, v0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 6
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Yb:Landroid/widget/ImageView;

    invoke-virtual {p1}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    iget p0, p0, Lcom/tw/music/MusicActivity;->ic:I

    invoke-virtual {p1, p0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    return-void
.end method

.method public Ha()Ljava/lang/String;
    .locals 0

    const-string p0, "MusicTheme.apk"

    return-object p0
.end method

.method public Ka()Lcom/eckom/xtlibrary/b/i/m;
    .locals 0

    .line 1
    new-instance p0, Lcom/tw/music/c/c;

    invoke-direct {p0}, Lcom/tw/music/c/c;-><init>()V

    return-object p0
.end method

.method public a(Landroid/media/MediaPlayer;)V
    .locals 0

    .line 41
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->a(Landroid/media/MediaPlayer;)V

    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/f/b/g;)V
    .locals 4

    if-nez p1, :cond_0

    return-void

    .line 35
    :cond_0
    iput-object p1, p0, Lcom/tw/music/MusicActivity;->mRecord:Lcom/eckom/xtlibrary/b/f/b/g;

    .line 36
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->xb:Lcom/tw/music/a/c;

    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v0, p1, v1}, Lcom/tw/music/a/c;->a(Lcom/eckom/xtlibrary/b/f/b/g;Lcom/tw/music/b/a;)V

    .line 37
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->ve()V

    const/4 v0, 0x0

    move v1, v0

    :goto_0
    const/4 v2, 0x4

    if-gt v1, v2, :cond_2

    .line 38
    iget v2, p1, Lcom/eckom/xtlibrary/b/f/b/g;->mIndex:I

    if-ne v1, v2, :cond_1

    .line 39
    iget-object v2, p0, Lcom/tw/music/MusicActivity;->gc:[I

    aget v2, v2, v1

    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/ImageView;

    invoke-virtual {v2}, Landroid/widget/ImageView;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    const/4 v3, 0x1

    invoke-virtual {v2, v3}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    goto :goto_1

    .line 40
    :cond_1
    iget-object v2, p0, Lcom/tw/music/MusicActivity;->gc:[I

    aget v2, v2, v1

    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/ImageView;

    invoke-virtual {v2}, Landroid/widget/ImageView;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v2

    invoke-virtual {v2, v0}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    :goto_1
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    :cond_2
    return-void
.end method

.method public a(Lcom/eckom/xtlibrary/b/i/m;Z)V
    .locals 1
    .annotation build Landroid/support/annotation/RequiresApi;
        api = 0x15
    .end annotation

    .line 45
    new-instance p2, Ljava/lang/StringBuilder;

    invoke-direct {p2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "onThemeSwitchFinish: "

    invoke-virtual {p2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/m;->Oc()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p2

    const-string v0, "MusicActivity"

    invoke-static {v0, p2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 46
    check-cast p1, Lcom/tw/music/c/c;

    invoke-virtual {p0, p1}, Lcom/tw/music/MusicActivity;->a(Lcom/tw/music/c/c;)V

    .line 47
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->c(Lcom/tw/music/c/c;)V

    return-void
.end method

.method public a(Lcom/tw/music/c/c;)V
    .locals 1
    .annotation build Landroid/support/annotation/RequiresApi;
        api = 0x15
    .end annotation

    .line 42
    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/m;->Nc()Lcom/eckom/xtlibrary/b/i/l;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/i/l;->Mc()Landroid/content/Context;

    move-result-object v0

    iput-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->eb:Landroid/content/Context;

    .line 43
    iput-object p1, p0, Lcom/tw/music/MusicActivity;->uc:Lcom/tw/music/c/c;

    .line 44
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->b(Lcom/tw/music/c/c;)V

    return-void
.end method

.method public a(Ljava/lang/Boolean;)V
    .locals 3

    .line 19
    invoke-virtual {p1}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v0

    const/4 v1, 0x1

    if-eqz v0, :cond_1

    .line 20
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    iget v2, v0, Lcom/tw/music/view/CircleImageView;->state:I

    if-eq v2, v1, :cond_0

    .line 21
    invoke-virtual {v0}, Lcom/tw/music/view/CircleImageView;->Va()V

    .line 22
    :cond_0
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bb:Lcom/tw/music/view/CircleImageView;

    invoke-virtual {v0}, Lcom/tw/music/view/CircleImageView;->Va()V

    .line 23
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Qb:Landroid/widget/ImageView;

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 24
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Pb:Landroid/widget/ImageView;

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 25
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Ub:Landroid/widget/ImageView;

    invoke-virtual {p0, v1}, Landroid/widget/ImageView;->setImageLevel(I)V

    goto :goto_0

    .line 26
    :cond_1
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    iget v2, v0, Lcom/tw/music/view/CircleImageView;->state:I

    if-ne v2, v1, :cond_2

    .line 27
    invoke-virtual {v0}, Lcom/tw/music/view/CircleImageView;->Ua()V

    const-string v0, "MusicActivity"

    const-string v2, "Downey:onPlayingState: mAlbumArt.pauseMusic()"

    .line 28
    invoke-static {v0, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 29
    :cond_2
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Bb:Lcom/tw/music/view/CircleImageView;

    iget v2, v0, Lcom/tw/music/view/CircleImageView;->state:I

    if-ne v2, v1, :cond_3

    .line 30
    invoke-virtual {v0}, Lcom/tw/music/view/CircleImageView;->Ua()V

    .line 31
    :cond_3
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Qb:Landroid/widget/ImageView;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 32
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Pb:Landroid/widget/ImageView;

    invoke-virtual {v0, v1}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 33
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Ub:Landroid/widget/ImageView;

    invoke-virtual {p0, v1}, Landroid/widget/ImageView;->setImageLevel(I)V

    .line 34
    :goto_0
    new-instance p0, Ljava/lang/StringBuilder;

    invoke-direct {p0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v0, "onPlayingState info..: "

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {p0}, Lcom/tw/music/utils/a;->d(Ljava/lang/String;)V

    return-void
.end method

.method public a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V
    .locals 2

    const p6, 0x7f0b004c

    const-string v0, " "

    if-eq p1, v0, :cond_0

    .line 7
    iget-object v1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v1, v1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v1, p1}, Lcom/tw/music/b/a;->mb(Ljava/lang/String;)V

    goto :goto_0

    .line 8
    :cond_0
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p0, p6}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p1, v1}, Lcom/tw/music/b/a;->mb(Ljava/lang/String;)V

    :goto_0
    if-eq p2, v0, :cond_1

    .line 9
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1, p2}, Lcom/tw/music/b/a;->kb(Ljava/lang/String;)V

    goto :goto_1

    .line 10
    :cond_1
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p0, p6}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Lcom/tw/music/b/a;->kb(Ljava/lang/String;)V

    :goto_1
    if-eq p3, v0, :cond_2

    .line 11
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1, p3}, Lcom/tw/music/b/a;->nb(Ljava/lang/String;)V

    goto :goto_2

    .line 12
    :cond_2
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p0, p6}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Lcom/tw/music/b/a;->nb(Ljava/lang/String;)V

    .line 13
    :goto_2
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1, p5}, Lcom/tw/music/b/a;->lb(Ljava/lang/String;)V

    .line 14
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1, p4}, Lcom/tw/music/b/a;->a(Landroid/graphics/Bitmap;)V

    .line 15
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1, p7}, Lcom/tw/music/b/a;->setIndex(I)V

    .line 16
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "info..: "

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p2, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p2, p2, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p2}, Lcom/tw/music/b/a;->toString()Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1}, Lcom/tw/music/utils/a;->d(Ljava/lang/String;)V

    .line 17
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->qe()V

    .line 18
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->ve()V

    return-void
.end method

.method public d(II)V
    .locals 11

    .line 1
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {v0, p1}, Lcom/tw/music/lrc/LrcView;->fa(I)V

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v0, v0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v0, p1}, Lcom/tw/music/b/a;->ua(I)V

    .line 3
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object v0, v0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {v0, p2}, Lcom/tw/music/b/a;->setDuration(I)V

    .line 4
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->te()Z

    move-result v0

    if-nez v0, :cond_0

    .line 5
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    invoke-virtual {v0, p1}, Landroid/widget/SeekBar;->setProgress(I)V

    :cond_0
    const/4 v0, 0x0

    .line 6
    invoke-direct {p0, v0}, Lcom/tw/music/MusicActivity;->J(Z)V

    .line 7
    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Cb:Landroid/widget/SeekBar;

    invoke-virtual {v1, p2}, Landroid/widget/SeekBar;->setMax(I)V

    .line 8
    invoke-direct {p0, p2}, Lcom/tw/music/MusicActivity;->Ba(I)I

    move-result v1

    .line 9
    invoke-direct {p0, p2}, Lcom/tw/music/MusicActivity;->Ca(I)I

    move-result v2

    .line 10
    invoke-direct {p0, p2}, Lcom/tw/music/MusicActivity;->Da(I)I

    move-result p2

    const-string v3, "%d:%02d"

    const/4 v4, 0x3

    const-string v5, "%d:%02d:%02d"

    const/4 v6, 0x2

    const/4 v7, 0x1

    if-nez v1, :cond_1

    .line 11
    iget-object v1, p0, Lcom/tw/music/MusicActivity;->Eb:Landroid/widget/TextView;

    sget-object v8, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v9, v6, [Ljava/lang/Object;

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    aput-object v2, v9, v0

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p2

    aput-object p2, v9, v7

    invoke-static {v8, v3, v9}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p2

    invoke-virtual {v1, p2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_0

    .line 12
    :cond_1
    iget-object v8, p0, Lcom/tw/music/MusicActivity;->Eb:Landroid/widget/TextView;

    sget-object v9, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v10, v4, [Ljava/lang/Object;

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    aput-object v1, v10, v0

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    aput-object v1, v10, v7

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p2

    aput-object p2, v10, v6

    invoke-static {v9, v5, v10}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p2

    invoke-virtual {v8, p2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 13
    :goto_0
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->Ba(I)I

    move-result p2

    .line 14
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->Ca(I)I

    move-result v1

    .line 15
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->Da(I)I

    move-result p1

    if-nez p2, :cond_2

    .line 16
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Db:Landroid/widget/TextView;

    sget-object p2, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v2, v6, [Ljava/lang/Object;

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    aput-object v1, v2, v0

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p1

    aput-object p1, v2, v7

    invoke-static {p2, v3, v2}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p0, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_1

    .line 17
    :cond_2
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Db:Landroid/widget/TextView;

    sget-object v2, Ljava/util/Locale;->US:Ljava/util/Locale;

    new-array v3, v4, [Ljava/lang/Object;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p2

    aput-object p2, v3, v0

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p2

    aput-object p2, v3, v7

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p1

    aput-object p1, v3, v6

    invoke-static {v2, v5, v3}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p0, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    :goto_1
    return-void
.end method

.method public dispatchKeyEvent(Landroid/view/KeyEvent;)Z
    .locals 3

    .line 1
    invoke-virtual {p1}, Landroid/view/KeyEvent;->getAction()I

    move-result v0

    const/4 v1, 0x1

    if-nez v0, :cond_1

    .line 2
    invoke-virtual {p1}, Landroid/view/KeyEvent;->getKeyCode()I

    move-result v0

    const/16 v2, 0x42

    if-eq v0, v2, :cond_0

    goto :goto_0

    .line 3
    :cond_0
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mListView:Landroid/widget/ListView;

    invoke-virtual {v0}, Landroid/widget/ListView;->getSelectedView()Landroid/view/View;

    move-result-object v0

    if-eqz v0, :cond_2

    .line 4
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mListView:Landroid/widget/ListView;

    invoke-virtual {v0}, Landroid/widget/ListView;->isFocused()Z

    move-result v0

    if-eqz v0, :cond_2

    .line 5
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p1, Lcom/eckom/xtlibrary/b/f/e/a;

    iget-object p0, p0, Lcom/tw/music/MusicActivity;->mListView:Landroid/widget/ListView;

    invoke-virtual {p0}, Landroid/widget/ListView;->getSelectedItemPosition()I

    move-result p0

    invoke-virtual {p1, p0}, Lcom/eckom/xtlibrary/b/f/e/a;->la(I)V

    return v1

    .line 6
    :cond_1
    invoke-virtual {p1}, Landroid/view/KeyEvent;->getAction()I

    move-result v0

    if-ne v0, v1, :cond_2

    .line 7
    invoke-virtual {p1}, Landroid/view/KeyEvent;->getKeyCode()I

    .line 8
    :cond_2
    :goto_0
    invoke-super {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->dispatchKeyEvent(Landroid/view/KeyEvent;)Z

    move-result p0

    return p0
.end method

.method protected getContentView()I
    .locals 0

    const p0, 0x7f0a0027

    return p0
.end method

.method public h(Z)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->cc:Landroid/widget/ImageView;

    invoke-virtual {p0, p1}, Landroid/widget/ImageView;->setImageLevel(I)V

    return-void
.end method

.method public onBackPressed()V
    .locals 4

    const v0, 0x7f080084

    .line 1
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    move-result v1

    const/4 v2, 0x0

    const/16 v3, 0x8

    if-nez v1, :cond_1

    .line 2
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    if-eqz v0, :cond_0

    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    move-result v0

    if-nez v0, :cond_0

    .line 3
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    invoke-virtual {v0, v3}, Landroid/view/View;->setVisibility(I)V

    .line 4
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->layout_player:Landroid/view/View;

    invoke-virtual {p0, v2}, Landroid/view/View;->setVisibility(I)V

    return-void

    .line 5
    :cond_0
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->onBackPressed()V

    .line 6
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x1e

    if-lt v0, v1, :cond_2

    .line 7
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    goto :goto_0

    :cond_1
    const v1, 0x7f080083

    .line 8
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1, v3}, Landroid/view/View;->setVisibility(I)V

    .line 9
    invoke-virtual {p0, v0}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v2}, Landroid/view/View;->setVisibility(I)V

    :cond_2
    :goto_0
    return-void
.end method

.method public onClick(Landroid/view/View;)V
    .locals 6

    .line 1
    invoke-virtual {p1}, Landroid/view/View;->getId()I

    move-result p1

    const/4 v0, 0x2

    const v1, 0x7f080084

    const v2, 0x7f080083

    const/4 v3, 0x1

    const/16 v4, 0x8

    const/4 v5, 0x0

    sparse-switch p1, :sswitch_data_0

    packed-switch p1, :pswitch_data_0

    packed-switch p1, :pswitch_data_1

    packed-switch p1, :pswitch_data_2

    goto/16 :goto_1

    .line 2
    :pswitch_0
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    if-nez p1, :cond_0

    .line 3
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->se()V

    .line 4
    :cond_0
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->layout_player:Landroid/view/View;

    invoke-virtual {p1, v4}, Landroid/view/View;->setVisibility(I)V

    .line 5
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->layout_settings:Landroid/view/View;

    invoke-virtual {p0, v5}, Landroid/view/View;->setVisibility(I)V

    goto/16 :goto_1

    :pswitch_1
    const p1, 0x7f080082

    .line 6
    invoke-virtual {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    move-result v0

    const v1, 0x7f080086

    const v2, 0x7f080066

    if-ne v0, v4, :cond_1

    .line 7
    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    invoke-virtual {v0}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v0, v5}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 8
    invoke-virtual {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    invoke-virtual {p1, v5}, Landroid/view/View;->setVisibility(I)V

    .line 9
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v4}, Landroid/view/View;->setVisibility(I)V

    goto/16 :goto_1

    .line 10
    :cond_1
    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    invoke-virtual {v0}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v0, v3}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    .line 11
    invoke-virtual {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    invoke-virtual {p1, v4}, Landroid/view/View;->setVisibility(I)V

    .line 12
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v5}, Landroid/view/View;->setVisibility(I)V

    goto/16 :goto_1

    .line 13
    :pswitch_2
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->we()V

    goto/16 :goto_1

    .line 14
    :pswitch_3
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Gb()V

    goto/16 :goto_1

    .line 15
    :pswitch_4
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Db()V

    goto/16 :goto_1

    .line 16
    :pswitch_5
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Cb()V

    goto/16 :goto_1

    .line 17
    :pswitch_6
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Bb()V

    goto/16 :goto_1

    .line 18
    :pswitch_7
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Eb()V

    goto/16 :goto_1

    .line 19
    :sswitch_0
    iget p1, p0, Lcom/tw/music/MusicActivity;->hc:I

    add-int/2addr p1, v3

    iput p1, p0, Lcom/tw/music/MusicActivity;->hc:I

    if-le p1, v3, :cond_2

    .line 20
    iput v5, p0, Lcom/tw/music/MusicActivity;->hc:I

    .line 21
    :cond_2
    iget p1, p0, Lcom/tw/music/MusicActivity;->hc:I

    iget v0, p0, Lcom/tw/music/MusicActivity;->ic:I

    invoke-direct {p0, p1, v0}, Lcom/tw/music/MusicActivity;->l(II)V

    goto/16 :goto_1

    .line 22
    :sswitch_1
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1}, Lcom/tw/music/b/a;->hd()I

    move-result p1

    add-int/2addr p1, v3

    if-le p1, v0, :cond_3

    move p1, v5

    .line 23
    :cond_3
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/e/a;->pa(I)V

    .line 24
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p0, p1}, Lcom/tw/music/b/a;->va(I)V

    goto/16 :goto_1

    .line 25
    :pswitch_8
    :sswitch_2
    invoke-direct {p0, v5}, Lcom/tw/music/MusicActivity;->Fa(I)V

    goto/16 :goto_1

    .line 26
    :pswitch_9
    :sswitch_3
    invoke-direct {p0, v3}, Lcom/tw/music/MusicActivity;->Fa(I)V

    goto/16 :goto_1

    .line 27
    :sswitch_4
    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    invoke-virtual {p1, v5}, Landroid/view/View;->setVisibility(I)V

    .line 28
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    invoke-virtual {p1, v4}, Landroid/view/View;->setVisibility(I)V

    .line 29
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->xb:Lcom/tw/music/a/c;

    invoke-virtual {p0}, Lcom/tw/music/a/c;->Xa()V

    goto :goto_1

    .line 30
    :pswitch_a
    :sswitch_5
    invoke-direct {p0, v0}, Lcom/tw/music/MusicActivity;->Fa(I)V

    goto :goto_1

    .line 31
    :sswitch_6
    new-instance p1, Landroid/content/Intent;

    const-string v0, "android.intent.action.MAIN"

    invoke-direct {p1, v0}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const/high16 v0, 0x10000000

    .line 32
    invoke-virtual {p1, v0}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    const-string v0, "android.intent.category.HOME"

    .line 33
    invoke-virtual {p1, v0}, Landroid/content/Intent;->addCategory(Ljava/lang/String;)Landroid/content/Intent;

    .line 34
    invoke-virtual {p0, p1}, Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V

    goto :goto_1

    .line 35
    :sswitch_7
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Ab()V

    goto :goto_1

    .line 36
    :sswitch_8
    iget p1, p0, Lcom/tw/music/MusicActivity;->c:I

    const/4 v0, 0x3

    if-ne p1, v0, :cond_4

    .line 37
    iput v5, p0, Lcom/tw/music/MusicActivity;->c:I

    goto :goto_0

    :cond_4
    add-int/2addr p1, v3

    .line 38
    iput p1, p0, Lcom/tw/music/MusicActivity;->c:I

    .line 39
    :goto_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object p1

    const-string v0, "-IPS"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-nez p1, :cond_5

    const p1, 0x7f080041

    .line 40
    invoke-virtual {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->zc:[I

    iget v1, p0, Lcom/tw/music/MusicActivity;->c:I

    aget v0, v0, v1

    invoke-virtual {p1, v0}, Landroid/view/View;->setBackgroundResource(I)V

    .line 41
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->ue()V

    .line 42
    :cond_5
    iget p1, p0, Lcom/tw/music/MusicActivity;->c:I

    const-string v0, "id"

    invoke-static {p0, v0, v0, p1}, Lcom/tw/music/utils/c;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    goto :goto_1

    .line 43
    :sswitch_9
    invoke-virtual {p0, v2}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    invoke-virtual {p1, v4}, Landroid/view/View;->setVisibility(I)V

    .line 44
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v5}, Landroid/view/View;->setVisibility(I)V

    goto :goto_1

    .line 45
    :sswitch_a
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    :goto_1
    return-void

    nop

    :sswitch_data_0
    .sparse-switch
        0x7f080021 -> :sswitch_a
        0x7f080024 -> :sswitch_9
        0x7f080026 -> :sswitch_8
        0x7f080036 -> :sswitch_7
        0x7f08004f -> :sswitch_6
        0x7f080090 -> :sswitch_5
        0x7f080097 -> :sswitch_4
        0x7f080099 -> :sswitch_3
        0x7f08009b -> :sswitch_2
        0x7f0800a0 -> :sswitch_1
        0x7f0800ba -> :sswitch_0
    .end sparse-switch

    :pswitch_data_0
    .packed-switch 0x7f080028
        :pswitch_7
        :pswitch_6
        :pswitch_5
        :pswitch_4
    .end packed-switch

    :pswitch_data_1
    .packed-switch 0x7f080064
        :pswitch_3
        :pswitch_2
        :pswitch_1
    .end packed-switch

    :pswitch_data_2
    .packed-switch 0x7f08006a
        :pswitch_a
        :pswitch_a
        :pswitch_9
        :pswitch_9
        :pswitch_8
        :pswitch_8
        :pswitch_0
    .end packed-switch
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 2

    .line 1
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->onCreate(Landroid/os/Bundle;)V

    .line 2
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->xe()V

    .line 3
    new-instance p1, Landroid/content/Intent;

    const-class v0, Lcom/tw/music/MusicService;

    invoke-direct {p1, p0, v0}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {p0, p1}, Landroid/app/Activity;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;

    .line 4
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->re()V

    .line 5
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->pe()V

    .line 6
    new-instance p1, Landroid/content/Intent;

    const-class v0, Lcom/tw/music/MusicService;

    invoke-direct {p1, p0, v0}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    iget-object v0, p0, Lcom/tw/music/MusicActivity;->vc:Landroid/content/ServiceConnection;

    const/4 v1, 0x1

    invoke-virtual {p0, p1, v0, v1}, Landroid/app/Activity;->bindService(Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z

    .line 7
    new-instance p1, Landroid/content/IntentFilter;

    invoke-direct {p1}, Landroid/content/IntentFilter;-><init>()V

    const-string v0, "com.android.launcher.widget_music_progress"

    .line 8
    invoke-virtual {p1, v0}, Landroid/content/IntentFilter;->addAction(Ljava/lang/String;)V

    .line 9
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mReceiver:Landroid/content/BroadcastReceiver;

    invoke-virtual {p0, v0, p1}, Landroid/app/Activity;->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;

    return-void
.end method

.method protected onDestroy()V
    .locals 2

    .line 1
    :try_start_0
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->vc:Landroid/content/ServiceConnection;

    invoke-virtual {p0, v0}, Landroid/app/Activity;->unbindService(Landroid/content/ServiceConnection;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    .line 2
    invoke-virtual {v0}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "MusicActivity"

    invoke-static {v1, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 3
    :goto_0
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->ec:Lcom/tw/music/view/BaseVisualizerView;

    if-eqz v0, :cond_0

    const/4 v1, 0x0

    .line 4
    invoke-virtual {v0, v1}, Lcom/tw/music/view/BaseVisualizerView;->setVisualizer(Landroid/media/audiofx/Visualizer;)V

    .line 5
    :cond_0
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->onDestroy()V

    return-void
.end method

.method public onLongClick(Landroid/view/View;)Z
    .locals 1

    .line 1
    invoke-virtual {p1}, Landroid/view/View;->getId()I

    move-result p1

    const v0, 0x7f08006a

    if-eq p1, v0, :cond_1

    const v0, 0x7f08006e

    if-eq p1, v0, :cond_0

    goto :goto_0

    .line 2
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Ib()V

    goto :goto_0

    .line 3
    :cond_1
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->Hb()V

    :goto_0
    const/4 p0, 0x1

    return p0
.end method

.method public onMultiWindowModeChanged(ZLandroid/content/res/Configuration;)V
    .locals 0

    .line 1
    iget-boolean p1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Za:Z

    if-eqz p1, :cond_0

    iget-boolean p1, p0, Lcom/tw/music/MusicActivity;->sc:Z

    if-nez p1, :cond_0

    const/4 p1, 0x2

    .line 2
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->Ea(I)V

    goto :goto_0

    :cond_0
    const/4 p1, 0x0

    .line 3
    invoke-direct {p0, p1}, Lcom/tw/music/MusicActivity;->Ea(I)V

    :goto_0
    return-void
.end method

.method protected onNewIntent(Landroid/content/Intent;)V
    .locals 2

    .line 1
    invoke-virtual {p1}, Landroid/content/Intent;->getData()Landroid/net/Uri;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v1, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {v0}, Landroid/net/Uri;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/e/a;->Ea(Ljava/lang/String;)V

    const/4 v0, 0x0

    .line 3
    invoke-virtual {p0, v0}, Landroid/app/Activity;->setIntent(Landroid/content/Intent;)V

    .line 4
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v4/app/FragmentActivity;->onNewIntent(Landroid/content/Intent;)V

    return-void
.end method

.method protected onPause()V
    .locals 0

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->onPause()V

    .line 2
    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    if-eqz p0, :cond_0

    .line 3
    invoke-virtual {p0}, Lcom/tw/music/view/CircleImageView;->Ua()V

    :cond_0
    return-void
.end method

.method protected onResume()V
    .locals 3

    .line 1
    invoke-virtual {p0}, Landroid/app/Activity;->getApplicationContext()Landroid/content/Context;

    move-result-object v0

    const-string v1, "MusicActivity"

    const-string v2, "lrcorVisible"

    invoke-static {v0, v1, v2}, Lcom/tw/music/utils/c;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v0

    .line 2
    invoke-direct {p0, v0}, Lcom/tw/music/MusicActivity;->K(Z)V

    .line 3
    invoke-virtual {p0}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    if-eqz v0, :cond_1

    .line 4
    invoke-virtual {v0}, Landroid/content/Intent;->getData()Landroid/net/Uri;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 5
    iget-object v1, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v1, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {v0}, Landroid/net/Uri;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Lcom/eckom/xtlibrary/b/f/e/a;->Ea(Ljava/lang/String;)V

    :cond_0
    const/4 v0, 0x0

    .line 6
    invoke-virtual {p0, v0}, Landroid/app/Activity;->setIntent(Landroid/content/Intent;)V

    .line 7
    :cond_1
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->mService:Lcom/tw/music/MusicService;

    if-eqz v0, :cond_2

    iget-object v0, v0, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    if-eqz v0, :cond_2

    invoke-virtual {v0}, Lcom/tw/music/b/a;->isPlaying()Z

    move-result v0

    if-eqz v0, :cond_2

    .line 8
    iget-object v0, p0, Lcom/tw/music/MusicActivity;->Fb:Lcom/tw/music/view/CircleImageView;

    if-eqz v0, :cond_2

    .line 9
    invoke-virtual {v0}, Lcom/tw/music/view/CircleImageView;->Va()V

    .line 10
    :cond_2
    invoke-super {p0}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->onResume()V

    return-void
.end method

.method public onWindowFocusChanged(Z)V
    .locals 0

    .line 1
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->onWindowFocusChanged(Z)V

    return-void
.end method

.method public q(Z)V
    .locals 3

    .line 1
    invoke-super {p0, p1}, Lcom/eckom/xtlibrary/twproject/activity/BaseMusicActivity;->q(Z)V

    const/high16 v0, -0x1000000

    const v1, 0x7f080041

    if-eqz p1, :cond_0

    .line 2
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundColor(I)V

    goto :goto_0

    .line 3
    :cond_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object p1

    const-string v2, "-IPS"

    invoke-virtual {p1, v2}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result p1

    if-eqz p1, :cond_1

    .line 4
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p0

    invoke-virtual {p0, v0}, Landroid/view/View;->setBackgroundColor(I)V

    goto :goto_0

    .line 5
    :cond_1
    invoke-direct {p0}, Lcom/tw/music/MusicActivity;->ue()V

    .line 6
    iget-object p1, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    if-eqz p1, :cond_2

    .line 7
    invoke-virtual {p1}, Lcom/tw/music/c/b;->yd()Landroid/graphics/drawable/Drawable;

    move-result-object p1

    if-eqz p1, :cond_2

    .line 8
    invoke-virtual {p0, v1}, Landroid/support/v7/app/AppCompatActivity;->findViewById(I)Landroid/view/View;

    move-result-object p1

    iget-object p0, p0, Lcom/tw/music/MusicActivity;->Bc:Lcom/tw/music/c/b;

    invoke-virtual {p0}, Lcom/tw/music/c/b;->yd()Landroid/graphics/drawable/Drawable;

    move-result-object p0

    invoke-virtual {p1, p0}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    :cond_2
    :goto_0
    return-void
.end method

.method public za()Lcom/eckom/xtlibrary/b/f/e/a;
    .locals 1

    .line 2
    new-instance v0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-direct {v0, p0}, Lcom/eckom/xtlibrary/b/f/e/a;-><init>(Landroid/content/Context;)V

    return-object v0
.end method

.method public bridge synthetic za()Lcom/eckom/xtlibrary/b/g/a;
    .locals 0

    .line 1
    invoke-virtual {p0}, Lcom/tw/music/MusicActivity;->za()Lcom/eckom/xtlibrary/b/f/e/a;

    move-result-object p0

    return-object p0
.end method

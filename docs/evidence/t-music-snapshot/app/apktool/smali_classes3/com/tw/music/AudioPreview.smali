.class public Lcom/tw/music/AudioPreview;
.super Landroid/app/Activity;
.source "AudioPreview.java"

# interfaces
.implements Landroid/media/MediaPlayer$OnPreparedListener;
.implements Landroid/media/MediaPlayer$OnErrorListener;
.implements Landroid/media/MediaPlayer$OnCompletionListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/tw/music/AudioPreview$a;,
        Lcom/tw/music/AudioPreview$b;
    }
.end annotation


# instance fields
.field private Ec:Landroid/widget/TextView;

.field private Fc:Landroid/widget/TextView;

.field private Gc:Landroid/widget/TextView;

.field private Hc:Landroid/widget/SeekBar;

.field private Ic:Landroid/os/Handler;

.field private Jc:Z

.field private Kc:Z

.field private Lc:Landroid/widget/SeekBar$OnSeekBarChangeListener;

.field private mAudioFocusListener:Landroid/media/AudioManager$OnAudioFocusChangeListener;

.field private mAudioManager:Landroid/media/AudioManager;

.field private mDuration:I

.field private mMediaId:J

.field private mPlayer:Lcom/tw/music/AudioPreview$a;

.field private mUri:Landroid/net/Uri;


# direct methods
.method public constructor <init>()V
    .locals 2

    .line 1
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput-boolean v0, p0, Lcom/tw/music/AudioPreview;->Jc:Z

    const-wide/16 v0, -0x1

    .line 3
    iput-wide v0, p0, Lcom/tw/music/AudioPreview;->mMediaId:J

    .line 4
    new-instance v0, Lcom/tw/music/b;

    invoke-direct {v0, p0}, Lcom/tw/music/b;-><init>(Lcom/tw/music/AudioPreview;)V

    iput-object v0, p0, Lcom/tw/music/AudioPreview;->mAudioFocusListener:Landroid/media/AudioManager$OnAudioFocusChangeListener;

    .line 5
    new-instance v0, Lcom/tw/music/c;

    invoke-direct {v0, p0}, Lcom/tw/music/c;-><init>(Lcom/tw/music/AudioPreview;)V

    iput-object v0, p0, Lcom/tw/music/AudioPreview;->Lc:Landroid/widget/SeekBar$OnSeekBarChangeListener;

    return-void
.end method

.method static synthetic a(Lcom/tw/music/AudioPreview;)I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/AudioPreview;->mDuration:I

    return p0
.end method

.method static synthetic a(Lcom/tw/music/AudioPreview;J)J
    .locals 0

    .line 2
    iput-wide p1, p0, Lcom/tw/music/AudioPreview;->mMediaId:J

    return-wide p1
.end method

.method static synthetic a(Lcom/tw/music/AudioPreview;Z)Z
    .locals 0

    .line 3
    iput-boolean p1, p0, Lcom/tw/music/AudioPreview;->Kc:Z

    return p1
.end method

.method static synthetic b(Lcom/tw/music/AudioPreview;)Landroid/widget/SeekBar;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->Hc:Landroid/widget/SeekBar;

    return-object p0
.end method

.method static synthetic b(Lcom/tw/music/AudioPreview;Z)Z
    .locals 0

    .line 2
    iput-boolean p1, p0, Lcom/tw/music/AudioPreview;->Jc:Z

    return p1
.end method

.method static synthetic c(Lcom/tw/music/AudioPreview;)Landroid/os/Handler;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->Ic:Landroid/os/Handler;

    return-object p0
.end method

.method static synthetic d(Lcom/tw/music/AudioPreview;)Landroid/widget/TextView;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->Ec:Landroid/widget/TextView;

    return-object p0
.end method

.method static synthetic e(Lcom/tw/music/AudioPreview;)Landroid/widget/TextView;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->Fc:Landroid/widget/TextView;

    return-object p0
.end method

.method static synthetic f(Lcom/tw/music/AudioPreview;)Lcom/tw/music/AudioPreview$a;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    return-object p0
.end method

.method static synthetic g(Lcom/tw/music/AudioPreview;)Landroid/media/AudioManager;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->mAudioManager:Landroid/media/AudioManager;

    return-object p0
.end method

.method static synthetic h(Lcom/tw/music/AudioPreview;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/tw/music/AudioPreview;->Kc:Z

    return p0
.end method

.method static synthetic i(Lcom/tw/music/AudioPreview;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->start()V

    return-void
.end method

.method static synthetic j(Lcom/tw/music/AudioPreview;)V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ze()V

    return-void
.end method

.method static synthetic k(Lcom/tw/music/AudioPreview;)Z
    .locals 0

    .line 1
    iget-boolean p0, p0, Lcom/tw/music/AudioPreview;->Jc:Z

    return p0
.end method

.method private start()V
    .locals 4

    .line 1
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->mAudioManager:Landroid/media/AudioManager;

    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mAudioFocusListener:Landroid/media/AudioManager$OnAudioFocusChangeListener;

    const/4 v2, 0x3

    const/4 v3, 0x1

    invoke-virtual {v0, v1, v2, v3}, Landroid/media/AudioManager;->requestAudioFocus(Landroid/media/AudioManager$OnAudioFocusChangeListener;II)I

    .line 2
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->start()V

    .line 3
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Ic:Landroid/os/Handler;

    new-instance v1, Lcom/tw/music/AudioPreview$b;

    invoke-direct {v1, p0}, Lcom/tw/music/AudioPreview$b;-><init>(Lcom/tw/music/AudioPreview;)V

    const-wide/16 v2, 0xc8

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    return-void
.end method

.method private stopPlayback()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Ic:Landroid/os/Handler;

    const/4 v1, 0x0

    if-eqz v0, :cond_0

    .line 2
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 3
    :cond_0
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    if-eqz v0, :cond_1

    .line 4
    invoke-virtual {v0}, Landroid/media/MediaPlayer;->release()V

    .line 5
    iput-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    .line 6
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->mAudioManager:Landroid/media/AudioManager;

    iget-object p0, p0, Lcom/tw/music/AudioPreview;->mAudioFocusListener:Landroid/media/AudioManager$OnAudioFocusChangeListener;

    invoke-virtual {v0, p0}, Landroid/media/AudioManager;->abandonAudioFocus(Landroid/media/AudioManager$OnAudioFocusChangeListener;)I

    :cond_1
    return-void
.end method

.method private ye()V
    .locals 4

    const v0, 0x7f0800bc

    .line 1
    invoke-virtual {p0, v0}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ProgressBar;

    const/16 v1, 0x8

    .line 2
    invoke-virtual {v0, v1}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 3
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {v0}, Landroid/media/MediaPlayer;->getDuration()I

    move-result v0

    iput v0, p0, Lcom/tw/music/AudioPreview;->mDuration:I

    .line 4
    iget v0, p0, Lcom/tw/music/AudioPreview;->mDuration:I

    const/4 v2, 0x0

    if-eqz v0, :cond_0

    .line 5
    iget-object v3, p0, Lcom/tw/music/AudioPreview;->Hc:Landroid/widget/SeekBar;

    invoke-virtual {v3, v0}, Landroid/widget/SeekBar;->setMax(I)V

    .line 6
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Hc:Landroid/widget/SeekBar;

    invoke-virtual {v0, v2}, Landroid/widget/SeekBar;->setVisibility(I)V

    .line 7
    :cond_0
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Hc:Landroid/widget/SeekBar;

    iget-object v3, p0, Lcom/tw/music/AudioPreview;->Lc:Landroid/widget/SeekBar$OnSeekBarChangeListener;

    invoke-virtual {v0, v3}, Landroid/widget/SeekBar;->setOnSeekBarChangeListener(Landroid/widget/SeekBar$OnSeekBarChangeListener;)V

    .line 8
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Gc:Landroid/widget/TextView;

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setVisibility(I)V

    const v0, 0x7f0800d4

    .line 9
    invoke-virtual {p0, v0}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    .line 10
    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V

    .line 11
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->mAudioManager:Landroid/media/AudioManager;

    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mAudioFocusListener:Landroid/media/AudioManager$OnAudioFocusChangeListener;

    const/4 v2, 0x3

    const/4 v3, 0x1

    invoke-virtual {v0, v1, v2, v3}, Landroid/media/AudioManager;->requestAudioFocus(Landroid/media/AudioManager$OnAudioFocusChangeListener;II)I

    .line 12
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Ic:Landroid/os/Handler;

    new-instance v1, Lcom/tw/music/AudioPreview$b;

    invoke-direct {v1, p0}, Lcom/tw/music/AudioPreview$b;-><init>(Lcom/tw/music/AudioPreview;)V

    const-wide/16 v2, 0xc8

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 13
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ze()V

    return-void
.end method

.method private ze()V
    .locals 2

    const v0, 0x7f080098

    .line 1
    invoke-virtual {p0, v0}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageButton;

    if-eqz v0, :cond_1

    .line 2
    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {v1}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result v1

    if-eqz v1, :cond_0

    const p0, 0x7f0700a1

    .line 3
    invoke-virtual {v0, p0}, Landroid/widget/ImageButton;->setImageResource(I)V

    goto :goto_0

    :cond_0
    const v1, 0x7f0700a2

    .line 4
    invoke-virtual {v0, v1}, Landroid/widget/ImageButton;->setImageResource(I)V

    .line 5
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->Ic:Landroid/os/Handler;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    :cond_1
    :goto_0
    return-void
.end method


# virtual methods
.method public Oa()V
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Ec:Landroid/widget/TextView;

    invoke-virtual {v0}, Landroid/widget/TextView;->getText()Ljava/lang/CharSequence;

    move-result-object v0

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Ec:Landroid/widget/TextView;

    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    invoke-virtual {v1}, Landroid/net/Uri;->getLastPathSegment()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 3
    :cond_0
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->Fc:Landroid/widget/TextView;

    invoke-virtual {v0}, Landroid/widget/TextView;->getText()Ljava/lang/CharSequence;

    move-result-object v0

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    .line 4
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->Fc:Landroid/widget/TextView;

    const/16 v0, 0x8

    invoke-virtual {p0, v0}, Landroid/widget/TextView;->setVisibility(I)V

    goto :goto_0

    .line 5
    :cond_1
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->Fc:Landroid/widget/TextView;

    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Landroid/widget/TextView;->setVisibility(I)V

    :goto_0
    return-void
.end method

.method public onCompletion(Landroid/media/MediaPlayer;)V
    .locals 1

    .line 1
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->Hc:Landroid/widget/SeekBar;

    iget v0, p0, Lcom/tw/music/AudioPreview;->mDuration:I

    invoke-virtual {p1, v0}, Landroid/widget/SeekBar;->setProgress(I)V

    .line 2
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ze()V

    return-void
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 11

    .line 1
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    .line 2
    invoke-virtual {p0}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    move-result-object p1

    if-nez p1, :cond_0

    .line 3
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    return-void

    .line 4
    :cond_0
    invoke-virtual {p1}, Landroid/content/Intent;->getData()Landroid/net/Uri;

    move-result-object p1

    iput-object p1, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    .line 5
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    if-nez p1, :cond_1

    .line 6
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    return-void

    .line 7
    :cond_1
    invoke-virtual {p1}, Landroid/net/Uri;->getScheme()Ljava/lang/String;

    move-result-object p1

    const/4 v0, 0x3

    .line 8
    invoke-virtual {p0, v0}, Landroid/app/Activity;->setVolumeControlStream(I)V

    const/4 v0, 0x1

    .line 9
    invoke-virtual {p0, v0}, Landroid/app/Activity;->requestWindowFeature(I)Z

    const v1, 0x7f0a001d

    .line 10
    invoke-virtual {p0, v1}, Landroid/app/Activity;->setContentView(I)V

    const v1, 0x7f080079

    .line 11
    invoke-virtual {p0, v1}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/TextView;

    iput-object v1, p0, Lcom/tw/music/AudioPreview;->Ec:Landroid/widget/TextView;

    const v1, 0x7f08007a

    .line 12
    invoke-virtual {p0, v1}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/TextView;

    iput-object v1, p0, Lcom/tw/music/AudioPreview;->Fc:Landroid/widget/TextView;

    const v1, 0x7f080085

    .line 13
    invoke-virtual {p0, v1}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/TextView;

    iput-object v1, p0, Lcom/tw/music/AudioPreview;->Gc:Landroid/widget/TextView;

    const-string v1, "http"

    .line 14
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    const/4 v2, 0x0

    if-eqz v1, :cond_2

    const v1, 0x7f0b004a

    new-array v3, v0, [Ljava/lang/Object;

    .line 15
    iget-object v4, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    invoke-virtual {v4}, Landroid/net/Uri;->getHost()Ljava/lang/String;

    move-result-object v4

    aput-object v4, v3, v2

    invoke-virtual {p0, v1, v3}, Landroid/app/Activity;->getString(I[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    .line 16
    iget-object v3, p0, Lcom/tw/music/AudioPreview;->Gc:Landroid/widget/TextView;

    invoke-virtual {v3, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_0

    .line 17
    :cond_2
    iget-object v1, p0, Lcom/tw/music/AudioPreview;->Gc:Landroid/widget/TextView;

    const/16 v3, 0x8

    invoke-virtual {v1, v3}, Landroid/widget/TextView;->setVisibility(I)V

    :goto_0
    const v1, 0x7f08009c

    .line 18
    invoke-virtual {p0, v1}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/SeekBar;

    iput-object v1, p0, Lcom/tw/music/AudioPreview;->Hc:Landroid/widget/SeekBar;

    .line 19
    new-instance v1, Landroid/os/Handler;

    invoke-direct {v1}, Landroid/os/Handler;-><init>()V

    iput-object v1, p0, Lcom/tw/music/AudioPreview;->Ic:Landroid/os/Handler;

    const-string v1, "audio"

    .line 20
    invoke-virtual {p0, v1}, Landroid/app/Activity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/media/AudioManager;

    iput-object v1, p0, Lcom/tw/music/AudioPreview;->mAudioManager:Landroid/media/AudioManager;

    .line 21
    invoke-virtual {p0}, Landroid/app/Activity;->getLastNonConfigurationInstance()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/tw/music/AudioPreview$a;

    if-nez v1, :cond_3

    .line 22
    new-instance v1, Lcom/tw/music/AudioPreview$a;

    const/4 v3, 0x0

    invoke-direct {v1, v3}, Lcom/tw/music/AudioPreview$a;-><init>(Lcom/tw/music/a;)V

    iput-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    .line 23
    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {v1, p0}, Lcom/tw/music/AudioPreview$a;->l(Lcom/tw/music/AudioPreview;)V

    .line 24
    :try_start_0
    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    iget-object v3, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    invoke-virtual {v1, v3}, Lcom/tw/music/AudioPreview$a;->a(Landroid/net/Uri;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    :catch_0
    move-exception p1

    .line 25
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "Failed to open file: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    const-string v0, "AudioPreview"

    invoke-static {v0, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const p1, 0x7f0b0041

    .line 26
    invoke-static {p0, p1, v2}, Landroid/widget/Toast;->makeText(Landroid/content/Context;II)Landroid/widget/Toast;

    move-result-object p1

    invoke-virtual {p1}, Landroid/widget/Toast;->show()V

    .line 27
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    return-void

    .line 28
    :cond_3
    iput-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    .line 29
    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {v1, p0}, Lcom/tw/music/AudioPreview$a;->l(Lcom/tw/music/AudioPreview;)V

    .line 30
    iget-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {v1}, Lcom/tw/music/AudioPreview$a;->Qa()Z

    move-result v1

    if-eqz v1, :cond_4

    .line 31
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ye()V

    .line 32
    :cond_4
    :goto_1
    new-instance v3, Lcom/tw/music/a;

    invoke-virtual {p0}, Landroid/app/Activity;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v1

    invoke-direct {v3, p0, v1}, Lcom/tw/music/a;-><init>(Lcom/tw/music/AudioPreview;Landroid/content/ContentResolver;)V

    const-string v1, "content"

    .line 33
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    const-string v4, "artist"

    const-string v5, "title"

    if-eqz v1, :cond_6

    .line 34
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    invoke-virtual {p1}, Landroid/net/Uri;->getAuthority()Ljava/lang/String;

    move-result-object p1

    const-string v0, "media"

    if-ne p1, v0, :cond_5

    const/4 p1, 0x0

    const/4 v0, 0x0

    .line 35
    iget-object v6, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    filled-new-array {v5, v4}, [Ljava/lang/String;

    move-result-object v7

    const/4 v8, 0x0

    const/4 v9, 0x0

    const/4 v10, 0x0

    move v4, p1

    move-object v5, v0

    invoke-virtual/range {v3 .. v10}, Landroid/content/AsyncQueryHandler;->startQuery(ILjava/lang/Object;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_2

    :cond_5
    const/4 v4, 0x0

    const/4 v5, 0x0

    .line 36
    iget-object v6, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    const/4 v7, 0x0

    const/4 v8, 0x0

    const/4 v9, 0x0

    const/4 v10, 0x0

    invoke-virtual/range {v3 .. v10}, Landroid/content/AsyncQueryHandler;->startQuery(ILjava/lang/Object;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_2

    :cond_6
    const-string v1, "file"

    .line 37
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_7

    .line 38
    iget-object p0, p0, Lcom/tw/music/AudioPreview;->mUri:Landroid/net/Uri;

    invoke-virtual {p0}, Landroid/net/Uri;->getPath()Ljava/lang/String;

    move-result-object p0

    const/4 p1, 0x0

    const/4 v1, 0x0

    .line 39
    sget-object v6, Landroid/provider/MediaStore$Audio$Media;->EXTERNAL_CONTENT_URI:Landroid/net/Uri;

    const-string v7, "_id"

    filled-new-array {v7, v5, v4}, [Ljava/lang/String;

    move-result-object v7

    new-array v9, v0, [Ljava/lang/String;

    aput-object p0, v9, v2

    const/4 v10, 0x0

    const-string v8, "_data=?"

    move v4, p1

    move-object v5, v1

    invoke-virtual/range {v3 .. v10}, Landroid/content/AsyncQueryHandler;->startQuery(ILjava/lang/Object;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_2

    .line 40
    :cond_7
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Lcom/tw/music/AudioPreview$a;->Qa()Z

    move-result p1

    if-eqz p1, :cond_8

    .line 41
    invoke-virtual {p0}, Lcom/tw/music/AudioPreview;->Oa()V

    :cond_8
    :goto_2
    return-void
.end method

.method public onCreateOptionsMenu(Landroid/view/Menu;)Z
    .locals 2

    .line 1
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreateOptionsMenu(Landroid/view/Menu;)Z

    const/4 p0, 0x1

    const/4 v0, 0x0

    const-string v1, "open in music"

    .line 2
    invoke-interface {p1, v0, p0, v0, v1}, Landroid/view/Menu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    return p0
.end method

.method public onDestroy()V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->stopPlayback()V

    .line 2
    invoke-super {p0}, Landroid/app/Activity;->onDestroy()V

    return-void
.end method

.method public onError(Landroid/media/MediaPlayer;II)Z
    .locals 0

    const p1, 0x7f0b0041

    const/4 p2, 0x0

    .line 1
    invoke-static {p0, p1, p2}, Landroid/widget/Toast;->makeText(Landroid/content/Context;II)Landroid/widget/Toast;

    move-result-object p1

    invoke-virtual {p1}, Landroid/widget/Toast;->show()V

    .line 2
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    const/4 p0, 0x1

    return p0
.end method

.method public onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 2

    const/4 v0, 0x4

    const/4 v1, 0x1

    if-eq p1, v0, :cond_5

    const/16 v0, 0x4f

    if-eq p1, v0, :cond_3

    const/16 v0, 0x7e

    if-eq p1, v0, :cond_2

    const/16 v0, 0x7f

    if-eq p1, v0, :cond_0

    packed-switch p1, :pswitch_data_0

    .line 1
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onKeyDown(ILandroid/view/KeyEvent;)Z

    move-result p0

    return p0

    :pswitch_0
    return v1

    .line 2
    :cond_0
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result p1

    if-eqz p1, :cond_1

    .line 3
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->pause()V

    .line 4
    :cond_1
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ze()V

    return v1

    .line 5
    :cond_2
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->start()V

    .line 6
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ze()V

    return v1

    .line 7
    :cond_3
    :pswitch_1
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result p1

    if-eqz p1, :cond_4

    .line 8
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->pause()V

    goto :goto_0

    .line 9
    :cond_4
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->start()V

    .line 10
    :goto_0
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ze()V

    return v1

    .line 11
    :cond_5
    :pswitch_2
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->stopPlayback()V

    .line 12
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    return v1

    nop

    :pswitch_data_0
    .packed-switch 0x55
        :pswitch_1
        :pswitch_2
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
    .end packed-switch
.end method

.method public onPrepareOptionsMenu(Landroid/view/Menu;)Z
    .locals 5

    const/4 v0, 0x1

    .line 1
    invoke-interface {p1, v0}, Landroid/view/Menu;->findItem(I)Landroid/view/MenuItem;

    move-result-object p1

    .line 2
    iget-wide v1, p0, Lcom/tw/music/AudioPreview;->mMediaId:J

    const-wide/16 v3, 0x0

    cmp-long p0, v1, v3

    if-ltz p0, :cond_0

    .line 3
    invoke-interface {p1, v0}, Landroid/view/MenuItem;->setVisible(Z)Landroid/view/MenuItem;

    return v0

    :cond_0
    const/4 p0, 0x0

    .line 4
    invoke-interface {p1, p0}, Landroid/view/MenuItem;->setVisible(Z)Landroid/view/MenuItem;

    return p0
.end method

.method public onPrepared(Landroid/media/MediaPlayer;)V
    .locals 1

    .line 1
    invoke-virtual {p0}, Landroid/app/Activity;->isFinishing()Z

    move-result v0

    if-eqz v0, :cond_0

    return-void

    .line 2
    :cond_0
    check-cast p1, Lcom/tw/music/AudioPreview$a;

    iput-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    .line 3
    invoke-virtual {p0}, Lcom/tw/music/AudioPreview;->Oa()V

    .line 4
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->start()V

    .line 5
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ye()V

    return-void
.end method

.method public onRetainNonConfigurationInstance()Ljava/lang/Object;
    .locals 2

    .line 1
    iget-object v0, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    const/4 v1, 0x0

    .line 2
    iput-object v1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    return-object v0
.end method

.method public onUserLeaveHint()V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->stopPlayback()V

    .line 2
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    .line 3
    invoke-super {p0}, Landroid/app/Activity;->onUserLeaveHint()V

    return-void
.end method

.method public playPauseClicked(Landroid/view/View;)V
    .locals 0

    .line 1
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result p1

    if-eqz p1, :cond_0

    .line 2
    iget-object p1, p0, Lcom/tw/music/AudioPreview;->mPlayer:Lcom/tw/music/AudioPreview$a;

    invoke-virtual {p1}, Landroid/media/MediaPlayer;->pause()V

    goto :goto_0

    .line 3
    :cond_0
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->start()V

    .line 4
    :goto_0
    invoke-direct {p0}, Lcom/tw/music/AudioPreview;->ze()V

    return-void
.end method

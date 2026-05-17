.class Lcom/tw/music/g;
.super Ljava/lang/Object;
.source "MusicActivity.java"

# interfaces
.implements Landroid/widget/SeekBar$OnSeekBarChangeListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/MusicActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/MusicActivity;


# direct methods
.method constructor <init>(Lcom/tw/music/MusicActivity;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/g;->this$0:Lcom/tw/music/MusicActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onProgressChanged(Landroid/widget/SeekBar;IZ)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/g;->this$0:Lcom/tw/music/MusicActivity;

    invoke-static {p0, p3}, Lcom/tw/music/MusicActivity;->a(Lcom/tw/music/MusicActivity;Z)V

    return-void
.end method

.method public onStartTrackingTouch(Landroid/widget/SeekBar;)V
    .locals 0

    return-void
.end method

.method public onStopTrackingTouch(Landroid/widget/SeekBar;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/g;->this$0:Lcom/tw/music/MusicActivity;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p1}, Landroid/widget/SeekBar;->getProgress()I

    move-result p1

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/f/e/a;->seekTo(I)V

    return-void
.end method

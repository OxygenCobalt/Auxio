.class Lcom/tw/music/d;
.super Ljava/lang/Object;
.source "MusicActivity.java"

# interfaces
.implements Landroid/content/ServiceConnection;


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
    iput-object p1, p0, Lcom/tw/music/d;->this$0:Lcom/tw/music/MusicActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onServiceConnected(Landroid/content/ComponentName;Landroid/os/IBinder;)V
    .locals 0

    .line 1
    iget-object p1, p0, Lcom/tw/music/d;->this$0:Lcom/tw/music/MusicActivity;

    check-cast p2, Lcom/tw/music/MusicService$a;

    invoke-virtual {p2}, Lcom/tw/music/MusicService$a;->getService()Lcom/tw/music/MusicService;

    move-result-object p2

    invoke-static {p1, p2}, Lcom/tw/music/MusicActivity;->a(Lcom/tw/music/MusicActivity;Lcom/tw/music/MusicService;)Lcom/tw/music/MusicService;

    .line 2
    iget-object p1, p0, Lcom/tw/music/d;->this$0:Lcom/tw/music/MusicActivity;

    invoke-static {p1}, Lcom/tw/music/MusicActivity;->a(Lcom/tw/music/MusicActivity;)Lcom/tw/music/MusicService;

    move-result-object p1

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    if-nez p1, :cond_0

    return-void

    .line 3
    :cond_0
    iget-object p1, p0, Lcom/tw/music/d;->this$0:Lcom/tw/music/MusicActivity;

    invoke-static {p1}, Lcom/tw/music/MusicActivity;->a(Lcom/tw/music/MusicActivity;)Lcom/tw/music/MusicService;

    move-result-object p1

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1}, Lcom/tw/music/b/a;->isPlaying()Z

    move-result p1

    if-nez p1, :cond_1

    .line 4
    iget-object p0, p0, Lcom/tw/music/d;->this$0:Lcom/tw/music/MusicActivity;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->fa()V

    :cond_1
    return-void
.end method

.method public onServiceDisconnected(Landroid/content/ComponentName;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/d;->this$0:Lcom/tw/music/MusicActivity;

    const/4 p1, 0x0

    invoke-static {p0, p1}, Lcom/tw/music/MusicActivity;->a(Lcom/tw/music/MusicActivity;Lcom/tw/music/MusicService;)Lcom/tw/music/MusicService;

    return-void
.end method

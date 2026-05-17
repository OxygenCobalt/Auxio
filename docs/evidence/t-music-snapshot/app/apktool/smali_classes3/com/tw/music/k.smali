.class Lcom/tw/music/k;
.super Landroid/content/BroadcastReceiver;
.source "MusicService.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/tw/music/MusicService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tw/music/MusicService;


# direct methods
.method constructor <init>(Lcom/tw/music/MusicService;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    return-void
.end method


# virtual methods
.method public onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 2

    .line 1
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "cmd"

    .line 2
    invoke-virtual {p2, v0}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    const-string v1, "prev"

    .line 3
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_6

    const-string v1, "com.tw.music.action.prev"

    invoke-virtual {v1, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    goto :goto_2

    :cond_0
    const-string v1, "next"

    .line 4
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_5

    const-string v1, "com.tw.music.action.next"

    invoke-virtual {v1, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_1

    goto :goto_1

    :cond_1
    const-string v1, "pp"

    .line 5
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_3

    const-string v1, "com.tw.music.action.pp"

    invoke-virtual {v1, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_2

    goto :goto_0

    :cond_2
    const-string p1, "update"

    .line 6
    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_7

    const-string p1, "appWidgetIds"

    .line 7
    invoke-virtual {p2, p1}, Landroid/content/Intent;->getIntArrayExtra(Ljava/lang/String;)[I

    move-result-object p1

    .line 8
    iget-object p2, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    invoke-static {p2}, Lcom/tw/music/MusicService;->b(Lcom/tw/music/MusicService;)Lcom/tw/music/view/MusicWidgetProvider;

    move-result-object p2

    iget-object p0, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    invoke-virtual {p2, p0, p1}, Lcom/tw/music/view/MusicWidgetProvider;->a(Lcom/tw/music/MusicService;[I)V

    goto :goto_3

    .line 9
    :cond_3
    :goto_0
    iget-object p1, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    iget-object p1, p1, Lcom/tw/music/MusicService;->Pa:Lcom/tw/music/b/a;

    invoke-virtual {p1}, Lcom/tw/music/b/a;->isPlaying()Z

    move-result p1

    if-eqz p1, :cond_4

    .line 10
    iget-object p0, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->ba()V

    goto :goto_3

    .line 11
    :cond_4
    iget-object p0, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->fa()V

    goto :goto_3

    .line 12
    :cond_5
    :goto_1
    iget-object p0, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->pb()V

    goto :goto_3

    .line 13
    :cond_6
    :goto_2
    iget-object p0, p0, Lcom/tw/music/k;->this$0:Lcom/tw/music/MusicService;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/service/XTService;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/f/e/a;->rb()V

    :cond_7
    :goto_3
    return-void
.end method

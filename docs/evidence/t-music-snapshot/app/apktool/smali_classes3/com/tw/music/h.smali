.class Lcom/tw/music/h;
.super Ljava/lang/Object;
.source "MusicActivity.java"

# interfaces
.implements Lcom/tw/music/lrc/LrcView$a;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tw/music/MusicActivity;->wb(Ljava/lang/String;)V
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
    iput-object p1, p0, Lcom/tw/music/h;->this$0:Lcom/tw/music/MusicActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public f(J)Z
    .locals 1

    .line 1
    iget-object v0, p0, Lcom/tw/music/h;->this$0:Lcom/tw/music/MusicActivity;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast v0, Lcom/eckom/xtlibrary/b/f/e/a;

    long-to-int p1, p1

    invoke-virtual {v0, p1}, Lcom/eckom/xtlibrary/b/f/e/a;->seekTo(I)V

    .line 2
    iget-object p0, p0, Lcom/tw/music/h;->this$0:Lcom/tw/music/MusicActivity;

    iget-object p0, p0, Lcom/tw/music/MusicActivity;->lrc_view:Lcom/tw/music/lrc/LrcView;

    invoke-virtual {p0, p1}, Lcom/tw/music/lrc/LrcView;->fa(I)V

    const/4 p0, 0x1

    return p0
.end method

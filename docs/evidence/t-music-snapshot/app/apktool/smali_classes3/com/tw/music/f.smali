.class Lcom/tw/music/f;
.super Ljava/lang/Object;
.source "MusicActivity.java"

# interfaces
.implements Lcom/tw/music/a/c$a;


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
    iput-object p1, p0, Lcom/tw/music/f;->this$0:Lcom/tw/music/MusicActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public a(Lcom/eckom/xtlibrary/b/f/b/f;Z)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/f;->this$0:Lcom/tw/music/MusicActivity;

    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->mPresenter:Lcom/eckom/xtlibrary/b/g/a;

    check-cast p0, Lcom/eckom/xtlibrary/b/f/e/a;

    invoke-virtual {p0, p1, p2}, Lcom/eckom/xtlibrary/b/f/e/a;->b(Lcom/eckom/xtlibrary/b/f/b/f;Z)V

    return-void
.end method

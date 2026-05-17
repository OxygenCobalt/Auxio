.class public Lcom/tw/music/c/c;
.super Lcom/eckom/xtlibrary/b/i/m;
.source "MusicThemeInfo.java"


# instance fields
.field public Bc:Lcom/tw/music/c/b;

.field public fm:Lcom/tw/music/c/a;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Lcom/eckom/xtlibrary/b/i/m;-><init>()V

    return-void
.end method


# virtual methods
.method public Pc()V
    .locals 2

    .line 1
    invoke-super {p0}, Lcom/eckom/xtlibrary/b/i/m;->Pc()V

    .line 2
    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/i/m;->Nc()Lcom/eckom/xtlibrary/b/i/l;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/i/l;->Mc()Landroid/content/Context;

    move-result-object v0

    const-string v1, "views_player_config.json"

    .line 3
    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/i/b;->b(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 4
    invoke-static {v1}, Lcom/tw/music/c/b;->sb(Ljava/lang/String;)Lcom/tw/music/c/b;

    move-result-object v1

    iput-object v1, p0, Lcom/tw/music/c/c;->Bc:Lcom/tw/music/c/b;

    const-string v1, "views_list_config.json"

    .line 5
    invoke-static {v0, v1}, Lcom/eckom/xtlibrary/b/i/b;->b(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 6
    invoke-static {v0}, Lcom/tw/music/c/a;->rb(Ljava/lang/String;)Lcom/tw/music/c/a;

    move-result-object v0

    iput-object v0, p0, Lcom/tw/music/c/c;->fm:Lcom/tw/music/c/a;

    return-void
.end method

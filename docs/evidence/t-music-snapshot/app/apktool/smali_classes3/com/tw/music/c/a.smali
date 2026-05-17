.class public Lcom/tw/music/c/a;
.super Ljava/lang/Object;
.source "ListPlugin.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/tw/music/c/a$a;
    }
.end annotation


# instance fields
.field private Lm:Landroid/graphics/drawable/Drawable;

.field private Mm:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/tw/music/c/a$a;",
            ">;"
        }
    .end annotation
.end field

.field private Nm:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/tw/music/c/a$a;",
            ">;"
        }
    .end annotation
.end field

.field private Om:Ljava/lang/String;

.field private Pm:Landroid/graphics/drawable/Drawable;

.field private Qm:I

.field private Rm:I

.field private Sm:Landroid/graphics/drawable/Drawable;

.field private album:Landroid/graphics/drawable/Drawable;

.field private next:Landroid/graphics/drawable/Drawable;

.field private prev:Landroid/graphics/drawable/Drawable;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 2
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/tw/music/c/a;->Mm:Ljava/util/List;

    .line 3
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/tw/music/c/a;->Nm:Ljava/util/List;

    return-void
.end method

.method public static rb(Ljava/lang/String;)Lcom/tw/music/c/a;
    .locals 8

    .line 1
    :try_start_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object v0

    .line 2
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    .line 3
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v1

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object v1

    invoke-virtual {v1}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    .line 4
    new-instance v1, Lcom/tw/music/c/a;

    invoke-direct {v1}, Lcom/tw/music/c/a;-><init>()V

    .line 5
    new-instance v2, Lorg/json/JSONArray;

    invoke-direct {v2, p0}, Lorg/json/JSONArray;-><init>(Ljava/lang/String;)V

    const/4 p0, 0x0

    .line 6
    invoke-virtual {v2, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lorg/json/JSONObject;

    const-string v4, "background"

    invoke-virtual {v3, v4}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-static {v0, v3}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v3

    .line 7
    invoke-virtual {v1, v3}, Lcom/tw/music/c/a;->f(Landroid/graphics/drawable/Drawable;)V

    .line 8
    invoke-virtual {v2, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lorg/json/JSONObject;

    const-string v4, "itemInfo"

    invoke-virtual {v3, v4}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v3

    .line 9
    invoke-virtual {v1}, Lcom/tw/music/c/a;->od()Ljava/util/List;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/List;->clear()V

    move v4, p0

    .line 10
    :goto_0
    invoke-virtual {v3}, Lorg/json/JSONArray;->length()I

    move-result v5
    :try_end_0
    .catch Lorg/json/JSONException; {:try_start_0 .. :try_end_0} :catch_0

    const-string v6, "btn_img"

    if-ge v4, v5, :cond_0

    .line 11
    :try_start_1
    new-instance v5, Lcom/tw/music/c/a$a;

    invoke-direct {v5}, Lcom/tw/music/c/a$a;-><init>()V

    .line 12
    invoke-virtual {v3, v4}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lorg/json/JSONObject;

    invoke-virtual {v7, v6}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    invoke-static {v0, v6}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v6

    invoke-virtual {v5, v6}, Lcom/tw/music/c/a$a;->d(Landroid/graphics/drawable/Drawable;)V

    .line 13
    invoke-virtual {v3, v4}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lorg/json/JSONObject;

    const-string v7, "btn_background"

    invoke-virtual {v6, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    invoke-static {v0, v6}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v6

    invoke-virtual {v5, v6}, Lcom/tw/music/c/a$a;->c(Landroid/graphics/drawable/Drawable;)V

    .line 14
    invoke-virtual {v1}, Lcom/tw/music/c/a;->od()Ljava/util/List;

    move-result-object v6

    invoke-interface {v6, v5}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    add-int/lit8 v4, v4, 0x1

    goto :goto_0

    :cond_0
    const/4 v3, 0x1

    .line 15
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    const-string v5, "listbackground"

    invoke-virtual {v4, v5}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v1, v4}, Lcom/tw/music/c/a;->qb(Ljava/lang/String;)V

    .line 16
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    const-string v5, "song"

    invoke-virtual {v4, v5}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v4

    .line 17
    invoke-virtual {v4, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lorg/json/JSONObject;

    const-string v7, "color_selector"

    invoke-virtual {v5, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v5

    invoke-static {v0, v5}, Lcom/eckom/xtlibrary/b/i/h;->c(Landroid/content/Context;Ljava/lang/String;)I

    move-result v5

    invoke-virtual {v1, v5}, Lcom/tw/music/c/a;->xa(I)V

    .line 18
    invoke-virtual {v4, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    const-string v5, "color_normal"

    invoke-virtual {v4, v5}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-static {v0, v4}, Lcom/eckom/xtlibrary/b/i/h;->c(Landroid/content/Context;Ljava/lang/String;)I

    move-result v4

    invoke-virtual {v1, v4}, Lcom/tw/music/c/a;->wa(I)V

    .line 19
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lorg/json/JSONObject;

    const-string v4, "itemInfolist"

    invoke-virtual {v3, v4}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v3

    .line 20
    invoke-virtual {v1}, Lcom/tw/music/c/a;->pd()Ljava/util/List;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/List;->clear()V

    move v4, p0

    .line 21
    :goto_1
    invoke-virtual {v3}, Lorg/json/JSONArray;->length()I

    move-result v5

    if-ge v4, v5, :cond_1

    .line 22
    new-instance v5, Lcom/tw/music/c/a$a;

    invoke-direct {v5}, Lcom/tw/music/c/a$a;-><init>()V

    .line 23
    invoke-virtual {v3, v4}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lorg/json/JSONObject;

    invoke-virtual {v7, v6}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v5, v7}, Lcom/tw/music/c/a$a;->pb(Ljava/lang/String;)V

    .line 24
    invoke-virtual {v1}, Lcom/tw/music/c/a;->pd()Ljava/util/List;

    move-result-object v7

    invoke-interface {v7, v5}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    add-int/lit8 v4, v4, 0x1

    goto :goto_1

    :cond_1
    const/4 v3, 0x2

    .line 25
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    const-string v5, "backgroud"

    invoke-virtual {v4, v5}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-static {v0, v4}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v4

    invoke-virtual {v1, v4}, Lcom/tw/music/c/a;->j(Landroid/graphics/drawable/Drawable;)V

    .line 26
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    const-string v5, "album"

    invoke-virtual {v4, v5}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v4

    .line 27
    invoke-virtual {v4, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    invoke-virtual {v4, v6}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-static {v0, v4}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v4

    invoke-virtual {v1, v4}, Lcom/tw/music/c/a;->e(Landroid/graphics/drawable/Drawable;)V

    .line 28
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    const-string v5, "prev"

    invoke-virtual {v4, v5}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v4

    .line 29
    invoke-virtual {v4, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    invoke-virtual {v4, v6}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-static {v0, v4}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v4

    invoke-virtual {v1, v4}, Lcom/tw/music/c/a;->i(Landroid/graphics/drawable/Drawable;)V

    .line 30
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    const-string v5, "playPause"

    invoke-virtual {v4, v5}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v4

    .line 31
    invoke-virtual {v4, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lorg/json/JSONObject;

    invoke-virtual {v4, v6}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-static {v0, v4}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v4

    invoke-virtual {v1, v4}, Lcom/tw/music/c/a;->h(Landroid/graphics/drawable/Drawable;)V

    .line 32
    invoke-virtual {v2, v3}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lorg/json/JSONObject;

    const-string v3, "next"

    invoke-virtual {v2, v3}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v2

    .line 33
    invoke-virtual {v2, p0}, Lorg/json/JSONArray;->get(I)Ljava/lang/Object;

    move-result-object p0

    check-cast p0, Lorg/json/JSONObject;

    invoke-virtual {p0, v6}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object p0

    invoke-virtual {v1, p0}, Lcom/tw/music/c/a;->g(Landroid/graphics/drawable/Drawable;)V
    :try_end_1
    .catch Lorg/json/JSONException; {:try_start_1 .. :try_end_1} :catch_0

    return-object v1

    :catch_0
    move-exception p0

    .line 34
    invoke-virtual {p0}, Lorg/json/JSONException;->printStackTrace()V

    const/4 p0, 0x0

    return-object p0
.end method


# virtual methods
.method public e(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a;->album:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public f(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a;->Lm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public g(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a;->next:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public getAlbum()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->album:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public getNext()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->next:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public h(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a;->Sm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public i(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a;->prev:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public j(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a;->Pm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public nd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->Lm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public od()Ljava/util/List;
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/tw/music/c/a$a;",
            ">;"
        }
    .end annotation

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->Mm:Ljava/util/List;

    return-object p0
.end method

.method public pd()Ljava/util/List;
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/tw/music/c/a$a;",
            ">;"
        }
    .end annotation

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->Nm:Ljava/util/List;

    return-object p0
.end method

.method public qb(Ljava/lang/String;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/a;->Om:Ljava/lang/String;

    return-void
.end method

.method public qd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->Sm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public rd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->prev:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public sd()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/c/a;->Rm:I

    return p0
.end method

.method public td()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/c/a;->Qm:I

    return p0
.end method

.method public ud()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/a;->Pm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public wa(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/c/a;->Rm:I

    return-void
.end method

.method public xa(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/c/a;->Qm:I

    return-void
.end method

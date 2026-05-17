.class public Lcom/tw/music/c/b;
.super Ljava/lang/Object;
.source "MainPlugin.java"


# instance fields
.field private Tm:Landroid/graphics/drawable/Drawable;

.field private Um:Landroid/graphics/drawable/Drawable;

.field private Vm:Landroid/graphics/drawable/Drawable;

.field private Wm:Landroid/graphics/drawable/Drawable;

.field private Xm:Landroid/graphics/drawable/Drawable;

.field private Ym:Landroid/graphics/drawable/Drawable;

.field private Zm:Landroid/graphics/drawable/Drawable;

.field private _m:Landroid/graphics/drawable/Drawable;

.field private album:Landroid/graphics/drawable/Drawable;

.field private album_bg:Landroid/graphics/drawable/Drawable;

.field private collect:Landroid/graphics/drawable/Drawable;

.field private dn:Landroid/graphics/drawable/Drawable;

.field private fn:Landroid/graphics/drawable/Drawable;

.field private gn:Landroid/graphics/drawable/Drawable;

.field private hn:Landroid/graphics/drawable/Drawable;

.field private jn:Landroid/graphics/drawable/Drawable;

.field private kn:Landroid/graphics/drawable/Drawable;

.field private ln:Landroid/graphics/drawable/Drawable;

.field private mn:Landroid/graphics/drawable/Drawable;

.field private nn:Landroid/graphics/drawable/Drawable;

.field private pn:I

.field private qn:I

.field private repeat:Landroid/graphics/drawable/Drawable;

.field private rn:Landroid/graphics/drawable/Drawable;

.field private tab_btn_layout:Landroid/graphics/drawable/Drawable;

.field private tn:Landroid/graphics/drawable/Drawable;

.field private un:Landroid/graphics/drawable/Drawable;

.field private vn:Landroid/graphics/drawable/Drawable;

.field private wn:Landroid/graphics/drawable/Drawable;

.field private xn:Landroid/graphics/drawable/Drawable;

.field private yn:Landroid/graphics/drawable/Drawable;

.field private zn:Landroid/graphics/drawable/Drawable;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 2
    iput v0, p0, Lcom/tw/music/c/b;->pn:I

    .line 3
    iput v0, p0, Lcom/tw/music/c/b;->qn:I

    return-void
.end method

.method public static sb(Ljava/lang/String;)Lcom/tw/music/c/b;
    .locals 17

    move-object/from16 v0, p0

    const-string v1, "idName"

    const-string v2, "itemInfo"

    const-string v3, "background"

    .line 1
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "parsingViewsConfig:111 "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    const-string v5, "MainPlugin"

    invoke-static {v5, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    new-instance v4, Lcom/tw/music/c/b;

    invoke-direct {v4}, Lcom/tw/music/c/b;-><init>()V

    .line 3
    :try_start_0
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object v6

    invoke-virtual {v6}, Lcom/eckom/xtlibrary/b/i/k;->Kc()Landroid/content/Context;

    move-result-object v6

    .line 4
    new-instance v7, Lorg/json/JSONObject;

    invoke-direct {v7, v0}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    .line 5
    invoke-virtual {v7, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 6
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->o(Landroid/graphics/drawable/Drawable;)V

    const-string v0, "music_play_bg"

    .line 7
    invoke-virtual {v7, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 8
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->t(Landroid/graphics/drawable/Drawable;)V

    const-string v0, "setting_layout_bg"

    .line 9
    invoke-virtual {v7, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 10
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->G(Landroid/graphics/drawable/Drawable;)V

    const-string v0, "toggle_bg"

    .line 11
    invoke-virtual {v7, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 12
    new-instance v8, Ljava/lang/StringBuilder;

    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    const-string v9, "parsingViewsConfig: toggle_bg:"

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v8, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v8

    invoke-static {v5, v8}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 13
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->I(Landroid/graphics/drawable/Drawable;)V

    const-string v0, "music_freq_cylinder_start_color"

    .line 14
    invoke-virtual {v7, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 15
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->c(Landroid/content/Context;Ljava/lang/String;)I

    move-result v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->za(I)V

    const-string v0, "music_freq_cylinder_end_color"

    .line 16
    invoke-virtual {v7, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 17
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->c(Landroid/content/Context;Ljava/lang/String;)I

    move-result v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->ya(I)V

    .line 18
    new-instance v0, Lorg/json/JSONArray;

    invoke-virtual {v7, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-direct {v0, v7}, Lorg/json/JSONArray;-><init>(Ljava/lang/String;)V

    const/4 v8, 0x0

    .line 19
    :goto_0
    invoke-virtual {v0}, Lorg/json/JSONArray;->length()I

    move-result v9

    if-ge v8, v9, :cond_c

    .line 20
    invoke-virtual {v0, v8}, Lorg/json/JSONArray;->optJSONObject(I)Lorg/json/JSONObject;

    move-result-object v9

    .line 21
    invoke-virtual {v9, v1}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v10

    const-string v11, "tab_btn_layout"

    .line 22
    invoke-virtual {v10, v11}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v11
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const-string v12, "btn_img"

    if-eqz v11, :cond_6

    .line 23
    :try_start_1
    invoke-virtual {v9, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    .line 24
    invoke-static {v6, v11}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v11

    invoke-virtual {v4, v11}, Lcom/tw/music/c/b;->H(Landroid/graphics/drawable/Drawable;)V

    .line 25
    new-instance v11, Lorg/json/JSONArray;

    invoke-virtual {v9, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v13

    invoke-direct {v11, v13}, Lorg/json/JSONArray;-><init>(Ljava/lang/String;)V

    const/4 v13, 0x0

    .line 26
    :goto_1
    invoke-virtual {v11}, Lorg/json/JSONArray;->length()I

    move-result v14

    if-ge v13, v14, :cond_6

    .line 27
    invoke-virtual {v11, v13}, Lorg/json/JSONArray;->optJSONObject(I)Lorg/json/JSONObject;

    move-result-object v14

    .line 28
    invoke-virtual {v14, v1}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    .line 29
    invoke-virtual {v14, v12}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    move-object/from16 v16, v0

    const-string v0, "btn_background"

    .line 30
    invoke-virtual {v14, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    const-string v14, "play_list"

    .line 31
    invoke-virtual {v15, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v14

    if-eqz v14, :cond_0

    .line 32
    invoke-static {v6, v7}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->u(Landroid/graphics/drawable/Drawable;)V

    .line 33
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->v(Landroid/graphics/drawable/Drawable;)V

    :cond_0
    const-string v14, "prev"

    .line 34
    invoke-virtual {v15, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v14

    if-eqz v14, :cond_1

    .line 35
    invoke-static {v6, v7}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->y(Landroid/graphics/drawable/Drawable;)V

    .line 36
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->z(Landroid/graphics/drawable/Drawable;)V

    :cond_1
    const-string v14, "pp"

    .line 37
    invoke-virtual {v15, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v14

    if-eqz v14, :cond_2

    .line 38
    invoke-static {v6, v7}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->w(Landroid/graphics/drawable/Drawable;)V

    .line 39
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->x(Landroid/graphics/drawable/Drawable;)V

    :cond_2
    const-string v14, "next"

    .line 40
    invoke-virtual {v15, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v14

    if-eqz v14, :cond_3

    .line 41
    invoke-static {v6, v7}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->r(Landroid/graphics/drawable/Drawable;)V

    .line 42
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->s(Landroid/graphics/drawable/Drawable;)V

    :cond_3
    const-string v14, "eq"

    .line 43
    invoke-virtual {v15, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v14

    if-eqz v14, :cond_4

    .line 44
    invoke-static {v6, v7}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->p(Landroid/graphics/drawable/Drawable;)V

    .line 45
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v14

    invoke-virtual {v4, v14}, Lcom/tw/music/c/b;->q(Landroid/graphics/drawable/Drawable;)V

    :cond_4
    const-string v14, "iv_setting"

    .line 46
    invoke-virtual {v15, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v14

    if-eqz v14, :cond_5

    .line 47
    invoke-static {v6, v7}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v7

    invoke-virtual {v4, v7}, Lcom/tw/music/c/b;->A(Landroid/graphics/drawable/Drawable;)V

    .line 48
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->B(Landroid/graphics/drawable/Drawable;)V

    :cond_5
    add-int/lit8 v13, v13, 0x1

    move-object/from16 v0, v16

    goto/16 :goto_1

    :cond_6
    move-object/from16 v16, v0

    const-string v0, "album_layout"

    .line 49
    invoke-virtual {v10, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_7

    const-string v0, "album_bg"

    .line 50
    invoke-virtual {v9, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    const-string v7, "album"

    .line 51
    invoke-virtual {v9, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    .line 52
    new-instance v11, Ljava/lang/StringBuilder;

    invoke-direct {v11}, Ljava/lang/StringBuilder;-><init>()V

    const-string v13, "album_bg: "

    invoke-virtual {v11, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v11, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v11}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v11

    invoke-static {v5, v11}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 53
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->k(Landroid/graphics/drawable/Drawable;)V

    .line 54
    invoke-static {v6, v7}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->e(Landroid/graphics/drawable/Drawable;)V

    :cond_7
    const-string v0, "media_info"

    .line 55
    invoke-virtual {v10, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_8

    const-string v0, "artist_icon"

    .line 56
    invoke-virtual {v9, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 57
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->m(Landroid/graphics/drawable/Drawable;)V

    const-string v0, "album_icon"

    .line 58
    invoke-virtual {v9, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 59
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->l(Landroid/graphics/drawable/Drawable;)V

    :cond_8
    const-string v0, "iv_collect"

    .line 60
    invoke-virtual {v10, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_9

    .line 61
    invoke-virtual {v9, v12}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 62
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->n(Landroid/graphics/drawable/Drawable;)V

    :cond_9
    const-string v0, "repeat"

    .line 63
    invoke-virtual {v10, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_a

    .line 64
    invoke-virtual {v9, v12}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 65
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->C(Landroid/graphics/drawable/Drawable;)V

    :cond_a
    const-string v0, "media_seekbar_layout"

    .line 66
    invoke-virtual {v10, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_b

    .line 67
    invoke-virtual {v9, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 68
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->E(Landroid/graphics/drawable/Drawable;)V

    const-string v0, "progressDrawable"

    .line 69
    invoke-virtual {v9, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 70
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->D(Landroid/graphics/drawable/Drawable;)V

    const-string v0, "thumb"

    .line 71
    invoke-virtual {v9, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 72
    invoke-static {v6, v0}, Lcom/eckom/xtlibrary/b/i/h;->d(Landroid/content/Context;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {v4, v0}, Lcom/tw/music/c/b;->F(Landroid/graphics/drawable/Drawable;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    :cond_b
    add-int/lit8 v8, v8, 0x1

    move-object/from16 v0, v16

    goto/16 :goto_0

    :catch_0
    move-exception v0

    .line 73
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "parsingViewsConfig: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->getLocalizedMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v5, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_c
    return-object v4
.end method


# virtual methods
.method public A(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->jn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Ad()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->mn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public B(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->kn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Bd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->Xm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public C(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->repeat:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Cd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->hn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public D(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->un:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Dd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->Wm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public E(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->rn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Ed()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->gn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public F(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->tn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Fd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->_m:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public G(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->yn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Gd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->nn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public H(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->tab_btn_layout:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Hd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->xn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public I(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->zn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public Id()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->Ym:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Jd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->ln:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Kd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->Vm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Ld()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->fn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Md()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->Um:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Nd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->dn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Od()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->jn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Pd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->kn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Qd()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/c/b;->qn:I

    return p0
.end method

.method public Rd()I
    .locals 0

    .line 1
    iget p0, p0, Lcom/tw/music/c/b;->pn:I

    return p0
.end method

.method public Sd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->repeat:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Td()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->un:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Ud()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->rn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Vd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->tn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Wd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->yn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Xd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->tab_btn_layout:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public Yd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->zn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public e(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->album:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public k(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->album_bg:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public l(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->wn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public m(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->vn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public n(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->collect:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public o(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->Tm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public p(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->Xm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public q(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->hn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public r(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->Wm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public s(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->gn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public t(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->xn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public u(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->Ym:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public v(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->ln:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public vd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->album_bg:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public w(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->Vm:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public wd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->wn:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public x(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->fn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public xd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->collect:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public y(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->Um:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public ya(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/c/b;->qn:I

    return-void
.end method

.method public yd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->Tm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.method public z(Landroid/graphics/drawable/Drawable;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/tw/music/c/b;->dn:Landroid/graphics/drawable/Drawable;

    return-void
.end method

.method public za(I)V
    .locals 0

    .line 1
    iput p1, p0, Lcom/tw/music/c/b;->pn:I

    return-void
.end method

.method public zd()Landroid/graphics/drawable/Drawable;
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/tw/music/c/b;->Zm:Landroid/graphics/drawable/Drawable;

    return-object p0
.end method

.class public Lcom/tw/music/utils/b;
.super Ljava/lang/Object;
.source "SPUtil.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/tw/music/utils/b$a;
    }
.end annotation


# instance fields
.field private Cn:Landroid/content/SharedPreferences$Editor;

.field private sh:Landroid/content/SharedPreferences;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static getInstance()Lcom/tw/music/utils/b;
    .locals 1

    .line 1
    invoke-static {}, Lcom/tw/music/utils/b$a;->access$000()Lcom/tw/music/utils/b;

    move-result-object v0

    return-object v0
.end method


# virtual methods
.method public init(Landroid/content/Context;)V
    .locals 2

    const-string v0, "tw_config"

    const/4 v1, 0x0

    .line 1
    invoke-virtual {p1, v0, v1}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object p1

    iput-object p1, p0, Lcom/tw/music/utils/b;->sh:Landroid/content/SharedPreferences;

    .line 2
    iget-object p1, p0, Lcom/tw/music/utils/b;->sh:Landroid/content/SharedPreferences;

    invoke-interface {p1}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object p1

    iput-object p1, p0, Lcom/tw/music/utils/b;->Cn:Landroid/content/SharedPreferences$Editor;

    return-void
.end method

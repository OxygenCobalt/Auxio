.class Lcom/tw/music/i;
.super Ljava/lang/Object;
.source "MusicActivity.java"

# interfaces
.implements Lcom/tw/preference/TogglePreference$a;


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
    iput-object p1, p0, Lcom/tw/music/i;->this$0:Lcom/tw/music/MusicActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public a(Lcom/tw/preference/TogglePreference;Z)V
    .locals 2

    .line 1
    iget-object p1, p0, Lcom/tw/music/i;->this$0:Lcom/tw/music/MusicActivity;

    invoke-virtual {p1}, Landroid/app/Activity;->getApplicationContext()Landroid/content/Context;

    move-result-object p1

    const-string v0, "MusicActivity"

    const-string v1, "lrcorVisible"

    invoke-static {p1, v0, v1, p2}, Lcom/tw/music/utils/c;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Z)V

    .line 2
    iget-object p0, p0, Lcom/tw/music/i;->this$0:Lcom/tw/music/MusicActivity;

    invoke-static {p0, p2}, Lcom/tw/music/MusicActivity;->b(Lcom/tw/music/MusicActivity;Z)V

    return-void
.end method

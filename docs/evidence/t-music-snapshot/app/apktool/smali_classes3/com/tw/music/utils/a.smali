.class public Lcom/tw/music/utils/a;
.super Ljava/lang/Object;
.source "LogUtil.java"


# static fields
.field public static An:Z = true

.field private static TAG:Ljava/lang/String; = "LogUtils"


# direct methods
.method public static d(Ljava/lang/String;)V
    .locals 1

    .line 1
    sget-boolean v0, Lcom/tw/music/utils/a;->An:Z

    if-eqz v0, :cond_0

    .line 2
    sget-object v0, Lcom/tw/music/utils/a;->TAG:Ljava/lang/String;

    invoke-static {v0, p0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_0
    return-void
.end method

.method public static e(Ljava/lang/String;)V
    .locals 1

    .line 1
    sget-boolean v0, Lcom/tw/music/utils/a;->An:Z

    if-eqz v0, :cond_0

    .line 2
    sget-object v0, Lcom/tw/music/utils/a;->TAG:Ljava/lang/String;

    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_0
    return-void
.end method

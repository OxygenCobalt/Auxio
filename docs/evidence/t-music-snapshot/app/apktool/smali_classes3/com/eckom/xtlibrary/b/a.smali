.class Lcom/eckom/xtlibrary/b/a;
.super Ljava/lang/Object;
.source "XTManage.java"

# interfaces
.implements Landroid/content/ServiceConnection;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/b;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/b;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/b;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a;->this$0:Lcom/eckom/xtlibrary/b/b;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onServiceConnected(Landroid/content/ComponentName;Landroid/os/IBinder;)V
    .locals 1

    :try_start_0
    const-string p1, "XTManage"

    const-string v0, "=onServiceConnected="

    .line 1
    invoke-static {p1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a;->this$0:Lcom/eckom/xtlibrary/b/b;

    invoke-static {p2}, Lc/b/a/a/a/d$a;->asInterface(Landroid/os/IBinder;)Lc/b/a/a/a/d;

    move-result-object p2

    iput-object p2, p1, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    .line 3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a;->this$0:Lcom/eckom/xtlibrary/b/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/b;->b(Lcom/eckom/xtlibrary/b/b;)V
    :try_end_0
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 4
    invoke-virtual {p0}, Landroid/os/RemoteException;->printStackTrace()V

    :goto_0
    return-void
.end method

.method public onServiceDisconnected(Landroid/content/ComponentName;)V
    .locals 1

    :try_start_0
    const-string p1, "XTManage"

    const-string v0, "=onServiceDisconnected="

    .line 1
    invoke-static {p1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a;->this$0:Lcom/eckom/xtlibrary/b/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/b;->a(Lcom/eckom/xtlibrary/b/b;)V
    :try_end_0
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception p0

    .line 3
    invoke-virtual {p0}, Landroid/os/RemoteException;->printStackTrace()V

    :goto_0
    return-void
.end method

.class Lcom/eckom/xtlibrary/twproject/activity/b;
.super Landroid/content/BroadcastReceiver;
.source "XTActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/twproject/activity/XTActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    return-void
.end method


# virtual methods
.method public onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 3

    .line 1
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "notify_theme_change"

    .line 2
    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_4

    const-string p1, "/data/tw/theme/theme_config.json"

    .line 3
    sput-object p1, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    const/4 p1, 0x2

    const-string v0, "theme_type"

    .line 4
    invoke-virtual {p2, v0, p1}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result p2

    .line 5
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "onReceive: themeType:"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "XTActivity"

    invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 6
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object v0, v0, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    sget-object v2, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-static {v0, v2}, Lcom/eckom/xtlibrary/b/i/n;->a(Lcom/eckom/xtlibrary/b/i/g;Ljava/lang/String;)V

    .line 7
    iget-object v0, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ka()Lcom/eckom/xtlibrary/b/i/m;

    move-result-object v0

    .line 8
    iget-object v2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-virtual {v2, v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->d(Lcom/eckom/xtlibrary/b/i/m;)V

    const/4 v2, 0x1

    if-nez p2, :cond_1

    .line 9
    iget p1, v0, Lcom/eckom/xtlibrary/b/i/m;->em:I

    if-ne p1, v2, :cond_0

    .line 10
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ha()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/i/g;->_a(Ljava/lang/String;)V

    .line 11
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    sget-object p2, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/i/n;->a(Lcom/eckom/xtlibrary/b/i/g;Ljava/lang/String;)V

    .line 12
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "NOTIFY_THEME_CHANGE:THEME_TYPE_ALL 1: "

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v1, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 13
    new-instance p1, Ljava/io/File;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object p2

    invoke-direct {p1, p2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p1}, Ljava/io/File;->exists()Z

    move-result p1

    if-eqz p1, :cond_3

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/b/i/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/i/m;)Lcom/eckom/xtlibrary/b/i/m;

    .line 15
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p1

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/i/k;->e(Lcom/eckom/xtlibrary/b/i/m;)V

    .line 16
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {p1, v2}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)Z

    goto/16 :goto_0

    .line 17
    :cond_0
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ha()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/i/g;->fb(Ljava/lang/String;)V

    .line 18
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    sget-object p2, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/i/n;->a(Lcom/eckom/xtlibrary/b/i/g;Ljava/lang/String;)V

    .line 19
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "NOTIFY_THEME_CHANGE:THEME_TYPE_ALL 2: "

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v1, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 20
    new-instance p1, Ljava/io/File;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object p2

    invoke-direct {p1, p2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p1}, Ljava/io/File;->exists()Z

    move-result p1

    if-eqz p1, :cond_3

    .line 21
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/b/i/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/i/m;)Lcom/eckom/xtlibrary/b/i/m;

    .line 22
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p1

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/i/k;->e(Lcom/eckom/xtlibrary/b/i/m;)V

    .line 23
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {p1, v2}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)Z

    goto/16 :goto_0

    :cond_1
    if-ne p2, v2, :cond_2

    .line 24
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ha()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/i/g;->_a(Ljava/lang/String;)V

    .line 25
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    sget-object p2, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/i/n;->a(Lcom/eckom/xtlibrary/b/i/g;Ljava/lang/String;)V

    .line 26
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "NOTIFY_THEME_CHANGE:THEME_TYPE_LAUNCHER: "

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v1, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 27
    new-instance p1, Ljava/io/File;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object p2

    invoke-direct {p1, p2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p1}, Ljava/io/File;->exists()Z

    move-result p1

    if-eqz p1, :cond_3

    .line 28
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/g;->Dc()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/b/i/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/i/m;)Lcom/eckom/xtlibrary/b/i/m;

    .line 29
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p1

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/i/k;->e(Lcom/eckom/xtlibrary/b/i/m;)V

    .line 30
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {p1, v2}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)Z

    goto :goto_0

    :cond_2
    if-ne p2, p1, :cond_3

    .line 31
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->Ha()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p2, p1}, Lcom/eckom/xtlibrary/b/i/g;->fb(Ljava/lang/String;)V

    .line 32
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    sget-object p2, Lcom/eckom/xtlibrary/b/i/g;->Tl:Ljava/lang/String;

    invoke-static {p1, p2}, Lcom/eckom/xtlibrary/b/i/n;->a(Lcom/eckom/xtlibrary/b/i/g;Ljava/lang/String;)V

    .line 33
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "NOTIFY_THEME_CHANGE:THEME_TYPE_SUB: "

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object p2

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v1, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 34
    new-instance p1, Ljava/io/File;

    iget-object p2, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p2, p2, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p2}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object p2

    invoke-direct {p1, p2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p1}, Ljava/io/File;->exists()Z

    move-result p1

    if-eqz p1, :cond_3

    .line 35
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    iget-object p1, p1, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->db:Lcom/eckom/xtlibrary/b/i/g;

    invoke-virtual {p1}, Lcom/eckom/xtlibrary/b/i/g;->Ic()Ljava/lang/String;

    move-result-object p1

    invoke-static {p1, v0}, Lcom/eckom/xtlibrary/b/i/h;->a(Ljava/lang/String;Lcom/eckom/xtlibrary/b/i/m;)Lcom/eckom/xtlibrary/b/i/m;

    .line 36
    invoke-static {}, Lcom/eckom/xtlibrary/b/i/k;->get()Lcom/eckom/xtlibrary/b/i/k;

    move-result-object p1

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/b/i/k;->e(Lcom/eckom/xtlibrary/b/i/m;)V

    .line 37
    iget-object p1, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-static {p1, v2}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->a(Lcom/eckom/xtlibrary/twproject/activity/XTActivity;Z)Z

    .line 38
    :cond_3
    :goto_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/twproject/activity/b;->this$0:Lcom/eckom/xtlibrary/twproject/activity/XTActivity;

    invoke-virtual {p0, v0}, Lcom/eckom/xtlibrary/twproject/activity/XTActivity;->c(Lcom/eckom/xtlibrary/b/i/m;)V

    :cond_4
    return-void
.end method

.class Lcom/eckom/xtlibrary/b/a/a/a;
.super Landroid/content/BroadcastReceiver;
.source "BroadcastManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/a/a/b;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/a/a/b;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/a/a/b;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    return-void
.end method


# virtual methods
.method public onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 7

    .line 1
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v0

    const-string v1, "net.easyconn.bt.checkstatus"

    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    const/4 v1, 0x2

    const/4 v2, 0x0

    const/4 v3, 0x1

    if-eqz v0, :cond_3

    .line 2
    new-instance p2, Landroid/content/Intent;

    invoke-direct {p2}, Landroid/content/Intent;-><init>()V

    .line 3
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-ne v0, v1, :cond_0

    const-string v0, "net.easyconn.bt.connected"

    .line 4
    invoke-virtual {p2, v0}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    .line 5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->zg:Ljava/lang/String;

    invoke-virtual {p0, v3, v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(ZLjava/lang/String;)V

    goto :goto_0

    .line 6
    :cond_0
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->zg:Ljava/lang/String;

    invoke-virtual {v0, v2, v1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(ZLjava/lang/String;)V

    const-string v0, "net.easyconn.bt.opened"

    .line 7
    invoke-virtual {p2, v0}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    .line 8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->zg:Ljava/lang/String;

    if-eqz v0, :cond_1

    .line 9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->zg:Ljava/lang/String;

    const-string v1, "name"

    invoke-virtual {p2, v1, v0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 10
    :cond_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Ag:Ljava/lang/String;

    if-eqz v0, :cond_2

    .line 11
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Ag:Ljava/lang/String;

    const-string v0, "pin"

    invoke-virtual {p2, v0, p0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 12
    :cond_2
    :goto_0
    invoke-virtual {p1, p2}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    goto/16 :goto_3

    .line 13
    :cond_3
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "net.easyconn.a2dp.acquire"

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_4

    .line 14
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object p1

    if-eqz p1, :cond_1a

    .line 15
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object p0

    invoke-interface {p0, v3}, Lcom/eckom/xtlibrary/b/a/a/b$b;->w(Z)V

    goto/16 :goto_3

    .line 16
    :cond_4
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "net.easyconn.a2dp.release"

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_5

    .line 17
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object p1

    if-eqz p1, :cond_1a

    .line 18
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object p0

    invoke-interface {p0, v2}, Lcom/eckom/xtlibrary/b/a/a/b$b;->w(Z)V

    goto/16 :goto_3

    .line 19
    :cond_5
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "com.unisound.intent.action.GET_CONTACTS"

    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_6

    .line 20
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p1

    iget p1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-ne p1, v1, :cond_1a

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    if-eqz p1, :cond_1a

    .line 21
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->c(Lcom/eckom/xtlibrary/b/a/a/b;)Landroid/content/Context;

    move-result-object p1

    new-instance p2, Landroid/content/Intent;

    const-string v0, "com.unisound.intent.action.SYNC_CONTACTS"

    invoke-direct {p2, v0}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    const-string v0, "mac"

    invoke-virtual {p2, v0, p0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    move-result-object p0

    invoke-virtual {p1, p0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    goto/16 :goto_3

    .line 22
    :cond_6
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "com.tw.launcher.btmsg"

    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    const/4 v0, -0x1

    if-eqz p1, :cond_7

    const-string p1, "msg"

    .line 23
    invoke-virtual {p2, p1, v0}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result p1

    .line 24
    iget-object p2, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p2}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object p2

    if-eqz p2, :cond_1a

    .line 25
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object p0

    invoke-interface {p0, p1}, Lcom/eckom/xtlibrary/b/a/a/b$b;->t(I)V

    goto/16 :goto_3

    .line 26
    :cond_7
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v4, "com.unisound.intent.action.GET_DEVICE_INFO"

    invoke-virtual {v4, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_8

    .line 27
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p2

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/a/b/a;->zg:Ljava/lang/String;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Ag:Ljava/lang/String;

    invoke-virtual {p1, p2, p0}, Lcom/eckom/xtlibrary/b/a/a/b;->x(Ljava/lang/String;Ljava/lang/String;)V

    goto/16 :goto_3

    .line 28
    :cond_8
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v4, "com.unisound.intent.action.GET_PAIR_INFO"

    invoke-virtual {v4, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_9

    .line 29
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p2

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-virtual {p1, p2, p0}, Lcom/eckom/xtlibrary/b/a/a/b;->y(Ljava/lang/String;Ljava/lang/String;)V

    goto/16 :goto_3

    .line 30
    :cond_9
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v4, "com.unisound.intent.action.GET_CALL_INFO"

    invoke-virtual {v4, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_a

    .line 31
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p2

    iget p2, p2, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->pg:Ljava/lang/String;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->og:Ljava/lang/String;

    invoke-virtual {p1, p2, v0, p0}, Lcom/eckom/xtlibrary/b/a/a/b;->i(ILjava/lang/String;Ljava/lang/String;)V

    goto/16 :goto_3

    .line 32
    :cond_a
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v4, "com.unisound.intent.action.GET_ID3_INFO"

    invoke-virtual {v4, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_b

    .line 33
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1, v3}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;Z)Z

    .line 34
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p2

    iget-object p2, p2, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    iget-object v1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->Lg:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iget p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Kg:I

    invoke-virtual {p1, p2, v0, v1, p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Ljava/lang/String;Ljava/lang/String;II)V

    goto/16 :goto_3

    .line 35
    :cond_b
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v4, "com.unisound.intent.action.GET_BATTARY_INFO"

    invoke-virtual {v4, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_c

    .line 36
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p2

    iget p2, p2, Lcom/eckom/xtlibrary/b/a/b/a;->Vg:I

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iget p0, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Ug:I

    invoke-virtual {p1, p2, p0}, Lcom/eckom/xtlibrary/b/a/a/b;->i(II)V

    goto/16 :goto_3

    .line 37
    :cond_c
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v4, "com.unisound.intent.action.GET_BT_INFO"

    invoke-virtual {v4, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    const-string v4, "BroadcastManager"

    if-eqz p1, :cond_d

    .line 38
    :try_start_0
    new-instance p1, Landroid/content/Intent;

    const-string p2, "com.unisound.intent.action.SEND_BT_INFO"

    invoke-direct {p1, p2}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    .line 39
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->c(Lcom/eckom/xtlibrary/b/a/a/b;)Landroid/content/Context;

    move-result-object p0

    invoke-virtual {p0, p1}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto/16 :goto_3

    :catch_0
    move-exception p0

    .line 40
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    const-string p2, "YZS_SEND_BT_INFO: "

    invoke-virtual {p1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {p1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v4, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_3

    .line 41
    :cond_d
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v5, "com.zjinnova.zlink"

    invoke-virtual {v5, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_18

    const-string p1, "status"

    .line 42
    invoke-virtual {p2, p1}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    const-string v5, "phoneMode"

    .line 43
    invoke-virtual {p2, v5}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p2

    .line 44
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "status:status:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v6, " phoneMode:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v4, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 45
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_17

    .line 46
    invoke-virtual {p1}, Ljava/lang/String;->hashCode()I

    move-result v4

    const/4 v5, 0x4

    const/4 v6, 0x3

    sparse-switch v4, :sswitch_data_0

    goto :goto_1

    :sswitch_0
    const-string v4, "PHONE_CALL_OFF"

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_e

    move v0, v6

    goto :goto_1

    :sswitch_1
    const-string v4, "DISCONNECT"

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_e

    move v0, v3

    goto :goto_1

    :sswitch_2
    const-string v4, "PHONE_CALL_ON"

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_e

    move v0, v1

    goto :goto_1

    :sswitch_3
    const-string v4, "MAIN_PAGE_SHOW"

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_e

    move v0, v5

    goto :goto_1

    :sswitch_4
    const-string v4, "CONNECTED"

    invoke-virtual {p1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_e

    move v0, v2

    :cond_e
    :goto_1
    if-eqz v0, :cond_14

    if-eq v0, v3, :cond_11

    if-eq v0, v1, :cond_17

    if-eq v0, v6, :cond_17

    if-eq v0, v5, :cond_f

    goto/16 :goto_2

    .line 47
    :cond_f
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-boolean v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Wg:Z

    if-eqz v0, :cond_17

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/a/b/a;->hb()Z

    move-result v0

    if-nez v0, :cond_10

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/a/b/a;->gb()Z

    move-result v0

    if-eqz v0, :cond_17

    .line 48
    :cond_10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object v0

    if-eqz v0, :cond_17

    .line 49
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object v0

    invoke-interface {v0, v3}, Lcom/eckom/xtlibrary/b/a/a/b$b;->w(Z)V

    goto/16 :goto_2

    .line 50
    :cond_11
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iput-boolean v2, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Wg:Z

    .line 51
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/a/b/a;->hb()Z

    move-result v0

    if-nez v0, :cond_12

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/a/b/a;->gb()Z

    move-result v0

    if-eqz v0, :cond_13

    .line 52
    :cond_12
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object v0

    if-eqz v0, :cond_13

    .line 53
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object v0

    invoke-interface {v0, v2}, Lcom/eckom/xtlibrary/b/a/a/b$b;->w(Z)V

    .line 54
    :cond_13
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    const-string v1, ""

    iput-object v1, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Yg:Ljava/lang/String;

    goto :goto_2

    .line 55
    :cond_14
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iput-boolean v3, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Wg:Z

    .line 56
    invoke-static {p2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_15

    .line 57
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iput-object p2, v0, Lcom/eckom/xtlibrary/b/a/b/a;->Yg:Ljava/lang/String;

    .line 58
    :cond_15
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/a/b/a;->hb()Z

    move-result v0

    if-nez v0, :cond_16

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    invoke-virtual {v0}, Lcom/eckom/xtlibrary/b/a/b/a;->gb()Z

    move-result v0

    if-eqz v0, :cond_17

    .line 59
    :cond_16
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object v0

    if-eqz v0, :cond_17

    .line 60
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->b(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$b;

    move-result-object v0

    invoke-interface {v0, v3}, Lcom/eckom/xtlibrary/b/a/a/b$b;->w(Z)V

    .line 61
    :cond_17
    :goto_2
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/a/b;->d(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$a;

    move-result-object v0

    if-eqz v0, :cond_1a

    .line 62
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->d(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/a/b$a;

    move-result-object p0

    invoke-interface {p0, p1, p2}, Lcom/eckom/xtlibrary/b/a/a/b$a;->t(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_3

    .line 63
    :cond_18
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string v0, "action.hicar.onconnect"

    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_19

    .line 64
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iput-boolean v3, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Xg:Z

    goto :goto_3

    .line 65
    :cond_19
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object p1

    const-string p2, "action.hicar.ondisconnect"

    invoke-virtual {p2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_1a

    .line 66
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/a/a/a;->this$0:Lcom/eckom/xtlibrary/b/a/a/b;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Lcom/eckom/xtlibrary/b/a/a/b;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object p0

    iput-boolean v2, p0, Lcom/eckom/xtlibrary/b/a/b/a;->Xg:Z

    :cond_1a
    :goto_3
    return-void

    :sswitch_data_0
    .sparse-switch
        -0x7c6dfd17 -> :sswitch_4
        -0x6de4a859 -> :sswitch_3
        -0x1da2ca91 -> :sswitch_2
        0x3c87449c -> :sswitch_1
        0x694977bf -> :sswitch_0
    .end sparse-switch
.end method

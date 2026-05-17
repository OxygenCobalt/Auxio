.class Lcom/eckom/xtlibrary/b/a/d/c;
.super Ljava/lang/Object;
.source "BTModel.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/a/d/f;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/a/d/f;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/a/d/f;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 17

    move-object/from16 v1, p0

    move-object/from16 v2, p1

    const-string v3, "BTModel"

    const/4 v4, 0x1

    .line 1
    :try_start_0
    iget-object v5, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v5, v5, Landroid/tw/john/TWUtil$TWObject;

    if-eqz v5, :cond_0

    .line 2
    iget-object v5, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v5, Landroid/tw/john/TWUtil$TWObject;

    .line 3
    iget-object v7, v5, Landroid/tw/john/TWUtil$TWObject;->obj3:Ljava/lang/Object;

    check-cast v7, Ljava/lang/String;

    .line 4
    iget-object v5, v5, Landroid/tw/john/TWUtil$TWObject;->obj4:Ljava/lang/Object;

    check-cast v5, Ljava/lang/String;

    move-object v13, v7

    goto :goto_1

    .line 5
    :cond_0
    iget-object v5, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v5, v5, [B

    if-eqz v5, :cond_1

    .line 6
    new-instance v7, Ljava/lang/String;

    iget-object v5, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v5, [B

    invoke-direct {v7, v5}, Ljava/lang/String;-><init>([B)V

    goto :goto_0

    .line 7
    :cond_1
    iget-object v5, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    move-object v7, v5

    check-cast v7, Ljava/lang/String;

    :goto_0
    move-object v13, v7

    const/4 v5, 0x0

    .line 8
    :goto_1
    iget v7, v2, Landroid/os/Message;->what:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_7

    const/4 v8, 0x4

    const/4 v10, 0x2

    if-eq v7, v8, :cond_7c

    const/4 v8, 0x7

    const/4 v14, 0x0

    if-eq v7, v8, :cond_78

    const/16 v8, 0x17

    const-string v11, ""

    if-eq v7, v8, :cond_75

    const/16 v8, 0xff

    const/16 v12, 0x18

    const v15, 0xff06

    if-eq v7, v12, :cond_66

    const/16 v6, 0x2f

    if-eq v7, v6, :cond_63

    const/16 v6, 0x30

    if-eq v7, v6, :cond_62

    const/16 v6, 0x36

    if-eq v7, v6, :cond_61

    const/16 v6, 0x37

    const/16 v9, 0x8

    if-eq v7, v6, :cond_5c

    const-string v6, " "

    const/16 v12, 0x503

    sparse-switch v7, :sswitch_data_0

    packed-switch v7, :pswitch_data_0

    packed-switch v7, :pswitch_data_1

    goto/16 :goto_56

    .line 9
    :pswitch_0
    :try_start_1
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    if-eq v5, v9, :cond_88

    iget v5, v2, Landroid/os/Message;->arg1:I

    if-ne v5, v4, :cond_88

    .line 10
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v1

    invoke-virtual {v1, v4}, Lcom/eckom/xtlibrary/b/a/b/c;->w(Z)V

    goto/16 :goto_56

    .line 11
    :pswitch_1
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-boolean v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Og:Z

    if-eqz v5, :cond_88

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-boolean v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Pg:Z

    if-eqz v5, :cond_88

    .line 12
    invoke-static {}, Ljava/lang/System;->nanoTime()J

    move v5, v14

    .line 13
    :goto_2
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    move-result v6

    if-ge v5, v6, :cond_4

    move v6, v14

    .line 14
    :goto_3
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v7}, Ljava/util/ArrayList;->size()I

    move-result v7

    if-ge v6, v7, :cond_3

    .line 15
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v7, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v7}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v7

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v8

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v8, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v8}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v8

    invoke-static {v7, v8}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v7

    if-eqz v7, :cond_2

    .line 16
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v7, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->A(Z)V

    :cond_2
    add-int/lit8 v6, v6, 0x1

    goto :goto_3

    :cond_3
    add-int/lit8 v5, v5, 0x1

    goto :goto_2

    :cond_4
    move v5, v14

    .line 17
    :goto_4
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    move-result v6

    if-ge v5, v6, :cond_7

    move v6, v14

    .line 18
    :goto_5
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v7}, Ljava/util/ArrayList;->size()I

    move-result v7

    if-ge v6, v7, :cond_6

    .line 19
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v7, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v7}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v7

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v8

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v8, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v8}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v8

    invoke-static {v7, v8}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v7

    if-eqz v7, :cond_5

    .line 20
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v7, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->A(Z)V

    :cond_5
    add-int/lit8 v6, v6, 0x1

    goto :goto_5

    :cond_6
    add-int/lit8 v5, v5, 0x1

    goto :goto_4

    :cond_7
    move v5, v14

    .line 21
    :goto_6
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    move-result v6

    if-ge v5, v6, :cond_a

    move v6, v14

    .line 22
    :goto_7
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v7}, Ljava/util/ArrayList;->size()I

    move-result v7

    if-ge v6, v7, :cond_9

    .line 23
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v7, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v7}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v7

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v8

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v8, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v8}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v8

    invoke-static {v7, v8}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v7

    if-eqz v7, :cond_8

    .line 24
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v7, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-virtual {v7, v4}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->A(Z)V

    :cond_8
    add-int/lit8 v6, v6, 0x1

    goto :goto_7

    :cond_9
    add-int/lit8 v5, v5, 0x1

    goto :goto_6

    .line 25
    :cond_a
    invoke-static {}, Ljava/lang/System;->nanoTime()J

    .line 26
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_8
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 27
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v5}, Lcom/eckom/xtlibrary/b/a/d/g;->T()V

    goto :goto_8

    .line 28
    :pswitch_2
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iput-boolean v4, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Pg:Z

    .line 29
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v6, 0xff0a

    invoke-virtual {v5, v6}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 30
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_9
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_88

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 31
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-interface {v6, v7}, Lcom/eckom/xtlibrary/b/a/d/g;->a(Ljava/util/ArrayList;)V

    goto :goto_9

    .line 32
    :pswitch_3
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-boolean v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Mg:Z

    if-eqz v5, :cond_b

    .line 33
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->h(Lcom/eckom/xtlibrary/b/a/d/f;)V

    goto/16 :goto_56

    .line 34
    :cond_b
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->i(Lcom/eckom/xtlibrary/b/a/d/f;)V

    goto/16 :goto_56

    .line 35
    :pswitch_4
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v5

    if-eqz v5, :cond_88

    .line 36
    iget v5, v2, Landroid/os/Message;->arg1:I

    if-nez v5, :cond_f

    .line 37
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->d(Lcom/eckom/xtlibrary/b/a/d/f;)I

    move-result v5

    if-eqz v5, :cond_e

    if-eq v5, v4, :cond_d

    if-eq v5, v10, :cond_c

    goto :goto_a

    .line 38
    :cond_c
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->jh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    goto :goto_a

    .line 39
    :cond_d
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->hh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    goto :goto_a

    .line 40
    :cond_e
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->ih:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 41
    :goto_a
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v5

    const/16 v6, 0x43

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->d(Lcom/eckom/xtlibrary/b/a/d/f;)I

    move-result v1

    invoke-virtual {v5, v6, v1}, Landroid/tw/john/TWUtil;->write(II)I

    goto/16 :goto_56

    .line 42
    :cond_f
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v1

    const/16 v5, 0x1a

    invoke-virtual {v1, v5, v8}, Landroid/tw/john/TWUtil;->write(II)I

    goto/16 :goto_56

    .line 43
    :pswitch_5
    iget v5, v2, Landroid/os/Message;->arg1:I

    if-eqz v5, :cond_12

    if-eq v5, v4, :cond_10

    goto/16 :goto_56

    .line 44
    :cond_10
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iput-boolean v4, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Og:Z

    .line 45
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "MSG_CONTACT_RETURN:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    move-result v6

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v3, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 46
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v6, 0xff0a

    invoke-virtual {v5, v6}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 47
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_b
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_11

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 48
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v7}, Ljava/util/ArrayList;->size()I

    move-result v7

    invoke-interface {v6, v7}, Lcom/eckom/xtlibrary/b/a/d/g;->w(I)V

    goto :goto_b

    .line 49
    :cond_11
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->ub()V

    goto/16 :goto_56

    .line 50
    :cond_12
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_c
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 51
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v5}, Lcom/eckom/xtlibrary/b/a/d/g;->W()V

    goto :goto_c

    .line 52
    :pswitch_6
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v5}, Landroid/os/Handler;->obtainMessage()Landroid/os/Message;

    move-result-object v5

    .line 53
    iput v15, v5, Landroid/os/Message;->what:I

    .line 54
    iput v4, v5, Landroid/os/Message;->arg1:I

    .line 55
    iput v14, v5, Landroid/os/Message;->arg2:I

    .line 56
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v6, v15}, Landroid/os/Handler;->removeMessages(I)V

    .line 57
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const-wide/16 v7, 0xbb8

    invoke-virtual {v6, v5, v7, v8}, Landroid/os/Handler;->sendMessageDelayed(Landroid/os/Message;J)Z

    .line 58
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-boolean v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Mg:Z

    if-eqz v5, :cond_13

    .line 59
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->f(Lcom/eckom/xtlibrary/b/a/d/f;)V

    goto/16 :goto_56

    .line 60
    :cond_13
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->g(Lcom/eckom/xtlibrary/b/a/d/f;)V

    goto/16 :goto_56

    .line 61
    :pswitch_7
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    if-ne v5, v10, :cond_14

    .line 62
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Fg:I

    add-int/2addr v6, v4

    iput v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Fg:I

    .line 63
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v6, 0xff04

    const-wide/16 v7, 0x3e8

    invoke-virtual {v5, v6, v7, v8}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    .line 64
    :cond_14
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_d
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_88

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 65
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Fg:I

    invoke-interface {v6, v7}, Lcom/eckom/xtlibrary/b/a/d/g;->P(I)V

    goto :goto_d

    .line 66
    :pswitch_8
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    if-ne v5, v9, :cond_88

    .line 67
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v1

    invoke-virtual {v1, v14}, Lcom/eckom/xtlibrary/b/a/b/c;->w(Z)V

    goto/16 :goto_56

    .line 68
    :pswitch_9
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v6, v2, Landroid/os/Message;->arg2:I

    iput v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Kg:I

    .line 69
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v6, v2, Landroid/os/Message;->arg1:I

    const v7, 0x7fffffff

    and-int/2addr v6, v7

    iput v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Lg:I

    .line 70
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "RETURN_MS:totalTime\uff1a"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Kg:I

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v6, " currentTime:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Lg:I

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v3, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 71
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_e
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_15

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 72
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Lg:I

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v8

    iget v8, v8, Lcom/eckom/xtlibrary/b/a/b/a;->Kg:I

    invoke-interface {v6, v7, v8}, Lcom/eckom/xtlibrary/b/a/d/g;->e(II)V

    goto :goto_e

    .line 73
    :cond_15
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v5

    if-eqz v5, :cond_88

    .line 74
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v5

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v8

    iget v8, v8, Lcom/eckom/xtlibrary/b/a/b/a;->Lg:I

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->Kg:I

    invoke-virtual {v5, v6, v7, v8, v1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Ljava/lang/String;Ljava/lang/String;II)V

    goto/16 :goto_56

    .line 75
    :pswitch_a
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_f
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_88

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 76
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v7, v2, Landroid/os/Message;->arg1:I

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget v9, v2, Landroid/os/Message;->arg1:I

    invoke-static {v8, v9}, Lcom/eckom/xtlibrary/b/a/d/f;->d(Lcom/eckom/xtlibrary/b/a/d/f;I)Ljava/lang/String;

    move-result-object v8

    invoke-interface {v6, v7, v8}, Lcom/eckom/xtlibrary/b/a/d/g;->a(ILjava/lang/String;)V

    goto :goto_f

    .line 77
    :pswitch_b
    invoke-static {v13}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v7

    if-nez v7, :cond_1f

    invoke-static {v5}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v7

    if-eqz v7, :cond_16

    goto/16 :goto_19

    .line 78
    :cond_16
    invoke-virtual {v5, v14, v4}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v7

    .line 79
    invoke-virtual {v5, v4}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v15

    .line 80
    invoke-virtual {v7}, Ljava/lang/String;->hashCode()I

    move-result v8

    packed-switch v8, :pswitch_data_2

    goto :goto_10

    :pswitch_c
    const-string v8, "6"

    invoke-virtual {v7, v8}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_17

    move v7, v10

    goto :goto_11

    :pswitch_d
    const-string v8, "5"

    invoke-virtual {v7, v8}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_17

    move v7, v4

    goto :goto_11

    :pswitch_e
    const-string v8, "4"

    invoke-virtual {v7, v8}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_17

    move v7, v14

    goto :goto_11

    :cond_17
    :goto_10
    const/4 v7, -0x1

    :goto_11
    if-eqz v7, :cond_1c

    if-eq v7, v4, :cond_1a

    if-eq v7, v10, :cond_18

    goto/16 :goto_18

    .line 81
    :cond_18
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-boolean v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Zg:Z

    if-eqz v7, :cond_19

    .line 82
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v14, v7, Lcom/eckom/xtlibrary/b/a/b/a;->jh:Ljava/util/ArrayList;

    new-instance v12, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v10

    iget v7, v2, Landroid/os/Message;->arg2:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v11

    const/16 v16, 0x2

    move-object v7, v12

    move-object v8, v15

    move-object v9, v13

    move-object v4, v12

    move/from16 v12, v16

    invoke-direct/range {v7 .. v12}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V

    invoke-virtual {v14, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_12

    .line 83
    :cond_19
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->jh:Ljava/util/ArrayList;

    new-instance v12, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v10

    iget v7, v2, Landroid/os/Message;->arg2:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v11

    const/16 v16, 0x2

    move-object v7, v12

    move-object v8, v15

    move-object v9, v13

    move-object v14, v12

    move/from16 v12, v16

    invoke-direct/range {v7 .. v12}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V

    const/4 v7, 0x0

    invoke-virtual {v4, v7, v14}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    .line 84
    :goto_12
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_13
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_1e

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 85
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v15, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->p(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_13

    .line 86
    :cond_1a
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Zg:Z

    if-eqz v4, :cond_1b

    .line 87
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->hh:Ljava/util/ArrayList;

    new-instance v14, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v10

    iget v7, v2, Landroid/os/Message;->arg2:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v11

    const/4 v12, 0x0

    move-object v7, v14

    move-object v8, v15

    move-object v9, v13

    invoke-direct/range {v7 .. v12}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V

    invoke-virtual {v4, v14}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_14

    .line 88
    :cond_1b
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->hh:Ljava/util/ArrayList;

    new-instance v14, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v10

    iget v7, v2, Landroid/os/Message;->arg2:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v11

    const/4 v12, 0x0

    move-object v7, v14

    move-object v8, v15

    move-object v9, v13

    invoke-direct/range {v7 .. v12}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V

    const/4 v7, 0x0

    invoke-virtual {v4, v7, v14}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    .line 89
    :goto_14
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_15
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_1e

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 90
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v15, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->s(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_15

    .line 91
    :cond_1c
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Zg:Z

    if-eqz v4, :cond_1d

    .line 92
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ih:Ljava/util/ArrayList;

    new-instance v14, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v10

    iget v7, v2, Landroid/os/Message;->arg2:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v11

    const/4 v12, 0x1

    move-object v7, v14

    move-object v8, v15

    move-object v9, v13

    invoke-direct/range {v7 .. v12}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V

    invoke-virtual {v4, v14}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_16

    .line 93
    :cond_1d
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ih:Ljava/util/ArrayList;

    new-instance v14, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v10

    iget v7, v2, Landroid/os/Message;->arg2:I

    invoke-static {v7}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v11

    const/4 v12, 0x1

    move-object v7, v14

    move-object v8, v15

    move-object v9, v13

    invoke-direct/range {v7 .. v12}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V

    const/4 v7, 0x0

    invoke-virtual {v4, v7, v14}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    .line 94
    :goto_16
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_17
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_1e

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 95
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v15, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->m(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_17

    .line 96
    :cond_1e
    :goto_18
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "RETURN_CALL_LOG_PHONE_DATA: "

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, v2, Landroid/os/Message;->arg1:I

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, v2, Landroid/os/Message;->arg2:I

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_56

    :cond_1f
    :goto_19
    move v1, v14

    return v1

    .line 97
    :pswitch_f
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->d(Lcom/eckom/xtlibrary/b/a/d/f;)I

    move-result v4

    if-ge v4, v10, :cond_20

    .line 98
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v5, 0xff07

    invoke-virtual {v4, v5}, Landroid/os/Handler;->removeMessages(I)V

    .line 99
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v4}, Landroid/os/Handler;->obtainMessage()Landroid/os/Message;

    move-result-object v4

    const v5, 0xff07

    .line 100
    iput v5, v4, Landroid/os/Message;->what:I

    const/4 v5, 0x0

    .line 101
    iput v5, v4, Landroid/os/Message;->arg1:I

    .line 102
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->d(Lcom/eckom/xtlibrary/b/a/d/f;)I

    move-result v5

    iput v5, v4, Landroid/os/Message;->arg2:I

    .line 103
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v5, v4}, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z

    .line 104
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->e(Lcom/eckom/xtlibrary/b/a/d/f;)I

    goto/16 :goto_56

    .line 105
    :cond_20
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v4, 0x0

    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;I)I

    goto/16 :goto_56

    .line 106
    :sswitch_0
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_1a
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 107
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/g;->N(I)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_7

    goto :goto_1a

    .line 108
    :sswitch_1
    :try_start_2
    iget-object v4, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v4, [B

    .line 109
    iget v5, v2, Landroid/os/Message;->arg1:I

    if-ne v5, v8, :cond_88

    .line 110
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v5, 0x0

    aget-byte v4, v4, v5

    and-int/2addr v4, v8

    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/b/a/d/f;->c(Lcom/eckom/xtlibrary/b/a/d/f;I)I
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto/16 :goto_56

    :catch_0
    move-exception v0

    move-object v1, v0

    .line 111
    :try_start_3
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "handleMessage: 0x0510:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_56

    .line 112
    :sswitch_2
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "handleMessage: 0x0301:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v5, v2, Landroid/os/Message;->arg1:I

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 113
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    iget v5, v2, Landroid/os/Message;->arg1:I

    if-eq v4, v5, :cond_88

    .line 114
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    if-ne v4, v9, :cond_21

    .line 115
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Gg:Z

    if-eqz v4, :cond_22

    .line 116
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v5, 0x14

    const/4 v6, 0x0

    invoke-virtual {v4, v5, v6}, Landroid/tw/john/TWUtil;->write(II)I

    goto :goto_1b

    .line 117
    :cond_21
    iget v4, v2, Landroid/os/Message;->arg1:I

    if-ne v4, v9, :cond_22

    .line 118
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v5, 0x14

    const/4 v6, 0x0

    invoke-virtual {v4, v5, v6}, Landroid/tw/john/TWUtil;->write(II)I

    .line 119
    :cond_22
    :goto_1b
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v5, v2, Landroid/os/Message;->arg1:I

    iput v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    .line 120
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v5, 0x2e

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->mSource:I

    if-ne v6, v9, :cond_23

    const/4 v6, 0x1

    goto :goto_1c

    :cond_23
    const/4 v6, 0x0

    :goto_1c
    invoke-virtual {v4, v5, v6}, Landroid/tw/john/TWUtil;->write(II)I

    .line 121
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_1d
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 122
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/g;->j(I)V

    goto :goto_1d

    .line 123
    :sswitch_3
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_1e
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 124
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    const/high16 v6, -0x80000000

    and-int/2addr v5, v6

    if-ne v5, v6, :cond_24

    const/4 v5, 0x1

    goto :goto_1f

    :cond_24
    const/4 v5, 0x0

    :goto_1f
    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/g;->f(Z)V

    goto :goto_1e

    .line 125
    :sswitch_4
    iget v4, v2, Landroid/os/Message;->arg2:I

    .line 126
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "handleMessage: 0x0201:keyCode:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v3, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const/16 v5, 0x9

    if-eq v4, v5, :cond_34

    const/16 v5, 0xa

    if-eq v4, v5, :cond_33

    const/16 v5, 0x18

    if-eq v4, v5, :cond_2b

    const/16 v5, 0x19

    if-eq v4, v5, :cond_2b

    const/16 v5, 0x21

    if-eq v4, v5, :cond_2a

    const/16 v5, 0x3f

    if-eq v4, v5, :cond_2a

    const/16 v5, 0x5a

    if-eq v4, v5, :cond_25

    packed-switch v4, :pswitch_data_3

    goto/16 :goto_23

    .line 127
    :pswitch_10
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->wg:Z

    goto/16 :goto_23

    .line 128
    :pswitch_11
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->wg:Z

    goto/16 :goto_23

    .line 129
    :cond_25
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    invoke-virtual {v4}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v4

    const-string v5, "SWC_VOICE_MODE"

    const/4 v6, 0x0

    invoke-static {v4, v5, v6}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v4

    if-nez v4, :cond_27

    .line 130
    iget v4, v2, Landroid/os/Message;->arg1:I

    if-ne v4, v10, :cond_29

    .line 131
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Wg:Z

    if-nez v4, :cond_26

    goto :goto_20

    .line 132
    :cond_26
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Xg:Z

    if-nez v4, :cond_29

    goto :goto_20

    .line 133
    :cond_27
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/4 v5, 0x1

    if-ne v4, v5, :cond_29

    .line 134
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Wg:Z

    if-nez v4, :cond_28

    :goto_20
    const/4 v4, 0x1

    goto :goto_21

    .line 135
    :cond_28
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Xg:Z

    if-nez v4, :cond_29

    goto :goto_20

    :cond_29
    const/4 v4, 0x0

    .line 136
    :goto_21
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "0x0201:0x5a:needWakeUp phone voice:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v3, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-eqz v4, :cond_35

    .line 137
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->yb()V

    goto/16 :goto_23

    .line 138
    :cond_2a
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->wg:Z

    goto/16 :goto_23

    .line 139
    :cond_2b
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-ne v5, v10, :cond_30

    .line 140
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    const/4 v6, 0x1

    if-eq v5, v6, :cond_2e

    if-eq v5, v10, :cond_2c

    const/4 v6, 0x3

    if-eq v5, v6, :cond_2c

    const/4 v6, 0x4

    if-eq v5, v6, :cond_2c

    goto/16 :goto_23

    :cond_2c
    const/16 v5, 0x18

    if-ne v4, v5, :cond_2d

    .line 141
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/4 v5, 0x1

    if-eq v4, v5, :cond_2d

    .line 142
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const v6, 0x8201

    const/16 v7, 0x2f

    invoke-virtual {v4, v6, v5, v7}, Landroid/tw/john/TWUtil;->write(III)I

    goto/16 :goto_23

    .line 143
    :cond_2d
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v5, 0xa

    const/4 v6, 0x0

    invoke-virtual {v4, v5, v6}, Landroid/tw/john/TWUtil;->write(II)I

    goto :goto_23

    :cond_2e
    const/16 v5, 0x18

    if-ne v4, v5, :cond_2f

    .line 144
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/4 v5, 0x1

    if-ne v4, v5, :cond_2f

    .line 145
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v6, 0xa

    invoke-virtual {v4, v6, v5}, Landroid/tw/john/TWUtil;->write(II)I

    goto :goto_23

    .line 146
    :cond_2f
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v5, 0xa

    invoke-virtual {v4, v5, v10}, Landroid/tw/john/TWUtil;->write(II)I

    goto :goto_23

    .line 147
    :cond_30
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-boolean v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Wg:Z

    if-eqz v5, :cond_32

    const/16 v5, 0x18

    if-ne v4, v5, :cond_32

    .line 148
    iget v5, v2, Landroid/os/Message;->arg1:I

    const/4 v6, 0x1

    if-ne v5, v6, :cond_31

    .line 149
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v6, 0x5

    invoke-static {v5, v6}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;I)V

    goto :goto_22

    .line 150
    :cond_31
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v6, 0x6

    invoke-static {v5, v6}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;I)V

    .line 151
    :cond_32
    :goto_22
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-boolean v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Wg:Z

    if-eqz v5, :cond_35

    const/16 v5, 0x19

    if-ne v4, v5, :cond_35

    .line 152
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v5, 0x6

    invoke-static {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;I)V

    goto :goto_23

    .line 153
    :cond_33
    :pswitch_12
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->wg:Z

    goto :goto_23

    .line 154
    :cond_34
    :pswitch_13
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->wg:Z

    .line 155
    :cond_35
    :goto_23
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_24
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 156
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg2:I

    iget v6, v2, Landroid/os/Message;->arg1:I

    const/4 v7, 0x1

    if-eq v6, v7, :cond_36

    const/4 v6, 0x1

    goto :goto_25

    :cond_36
    const/4 v6, 0x0

    :goto_25
    invoke-interface {v4, v5, v6}, Lcom/eckom/xtlibrary/b/a/d/g;->b(IZ)V

    goto :goto_24

    .line 157
    :sswitch_5
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Tc()Ljava/lang/String;

    move-result-object v4

    const-string v5, "GT"

    invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v4

    if-eqz v4, :cond_37

    .line 158
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v5, 0x1

    iput-boolean v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Rg:Z

    goto :goto_26

    .line 159
    :cond_37
    iget v4, v2, Landroid/os/Message;->arg1:I

    and-int/lit8 v4, v4, 0x6

    const/4 v5, 0x6

    if-ne v4, v5, :cond_39

    .line 160
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Zc()Z

    move-result v4

    if-nez v4, :cond_38

    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->ad()Z

    move-result v4

    if-eqz v4, :cond_39

    .line 161
    :cond_38
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v5, 0x1

    iput-boolean v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Rg:Z

    .line 162
    :cond_39
    :goto_26
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/high16 v5, 0x10000

    and-int/2addr v4, v5

    if-ne v4, v5, :cond_3a

    const/4 v4, 0x1

    goto :goto_27

    :cond_3a
    const/4 v4, 0x0

    .line 163
    :goto_27
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_28
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 164
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v5, v4}, Lcom/eckom/xtlibrary/b/a/d/g;->q(Z)V
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_7

    goto :goto_28

    .line 165
    :sswitch_6
    :try_start_4
    iget-object v4, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v4, [B

    .line 166
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "0x010b: "

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-static {v4}, Ljava/util/Arrays;->toString([B)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v3, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 167
    array-length v5, v4

    const/16 v6, 0x14

    if-le v5, v6, :cond_3b

    const/16 v5, 0x14

    .line 168
    aget-byte v5, v4, v5

    if-lez v5, :cond_3b

    .line 169
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    const/4 v6, 0x1

    iput-boolean v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Sg:Z

    .line 170
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    const/16 v7, 0x14

    aget-byte v4, v4, v7

    sub-int/2addr v4, v6

    iput v4, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Tg:I

    .line 171
    :cond_3b
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->_c()Z

    move-result v4

    if-nez v4, :cond_3c

    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Zc()Z

    move-result v4

    if-eqz v4, :cond_88

    .line 172
    :cond_3c
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v5, 0x1

    iput-boolean v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Sg:Z

    .line 173
    invoke-static {}, Lcom/eckom/xtlibrary/b/j/b;->Rc()Ljava/lang/String;

    move-result-object v4

    .line 174
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    invoke-static {v4}, Ljava/lang/Integer;->getInteger(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/Integer;->intValue()I

    move-result v5

    iput v5, v1, Lcom/eckom/xtlibrary/b/a/b/a;->Tg:I

    const-string v1, "mxy"

    .line 175
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "010b mic:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v1, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_1

    goto/16 :goto_56

    :catch_1
    move-exception v0

    move-object v1, v0

    .line 176
    :try_start_5
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "0x010b Error : "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_56

    .line 177
    :sswitch_7
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-ne v4, v10, :cond_88

    .line 178
    iget v4, v2, Landroid/os/Message;->arg1:I

    shl-int/lit8 v4, v4, 0x4

    iget v5, v2, Landroid/os/Message;->arg2:I

    or-int/2addr v4, v5

    .line 179
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v5

    const/16 v6, 0x10

    invoke-virtual {v5, v12, v6, v4}, Landroid/tw/john/TWUtil;->write(III)I

    .line 180
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v5, v2, Landroid/os/Message;->arg2:I

    iput v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Ug:I

    .line 181
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    const-string v5, "TABLE_BT"

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    const-string v6, "BATTERY_LEVEL"

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Ug:I

    invoke-static {v4, v5, v6, v7}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)V

    .line 182
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_29
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 183
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    iget v6, v2, Landroid/os/Message;->arg2:I

    invoke-interface {v4, v5, v6}, Lcom/eckom/xtlibrary/b/a/d/g;->i(II)V

    goto :goto_29

    .line 184
    :sswitch_8
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "RETURN_SPK_GAIN: "

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, v2, Landroid/os/Message;->arg1:I

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, v2, Landroid/os/Message;->arg2:I

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_56

    .line 185
    :sswitch_9
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "RETURN_MIC_GAIN: "

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, v2, Landroid/os/Message;->arg1:I

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, v2, Landroid/os/Message;->arg2:I

    invoke-virtual {v1, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_56

    .line 186
    :sswitch_a
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_2a
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_3d

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 187
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v6, v7, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->b(ILjava/lang/String;Ljava/lang/String;)V

    goto :goto_2a

    .line 188
    :cond_3d
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    if-eqz v4, :cond_88

    .line 189
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v1

    iget v4, v2, Landroid/os/Message;->arg1:I

    invoke-virtual {v1, v4, v5, v13}, Lcom/eckom/xtlibrary/b/a/a/b;->h(ILjava/lang/String;Ljava/lang/String;)V

    goto/16 :goto_56

    .line 190
    :sswitch_b
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_2b
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 191
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    const/4 v6, 0x1

    add-int/2addr v5, v6

    const/16 v6, 0xa

    mul-int/2addr v5, v6

    iget v7, v2, Landroid/os/Message;->arg1:I

    const/16 v8, 0x9

    if-ge v7, v8, :cond_3e

    const/4 v7, 0x0

    goto :goto_2c

    :cond_3e
    const/4 v7, 0x1

    :goto_2c
    invoke-interface {v4, v5, v7}, Lcom/eckom/xtlibrary/b/a/d/g;->a(IZ)V

    goto :goto_2b

    .line 192
    :sswitch_c
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v13, v4, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    .line 193
    invoke-static {v13}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_43

    .line 194
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    const-string v5, "CQ"

    invoke-virtual {v4, v5}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_7

    if-eqz v4, :cond_41

    .line 195
    :try_start_6
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    const-string v5, "CQ131"

    invoke-virtual {v4, v5}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_3f

    .line 196
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v5, 0x1

    iput-boolean v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Rg:Z

    .line 197
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-boolean v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Zg:Z

    :cond_3f
    const-string v4, "_"

    .line 198
    invoke-virtual {v13, v4}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v4

    .line 199
    aget-object v4, v4, v10

    const-string v5, "\\("

    .line 200
    invoke-virtual {v4, v5}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v4

    .line 201
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    const/4 v6, 0x0

    aget-object v7, v4, v6

    iput-object v7, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Bg:Ljava/lang/String;

    const-string v5, "yyyy/MM/dd:HH:mm:ss"

    .line 202
    aget-object v4, v4, v6

    const-string v6, "2021/06/02:00:00:00"

    invoke-static {v5, v4, v6}, Lcom/eckom/xtlibrary/b/j/a;->g(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_40

    .line 203
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v5, 0x1

    invoke-static {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;Z)Z

    .line 204
    :cond_40
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "RETURN_VERSION CQ: mVersionName:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v5, " mVersionDate:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Bg:Ljava/lang/String;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_2

    goto/16 :goto_2d

    :catch_2
    move-exception v0

    move-object v4, v0

    .line 205
    :try_start_7
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "RETURN_VERSION: "

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_2d

    .line 206
    :cond_41
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    const-string v5, "FD"

    invoke-virtual {v4, v5}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_7

    if-eqz v4, :cond_42

    goto :goto_2d

    :cond_42
    :try_start_8
    const-string v4, "_"

    .line 207
    invoke-virtual {v13, v4}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v4

    .line 208
    aget-object v4, v4, v10

    .line 209
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iput-object v4, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Bg:Ljava/lang/String;

    .line 210
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "RETURN_VERSION GK: mVersionName:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->mVersionName:Ljava/lang/String;

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v6, " mVersionDate:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Bg:Ljava/lang/String;

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v3, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const-string v5, "yyyy/MM/dd:HH:mm:ss"

    const-string v6, "2021/05/26:00:00:00"

    .line 211
    invoke-static {v5, v4, v6}, Lcom/eckom/xtlibrary/b/j/a;->g(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_43

    .line 212
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v5, 0x1

    invoke-static {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;Z)Z
    :try_end_8
    .catch Ljava/lang/Exception; {:try_start_8 .. :try_end_8} :catch_3

    goto :goto_2d

    :catch_3
    move-exception v0

    move-object v4, v0

    .line 213
    :try_start_9
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "RETURN_VERSION: "

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 214
    :cond_43
    :goto_2d
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->q(Lcom/eckom/xtlibrary/b/a/d/f;)Z

    move-result v4

    if-eqz v4, :cond_88

    .line 215
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_2e
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_44

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 216
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->ja(Ljava/lang/String;)V

    goto :goto_2e

    .line 217
    :cond_44
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    const/4 v4, 0x0

    invoke-static {v1, v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;Z)Z

    goto/16 :goto_56

    .line 218
    :sswitch_d
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_2f
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 219
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    iget v6, v2, Landroid/os/Message;->arg2:I

    invoke-interface {v4, v5, v6}, Lcom/eckom/xtlibrary/b/a/d/g;->f(II)V
    :try_end_9
    .catch Ljava/lang/Exception; {:try_start_9 .. :try_end_9} :catch_7

    goto :goto_2f

    .line 220
    :sswitch_e
    :try_start_a
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v5, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v5, Ljava/lang/String;

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Ag:Ljava/lang/String;
    :try_end_a
    .catch Ljava/lang/ClassCastException; {:try_start_a .. :try_end_a} :catch_4
    .catch Ljava/lang/Exception; {:try_start_a .. :try_end_a} :catch_7

    goto :goto_30

    :catch_4
    move-exception v0

    move-object v4, v0

    .line 221
    :try_start_b
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v5, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/ClassCastException;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 222
    :goto_30
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_31
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 223
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->ka(Ljava/lang/String;)V
    :try_end_b
    .catch Ljava/lang/Exception; {:try_start_b .. :try_end_b} :catch_7

    goto :goto_31

    .line 224
    :sswitch_f
    :try_start_c
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v5, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v5, Ljava/lang/String;

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->zg:Ljava/lang/String;
    :try_end_c
    .catch Ljava/lang/ClassCastException; {:try_start_c .. :try_end_c} :catch_5
    .catch Ljava/lang/Exception; {:try_start_c .. :try_end_c} :catch_7

    goto :goto_32

    :catch_5
    move-exception v0

    move-object v4, v0

    .line 225
    :try_start_d
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v5, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/ClassCastException;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 226
    :goto_32
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_33
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 227
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->ga(Ljava/lang/String;)V

    goto :goto_33

    .line 228
    :sswitch_10
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_34
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 229
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v6, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v6, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->e(ILjava/lang/String;Ljava/lang/String;)V

    goto :goto_34

    .line 230
    :sswitch_11
    iget v4, v2, Landroid/os/Message;->arg1:I

    const v6, 0xffffff

    and-int/2addr v4, v6

    if-nez v4, :cond_45

    .line 231
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->hh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 232
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ih:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 233
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->jh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 234
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_35
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_45

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 235
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v6}, Lcom/eckom/xtlibrary/b/a/d/g;->I()V

    goto :goto_35

    .line 236
    :cond_45
    invoke-static {v13}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-eqz v4, :cond_46

    const/4 v4, 0x0

    return v4

    .line 237
    :cond_46
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/16 v6, 0x18

    shr-int/2addr v4, v6

    and-int/2addr v4, v8

    and-int/lit8 v6, v4, 0x1

    const/4 v7, 0x1

    if-ne v6, v7, :cond_48

    and-int/2addr v4, v10

    if-ne v4, v10, :cond_47

    .line 238
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->hh:Ljava/util/ArrayList;

    new-instance v6, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    const/4 v7, 0x0

    invoke-direct {v6, v5, v13, v7}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;I)V

    invoke-virtual {v4, v7, v6}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    .line 239
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_36
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 240
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->s(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_36

    .line 241
    :cond_47
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->jh:Ljava/util/ArrayList;

    new-instance v6, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    invoke-direct {v6, v5, v13, v10}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;I)V

    const/4 v7, 0x0

    invoke-virtual {v4, v7, v6}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    .line 242
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_37
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 243
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->p(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_37

    .line 244
    :cond_48
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ih:Ljava/util/ArrayList;

    new-instance v6, Lcom/eckom/xtlibrary/twproject/bt/bean/c;

    const/4 v7, 0x1

    invoke-direct {v6, v5, v13, v7}, Lcom/eckom/xtlibrary/twproject/bt/bean/c;-><init>(Ljava/lang/String;Ljava/lang/String;I)V

    const/4 v7, 0x0

    invoke-virtual {v4, v7, v6}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    .line 245
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_38
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 246
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->m(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_38

    .line 247
    :sswitch_12
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v5, v2, Landroid/os/Message;->arg1:I

    if-eqz v5, :cond_49

    const/4 v5, 0x1

    goto :goto_39

    :cond_49
    const/4 v5, 0x0

    :goto_39
    iput-boolean v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Gg:Z

    .line 248
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    if-eqz v4, :cond_4a

    .line 249
    new-instance v4, Landroid/os/Bundle;

    invoke-direct {v4}, Landroid/os/Bundle;-><init>()V

    const-string v5, "dateType"

    const-string v6, "send"

    .line 250
    invoke-virtual {v4, v5, v6}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string v5, "action"

    const-string v6, "com.tw.bt.av.play"

    .line 251
    invoke-virtual {v4, v5, v6}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string v5, "playState"

    .line 252
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-boolean v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Gg:Z

    invoke-virtual {v4, v5, v6}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 253
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    invoke-interface {v5, v4}, Lc/b/a/a/a/d;->a(Landroid/os/Bundle;)V

    .line 254
    :cond_4a
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const v5, 0x9f00

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-boolean v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Gg:Z

    if-eqz v6, :cond_4b

    const/4 v6, 0x1

    goto :goto_3a

    :cond_4b
    const/4 v6, 0x0

    :goto_3a
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    invoke-virtual {v4, v5, v9, v6, v7}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    .line 255
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_3b
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 256
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    if-eqz v5, :cond_4c

    const/4 v5, 0x1

    goto :goto_3c

    :cond_4c
    const/4 v5, 0x0

    :goto_3c
    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/g;->j(Z)V

    goto :goto_3b

    .line 257
    :sswitch_13
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_3d
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 258
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/g;->Q(I)V

    goto :goto_3d

    .line 259
    :sswitch_14
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v6, v2, Landroid/os/Message;->arg1:I

    iput v6, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    .line 260
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    if-eqz v4, :cond_4d

    if-eqz v13, :cond_4d

    .line 261
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->pg:Ljava/lang/String;

    .line 262
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v13, v4, Lcom/eckom/xtlibrary/b/a/b/a;->og:Ljava/lang/String;

    goto :goto_3e

    .line 263
    :cond_4d
    iget v4, v2, Landroid/os/Message;->arg1:I

    if-nez v4, :cond_4e

    .line 264
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v6, 0x0

    iput-object v6, v4, Lcom/eckom/xtlibrary/b/a/b/a;->pg:Ljava/lang/String;

    .line 265
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v6, v4, Lcom/eckom/xtlibrary/b/a/b/a;->og:Ljava/lang/String;

    .line 266
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->ub()V

    .line 267
    :cond_4e
    :goto_3e
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    if-eqz v4, :cond_53

    const/4 v6, 0x1

    if-eq v4, v6, :cond_52

    if-eq v4, v10, :cond_51

    const/4 v6, 0x3

    if-eq v4, v6, :cond_50

    const/4 v6, 0x4

    if-eq v4, v6, :cond_4f

    goto/16 :goto_3f

    .line 268
    :cond_4f
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Dg:I

    const/4 v6, -0x1

    if-ne v4, v6, :cond_56

    .line 269
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Eg:I

    iput v6, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Dg:I

    goto/16 :goto_3f

    .line 270
    :cond_50
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    invoke-virtual {v4, v12, v10}, Landroid/tw/john/TWUtil;->write(II)I

    goto/16 :goto_3f

    .line 271
    :cond_51
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/4 v6, 0x3

    invoke-virtual {v4, v12, v6}, Landroid/tw/john/TWUtil;->write(II)I

    .line 272
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v6, 0xff04

    invoke-virtual {v4, v6}, Landroid/os/Handler;->removeMessages(I)V

    .line 273
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v6, 0xff04

    const-wide/16 v7, 0x1f4

    invoke-virtual {v4, v6, v7, v8}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    goto :goto_3f

    .line 274
    :cond_52
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v13, v4, Lcom/eckom/xtlibrary/b/a/b/a;->og:Ljava/lang/String;

    .line 275
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->pg:Ljava/lang/String;

    .line 276
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v6, 0x0

    iput v6, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Fg:I

    .line 277
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/4 v6, 0x1

    invoke-virtual {v4, v12, v6}, Landroid/tw/john/TWUtil;->write(II)I

    goto :goto_3f

    .line 278
    :cond_53
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Dg:I

    const/4 v6, -0x1

    if-eq v4, v6, :cond_54

    .line 279
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput v6, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Dg:I

    .line 280
    :cond_54
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    if-eqz v4, :cond_55

    .line 281
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result v4

    if-eqz v4, :cond_55

    .line 282
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->pause()V

    .line 283
    :cond_55
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v6, 0x0

    iput v6, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Fg:I

    .line 284
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    invoke-virtual {v4, v12, v6}, Landroid/tw/john/TWUtil;->write(II)I

    :cond_56
    :goto_3f
    const-string v4, "++++"

    .line 285
    invoke-virtual {v4, v13}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_57

    .line 286
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->l(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/h/d;

    move-result-object v4

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/h/d;->show()V

    goto :goto_41

    .line 287
    :cond_57
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_40
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_58

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 288
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v6, v7, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->f(ILjava/lang/String;Ljava/lang/String;)V

    goto :goto_40

    .line 289
    :cond_58
    :goto_41
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    if-eqz v4, :cond_59

    .line 290
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v1

    iget v4, v2, Landroid/os/Message;->arg1:I

    invoke-virtual {v1, v4, v5, v13}, Lcom/eckom/xtlibrary/b/a/a/b;->i(ILjava/lang/String;Ljava/lang/String;)V

    .line 291
    :cond_59
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    if-eqz v1, :cond_88

    .line 292
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget v4, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v1, v4, v5, v13}, Lc/b/a/a/a/d;->d(ILjava/lang/String;Ljava/lang/String;)V

    goto/16 :goto_56

    .line 293
    :sswitch_15
    iget v4, v2, Landroid/os/Message;->arg1:I

    packed-switch v4, :pswitch_data_4

    :pswitch_14
    const/4 v4, 0x0

    goto :goto_42

    :pswitch_15
    move v4, v10

    goto :goto_42

    :pswitch_16
    const/4 v4, 0x1

    .line 294
    :goto_42
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-eq v5, v4, :cond_88

    .line 295
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iput v4, v5, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    .line 296
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-ne v5, v10, :cond_5a

    .line 297
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v5

    const/4 v6, 0x3

    const/4 v7, 0x1

    invoke-virtual {v5, v6, v7}, Landroid/tw/john/TWUtil;->write(II)I

    goto :goto_43

    .line 298
    :cond_5a
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    const/4 v6, 0x0

    iput v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    .line 299
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    const/4 v6, 0x0

    iput-object v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    .line 300
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iput-object v6, v5, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    .line 301
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 302
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 303
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 304
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 305
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->hh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 306
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->ih:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 307
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->jh:Ljava/util/ArrayList;

    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    .line 308
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v5

    const/4 v6, 0x3

    invoke-virtual {v5, v6, v10}, Landroid/tw/john/TWUtil;->write(II)I

    .line 309
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const v6, 0xff00

    invoke-virtual {v5, v6}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 310
    :goto_43
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_44
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_5b

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 311
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v6, v4}, Lcom/eckom/xtlibrary/b/a/d/g;->p(I)V

    goto :goto_44

    .line 312
    :cond_5b
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v5, 0x10

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    invoke-virtual {v4, v12, v5, v1}, Landroid/tw/john/TWUtil;->write(III)I
    :try_end_d
    .catch Ljava/lang/Exception; {:try_start_d .. :try_end_d} :catch_7

    goto/16 :goto_56

    :cond_5c
    :try_start_e
    const-string v4, "-#"

    .line 313
    invoke-virtual {v5, v4}, Ljava/lang/String;->lastIndexOf(Ljava/lang/String;)I

    move-result v4

    const/4 v6, -0x1

    if-ne v4, v6, :cond_5d

    .line 314
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    .line 315
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v11, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Ig:Ljava/lang/String;

    goto :goto_45

    .line 316
    :cond_5d
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    const/4 v7, 0x0

    invoke-virtual {v5, v7, v4}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v8

    iput-object v8, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    .line 317
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    add-int/2addr v4, v10

    invoke-virtual {v5, v4}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v4

    iput-object v4, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Ig:Ljava/lang/String;
    :try_end_e
    .catch Ljava/lang/Exception; {:try_start_e .. :try_end_e} :catch_6

    goto :goto_45

    :catch_6
    move-exception v0

    move-object v4, v0

    .line 318
    :try_start_f
    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    const-string v7, "RETURN_ID3:"

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v6, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 319
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    .line 320
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v11, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Ig:Ljava/lang/String;

    .line 321
    :goto_45
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v13, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    .line 322
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "RETURN_ID3:musicTitle:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v5, " musicAlbum:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Ig:Ljava/lang/String;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v5, " musicArtist:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 323
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const v5, 0x9f00

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-boolean v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Gg:Z

    if-eqz v6, :cond_5e

    const/4 v6, 0x1

    goto :goto_46

    :cond_5e
    const/4 v6, 0x0

    :goto_46
    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    invoke-virtual {v4, v5, v9, v6, v7}, Landroid/tw/john/TWUtil;->write(IIILjava/lang/Object;)I

    .line 324
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    const/4 v6, 0x0

    invoke-static {v4, v6, v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;ILjava/lang/String;)V

    .line 325
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    new-instance v5, Lcom/eckom/xtlibrary/b/a/d/a;

    invoke-direct {v5, v1}, Lcom/eckom/xtlibrary/b/a/d/a;-><init>(Lcom/eckom/xtlibrary/b/a/d/c;)V

    const-wide/16 v6, 0x64

    invoke-virtual {v4, v5, v6, v7}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 326
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    new-instance v5, Lcom/eckom/xtlibrary/b/a/d/b;

    invoke-direct {v5, v1}, Lcom/eckom/xtlibrary/b/a/d/b;-><init>(Lcom/eckom/xtlibrary/b/a/d/c;)V

    const-wide/16 v6, 0xc8

    invoke-virtual {v4, v5, v6, v7}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 327
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_47
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_5f

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 328
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    invoke-interface {v5, v6, v7}, Lcom/eckom/xtlibrary/b/a/d/g;->r(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_47

    .line 329
    :cond_5f
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_48
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_60

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 330
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    iget-object v8, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v8

    iget-object v8, v8, Lcom/eckom/xtlibrary/b/a/b/a;->Ig:Ljava/lang/String;

    invoke-interface {v5, v6, v7, v8}, Lcom/eckom/xtlibrary/b/a/d/g;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_48

    .line 331
    :cond_60
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    if-eqz v4, :cond_88

    .line 332
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->Hg:Ljava/lang/String;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->Jg:Ljava/lang/String;

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->Lg:I

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->Kg:I

    invoke-virtual {v4, v5, v6, v7, v1}, Lcom/eckom/xtlibrary/b/a/a/b;->a(Ljava/lang/String;Ljava/lang/String;II)V

    goto/16 :goto_56

    .line 333
    :cond_61
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_49
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 334
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v4, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->da(Ljava/lang/String;)V

    goto :goto_49

    .line 335
    :cond_62
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_4a
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 336
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/g;->H(I)V

    goto :goto_4a

    .line 337
    :cond_63
    iget v4, v2, Landroid/os/Message;->arg1:I

    if-nez v4, :cond_64

    .line 338
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    if-eqz v4, :cond_65

    .line 339
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result v4

    if-eqz v4, :cond_65

    .line 340
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->pause()V

    goto :goto_4b

    .line 341
    :cond_64
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/16 v5, 0x302

    invoke-virtual {v4, v5, v10}, Landroid/tw/john/TWUtil;->write(II)I

    .line 342
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->j(Lcom/eckom/xtlibrary/b/a/d/f;)V

    .line 343
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    if-eqz v4, :cond_65

    .line 344
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->isPlaying()Z

    move-result v4

    if-nez v4, :cond_65

    .line 345
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->b(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/media/MediaPlayer;

    move-result-object v4

    invoke-virtual {v4}, Landroid/media/MediaPlayer;->start()V

    .line 346
    :cond_65
    :goto_4b
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_4c
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_88

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 347
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v5, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/a/d/g;->J(I)V

    goto :goto_4c

    .line 348
    :cond_66
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/16 v6, 0x18

    shr-int/2addr v4, v6

    and-int/2addr v4, v8

    if-eqz v4, :cond_6d

    const/4 v6, 0x1

    if-eq v4, v6, :cond_67

    goto/16 :goto_56

    .line 349
    :cond_67
    iget v4, v2, Landroid/os/Message;->arg1:I

    const v6, 0xffffff

    and-int/2addr v4, v6

    if-nez v4, :cond_69

    .line 350
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_4d
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_68

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 351
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v6}, Lcom/eckom/xtlibrary/b/a/d/g;->N()V

    goto :goto_4d

    .line 352
    :cond_68
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 353
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v4}, Landroid/os/Handler;->obtainMessage()Landroid/os/Message;

    move-result-object v4

    .line 354
    iput v15, v4, Landroid/os/Message;->what:I

    const/4 v6, 0x0

    .line 355
    iput v6, v4, Landroid/os/Message;->arg1:I

    const/4 v6, 0x1

    .line 356
    iput v6, v4, Landroid/os/Message;->arg2:I

    .line 357
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v6, v15}, Landroid/os/Handler;->removeMessages(I)V

    .line 358
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v6, v4}, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z

    .line 359
    :cond_69
    new-instance v4, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-static {v5}, Landroid/tw/john/PinyinConv;->cn2py(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    invoke-direct {v4, v5, v13, v6}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 360
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v6

    if-nez v6, :cond_6a

    .line 361
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 362
    :cond_6a
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v6

    if-nez v6, :cond_6b

    .line 363
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 364
    :cond_6b
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_4e
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_6c

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 365
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v6, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->o(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_4e

    .line 366
    :cond_6c
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v4}, Landroid/os/Handler;->obtainMessage()Landroid/os/Message;

    move-result-object v4

    .line 367
    iput v15, v4, Landroid/os/Message;->what:I

    const/4 v5, 0x1

    .line 368
    iput v5, v4, Landroid/os/Message;->arg1:I

    .line 369
    iput v5, v4, Landroid/os/Message;->arg2:I

    .line 370
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v5, v15}, Landroid/os/Handler;->removeMessages(I)V

    .line 371
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const-wide/16 v5, 0x7d0

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->sendMessageDelayed(Landroid/os/Message;J)Z

    goto/16 :goto_56

    .line 372
    :cond_6d
    iget v4, v2, Landroid/os/Message;->arg1:I

    const v6, 0xffffff

    and-int/2addr v4, v6

    if-nez v4, :cond_6f

    .line 373
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_4f
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_6e

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 374
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v6}, Lcom/eckom/xtlibrary/b/a/d/g;->X()V

    goto :goto_4f

    .line 375
    :cond_6e
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 376
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 377
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v4}, Landroid/os/Handler;->obtainMessage()Landroid/os/Message;

    move-result-object v4

    .line 378
    iput v15, v4, Landroid/os/Message;->what:I

    const/4 v6, 0x0

    .line 379
    iput v6, v4, Landroid/os/Message;->arg1:I

    .line 380
    iput v6, v4, Landroid/os/Message;->arg2:I

    .line 381
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v6, v15}, Landroid/os/Handler;->removeMessages(I)V

    .line 382
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v6, v4}, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z

    :cond_6f
    if-eqz v5, :cond_70

    .line 383
    invoke-virtual {v5}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v5

    :cond_70
    if-eqz v13, :cond_71

    .line 384
    invoke-virtual {v13}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v13

    .line 385
    :cond_71
    new-instance v4, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;

    invoke-static {v5}, Landroid/tw/john/PinyinConv;->cn2py(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    invoke-direct {v4, v5, v13, v6}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 386
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v6

    if-nez v6, :cond_72

    .line 387
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 388
    :cond_72
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v6

    if-nez v6, :cond_73

    .line 389
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 390
    :cond_73
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_50
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_74

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 391
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    invoke-interface {v6, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->q(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_50

    .line 392
    :cond_74
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v4}, Landroid/os/Handler;->obtainMessage()Landroid/os/Message;

    move-result-object v4

    .line 393
    iput v15, v4, Landroid/os/Message;->what:I

    const/4 v5, 0x1

    .line 394
    iput v5, v4, Landroid/os/Message;->arg1:I

    const/4 v5, 0x0

    .line 395
    iput v5, v4, Landroid/os/Message;->arg2:I

    .line 396
    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    invoke-virtual {v5, v15}, Landroid/os/Handler;->removeMessages(I)V

    .line 397
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/f;->mHandler:Landroid/os/Handler;

    const-wide/16 v5, 0xbb8

    invoke-virtual {v1, v4, v5, v6}, Landroid/os/Handler;->sendMessageDelayed(Landroid/os/Message;J)Z

    goto/16 :goto_56

    .line 398
    :cond_75
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v5, v2, Landroid/os/Message;->arg1:I

    iput v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->tg:I

    .line 399
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->tg:I

    const/4 v5, 0x1

    if-ne v5, v4, :cond_76

    .line 400
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    const-string v5, "ConnectDeviceMac"

    invoke-static {v4, v3, v5, v11}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 401
    :cond_76
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_51
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_77

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 402
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v6, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/a/d/g;->M(I)V

    goto :goto_51

    .line 403
    :cond_77
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->tg:I

    const/4 v5, 0x1

    if-eq v4, v5, :cond_88

    .line 404
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    const-string v5, "KED18-0395"

    invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v4

    if-eqz v4, :cond_88

    .line 405
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v4, v3, v5}, Lcom/eckom/xtlibrary/b/j/o;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_88

    .line 406
    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->c(Lcom/eckom/xtlibrary/b/a/d/f;)V

    goto/16 :goto_56

    .line 407
    :cond_78
    :sswitch_16
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    iget v5, v2, Landroid/os/Message;->arg1:I

    if-eq v4, v5, :cond_7a

    .line 408
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v5, v2, Landroid/os/Message;->arg1:I

    iput v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    .line 409
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-ne v4, v10, :cond_79

    .line 410
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/4 v5, 0x3

    const/4 v6, 0x1

    invoke-virtual {v4, v5, v6}, Landroid/tw/john/TWUtil;->write(II)I

    goto/16 :goto_52

    .line 411
    :cond_79
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v5, 0x0

    iput v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Cg:I

    .line 412
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    const/4 v5, 0x0

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    .line 413
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    .line 414
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->dh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 415
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->eh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 416
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 417
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->gh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 418
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->hh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 419
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ih:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 420
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->jh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 421
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/4 v5, 0x3

    invoke-virtual {v4, v5, v10}, Landroid/tw/john/TWUtil;->write(II)I

    .line 422
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    const/4 v6, 0x0

    invoke-virtual {v4, v6, v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(ZLjava/lang/String;)V

    .line 423
    :cond_7a
    :goto_52
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_53
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_7b

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Ljava/util/Map$Entry;

    .line 424
    invoke-interface {v5}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v6, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/a/d/g;->C(I)V

    goto :goto_53

    .line 425
    :cond_7b
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    if-eqz v4, :cond_88

    .line 426
    invoke-static {}, Lcom/eckom/xtlibrary/b/b;->getInstant()Lcom/eckom/xtlibrary/b/b;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/b;->cd:Lc/b/a/a/a/d;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    invoke-interface {v4, v1}, Lc/b/a/a/a/d;->m(I)V

    goto/16 :goto_56

    .line 427
    :cond_7c
    iget v4, v2, Landroid/os/Message;->arg1:I

    if-nez v4, :cond_81

    .line 428
    invoke-static {v13}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_7d

    .line 429
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v13, v4, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    .line 430
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    const-string v6, "KED18-0395"

    invoke-virtual {v4, v6}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v4

    if-eqz v4, :cond_7d

    .line 431
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 432
    :cond_7d
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->k(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/c;

    move-result-object v4

    const/4 v6, 0x3

    invoke-virtual {v4, v6, v10}, Landroid/tw/john/TWUtil;->write(II)I

    .line 433
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    if-eqz v4, :cond_86

    .line 434
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Mg:Z

    if-eqz v4, :cond_7e

    .line 435
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v4, v6}, Lcom/eckom/xtlibrary/b/a/c/b;->a(Landroid/content/Context;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/a/c/b;

    .line 436
    :cond_7e
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    const-string v6, "ConnectDeviceMac"

    invoke-static {v4, v3, v6}, Lcom/eckom/xtlibrary/b/j/o;->c(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    .line 437
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v6, v4}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_80

    .line 438
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    const-string v6, "KED18-0395"

    invoke-virtual {v4, v6}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_7f

    .line 439
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->p(Lcom/eckom/xtlibrary/b/a/d/f;)V

    .line 440
    :cond_7f
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    if-eqz v4, :cond_80

    .line 441
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/a/b;->fb()V

    .line 442
    :cond_80
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->tb()V

    .line 443
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->wb()V

    .line 444
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->vb()V

    goto/16 :goto_54

    .line 445
    :cond_81
    iget v4, v2, Landroid/os/Message;->arg1:I

    const/4 v6, 0x1

    if-ne v4, v6, :cond_85

    .line 446
    invoke-static {}, Lcom/eckom/xtlibrary/b/a/b/a;->getInstance()Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->_g:Ljava/util/ArrayList;

    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    .line 447
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->sg:I

    if-ne v4, v10, :cond_85

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    if-nez v4, :cond_85

    .line 448
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v13, v4, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    .line 449
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    if-eqz v4, :cond_85

    .line 450
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-boolean v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->Mg:Z

    if-eqz v4, :cond_82

    .line 451
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v4, v6}, Lcom/eckom/xtlibrary/b/a/c/b;->a(Landroid/content/Context;Ljava/lang/String;)Lcom/eckom/xtlibrary/b/a/c/b;

    .line 452
    :cond_82
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v4

    const-string v6, "ConnectDeviceMac"

    invoke-static {v4, v3, v6}, Lcom/eckom/xtlibrary/b/j/o;->c(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    .line 453
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v6, v4}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_84

    .line 454
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->ng:Ljava/lang/String;

    const-string v6, "KED18-0395"

    invoke-virtual {v4, v6}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_83

    .line 455
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->p(Lcom/eckom/xtlibrary/b/a/d/f;)V

    .line 456
    :cond_83
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    if-eqz v4, :cond_84

    .line 457
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/a/b;->fb()V

    .line 458
    :cond_84
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->tb()V

    .line 459
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->wb()V

    .line 460
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-virtual {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->vb()V

    .line 461
    :cond_85
    new-instance v4, Lcom/eckom/xtlibrary/twproject/bt/bean/b;

    invoke-direct {v4, v5, v13}, Lcom/eckom/xtlibrary/twproject/bt/bean/b;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    .line 462
    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->_g:Ljava/util/ArrayList;

    invoke-virtual {v6, v4}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v4

    if-nez v4, :cond_86

    .line 463
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->_g:Ljava/util/ArrayList;

    new-instance v6, Lcom/eckom/xtlibrary/twproject/bt/bean/b;

    invoke-direct {v6, v5, v13}, Lcom/eckom/xtlibrary/twproject/bt/bean/b;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v4, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 464
    :cond_86
    :goto_54
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->m(Lcom/eckom/xtlibrary/b/a/d/f;)Ljava/util/Map;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_55
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_87

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 465
    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Lcom/eckom/xtlibrary/b/a/d/g;

    iget v7, v2, Landroid/os/Message;->arg1:I

    invoke-interface {v6, v7, v5, v13}, Lcom/eckom/xtlibrary/b/a/d/g;->a(ILjava/lang/String;Ljava/lang/String;)V

    goto :goto_55

    .line 466
    :cond_87
    invoke-static {v13}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_88

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-static {v4, v13}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    move-result v4

    if-eqz v4, :cond_88

    .line 467
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    .line 468
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    if-eqz v4, :cond_88

    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_88

    .line 469
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/a/d/f;->n(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/a/b;

    move-result-object v4

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    iget-object v6, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v6}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v6

    iget-object v6, v6, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-virtual {v4, v5, v6}, Lcom/eckom/xtlibrary/b/a/a/b;->y(Ljava/lang/String;Ljava/lang/String;)V

    .line 470
    iget-object v4, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/c;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->xg:Ljava/lang/String;

    const/4 v5, 0x1

    invoke-virtual {v4, v5, v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(ZLjava/lang/String;)V
    :try_end_f
    .catch Ljava/lang/Exception; {:try_start_f .. :try_end_f} :catch_7

    goto :goto_56

    :catch_7
    move-exception v0

    move-object v1, v0

    .line 471
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "handleMessage: msg.what:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v2, v2, Landroid/os/Message;->what:I

    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v2, " Error:"

    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_88
    :goto_56
    const/4 v1, 0x1

    return v1

    :sswitch_data_0
    .sparse-switch
        0x7 -> :sswitch_16
        0x9 -> :sswitch_15
        0xb -> :sswitch_14
        0xd -> :sswitch_13
        0x15 -> :sswitch_12
        0x1b -> :sswitch_11
        0x1d -> :sswitch_10
        0x1f -> :sswitch_f
        0x21 -> :sswitch_e
        0x23 -> :sswitch_d
        0x2d -> :sswitch_c
        0x32 -> :sswitch_b
        0x3b -> :sswitch_a
        0x3d -> :sswitch_9
        0x3f -> :sswitch_8
        0x41 -> :sswitch_7
        0x10b -> :sswitch_6
        0x112 -> :sswitch_5
        0x201 -> :sswitch_4
        0x203 -> :sswitch_3
        0x301 -> :sswitch_2
        0x510 -> :sswitch_1
        0x9e08 -> :sswitch_0
    .end sparse-switch

    :pswitch_data_0
    .packed-switch 0x44
        :pswitch_f
        :pswitch_b
        :pswitch_a
        :pswitch_9
    .end packed-switch

    :pswitch_data_1
    .packed-switch 0xff03
        :pswitch_8
        :pswitch_7
        :pswitch_6
        :pswitch_5
        :pswitch_4
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch

    :pswitch_data_2
    .packed-switch 0x34
        :pswitch_e
        :pswitch_d
        :pswitch_c
    .end packed-switch

    :pswitch_data_3
    .packed-switch 0x3a
        :pswitch_13
        :pswitch_12
        :pswitch_11
        :pswitch_10
    .end packed-switch

    :pswitch_data_4
    .packed-switch 0x0
        :pswitch_14
        :pswitch_14
        :pswitch_16
        :pswitch_15
        :pswitch_15
        :pswitch_15
        :pswitch_15
    .end packed-switch
.end method

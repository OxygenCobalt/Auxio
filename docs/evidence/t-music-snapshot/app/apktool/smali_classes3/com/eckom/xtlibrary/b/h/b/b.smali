.class Lcom/eckom/xtlibrary/b/h/b/b;
.super Ljava/lang/Object;
.source "RadioModel.java"

# interfaces
.implements Landroid/os/Handler$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/eckom/xtlibrary/b/h/b/e;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eckom/xtlibrary/b/h/b/e;


# direct methods
.method constructor <init>(Lcom/eckom/xtlibrary/b/h/b/e;)V
    .locals 0

    .line 1
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)Z
    .locals 12

    const-string v0, "Radio"

    const-string v1, "handleMessage: "

    const-string v2, "RadioModel"

    const/4 v3, 0x1

    .line 1
    :try_start_0
    iget v4, p1, Landroid/os/Message;->what:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const/16 v5, 0x109

    const/4 v6, 0x3

    const-string v7, ","

    const/4 v8, 0x0

    if-eq v4, v5, :cond_19

    const/16 v0, 0x10a

    const/4 v5, 0x2

    const/16 v9, 0x10

    if-eq v4, v0, :cond_16

    const/16 v0, 0x112

    if-eq v4, v0, :cond_14

    const/16 v0, 0x201

    if-eq v4, v0, :cond_13

    const/16 v0, 0x203

    if-eq v4, v0, :cond_10

    const/16 v0, 0x301

    if-eq v4, v0, :cond_f

    const/16 v0, 0x401

    if-eq v4, v0, :cond_e

    const/16 v0, 0x402

    if-eq v4, v0, :cond_7

    packed-switch v4, :pswitch_data_0

    packed-switch v4, :pswitch_data_1

    packed-switch v4, :pswitch_data_2

    goto/16 :goto_d

    .line 2
    :pswitch_0
    :try_start_1
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->c(Lcom/eckom/xtlibrary/b/h/b/e;)I

    move-result v0

    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Wk:I

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/h/b/e;->g(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    move-result-object v5

    iget-object v6, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v6, v6, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    invoke-static {v0, v4, v5, v6}, Lcom/eckom/xtlibrary/b/j/b;->a(IILcom/eckom/xtlibrary/twproject/radio/utils/b;I)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    .line 3
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iput-object v0, v4, Lcom/eckom/xtlibrary/b/h/a;->ml:Landroid/graphics/drawable/Drawable;

    .line 4
    iget-object v4, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v4, v4, Ljava/lang/String;

    if-eqz v4, :cond_0

    .line 5
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget-object v5, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v5, Ljava/lang/String;

    iput-object v5, v4, Lcom/eckom/xtlibrary/b/h/a;->gl:Ljava/lang/String;

    .line 6
    :cond_0
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_0
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_1d

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 7
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p1, Landroid/os/Message;->arg2:I

    shr-int/lit8 v6, v6, 0x8

    and-int/lit16 v6, v6, 0xff

    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->E(I)V

    .line 8
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v6, p1, Landroid/os/Message;->arg2:I

    and-int/lit16 v6, v6, 0xff

    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->A(I)V

    .line 9
    iget-object v5, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    if-eqz v5, :cond_1

    .line 10
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v6, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v6, Ljava/lang/String;

    invoke-interface {v5, v6}, Lcom/eckom/xtlibrary/b/h/b/f;->ba(Ljava/lang/String;)V

    .line 11
    :cond_1
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v4, v0}, Lcom/eckom/xtlibrary/b/h/b/f;->a(Landroid/graphics/drawable/Drawable;)V

    goto :goto_0

    .line 12
    :pswitch_1
    iget p1, p1, Landroid/os/Message;->arg1:I

    .line 13
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_1
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1d

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 14
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v0, p1}, Lcom/eckom/xtlibrary/b/h/b/f;->r(I)V

    goto :goto_1

    :pswitch_2
    const-string p1, "handleMessage: MSG_DB_HELPER_UPDATE"

    .line 15
    invoke-static {v2, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 16
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->g(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    move-result-object p1

    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    invoke-virtual {p1, v0}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->ba(I)V

    .line 17
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->g(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    move-result-object p1

    invoke-virtual {p1}, Landroid/database/sqlite/SQLiteOpenHelper;->getWritableDatabase()Landroid/database/sqlite/SQLiteDatabase;

    move-result-object p1

    .line 18
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->g(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    move-result-object p0

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/twproject/radio/utils/b;->a(Landroid/database/sqlite/SQLiteDatabase;)V

    goto/16 :goto_d

    .line 19
    :pswitch_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->h(Lcom/eckom/xtlibrary/b/h/b/e;)Z

    move-result p1

    if-eqz p1, :cond_1d

    .line 20
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->Yb()V

    goto/16 :goto_d

    .line 21
    :pswitch_4
    iget p1, p1, Landroid/os/Message;->arg1:I

    if-eq p1, v6, :cond_5

    const/4 v0, 0x4

    if-eq p1, v0, :cond_4

    const/16 v0, 0x9

    if-eq p1, v0, :cond_3

    const/16 v0, 0xa

    if-eq p1, v0, :cond_2

    goto/16 :goto_d

    .line 22
    :cond_2
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->ec()V

    goto/16 :goto_d

    .line 23
    :cond_3
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->dc()V

    goto/16 :goto_d

    .line 24
    :cond_4
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->ac()V

    goto/16 :goto_d

    .line 25
    :cond_5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->next()V

    goto/16 :goto_d

    .line 26
    :pswitch_5
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget p1, p1, Landroid/os/Message;->arg1:I

    iput p1, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mActivity:I

    goto/16 :goto_d

    .line 27
    :pswitch_6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;Landroid/os/Message;)V

    goto/16 :goto_d

    .line 28
    :pswitch_7
    iget-object v0, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v0, v0, Ljava/lang/String;

    if-eqz v0, :cond_6

    .line 29
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v0

    iget-object v4, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v4, Ljava/lang/String;

    iput-object v4, v0, Lcom/eckom/xtlibrary/b/h/a;->hl:Ljava/lang/String;

    .line 30
    :cond_6
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_2
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1d

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 31
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget-object v4, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v4, Ljava/lang/String;

    invoke-interface {v0, v4}, Lcom/eckom/xtlibrary/b/h/b/f;->ha(Ljava/lang/String;)V

    goto :goto_2

    .line 32
    :pswitch_8
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->a(Lcom/eckom/xtlibrary/b/h/b/e;Landroid/os/Message;)V

    goto/16 :goto_d

    .line 33
    :cond_7
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->f(Lcom/eckom/xtlibrary/b/h/b/e;)I

    move-result v0

    const/16 v4, 0x80

    and-int/2addr v0, v4

    if-ne v0, v4, :cond_b

    .line 34
    iget v0, p1, Landroid/os/Message;->arg1:I

    if-eqz v0, :cond_a

    const/4 v4, 0x6

    if-eq v0, v4, :cond_9

    const/16 v4, 0xc

    if-eq v0, v4, :cond_8

    goto :goto_6

    .line 35
    :cond_8
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_3
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_b

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 36
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v4, v5}, Lcom/eckom/xtlibrary/b/h/b/f;->s(I)V

    goto :goto_3

    .line 37
    :cond_9
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_4
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_b

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 38
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v4, v3}, Lcom/eckom/xtlibrary/b/h/b/f;->s(I)V

    goto :goto_4

    .line 39
    :cond_a
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_5
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_b

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/Map$Entry;

    .line 40
    invoke-interface {v4}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {v4, v8}, Lcom/eckom/xtlibrary/b/h/b/f;->s(I)V

    goto :goto_5

    .line 41
    :cond_b
    :goto_6
    iget v0, p1, Landroid/os/Message;->arg1:I

    and-int/lit16 v0, v0, 0xff

    .line 42
    iget v4, p1, Landroid/os/Message;->arg2:I

    .line 43
    iget v5, p1, Landroid/os/Message;->arg1:I

    shr-int/lit8 v5, v5, 0x8

    and-int/lit16 v5, v5, 0xff

    .line 44
    iget v6, p1, Landroid/os/Message;->arg1:I

    shr-int/2addr v6, v9

    const v8, 0xffff

    and-int/2addr v6, v8

    .line 45
    invoke-static {v6}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    move-result-object v8

    invoke-static {v8}, Lcom/eckom/xtlibrary/b/j/b;->ib(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    .line 46
    iget-object v9, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v9}, Lcom/eckom/xtlibrary/b/h/b/e;->g(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/twproject/radio/utils/b;

    move-result-object v9

    iget-object v10, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v10, v10, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    invoke-static {v6, v4, v9, v10}, Lcom/eckom/xtlibrary/b/j/b;->a(IILcom/eckom/xtlibrary/twproject/radio/utils/b;I)Landroid/graphics/drawable/Drawable;

    move-result-object v9

    .line 47
    new-instance v10, Ljava/lang/StringBuilder;

    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    const-string v11, "0402:"

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v10, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v10, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v7, p1, Landroid/os/Message;->arg2:I

    invoke-virtual {v10, v7}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v7, "  ||    "

    invoke-virtual {v10, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v10, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v7, "  ||   "

    invoke-virtual {v10, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v10, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v7, "|| icon == "

    invoke-virtual {v10, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v10, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v7

    invoke-static {v7}, Lcom/eckom/xtlibrary/a/b;->a(Ljava/lang/Object;)V

    .line 48
    iget-object v7, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    array-length v7, v7

    if-ge v0, v7, :cond_1d

    .line 49
    iget-object v7, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v7}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v7, v7, v0

    iput v5, v7, Lcom/eckom/xtlibrary/b/h/a/a;->sl:I

    .line 50
    iget-object v5, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v5}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v5, v5, v0

    iput v4, v5, Lcom/eckom/xtlibrary/b/h/a/a;->tl:I

    .line 51
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v4, v4, v0

    iput-object v9, v4, Lcom/eckom/xtlibrary/b/h/a/a;->xl:Landroid/graphics/drawable/Drawable;

    .line 52
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v4, v4, v0

    iput-object v8, v4, Lcom/eckom/xtlibrary/b/h/a/a;->mPi:Ljava/lang/String;

    .line 53
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v4, v4, v0

    iput v6, v4, Lcom/eckom/xtlibrary/b/h/a/a;->wl:I

    .line 54
    iget-object v4, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v4, v4, Ljava/lang/String;
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    const-string v5, ""

    if-eqz v4, :cond_d

    .line 55
    :try_start_2
    iget-object v4, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v4, Ljava/lang/String;

    .line 56
    invoke-static {v4}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v4

    if-nez v4, :cond_c

    .line 57
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v4

    iget-object v4, v4, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object v4, v4, v0

    iget-object p1, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast p1, Ljava/lang/String;

    iput-object p1, v4, Lcom/eckom/xtlibrary/b/h/a/a;->ul:Ljava/lang/String;

    goto :goto_7

    .line 58
    :cond_c
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object p1, p1, v0

    iput-object v5, p1, Lcom/eckom/xtlibrary/b/h/a/a;->ul:Ljava/lang/String;

    goto :goto_7

    .line 59
    :cond_d
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/h/a;->Gi:[Lcom/eckom/xtlibrary/b/h/a/a;

    aget-object p1, p1, v0

    iput-object v5, p1, Lcom/eckom/xtlibrary/b/h/a/a;->ul:Ljava/lang/String;

    .line 60
    :goto_7
    invoke-static {}, Landroid/os/Message;->obtain()Landroid/os/Message;

    move-result-object p1

    .line 61
    iput v0, p1, Landroid/os/Message;->arg1:I

    const v0, 0xff02

    .line 62
    iput v0, p1, Landroid/os/Message;->what:I

    .line 63
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    invoke-virtual {p0, p1}, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z

    goto/16 :goto_d

    .line 64
    :cond_e
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->a(Landroid/os/Message;)V

    goto/16 :goto_d

    .line 65
    :cond_f
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Landroid/os/Message;)V

    goto/16 :goto_d

    .line 66
    :cond_10
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v0

    iget v4, p1, Landroid/os/Message;->arg1:I

    const/high16 v5, -0x80000000

    and-int/2addr v4, v5

    if-ne v4, v5, :cond_11

    move v4, v3

    goto :goto_8

    :cond_11
    move v4, v8

    :goto_8
    iput-boolean v4, v0, Lcom/eckom/xtlibrary/b/h/a;->ol:Z

    .line 67
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_9
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1d

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    .line 68
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/b/h/b/f;

    iget v4, p1, Landroid/os/Message;->arg1:I

    and-int/2addr v4, v5

    if-ne v4, v5, :cond_12

    move v4, v3

    goto :goto_a

    :cond_12
    move v4, v8

    :goto_a
    invoke-interface {v0, v4}, Lcom/eckom/xtlibrary/b/h/b/f;->f(Z)V

    goto :goto_9

    .line 69
    :cond_13
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-virtual {p0, p1}, Lcom/eckom/xtlibrary/b/h/b/e;->c(Landroid/os/Message;)V

    goto/16 :goto_d

    .line 70
    :cond_14
    iget p1, p1, Landroid/os/Message;->arg1:I

    const/high16 v0, 0x10000

    and-int/2addr p1, v0

    if-ne p1, v0, :cond_15

    move v8, v3

    .line 71
    :cond_15
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->b(Lcom/eckom/xtlibrary/b/h/b/e;)Ljava/util/Map;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object p0

    invoke-interface {p0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object p0

    :goto_b
    invoke-interface {p0}, Ljava/util/Iterator;->hasNext()Z

    move-result p1

    if-eqz p1, :cond_1d

    invoke-interface {p0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/util/Map$Entry;

    .line 72
    invoke-interface {p1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Lcom/eckom/xtlibrary/b/h/b/f;

    invoke-interface {p1, v8}, Lcom/eckom/xtlibrary/b/h/b/f;->q(Z)V

    goto :goto_b

    .line 73
    :cond_16
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, p1, Landroid/os/Message;->arg1:I

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 74
    iget v0, p1, Landroid/os/Message;->arg1:I

    if-eqz v0, :cond_17

    goto/16 :goto_d

    .line 75
    :cond_17
    iget-object v0, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v0, v0, Ljava/lang/String;

    if-eqz v0, :cond_18

    .line 76
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object v0

    iget-object p1, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast p1, Ljava/lang/String;

    iput-object p1, v0, Lcom/eckom/xtlibrary/b/h/a;->pl:Ljava/lang/String;

    .line 77
    :cond_18
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/h/a;->pl:Ljava/lang/String;

    if-eqz p1, :cond_1d

    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/h/a;->pl:Ljava/lang/String;

    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result p1
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    if-lez p1, :cond_1d

    .line 78
    :try_start_3
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object p1

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/h/a;->pl:Ljava/lang/String;

    const-string v0, "-"

    invoke-virtual {p1, v0}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object p1

    .line 79
    aget-object p1, p1, v6

    .line 80
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p0}, Lcom/eckom/xtlibrary/b/h/b/e;->d(Lcom/eckom/xtlibrary/b/h/b/e;)Lcom/eckom/xtlibrary/b/h/a;

    move-result-object p0

    invoke-virtual {p1, v5, v6}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object p1

    invoke-static {p1, v9}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;I)I

    move-result p1

    iput p1, p0, Lcom/eckom/xtlibrary/b/h/a;->ql:I
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    goto/16 :goto_d

    .line 81
    :cond_19
    :try_start_4
    iget-object p1, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast p1, [B

    .line 82
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "handleMessage: 0x0109="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    aget-byte v5, p1, v8

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v4, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v5, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v5, v5, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v2, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 83
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v4, v4, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    aget-byte v5, p1, v8

    if-eq v4, v5, :cond_1d

    .line 84
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    aget-byte p1, p1, v8

    iput p1, v4, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    .line 85
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {p1}, Lcom/eckom/xtlibrary/b/h/b/e;->e(Lcom/eckom/xtlibrary/b/h/b/e;)Landroid/content/Context;

    move-result-object p1

    const-string v4, "radio_location"

    const/4 v5, -0x1

    invoke-static {p1, v0, v4, v5}, Lcom/eckom/xtlibrary/b/j/o;->b(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;I)I

    move-result p1

    .line 86
    iget-object v4, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    invoke-static {v4}, Lcom/eckom/xtlibrary/b/h/b/e;->e(Lcom/eckom/xtlibrary/b/h/b/e;)Landroid/content/Context;

    move-result-object v4

    const-string v5, "radio_freq_logo_data_size"

    const-wide/16 v8, -0x1

    invoke-static {v4, v0, v5, v8, v9}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/Long;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Long;->longValue()J

    move-result-wide v4

    .line 87
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    if-eq v0, v6, :cond_1a

    const-string v0, "radio_data.txt"

    goto :goto_c

    :cond_1a
    const-string v0, "radio_data_dny.txt"

    .line 88
    :goto_c
    new-instance v6, Ljava/io/File;

    new-instance v8, Ljava/lang/StringBuilder;

    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    const-string v9, "/sdcard/iNand/radio//"

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v8, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {v6, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 89
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v8, "handleMessage: 0x0109=,"

    invoke-virtual {v0, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/io/File;->exists()Z

    move-result v8

    invoke-virtual {v0, v8}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    const-string v8, ",logoSize="

    invoke-virtual {v0, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v4, v5}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v6}, Ljava/io/File;->length()J

    move-result-wide v8

    invoke-virtual {v0, v8, v9}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v7, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v7, v7, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    invoke-virtual {v0, v7}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 90
    invoke-virtual {v6}, Ljava/io/File;->exists()Z

    move-result v0

    if-eqz v0, :cond_1b

    invoke-virtual {v6}, Ljava/io/File;->length()J

    move-result-wide v6

    cmp-long v0, v6, v4

    if-nez v0, :cond_1c

    :cond_1b
    iget-object v0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget v0, v0, Lcom/eckom/xtlibrary/b/h/b/e;->location:I

    if-eq p1, v0, :cond_1d

    .line 91
    :cond_1c
    iget-object p1, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget-object p1, p1, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    const v0, 0xff01

    invoke-virtual {p1, v0}, Landroid/os/Handler;->removeMessages(I)V

    .line 92
    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/b;->this$0:Lcom/eckom/xtlibrary/b/h/b/e;

    iget-object p0, p0, Lcom/eckom/xtlibrary/b/h/b/e;->mHandler:Landroid/os/Handler;

    const p1, 0xff01

    const-wide/16 v4, 0x7d0

    invoke-virtual {p0, p1, v4, v5}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_0

    goto :goto_d

    :catch_0
    move-exception p0

    .line 93
    new-instance p1, Ljava/lang/StringBuilder;

    invoke-direct {p1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p1, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v2, p1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 94
    invoke-virtual {p0}, Ljava/lang/Exception;->printStackTrace()V

    :catch_1
    :cond_1d
    :goto_d
    return v3

    nop

    :pswitch_data_0
    .packed-switch 0x404
        :pswitch_8
        :pswitch_7
        :pswitch_6
    .end packed-switch

    :pswitch_data_1
    .packed-switch 0x9e00
        :pswitch_5
        :pswitch_4
    .end packed-switch

    :pswitch_data_2
    .packed-switch 0xff00
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

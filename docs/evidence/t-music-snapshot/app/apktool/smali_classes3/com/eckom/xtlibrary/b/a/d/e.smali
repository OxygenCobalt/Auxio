.class Lcom/eckom/xtlibrary/b/a/d/e;
.super Ljava/lang/Object;
.source "BTModel.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eckom/xtlibrary/b/a/d/f;->Ke()V
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
    iput-object p1, p0, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 24

    move-object/from16 v1, p0

    const-string v2, "addContactToSystemDatabase: "

    .line 1
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v0

    new-instance v3, Landroid/content/Intent;

    const-string v4, "com.tw.bt.startaddContact"

    invoke-direct {v3, v4}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v3}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    .line 2
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "addContactToSystemDatabase: start:contacts count:"

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v3, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v3}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v3

    iget-object v3, v3, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    move-result v3

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v3, "BTModel"

    invoke-static {v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 3
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v4

    .line 4
    :try_start_0
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v0

    invoke-virtual {v0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v6

    .line 5
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    .line 6
    div-int/lit16 v7, v0, 0x12c

    const/16 v8, 0x12c

    .line 7
    rem-int/lit16 v9, v0, 0x12c
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_d

    const/4 v0, 0x0

    :goto_0
    add-int/lit8 v11, v7, -0x1

    const-string v12, "vnd.android.cursor.item/phone_v2"

    const-string v13, "vnd.android.cursor.item/name"

    const-string v14, "account_name"

    const-string v15, "account_type"

    const-string v8, "com.android.contacts"

    const-string v10, "data1"

    move/from16 v17, v11

    const-string v11, "mimetype"

    move-wide/from16 v18, v4

    const-string v4, "raw_contact_id"

    if-lez v7, :cond_3

    .line 8
    :try_start_1
    new-instance v7, Ljava/util/ArrayList;

    invoke-direct {v7}, Ljava/util/ArrayList;-><init>()V

    move v5, v0

    const/16 v0, 0x12c

    :goto_1
    add-int/lit8 v20, v0, -0x1

    if-lez v0, :cond_2

    .line 9
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v0

    iget-object v0, v0, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    add-int/lit8 v21, v5, 0x1

    invoke-virtual {v0, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_d

    .line 10
    :try_start_2
    invoke-virtual {v7}, Ljava/util/ArrayList;->size()I

    move-result v5

    .line 11
    sget-object v22, Landroid/provider/ContactsContract$RawContacts;->CONTENT_URI:Landroid/net/Uri;
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    move/from16 v23, v9

    :try_start_3
    invoke-static/range {v22 .. v22}, Landroid/content/ContentProviderOperation;->newInsert(Landroid/net/Uri;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v9
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    move-object/from16 v22, v3

    const/4 v3, 0x0

    .line 12
    :try_start_4
    invoke-virtual {v9, v15, v3}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v9

    .line 13
    invoke-virtual {v9, v14, v3}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v9

    const/4 v3, 0x1

    .line 14
    invoke-virtual {v9, v3}, Landroid/content/ContentProviderOperation$Builder;->withYieldAllowed(Z)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v9

    invoke-virtual {v9}, Landroid/content/ContentProviderOperation$Builder;->build()Landroid/content/ContentProviderOperation;

    move-result-object v3

    .line 15
    invoke-virtual {v7, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 16
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->getContactName()Ljava/lang/String;

    move-result-object v3

    if-eqz v3, :cond_0

    .line 17
    invoke-virtual {v3}, Ljava/lang/String;->length()I

    move-result v9

    if-lez v9, :cond_0

    .line 18
    sget-object v9, Landroid/provider/ContactsContract$Data;->CONTENT_URI:Landroid/net/Uri;

    invoke-static {v9}, Landroid/content/ContentProviderOperation;->newInsert(Landroid/net/Uri;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v9

    .line 19
    invoke-virtual {v9, v4, v5}, Landroid/content/ContentProviderOperation$Builder;->withValueBackReference(Ljava/lang/String;I)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v9

    .line 20
    invoke-virtual {v9, v11, v13}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v9

    .line 21
    invoke-virtual {v9, v10, v3}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v3

    const/4 v9, 0x1

    .line 22
    invoke-virtual {v3, v9}, Landroid/content/ContentProviderOperation$Builder;->withYieldAllowed(Z)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v3

    invoke-virtual {v3}, Landroid/content/ContentProviderOperation$Builder;->build()Landroid/content/ContentProviderOperation;

    move-result-object v3

    .line 23
    invoke-virtual {v7, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 24
    :cond_0
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_1

    .line 25
    invoke-virtual {v0}, Ljava/lang/String;->length()I

    move-result v3

    if-lez v3, :cond_1

    .line 26
    sget-object v3, Landroid/provider/ContactsContract$Data;->CONTENT_URI:Landroid/net/Uri;

    invoke-static {v3}, Landroid/content/ContentProviderOperation;->newInsert(Landroid/net/Uri;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v3

    .line 27
    invoke-virtual {v3, v4, v5}, Landroid/content/ContentProviderOperation$Builder;->withValueBackReference(Ljava/lang/String;I)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v3

    .line 28
    invoke-virtual {v3, v11, v12}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v3

    .line 29
    invoke-virtual {v3, v10, v0}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v0

    const-string v3, "data2"

    iget-object v5, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    .line 30
    invoke-static {v5}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v5

    iget-object v5, v5, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-virtual {v0, v3, v5}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v0

    const/4 v3, 0x1

    .line 31
    invoke-virtual {v0, v3}, Landroid/content/ContentProviderOperation$Builder;->withYieldAllowed(Z)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v0

    invoke-virtual {v0}, Landroid/content/ContentProviderOperation$Builder;->build()Landroid/content/ContentProviderOperation;

    move-result-object v0

    .line 32
    invoke-virtual {v7, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_0

    goto :goto_3

    :catch_0
    move-exception v0

    goto :goto_2

    :catch_1
    move-exception v0

    move-object/from16 v22, v3

    goto :goto_2

    :catch_2
    move-exception v0

    move-object/from16 v22, v3

    move/from16 v23, v9

    .line 33
    :goto_2
    :try_start_5
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_4

    :cond_1
    :goto_3
    move/from16 v0, v20

    move/from16 v5, v21

    move-object/from16 v3, v22

    move/from16 v9, v23

    goto/16 :goto_1

    :cond_2
    move-object/from16 v22, v3

    move/from16 v23, v9

    .line 34
    :try_start_6
    invoke-virtual {v6, v8, v7}, Landroid/content/ContentResolver;->applyBatch(Ljava/lang/String;Ljava/util/ArrayList;)[Landroid/content/ContentProviderResult;
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_3

    move-object/from16 v7, v22

    goto :goto_4

    :catch_3
    move-exception v0

    move-object v3, v0

    .line 35
    :try_start_7
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_4

    move-object/from16 v7, v22

    :try_start_8
    invoke-static {v7, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 36
    invoke-virtual {v3}, Ljava/lang/Exception;->printStackTrace()V

    :goto_4
    move v0, v5

    move-object v3, v7

    move/from16 v7, v17

    move-wide/from16 v4, v18

    move/from16 v9, v23

    const/16 v8, 0x12c

    goto/16 :goto_0

    :catch_4
    move-exception v0

    move-object/from16 v4, v22

    goto/16 :goto_b

    :cond_3
    move-object v7, v3

    move/from16 v23, v9

    if-lez v23, :cond_7

    .line 37
    new-instance v3, Ljava/util/ArrayList;

    invoke-direct {v3}, Ljava/util/ArrayList;-><init>()V

    :goto_5
    add-int/lit8 v5, v23, -0x1

    if-lez v23, :cond_6

    .line 38
    iget-object v9, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v9}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v9

    iget-object v9, v9, Lcom/eckom/xtlibrary/b/a/b/a;->fh:Ljava/util/ArrayList;

    add-int/lit8 v16, v0, 0x1

    invoke-virtual {v9, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;
    :try_end_8
    .catch Ljava/lang/Exception; {:try_start_8 .. :try_end_8} :catch_b

    .line 39
    :try_start_9
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    move-result v9

    .line 40
    sget-object v17, Landroid/provider/ContactsContract$RawContacts;->CONTENT_URI:Landroid/net/Uri;
    :try_end_9
    .catch Ljava/lang/Exception; {:try_start_9 .. :try_end_9} :catch_9

    move/from16 v20, v5

    :try_start_a
    invoke-static/range {v17 .. v17}, Landroid/content/ContentProviderOperation;->newInsert(Landroid/net/Uri;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5
    :try_end_a
    .catch Ljava/lang/Exception; {:try_start_a .. :try_end_a} :catch_8

    move-object/from16 v22, v7

    const/4 v7, 0x0

    .line 41
    :try_start_b
    invoke-virtual {v5, v15, v7}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5

    .line 42
    invoke-virtual {v5, v14, v7}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5
    :try_end_b
    .catch Ljava/lang/Exception; {:try_start_b .. :try_end_b} :catch_7

    const/4 v7, 0x1

    .line 43
    :try_start_c
    invoke-virtual {v5, v7}, Landroid/content/ContentProviderOperation$Builder;->withYieldAllowed(Z)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5
    :try_end_c
    .catch Ljava/lang/Exception; {:try_start_c .. :try_end_c} :catch_6

    :try_start_d
    invoke-virtual {v5}, Landroid/content/ContentProviderOperation$Builder;->build()Landroid/content/ContentProviderOperation;

    move-result-object v5

    .line 44
    invoke-virtual {v3, v5}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 45
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->getContactName()Ljava/lang/String;

    move-result-object v5

    if-eqz v5, :cond_4

    .line 46
    invoke-virtual {v5}, Ljava/lang/String;->length()I

    move-result v7

    if-lez v7, :cond_4

    .line 47
    sget-object v7, Landroid/provider/ContactsContract$Data;->CONTENT_URI:Landroid/net/Uri;

    invoke-static {v7}, Landroid/content/ContentProviderOperation;->newInsert(Landroid/net/Uri;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v7

    .line 48
    invoke-virtual {v7, v4, v9}, Landroid/content/ContentProviderOperation$Builder;->withValueBackReference(Ljava/lang/String;I)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v7

    .line 49
    invoke-virtual {v7, v11, v13}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v7

    .line 50
    invoke-virtual {v7, v10, v5}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5
    :try_end_d
    .catch Ljava/lang/Exception; {:try_start_d .. :try_end_d} :catch_7

    const/4 v7, 0x1

    .line 51
    :try_start_e
    invoke-virtual {v5, v7}, Landroid/content/ContentProviderOperation$Builder;->withYieldAllowed(Z)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5
    :try_end_e
    .catch Ljava/lang/Exception; {:try_start_e .. :try_end_e} :catch_6

    :try_start_f
    invoke-virtual {v5}, Landroid/content/ContentProviderOperation$Builder;->build()Landroid/content/ContentProviderOperation;

    move-result-object v5

    .line 52
    invoke-virtual {v3, v5}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 53
    :cond_4
    invoke-virtual {v0}, Lcom/eckom/xtlibrary/twproject/bt/bean/TWContact;->eb()Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_5

    .line 54
    invoke-virtual {v0}, Ljava/lang/String;->length()I

    move-result v5

    if-lez v5, :cond_5

    .line 55
    sget-object v5, Landroid/provider/ContactsContract$Data;->CONTENT_URI:Landroid/net/Uri;

    invoke-static {v5}, Landroid/content/ContentProviderOperation;->newInsert(Landroid/net/Uri;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5

    .line 56
    invoke-virtual {v5, v4, v9}, Landroid/content/ContentProviderOperation$Builder;->withValueBackReference(Ljava/lang/String;I)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5

    .line 57
    invoke-virtual {v5, v11, v12}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v5

    .line 58
    invoke-virtual {v5, v10, v0}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v0

    const-string v5, "data2"

    iget-object v7, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    .line 59
    invoke-static {v7}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v7

    iget-object v7, v7, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    invoke-virtual {v0, v5, v7}, Landroid/content/ContentProviderOperation$Builder;->withValue(Ljava/lang/String;Ljava/lang/Object;)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v0
    :try_end_f
    .catch Ljava/lang/Exception; {:try_start_f .. :try_end_f} :catch_7

    const/4 v5, 0x1

    .line 60
    :try_start_10
    invoke-virtual {v0, v5}, Landroid/content/ContentProviderOperation$Builder;->withYieldAllowed(Z)Landroid/content/ContentProviderOperation$Builder;

    move-result-object v0

    invoke-virtual {v0}, Landroid/content/ContentProviderOperation$Builder;->build()Landroid/content/ContentProviderOperation;

    move-result-object v0

    .line 61
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z
    :try_end_10
    .catch Ljava/lang/Exception; {:try_start_10 .. :try_end_10} :catch_5

    goto :goto_9

    :catch_5
    move-exception v0

    goto :goto_8

    :cond_5
    const/4 v5, 0x1

    goto :goto_9

    :catch_6
    move-exception v0

    move v5, v7

    goto :goto_8

    :catch_7
    move-exception v0

    goto :goto_7

    :catch_8
    move-exception v0

    goto :goto_6

    :catch_9
    move-exception v0

    move/from16 v20, v5

    :goto_6
    move-object/from16 v22, v7

    :goto_7
    const/4 v5, 0x1

    .line 62
    :goto_8
    :try_start_11
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V
    :try_end_11
    .catch Ljava/lang/Exception; {:try_start_11 .. :try_end_11} :catch_4

    :goto_9
    move/from16 v0, v16

    move/from16 v23, v20

    move-object/from16 v7, v22

    goto/16 :goto_5

    :cond_6
    move-object/from16 v22, v7

    .line 63
    :try_start_12
    invoke-virtual {v6, v8, v3}, Landroid/content/ContentResolver;->applyBatch(Ljava/lang/String;Ljava/util/ArrayList;)[Landroid/content/ContentProviderResult;
    :try_end_12
    .catch Ljava/lang/Exception; {:try_start_12 .. :try_end_12} :catch_a

    move-object/from16 v4, v22

    goto :goto_a

    :catch_a
    move-exception v0

    move-object v3, v0

    .line 64
    :try_start_13
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0
    :try_end_13
    .catch Ljava/lang/Exception; {:try_start_13 .. :try_end_13} :catch_4

    move-object/from16 v4, v22

    :try_start_14
    invoke-static {v4, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 65
    invoke-virtual {v3}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_a

    :catch_b
    move-exception v0

    move-object v4, v7

    goto :goto_b

    :cond_7
    move-object v4, v7

    :goto_a
    const-string v0, "addContactToSystemDatabase:completed"

    .line 66
    invoke-static {v4, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 67
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v5

    .line 68
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v0

    new-instance v3, Landroid/content/Intent;

    const-string v7, "com.tw.bt.endaddContact"

    invoke-direct {v3, v7}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v3}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    .line 69
    iget-object v0, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v0}, Lcom/eckom/xtlibrary/b/a/d/f;->o(Lcom/eckom/xtlibrary/b/a/d/f;)Landroid/content/Context;

    move-result-object v0

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/d/e;->this$0:Lcom/eckom/xtlibrary/b/a/d/f;

    invoke-static {v1}, Lcom/eckom/xtlibrary/b/a/d/f;->a(Lcom/eckom/xtlibrary/b/a/d/f;)Lcom/eckom/xtlibrary/b/a/b/a;

    move-result-object v1

    iget-object v1, v1, Lcom/eckom/xtlibrary/b/a/b/a;->yg:Ljava/lang/String;

    const/4 v3, 0x0

    invoke-static {v0, v4, v1, v3}, Lcom/eckom/xtlibrary/b/j/o;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Z)V

    const-string v0, "tssDebug"

    .line 70
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "\u5171\u8017\u65f6\uff1a"

    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    sub-long v5, v5, v18

    invoke-virtual {v1, v5, v6}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_14
    .catch Ljava/lang/Exception; {:try_start_14 .. :try_end_14} :catch_c

    goto :goto_c

    :catch_c
    move-exception v0

    goto :goto_b

    :catch_d
    move-exception v0

    move-object v4, v3

    .line 71
    :goto_b
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v4, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 72
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    :goto_c
    return-void
.end method
